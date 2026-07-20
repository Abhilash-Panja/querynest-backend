package org.com.quora_backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Username cannot be blank")

    @Size(min = 4, max = 20,
            message = "Username must be between 4 and 20 characters")

    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "Username must start with a letter and contain only letters, numbers, and underscores"
    )
    private String username;

    @Size(min = 10, max = 100, message = "Bio should contain 10-100 characters")
    private String bio;
}
