package org.com.quora_backend.dto.question;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCountResponse {

    private Long userId;

    private long questionCount;

}
