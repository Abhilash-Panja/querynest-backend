package org.com.quora_backend.repository;

import org.com.quora_backend.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
    List<Answer> findByUserId(Long userId);
    long countByUserId(Long userId);
}
