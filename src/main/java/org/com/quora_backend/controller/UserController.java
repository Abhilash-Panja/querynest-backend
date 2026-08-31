package org.com.quora_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerCountResponse;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.common.ErrorResponse;
import org.com.quora_backend.dto.question.QuestionCountResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.user.*;
import org.com.quora_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Tag(name = "Users", description = "User profile management and lookups")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Validation failed (e.g. missing name, weak password)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Username or email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements // public endpoint
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request){
        UserResponse response  = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Get a user by id")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements // public endpoint
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@Parameter(description = "User id") @PathVariable Long id){
         UserResponse response = userService.getUserById(id);
         return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Fully update a user",
            description = "Requires the caller to be the owner of the account or an ADMIN.")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "403", description = "Not the owner and not an admin")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse response = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Partially update a user",
            description = "Requires the caller to be the owner of the account or an ADMIN.")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "403", description = "Not the owner and not an admin")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateUserPatchRequest request){
        UserResponse response = userService.patchUser(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Delete a user",
            description = "Requires the caller to be the owner of the account or an ADMIN.")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "403", description = "Not the owner and not an admin")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List questions asked by a user")
    @SecurityRequirements // public endpoint
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<QuestionSearchResponse>> getUserQuestions(@PathVariable Long id){
        List<QuestionSearchResponse> response = userService.getUserQuestions(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "List answers written by a user")
    @SecurityRequirements // public endpoint
    @GetMapping("/{id}/answers")
    public ResponseEntity<List<AnswerResponse>> getUserAnswers(@PathVariable Long id){
        List<AnswerResponse> response = userService.getUserAnswers(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Check whether a username is available")
    @SecurityRequirements // public endpoint
    @GetMapping("/check-username")
    public ResponseEntity<UsernameAvailabilityResponse> checkUsername(@RequestParam String username){
        UsernameAvailabilityResponse response = userService.checkUsernameAvailability(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Check whether an email is available")
    @SecurityRequirements // public endpoint
    @GetMapping("/check-email")
    public ResponseEntity<EmailAvailabilityResponse> checkEmail(@RequestParam String email){
        EmailAvailabilityResponse response = userService.checkEmailAvailability(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get the number of questions asked by a user")
    @GetMapping("/{id}/question-count")
    public ResponseEntity<QuestionCountResponse> getUserQuestionCount(@PathVariable Long id){
        QuestionCountResponse response = userService.getUserQuestionCount(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get the number of answers written by a user")
    @GetMapping("/{id}/answer-count")
    public ResponseEntity<AnswerCountResponse> getUserAnswerCount(@PathVariable Long id){
        AnswerCountResponse response = userService.getUserAnswerCount(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
