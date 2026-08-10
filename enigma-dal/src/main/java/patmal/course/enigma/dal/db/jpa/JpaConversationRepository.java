package patmal.course.enigma.dal.db.jpa;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import patmal.course.enigma.dal.dto.ConversationEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaConversationRepository extends JpaRepository<ConversationEntity, UUID> {
    Optional<ConversationEntity> findByInviteCode(String inviteCode);

    // SELECT ... FOR UPDATE: serializes concurrent sends so the shared rotor
    // state advances one message at a time (two users typing simultaneously).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ConversationEntity c where c.id = :id")
    Optional<ConversationEntity> lockById(@Param("id") UUID id);
}
