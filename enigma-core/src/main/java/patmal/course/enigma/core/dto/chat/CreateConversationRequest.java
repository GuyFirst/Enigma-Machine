package patmal.course.enigma.core.dto.chat;

import java.util.List;

/**
 * Machine setup chosen by whoever starts the conversation - the equivalent of
 * setting up a physical Enigma before the first message. Everything except
 * machineName is optional; anything left out is chosen at random.
 *
 * @param rotorIds        rotor order, left to right
 * @param plugPairs       plugboard cables as two-letter strings, e.g. ["AC", "BE"]
 * @param initialPositions rotor letters the machine rests at, left to right
 */
public record CreateConversationRequest(
        String machineName,
        List<Integer> rotorIds,
        String reflectorId,
        List<String> plugPairs,
        List<Character> initialPositions) {
}
