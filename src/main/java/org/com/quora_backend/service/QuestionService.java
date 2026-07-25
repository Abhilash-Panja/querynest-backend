package org.com.quora_backend.service;

import org.com.quora_backend.dto.question.CreateQuestionRequest;
import org.com.quora_backend.dto.question.QuestionResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.question.UpdateQuestionRequest;

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



}
