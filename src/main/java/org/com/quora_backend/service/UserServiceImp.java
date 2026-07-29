package org.com.quora_backend.service;

import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerCountResponse;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.question.QuestionCountResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.user.*;
import org.com.quora_backend.exception.EmailAlreadyExistsException;
import org.com.quora_backend.exception.UserNotFoundException;
import org.com.quora_backend.exception.UsernameAlreadyExistsException;
import org.com.quora_backend.mapper.AnswerMapper;
import org.com.quora_backend.mapper.QuestionMapper;
import org.com.quora_backend.mapper.UserMapper;
import org.com.quora_backend.model.Answer;
import org.com.quora_backend.model.Question;
import org.com.quora_backend.model.User;
import org.com.quora_backend.repository.AnswerRepository;
import org.com.quora_backend.repository.QuestionRepository;
import org.com.quora_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class UserServiceImp implements UserService{
    private UserRepository userRepository;
    private UserMapper userMapper;
    private QuestionRepository questionRepository;
    private QuestionMapper questionMapper;
    private AnswerRepository answerRepository;
    private AnswerMapper answerMapper;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if(userRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        // 1. Convert DTO → Entity
       User user = userMapper.toEntity(request);
        // 2. Save Entity
        User savedUser = userRepository.save(user);

        // 3. Convert Entity → Response DTO
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user= getUser(id);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        Optional<User> repositoryById= userRepository.findById(id);
        User user= getUser(id);
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        user.setName(request.getName());
        user.setBio(request.getBio());
        user.setUsername(request.getUsername());
        userRepository.save(user);
        return userMapper.toResponse(user);
    }
    @Override
    public UserResponse patchUser(Long id, UpdateUserPatchRequest request) {
        User user = getUser(id);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!request.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException(request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException(request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getBio() != null && !request.getBio().isBlank()) {
            user.setBio(request.getBio());
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<QuestionSearchResponse> getUserQuestions(Long id) {
        getUser(id);
        List<Question> questions = questionRepository.findByUserId(id);
        return questions.stream()
                .map(questionMapper::toSearchResponse)
                .toList();
    }

    public List<AnswerResponse> getUserAnswers(Long id){
        getUser(id);
        List<Answer>  answers = answerRepository.findByUserId(id);
        return answers.stream()
                .map(answerMapper::toResponse)
                .toList();
    }
    @Override
    public UsernameAvailabilityResponse checkUsernameAvailability(String username) {
        boolean exists = userRepository.existsByUsername(username);
        return UsernameAvailabilityResponse.builder()
                .username(username)
                .available(!exists)
                .build();
    }

    @Override
    public EmailAvailabilityResponse checkEmailAvailability(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return EmailAvailabilityResponse.builder()
                .email(email)
                .available(!exists)
                .build();
    }
    private User getUser(Long id){

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

    }
    @Override
    public QuestionCountResponse getUserQuestionCount(Long id) {
        getUser(id); // validates user exists → 404 if not
        long count = questionRepository.countByUserId(id);
        return QuestionCountResponse.builder()
                .userId(id)
                .questionCount(count)
                .build();
    }

    @Override
    public AnswerCountResponse getUserAnswerCount(Long id) {
        getUser(id); // validates user exists → 404 if not
        long count = answerRepository.countByUserId(id);
        return AnswerCountResponse.builder()
                .userId(id)
                .answerCount(count)
                .build();
    }

}
