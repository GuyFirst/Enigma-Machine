package patmal.course.enigma.controller.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import patmal.course.enigma.core.dto.chat.ChatMessageDTO;
import patmal.course.enigma.core.dto.chat.MessagesResponseDTO;
import patmal.course.enigma.core.dto.chat.SendMessageRequest;
import patmal.course.enigma.core.service.chat.ChatMessageService;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping
    public ChatMessageDTO send(@PathVariable("conversationId") UUID conversationId,
                               @RequestBody SendMessageRequest request) {
        return chatMessageService.send(CurrentUser.id(), conversationId,
                request.ciphertext(), request.startPositions());
    }

    // Polling endpoint: clients pass the last seq they have; 0 returns everything
    @GetMapping
    public MessagesResponseDTO get(@PathVariable("conversationId") UUID conversationId,
                                   @RequestParam(value = "afterSeq", defaultValue = "0") long afterSeq) {
        return chatMessageService.getMessages(CurrentUser.id(), conversationId, afterSeq);
    }
}
