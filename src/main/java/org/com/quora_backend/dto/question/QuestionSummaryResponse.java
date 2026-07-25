package org.com.quora_backend.dto.question;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSummaryResponse {

    private Long id;

    private String title;
}
