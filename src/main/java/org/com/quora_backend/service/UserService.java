package org.com.quora_backend.service;


import org.com.quora_backend.dto.answer.AnswerCountResponse;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.question.QuestionCountResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.user.*;

import java.util.List;


public interface UserService  {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse patchUser(Long id, UpdateUserPatchRequest request);

    void deleteUser(Long id);

    List<QuestionSearchResponse> getUserQuestions(Long id);

    List<AnswerResponse> getUserAnswers(Long id);

    UsernameAvailabilityResponse checkUsernameAvailability(String username);

    EmailAvailabilityResponse checkEmailAvailability(String email);

    QuestionCountResponse getUserQuestionCount(Long id);

    AnswerCountResponse getUserAnswerCount(Long id);
}
