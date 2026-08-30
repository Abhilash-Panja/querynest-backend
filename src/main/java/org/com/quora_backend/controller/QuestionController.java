package org.com.quora_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.com.quora_backend.dto.question.CreateQuestionRequest;
import org.com.quora_backend.dto.question.QuestionResponse;
import org.com.quora_backend.dto.question.QuestionSearchResponse;
import org.com.quora_backend.dto.question.UpdateQuestionRequest;
import org.com.quora_backend.security.UserPrincipal;
import org.com.quora_backend.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/questions")
public class QuestionController {
    private final QuestionService questionService;
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request,
            @AuthenticationPrincipal UserPrincipal principal){
        QuestionResponse response  = questionService.createQuestion(request, principal.getUser().getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id){
        QuestionResponse response = questionService.getQuestionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwnedByCurrentUser(#id, authentication.principal.user.id)")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateQuestionRequest updateQuestionRequest){
        QuestionResponse response = questionService.updateQuestion(id, updateQuestionRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwnedByCurrentUser(#id, authentication.principal.user.id)")
    public ResponseEntity<Void> deletequestionById(@PathVariable Long id){
        questionService.deletequestionById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<QuestionSearchResponse>> searchQuestions(@RequestParam String keyword){
        List<QuestionSearchResponse> response = questionService.searchQuestions(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<QuestionResponse> response = questionService.getAllQuestions(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}