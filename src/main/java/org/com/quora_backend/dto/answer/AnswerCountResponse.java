package org.com.quora_backend.dto.answer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerCountResponse {

    private Long userId;

    private long answerCount;

}
