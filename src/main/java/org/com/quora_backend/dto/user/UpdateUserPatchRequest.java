package org.com.quora_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserPatchRequest {

    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "Username must start with a letter and contain only letters, numbers, and underscores"
    )
    private String username;

    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 100, message = "Bio should contain 10-100 characters")
    private String bio;
}
