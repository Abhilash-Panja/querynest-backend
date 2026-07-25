package org.com.quora_backend.service;

import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.dto.answer.UpdateAnswerRequest;
import org.com.quora_backend.model.Answer;

public interface AnswerService {

    AnswerResponse createAnswer(CreateAnswerRequest request, Long userId, Long questionId);

    AnswerResponse getAnswerById(Long id);

    AnswerResponse updateAnswer(Long id, UpdateAnswerRequest request, Long userId);

    void deleteAnswer(Long id, Long userId);

    void validateAnswerOwnership(Answer answer, Long userId);

    Answer findAnswerById(Long id);
}
