package org.com.quora_backend.mapper;

import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.question.CreateQuestionRequest;
import org.com.quora_backend.dto.question.QuestionResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.question.QuestionSummaryResponse;
import org.com.quora_backend.model.Question;
import org.com.quora_backend.model.User;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class QuestionMapper {
    private final UserMapper userMapper;

    public  QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .description(question.getDescription())
                .userSummaryResponse(userMapper.toUserSummaryResponse(question.getUser()))
                .answerCount(question.getAnswers() != null ? question.getAnswers().size() : 0)
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    public Question toEntity(CreateQuestionRequest request,
                             User user){
        return Question.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();
    }
    public QuestionSummaryResponse toQuestionSummaryResponse(Question question) {
        if (question == null) return null;
        return QuestionSummaryResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .build();
    }
    public QuestionSearchResponse toSearchResponse(Question question) {
        if (question == null) {
            return null;
        }
        return QuestionSearchResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .author(question.getUser() != null ? question.getUser().getName() : null)
                .build();
    }
}

