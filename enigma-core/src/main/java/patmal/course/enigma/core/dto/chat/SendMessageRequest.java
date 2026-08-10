package patmal.course.enigma.core.dto.chat;

import java.util.List;

/**
 * The client encrypts locally and reports what the machine did:
 * the ciphertext, the rotor position it started from, and where the rotors
 * ended up (which becomes the conversation's new machine state).
 *
 * @param expectedSeq the sequence number the sender believed was current. If
 *                    someone else transmitted first the machine has already
 *                    moved, the ciphertext was produced from a stale position,
 *                    and the send is rejected so it can be re-encrypted.
 */
public record SendMessageRequest(
        String ciphertext,
        List<Character> startPositions,
        List<Character> endPositions,
        Long expectedSeq) {
}
