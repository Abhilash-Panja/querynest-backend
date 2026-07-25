package org.com.quora_backend.dto.question;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSearchResponse {

    private Long id;

    private String title;

    private String author;

}
