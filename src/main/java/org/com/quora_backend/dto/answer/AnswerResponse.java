package org.com.quora_backend.dto.answer;

import lombok.*;
import org.com.quora_backend.dto.question.QuestionSummaryResponse;
import org.com.quora_backend.dto.user.UserSummaryResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {

    private Long id;
    private String content;
    private UserSummaryResponse author;
    private QuestionSummaryResponse question;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
