package org.com.quora_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.dto.answer.UpdateAnswerRequest;
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
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerResponse> createAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody CreateAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        AnswerResponse response = answerService.createAnswer(request, principal.getUser().getId(), questionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/answers/{id}")
    public ResponseEntity<AnswerResponse> getAnswerById(@PathVariable Long id) {
        AnswerResponse response = answerService.getAnswerById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/answers/{id}")
    @PreAuthorize("hasRole('ADMIN') or @answerService.isOwnedByCurrentUser(#id, principal.user.id)")
    public ResponseEntity<AnswerResponse> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        AnswerResponse response = answerService.updateAnswer(id, request, principal.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/answers/{id}")
    @PreAuthorize("hasRole('ADMIN') or @answerService.isOwnedByCurrentUser(#id, principal.user.id)")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        answerService.deleteAnswer(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/answers/{id}/vote")
    public ResponseEntity<AnswerResponse> voteAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VoteRequest request) {

        AnswerResponse response = answerService.voteAnswer(id, principal.getUser().getId(), request);
        return ResponseEntity.ok(response);
    }
}