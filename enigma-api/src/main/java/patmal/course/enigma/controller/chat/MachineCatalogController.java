package patmal.course.enigma.controller.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import patmal.course.enigma.core.dto.chat.MachineSummaryDTO;
import patmal.course.enigma.core.dto.chat.MachineWiringDTO;
import patmal.course.enigma.core.service.chat.ConversationService;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineCatalogController {
    private final ConversationService conversationService;

    public MachineCatalogController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public List<MachineSummaryDTO> list() {
        return conversationService.listMachines();
    }

    /** Wiring for the browser-side machine. */
    @GetMapping("/{name}/wiring")
    public MachineWiringDTO wiring(@PathVariable("name") String name) {
        return conversationService.getWiring(name);
    }
}
