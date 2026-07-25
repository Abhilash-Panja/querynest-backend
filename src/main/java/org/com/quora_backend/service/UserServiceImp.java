package org.com.quora_backend.service;

import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.user.CreateUserRequest;
import org.com.quora_backend.dto.user.UpdateUserPatchRequest;
import org.com.quora_backend.dto.user.UpdateUserRequest;
import org.com.quora_backend.dto.user.UserResponse;
import org.com.quora_backend.exception.EmailAlreadyExistsException;
import org.com.quora_backend.exception.UserNotFoundException;
import org.com.quora_backend.exception.UsernameAlreadyExistsException;
import org.com.quora_backend.mapper.QuestionMapper;
import org.com.quora_backend.mapper.UserMapper;
import org.com.quora_backend.model.Question;
import org.com.quora_backend.model.User;
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

    private User getUser(Long id){

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

    }

}
