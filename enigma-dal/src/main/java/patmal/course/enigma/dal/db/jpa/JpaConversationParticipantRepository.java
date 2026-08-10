package patmal.course.enigma.dal.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import patmal.course.enigma.dal.dto.ConversationParticipantEntity;

import java.util.List;
import java.util.UUID;

public interface JpaConversationParticipantRepository extends JpaRepository<ConversationParticipantEntity, UUID> {
    List<ConversationParticipantEntity> findByUserIdOrderByJoinedAtDesc(UUID userId);
    List<ConversationParticipantEntity> findByConversationId(UUID conversationId);
    boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);
    long countByConversationId(UUID conversationId);
}
