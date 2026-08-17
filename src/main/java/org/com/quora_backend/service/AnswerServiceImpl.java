package org.com.quora_backend.service;

import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.dto.answer.UpdateAnswerRequest;
import org.com.quora_backend.dto.vote.VoteRequest;
import org.com.quora_backend.exception.AnswerNotFoundException;
import org.com.quora_backend.exception.ResourceNotFoundException;
import org.com.quora_backend.exception.UnauthorizedAccessException;
import org.com.quora_backend.exception.UserNotFoundException;
import org.com.quora_backend.mapper.AnswerMapper;
import org.com.quora_backend.model.*;
import org.com.quora_backend.repository.AnswerRepository;
import org.com.quora_backend.repository.QuestionRepository;
import org.com.quora_backend.repository.UserRepository;
import org.com.quora_backend.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;
    private final VoteRepository voteRepository;

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
    @Override
    public AnswerResponse voteAnswer(Long answerId, Long userId, VoteRequest request) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Optional<Vote> existingVoteOpt = voteRepository.findByUserIdAndAnswerId(userId, answerId);
        int currentScore = answer.getVoteScore();

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();

            if (existingVote.getVoteType() == request.getVoteType()) {
                // Same vote clicked again → toggle off
                int newScore = calculateNewScore(currentScore, existingVote.getVoteType(), null);
                answer.setVoteScore(newScore);
                voteRepository.delete(existingVote);
            } else {
                // Switching vote type
                int newScore = calculateNewScore(currentScore, existingVote.getVoteType(), request.getVoteType());
                answer.setVoteScore(newScore);
                existingVote.setVoteType(request.getVoteType());
                voteRepository.save(existingVote);
            }
        } else {
            // Brand new vote
            int newScore = calculateNewScore(currentScore, null, request.getVoteType());
            answer.setVoteScore(newScore);

            Vote vote = Vote.builder()
                    .user(user)
                    .answer(answer)
                    .voteType(request.getVoteType())
                    .build();
            voteRepository.save(vote);
        }

        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }
    private int contributionOf(VoteType voteType) {
        if (voteType == null) return 0;
        return voteType == VoteType.UPVOTE ? 1 : -1;
    }
    @Override
    public boolean isOwnedByCurrentUser(Long answerId, Long userId) {
        return answerRepository.findById(answerId)
                .map(answer -> answer.getUser().getId().equals(userId))
                .orElse(false);
    }

    private int calculateNewScore(int currentScore, VoteType oldVote, VoteType newVote) {
        int oldContribution = contributionOf(oldVote);
        int newContribution = contributionOf(newVote);
        int delta = newContribution - oldContribution;
        return currentScore + delta;
    }
}