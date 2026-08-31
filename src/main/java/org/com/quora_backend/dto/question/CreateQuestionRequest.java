package org.com.quora_backend.dto.question;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionRequest {

    @NotBlank(message = "title cannot be blank")
    @Schema(example = "How does JWT authentication work?")
    private String title;

    @NotBlank(message = "description cannot be blank")
    @Schema(example = "I'm trying to understand how JWTs are validated on each request without hitting the database every time.")
    private String description;
}
