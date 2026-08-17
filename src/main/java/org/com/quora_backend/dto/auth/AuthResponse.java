package org.com.quora_backend.dto.auth;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthResponse {
    private String token;
}