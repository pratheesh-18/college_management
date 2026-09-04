package com.edusphere.repository;

import com.edusphere.entity.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {
    List<AiChatMessage> findByUserIdOrderByTimestampAsc(Long userId);
    List<AiChatMessage> findByUserEmailOrderByTimestampAsc(String email);
}
