package com.dias.dteacher.repository;

import com.dias.dteacher.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    Optional<ChatSession> findByIdAndUserEmail(UUID id, String userEmail);
}
