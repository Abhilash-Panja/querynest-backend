package org.com.quora_backend.service;

import org.com.quora_backend.dto.question.CreateQuestionRequest;
import org.com.quora_backend.dto.question.QuestionResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.question.UpdateQuestionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    QuestionResponse updateQuestion(
            Long id,
            UpdateQuestionRequest updateQuestionRequest);

    QuestionResponse getQuestionById(Long id);

    QuestionResponse createQuestion(
            CreateQuestionRequest createQuestionRequest);

    void deletequestionById(Long id);

    List<QuestionSearchResponse> searchQuestions(String keyword);

    Page<QuestionResponse> getAllQuestions(Pageable pageable);

    boolean isOwnedByCurrentUser(Long questionId, Long userId);
}
