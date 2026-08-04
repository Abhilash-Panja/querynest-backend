package org.com.quora_backend.mapper;

import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.user.CreateUserRequest;
import org.com.quora_backend.dto.user.UserResponse;
import org.com.quora_backend.dto.user.UserSummaryResponse;
import org.com.quora_backend.model.Role;
import org.com.quora_backend.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    public UserSummaryResponse toUserSummaryResponse(User user) {
        if (user == null) return null;
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .build();
    }

    public UserResponse toResponse(User user){
        return  UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .bio(request.getBio())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
    }
}
