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
import org.com.quora_backend.dto.common.ErrorResponse;
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
@Tag(name = "Questions", description = "Ask, browse, search, update and delete questions")
public class QuestionController {
    private final QuestionService questionService;

    @Operation(summary = "Ask a new question", description = "Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Question created")
    @ApiResponse(responseCode = "400", description = "Validation failed (blank title/description)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT")
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request,
            @AuthenticationPrincipal UserPrincipal principal){
        QuestionResponse response  = questionService.createQuestion(request, principal.getUser().getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Get a question by id")
    @ApiResponse(responseCode = "200", description = "Question found")
    @ApiResponse(responseCode = "404", description = "Question not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements // public endpoint
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@Parameter(description = "Question id") @PathVariable Long id){
        QuestionResponse response = questionService.getQuestionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Update a question",
            description = "Requires the caller to be the question's owner or an ADMIN.")
    @ApiResponse(responseCode = "200", description = "Question updated")
    @ApiResponse(responseCode = "403", description = "Not the owner and not an admin")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwnedByCurrentUser(#id, authentication.principal.user.id)")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateQuestionRequest updateQuestionRequest){
        QuestionResponse response = questionService.updateQuestion(id, updateQuestionRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Delete a question",
            description = "Requires the caller to be the question's owner or an ADMIN.")
    @ApiResponse(responseCode = "204", description = "Question deleted")
    @ApiResponse(responseCode = "403", description = "Not the owner and not an admin")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwnedByCurrentUser(#id, authentication.principal.user.id)")
    public ResponseEntity<Void> deletequestionById(@PathVariable Long id){
        questionService.deletequestionById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search questions by keyword")
    @SecurityRequirements // public endpoint
    @GetMapping("/search")
    public ResponseEntity<List<QuestionSearchResponse>> searchQuestions(
            @Parameter(description = "Keyword to match against question title/body") @RequestParam String keyword){
        List<QuestionSearchResponse> response = questionService.searchQuestions(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "List all questions",
            description = "Paginated and sortable. Defaults to 10 per page, newest first.")
    @SecurityRequirements // public endpoint
    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<QuestionResponse> response = questionService.getAllQuestions(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}