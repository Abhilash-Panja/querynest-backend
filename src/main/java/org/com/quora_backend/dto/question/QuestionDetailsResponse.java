package org.com.quora_backend.dto.question;

import lombok.*;
import org.com.quora_backend.dto.answer.AnswerResponse;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionDetailsResponse {

    private Long id;

    private String title;

    private String description;

    private String username;

    private List<AnswerResponse> answers;

    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;

}
