package patmal.course.enigma.core.dto.chat;

import java.util.List;

/** The client encrypts locally and sends only the result plus its message key. */
public record SendMessageRequest(String ciphertext, List<Character> startPositions) {
}
