package org.com.quora_backend.service;

import org.com.quora_backend.dto.user.CreateUserRequest;
import org.com.quora_backend.dto.user.UpdateUserRequest;
import org.com.quora_backend.dto.user.UserResponse;
import org.com.quora_backend.exception.UserNotFoundException;
import org.com.quora_backend.exception.UsernameAlreadyExistsException;
import org.com.quora_backend.mapper.UserMapper;
import org.com.quora_backend.model.User;
import org.com.quora_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserService{
    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserServiceImp(UserRepository userRepository,UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper=userMapper;
    }

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
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    private User getUser(Long id){

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

    }
}
