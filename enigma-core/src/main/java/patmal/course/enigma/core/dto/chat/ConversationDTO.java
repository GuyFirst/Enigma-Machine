package patmal.course.enigma.core.dto.chat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationDTO(
        UUID id,
        String machineName,
        String abc,
        String inviteCode,
        UUID createdBy,
        List<ProfileDTO> participants,
        List<Integer> rotorIds,
        String reflectorId,
        List<String> plugs,
        List<Character> originalPositions,
        List<Character> currentPositions,
        String currentPositionsCompact,
        long lastSeq,
        Instant createdAt) {
}
