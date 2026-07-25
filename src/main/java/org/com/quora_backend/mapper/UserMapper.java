package org.com.quora_backend.mapper;

import org.com.quora_backend.dto.user.CreateUserRequest;
import org.com.quora_backend.dto.user.UserResponse;
import org.com.quora_backend.dto.user.UserSummaryResponse;
import org.com.quora_backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
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

    public User toEntity(CreateUserRequest createUserRequest){
        return User.builder()
                .username(createUserRequest.getUsername())
                .email(createUserRequest.getEmail())
                .bio(createUserRequest.getBio())
                .name(createUserRequest.getName())
                .build();
    }
}
