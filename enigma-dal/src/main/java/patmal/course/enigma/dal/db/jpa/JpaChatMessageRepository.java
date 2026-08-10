package patmal.course.enigma.dal.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import patmal.course.enigma.dal.dto.ChatMessageEntity;

import java.util.List;
import java.util.UUID;

public interface JpaChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {
    List<ChatMessageEntity> findByConversationIdAndSeqGreaterThanOrderBySeqAsc(UUID conversationId, long afterSeq);
}
