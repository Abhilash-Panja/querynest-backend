package org.com.quora_backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username cannot be blank")
    @Schema(example = "jane_doe")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Schema(example = "SecurePass123!")
    private String password;
}