package org.com.quora_backend.dto.answer;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAnswerRequest {

    @NotBlank(message = "content cannot be blank")
    @Schema(example = "JWT validation is stateless: the server verifies the signature using its secret key, so it never needs to query the database to trust the token's claims.")
    private String content;

    // Not read by the service layer - questionId comes from the URL path and
    // userId from the authenticated principal. Hidden so the docs don't imply
    // callers need to set these.
    @Schema(hidden = true)
    private Long questionId;

    @Schema(hidden = true)
    private Long userId;
}
