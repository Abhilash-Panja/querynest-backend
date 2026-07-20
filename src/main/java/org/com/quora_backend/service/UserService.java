package org.com.quora_backend.service;


import org.com.quora_backend.dto.user.CreateUserRequest;
import org.com.quora_backend.dto.user.UpdateUserRequest;
import org.com.quora_backend.dto.user.UserResponse;


public interface UserService  {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}
