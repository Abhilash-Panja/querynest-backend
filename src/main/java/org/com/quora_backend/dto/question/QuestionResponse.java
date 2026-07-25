package org.com.quora_backend.dto.question;


import lombok.*;
import org.com.quora_backend.dto.user.UserSummaryResponse;


import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionResponse {
    private Long id;

    private String title;

    private String description;

    private UserSummaryResponse userSummaryResponse;

    private Integer answerCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
