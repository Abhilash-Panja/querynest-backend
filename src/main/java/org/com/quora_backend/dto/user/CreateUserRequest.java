package org.com.quora_backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Setter
@Jacksonized
public class CreateUserRequest {

    @NotBlank(message = "Username cannot be blank")

    @Size(min = 2, max = 20,
            message = "Username must be between 2 and 20 characters")

    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "Username must start with a letter and contain only letters, numbers, and underscores"
    )
    @Schema(example = "jane_doe")
    private String username;

    @NotBlank(message = "Name cannot be blank")
    @Schema(example = "Jane Doe")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Schema(example = "jane.doe@example.com")
    private String email;

    @Size(min = 10, max = 100, message = "Bio should contain 10-100 characters")
    @Schema(example = "Backend developer interested in distributed systems.")
    private String bio;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(example = "SecurePass123!")
    private String password;
}
