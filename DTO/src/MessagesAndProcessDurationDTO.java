import java.util.List;
import java.util.Map;

public class MessagesAndProcessDurationDTO {
    private final List<MessageDTO> allMessages;
    private final Map<MessageDTO, Long> processDurationInMillis;

    public MessagesAndProcessDurationDTO(List<MessageDTO> allMessages, Map<MessageDTO, Long> processDurationInMillis) {
        this.allMessages = allMessages;
        this.processDurationInMillis = processDurationInMillis;
    }

    public List<MessageDTO> getAllMessages() {
        return allMessages;
    }

    public Map<MessageDTO, Long> getProcessDurationInMillis() {
        return processDurationInMillis;
    }
}
