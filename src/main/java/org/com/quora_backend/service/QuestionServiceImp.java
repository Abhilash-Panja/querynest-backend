package org.com.quora_backend.service;


import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.question.CreateQuestionRequest;
import org.com.quora_backend.dto.question.QuestionResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.question.UpdateQuestionRequest;
import org.com.quora_backend.exception.QuestionNotFoundException;
import org.com.quora_backend.exception.UserNotFoundException;
import org.com.quora_backend.mapper.QuestionMapper;
import org.com.quora_backend.model.Question;
import org.com.quora_backend.model.User;
import org.com.quora_backend.repository.QuestionRepository;
import org.com.quora_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class QuestionServiceImp implements QuestionService{
    private QuestionRepository questionRepository;
    private QuestionMapper questionMapper;
    private final UserRepository userRepository;
    @Override
    public QuestionResponse updateQuestion(Long id,
                                           UpdateQuestionRequest updateQuestionRequest) {
        Question question=questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        question.setTitle(updateQuestionRequest.getTitle());
        question.setDescription(updateQuestionRequest.getDescription());
        Question savedQuestion= questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public QuestionResponse getQuestionById(Long id) {
        Question question=questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        return questionMapper.toResponse(question);
    }

    @Override
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        Question question = questionMapper.toEntity(request, user);

        Question savedQuestion = questionRepository.save(question);

        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public void deletequestionById(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public List<QuestionSearchResponse> searchQuestions(String keyword) {
        List<Question> questions = questionRepository.findByTitleContainingIgnoreCase(keyword);
        return questions.stream()
                .map(questionMapper::toSearchResponse)
                .toList();
    }
    @Override
    public Page<QuestionResponse> getAllQuestions(Pageable pageable) {
        Page<Question> questions = questionRepository.findAll(pageable);
        return questions.map(questionMapper::toResponse);
    }

    @Override
    public boolean isOwnedByCurrentUser(Long questionId, Long userId) {
        return questionRepository.findById(questionId)
                .map(question -> question.getUser().getId().equals(userId))
                .orElse(false); // question doesn't exist → let the method run,
        // it will 404 naturally rather than 403
    }
}
