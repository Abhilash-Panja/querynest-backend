package org.com.quora_backend.dto.answer;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAnswerRequest {

    @NotBlank(message = "content cannot be blank")
    private String content;

    private Long questionId;

    private Long userId;
}
