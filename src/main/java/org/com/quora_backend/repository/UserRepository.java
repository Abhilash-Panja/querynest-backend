package org.com.quora_backend.repository;

import jakarta.validation.constraints.Email;
import org.com.quora_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(@Email(message = "Invalid email format") String email);
}
