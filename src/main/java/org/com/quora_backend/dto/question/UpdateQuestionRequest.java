package org.com.quora_backend.dto.question;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateQuestionRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
