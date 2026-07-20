package org.com.quora_backend.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;

    private String name;

    private String email;

    private String bio;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
