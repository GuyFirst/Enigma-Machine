package patmal.course.enigma.core.dto.chat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A conversation is the shared "code book page": the machine composition is
 * fixed for its lifetime. Per-message rotor start positions are NOT here -
 * each message carries its own (see {@link ChatMessageDTO#startPositions()}).
 */
public record ConversationDTO(
        UUID id,
        String machineName,
        String alphabet,
        String inviteCode,
        UUID createdBy,
        List<ProfileDTO> participants,
        List<Integer> rotorIds,
        String reflectorId,
        Map<String, String> plugs,
        List<Character> initialPositions,
        long lastSeq,
        Instant createdAt) {
}
