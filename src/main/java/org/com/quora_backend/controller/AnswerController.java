package org.com.quora_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.dto.answer.UpdateAnswerRequest;
import org.com.quora_backend.dto.common.ErrorResponse;
import org.com.quora_backend.dto.vote.VoteRequest;
import org.com.quora_backend.security.UserPrincipal;
import org.com.quora_backend.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Tag(name = "Answers", description = "Post, update, delete and vote on answers")
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "Post an answer to a question", description = "Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Answer created")
    @ApiResponse(responseCode = "400", description = "Validation failed (blank content)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Question not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerResponse> createAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody CreateAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        AnswerResponse response = answerService.createAnswer(request, principal.getUser().getId(), questionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get an answer by id")
    @ApiResponse(responseCode = "200", description = "Answer found")
    @ApiResponse(responseCode = "404", description = "Answer not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements // public endpoint
    @GetMapping("/answers/{id}")
    public ResponseEntity<AnswerResponse> getAnswerById(@PathVariable Long id) {
        AnswerResponse response = answerService.getAnswerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update an answer",
            description = "Requires the caller to be the answer's owner or an ADMIN.")
    @ApiResponse(responseCode = "200", description = "Answer updated")
    @ApiResponse(responseCode = "403", description = "Not the owner and not an admin")
    @PutMapping("/answers/{id}")
    @PreAuthorize("hasRole('ADMIN') or @answerService.isOwnedByCurrentUser(#id, principal.user.id)")
    public ResponseEntity<AnswerResponse> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        AnswerResponse response = answerService.updateAnswer(id, request, principal.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete an answer",
            description = "Requires the caller to be the answer's owner or an ADMIN.")
    @ApiResponse(responseCode = "204", description = "Answer deleted")
    @ApiResponse(responseCode = "403", description = "Not the owner and not an admin")
    @DeleteMapping("/answers/{id}")
    @PreAuthorize("hasRole('ADMIN') or @answerService.isOwnedByCurrentUser(#id, principal.user.id)")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        answerService.deleteAnswer(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upvote or downvote an answer", description = "Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Vote recorded, updated answer returned")
    @PostMapping("/answers/{id}/vote")
    public ResponseEntity<AnswerResponse> voteAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VoteRequest request) {

        AnswerResponse response = answerService.voteAnswer(id, principal.getUser().getId(), request);
        return ResponseEntity.ok(response);
    }
}