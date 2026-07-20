package org.com.quora_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Username cannot be blank")

    @Size(min = 4, max = 20,
            message = "Username must be between 4 and 20 characters")

    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "Username must start with a letter and contain only letters, numbers, and underscores"
    )
    private String username;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Size(min = 10, max = 100, message = "Bio should contain 10-100 characters")
    private String bio;
}
