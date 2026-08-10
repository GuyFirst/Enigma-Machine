package patmal.course.enigma.controller.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import patmal.course.enigma.core.service.chat.StaleMachineStateException;

import java.util.Map;
import java.util.NoSuchElementException;

/** Maps chat-domain exceptions to consistent JSON errors for the /api endpoints. */
@RestControllerAdvice(basePackages = "patmal.course.enigma.controller.chat")
public class ChatApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> forbidden(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> notFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    /** The shared machine moved while the sender was composing. */
    @ExceptionHandler(StaleMachineStateException.class)
    public ResponseEntity<Map<String, String>> conflict(StaleMachineStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
}
