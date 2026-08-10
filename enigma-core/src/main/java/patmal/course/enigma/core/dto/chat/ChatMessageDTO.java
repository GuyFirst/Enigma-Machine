package patmal.course.enigma.core.dto.chat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A message as it travels and is stored: ciphertext only. `startPositions` is
 * the message key indicator - transmitted in the clear, exactly like the
 * historical protocol - so a recipient holding the conversation's machine
 * settings can set their rotors and decrypt. The plaintext exists only in the
 * sender's and recipient's browsers.
 */
public record ChatMessageDTO(
        UUID id,
        long seq,
        UUID senderId,
        String senderUsername,
        String ciphertext,
        List<Character> startPositions,
        Instant createdAt) {
}
