package org.com.quora_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.answer.AnswerResponse;
import org.com.quora_backend.dto.answer.CreateAnswerRequest;
import org.com.quora_backend.dto.answer.UpdateAnswerRequest;
import org.com.quora_backend.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestHeader("X-User-Id") Long userId) {

        AnswerResponse response = answerService.createAnswer(request, userId, questionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/answers/{id}")
    public ResponseEntity<AnswerResponse> getAnswerById(@PathVariable Long id) {
        AnswerResponse response = answerService.getAnswerById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/answers/{id}")
    public ResponseEntity<AnswerResponse> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnswerRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        AnswerResponse response = answerService.updateAnswer(id, request, userId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/answers/{id}")
    public  ResponseEntity<Void> deleteAnswer(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        answerService.deleteAnswer(id, userId);
        return ResponseEntity.noContent().build();
    }
}
