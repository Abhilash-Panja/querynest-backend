package org.com.quora_backend.service;


import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.user.CreateUserRequest;
import org.com.quora_backend.dto.user.UpdateUserPatchRequest;
import org.com.quora_backend.dto.user.UpdateUserRequest;
import org.com.quora_backend.dto.user.UserResponse;

import java.util.List;


public interface UserService  {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse patchUser(Long id, UpdateUserPatchRequest request);

    void deleteUser(Long id);

    List<QuestionSearchResponse> getUserQuestions(Long id);
}
