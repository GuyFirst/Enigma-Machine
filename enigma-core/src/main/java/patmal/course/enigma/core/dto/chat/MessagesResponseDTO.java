package patmal.course.enigma.core.dto.chat;

import java.util.List;

public record MessagesResponseDTO(
        List<ChatMessageDTO> messages,
        long lastSeq,
        List<Character> currentPositions,
        String currentPositionsCompact) {
}
