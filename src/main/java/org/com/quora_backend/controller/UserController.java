package org.com.quora_backend.controller;

import jakarta.validation.Valid;
import org.com.quora_backend.dto.answer.AnswerCountResponse;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.question.QuestionCountResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.user.*;
import org.com.quora_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request){
        UserResponse response  = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
         UserResponse response = userService.getUserById(id);
         return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse response = userService.updateUser(id,updateUserRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateUserPatchRequest request){
        UserResponse response = userService.patchUser(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<QuestionSearchResponse>> getUserQuestions(@PathVariable Long id){
        List<QuestionSearchResponse> response = userService.getUserQuestions(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/{id}/answers")
    public ResponseEntity<List<AnswerResponse>> getUserAnswers(@PathVariable Long id){
        List<AnswerResponse> response = userService.getUserAnswers(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/check-username")
    public ResponseEntity<UsernameAvailabilityResponse> checkUsername(@RequestParam String username){
        UsernameAvailabilityResponse response = userService.checkUsernameAvailability(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/check-email")
    public ResponseEntity<EmailAvailabilityResponse> checkEmail(@RequestParam String email){
        EmailAvailabilityResponse response = userService.checkEmailAvailability(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/{id}/question-count")
    public ResponseEntity<QuestionCountResponse> getUserQuestionCount(@PathVariable Long id){
        QuestionCountResponse response = userService.getUserQuestionCount(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/{id}/answer-count")
    public ResponseEntity<AnswerCountResponse> getUserAnswerCount(@PathVariable Long id){
        AnswerCountResponse response = userService.getUserAnswerCount(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
