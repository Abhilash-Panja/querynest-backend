package org.com.quora_backend.dto.question;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionRequest {

    @NotBlank(message = "title cannot be blank")
    private String title;

    @NotBlank(message = "description cannot be blank")
    private String description;

    private Long userId;
}
