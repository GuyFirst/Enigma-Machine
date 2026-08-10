package patmal.course.enigma.controller.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import patmal.course.enigma.core.dto.chat.ConversationDTO;
import patmal.course.enigma.core.dto.chat.CreateConversationRequest;
import patmal.course.enigma.core.dto.chat.JoinConversationRequest;
import patmal.course.enigma.core.service.chat.ConversationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ConversationDTO create(@RequestBody CreateConversationRequest request) {
        return conversationService.create(CurrentUser.id(), request.machineName());
    }

    @PostMapping("/join")
    public ConversationDTO join(@RequestBody JoinConversationRequest request) {
        return conversationService.join(CurrentUser.id(), request.inviteCode());
    }

    @GetMapping
    public List<ConversationDTO> list() {
        return conversationService.listForUser(CurrentUser.id());
    }

    @GetMapping("/{id}")
    public ConversationDTO get(@PathVariable("id") UUID id) {
        return conversationService.getForUser(CurrentUser.id(), id);
    }
}
