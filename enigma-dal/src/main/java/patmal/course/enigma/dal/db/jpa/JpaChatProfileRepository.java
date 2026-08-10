package patmal.course.enigma.dal.db.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import patmal.course.enigma.dal.dto.ChatProfileEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaChatProfileRepository extends JpaRepository<ChatProfileEntity, UUID> {
    Optional<ChatProfileEntity> findByUsernameIgnoreCase(String username);
}
