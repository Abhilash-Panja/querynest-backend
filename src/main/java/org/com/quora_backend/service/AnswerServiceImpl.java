package org.com.quora_backend.service;

import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.dto.answer.UpdateAnswerRequest;
import org.com.quora_backend.exception.ResourceNotFoundException;
import org.com.quora_backend.exception.UnauthorizedAccessException;
import org.com.quora_backend.mapper.AnswerMapper;
import org.com.quora_backend.model.Answer;
import org.com.quora_backend.model.Question;
import org.com.quora_backend.model.User;
import org.com.quora_backend.repository.AnswerRepository;
import org.com.quora_backend.repository.QuestionRepository;
import org.com.quora_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;

    @Override
    @Transactional
    public AnswerResponse createAnswer(CreateAnswerRequest request, Long userId, Long questionId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        Answer answer = answerMapper.toEntity(request, author, question);
        Answer savedAnswer = answerRepository.save(answer);

        return answerMapper.toResponse(savedAnswer);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerResponse getAnswerById(Long id) {
        Answer answer = findAnswerById(id);
        return answerMapper.toResponse(answer);
    }

    @Override
    @Transactional
    public AnswerResponse updateAnswer(Long id, UpdateAnswerRequest request, Long userId) {
        Answer answer = findAnswerById(id);
        validateAnswerOwnership(answer, userId);

        answerMapper.updateEntity(answer, request);
        Answer updatedAnswer = answerRepository.save(answer);

        return answerMapper.toResponse(updatedAnswer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id, Long userId) {
        Answer answer = findAnswerById(id);
        validateAnswerOwnership(answer, userId);
        answerRepository.delete(answer);
    }
    @Override
    public Answer findAnswerById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
    }
    @Override
    public void validateAnswerOwnership(Answer answer, Long userId) {
        if (!answer.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("User is not authorized to modify this answer.");
        }

    }

}