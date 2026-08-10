package patmal.course.enigma.core.service.chat;

/**
 * The sender encrypted from a rotor position the shared machine has already
 * left - typically because the other participant transmitted first. Surfaced
 * as HTTP 409 so the client can re-encrypt from the current position.
 */
public class StaleMachineStateException extends RuntimeException {
    public StaleMachineStateException(String message) {
        super(message);
    }
}
