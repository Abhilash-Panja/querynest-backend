package org.com.quora_backend.mapper;

import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.dto.answer.UpdateAnswerRequest;
import org.com.quora_backend.model.Answer;
import org.com.quora_backend.model.Question;
import org.com.quora_backend.model.User;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnswerMapper {
    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;
    public Answer toEntity(CreateAnswerRequest request, User author, Question question) {
        if (request == null) {
            return null;
        }
        Answer answer = new Answer();
        answer.setContent(request.getContent());
        answer.setUser(author);
        answer.setQuestion(question);
        return answer;
    }


    public AnswerResponse toResponse(Answer answer) {
        if (answer == null) {
            return null;
        }
        return AnswerResponse.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .author(userMapper.toUserSummaryResponse(answer.getUser()))
                .question(questionMapper.toQuestionSummaryResponse(answer.getQuestion()))
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }

    public void updateEntity(Answer answer, UpdateAnswerRequest request) {
        if (answer == null || request == null) {
            return;
        }
        if (request.getContent() != null && !request.getContent().isBlank()) {
            answer.setContent(request.getContent());
        }
        answer.setUpdatedAt(answer.getUpdatedAt());
    }
}