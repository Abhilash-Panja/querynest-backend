package org.com.quora_backend.repository;

import org.com.quora_backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByTitleContainingIgnoreCase(String keyword);
    List<Question> findByUserId(Long userId);
    long countByUserId(Long userId);
}
