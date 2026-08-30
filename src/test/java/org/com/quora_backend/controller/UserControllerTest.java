package org.com.quora_backend.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.com.quora_backend.config.SecurityConfig;
import org.com.quora_backend.dto.user.CreateUserRequest;
import org.com.quora_backend.dto.user.UserResponse;
import org.com.quora_backend.dto.user.UsernameAvailabilityResponse;
import org.com.quora_backend.model.User;
import org.com.quora_backend.security.CustomAccessDeniedHandler;
import org.com.quora_backend.security.CustomAuthenticationEntryPoint;
import org.com.quora_backend.security.JwtAuthenticationFilter;
import org.com.quora_backend.security.UserPrincipal;
import org.com.quora_backend.service.CustomUserDetailsService;
import org.com.quora_backend.service.JwtService;
import org.com.quora_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // replaces @MockBean, SB 3.4+
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({
        SecurityConfig.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void stubJwtFilterToPassThrough() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    // ---------- Simple, no-security endpoints ----------

    @Test
    void createUser_shouldReturn201_whenRequestValid() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("alice123")
                .name("Alice")
                .email("alice@mail.com")
                .password("password123")
                .build();
        UserResponse fakeResponse = UserResponse.builder()
                .id(1L)
                .username("alice123")
                .name("Alice")
                .email("alice@mail.com")
                .build();


        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice123"));
    }

    @Test
    void createUser_shouldReturn400_whenBodyInvalid() throws Exception {
        // Missing/blank fields — should fail @Valid before hitting the service
        String invalidJson = "{\"username\": \"\"}";

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService); // proves validation stopped it early
    }

    @Test
    void getUser_shouldReturn200_whenUserExists() throws Exception {
        when(userService.getUserById(1L)).thenReturn(UserResponse.builder()
                .id(1L)
                .username("alice123")
                .name("Alice")
                .email("alice@mail.com")
                .build());

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice123"));
    }

    @Test
    void checkUsername_shouldReturn400_whenParamMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/check-username"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkUsername_shouldReturn200_whenParamPresent() throws Exception {
        when(userService.checkUsernameAvailability("Alice"))
                .thenReturn(UsernameAvailabilityResponse
                        .builder()
                        .available(true)
                        .build());

        mockMvc.perform(get("/api/v1/users/check-username").param("username", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    // ---------- @PreAuthorize endpoints ----------

    @Test
    void deleteUser_shouldReturn401or403_whenUnauthenticated() throws Exception {
        // No auth set up at all — this is a genuinely unauthenticated request,
        // so ExceptionTranslationFilter routes to the AuthenticationEntryPoint → 401
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    void deleteUser_shouldReturn204_whenCallerIsAdmin() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1")
                        .with(authentication(auth))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
    @Test
    void deleteUser_shouldReturn204_whenCallerIsOwner() throws Exception {

        UserPrincipal principal = UserPrincipal.builder()
                .user(User.builder()
                        .id(1L)
                        .username("alice123")
                        .email("alice@mail.com")
                        .build())
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of()
        );

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(
                        delete("/api/v1/users/1")
                                .with(authentication(auth))
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
}
