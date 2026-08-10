package patmal.course.enigma.core.dto.chat;

import java.util.List;

/**
 * @param currentPositions where the conversation's shared machine now stands -
 *                         what the next message will be encrypted from
 */
public record MessagesResponseDTO(
        List<ChatMessageDTO> messages,
        long lastSeq,
        List<Character> currentPositions) {
}
