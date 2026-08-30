package org.com.quora_backend.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.model.Role;
import org.com.quora_backend.model.User;
import org.com.quora_backend.security.JwtAuthenticationFilter;
import org.com.quora_backend.security.UserPrincipal;
import org.com.quora_backend.service.AnswerService;
import org.com.quora_backend.service.CustomUserDetailsService;
import org.com.quora_backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AnswerController.class)
class AnswerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnswerService answerService;

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

    private AnswerResponse answerResponse;


    @BeforeEach
    void setUp() {

         answerResponse = AnswerResponse.builder()
                .id(1L)
                .content("This is an answer")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }



    @Test
    void shouldCreateAnswerSuccessfully() throws Exception {

        User testUser = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();

        UserPrincipal principal = new UserPrincipal(testUser);

        CreateAnswerRequest request = CreateAnswerRequest.builder()
                .content("Spring Boot Answer")
                .build();

        when(answerService.createAnswer(any(), anyLong(), anyLong()))
                .thenReturn(answerResponse);

        mockMvc.perform(
                        post("/api/v1/questions/1/answers")
                                .with(user(principal))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        verify(answerService).createAnswer(any(), anyLong(), anyLong());
    }
}