package patmal.course.enigma.core.dto.chat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatMessageDTO(
        UUID id,
        long seq,
        UUID senderId,
        String senderUsername,
        String ciphertext,
        String plaintext,
        List<Character> positionsAtEncryption,
        String codeCompact,
        Instant createdAt) {
}
