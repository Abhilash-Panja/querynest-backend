package org.com.quora_backend.dto.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateAnswerRequest {

    @NotBlank
    private String content;
}
