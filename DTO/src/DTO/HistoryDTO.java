package DTO;

import java.util.List;
import java.util.Map;

public class HistoryDTO {
    private final List<CodeSpecDTO> allOriginalCodeSpecs;
    private final Map<CodeSpecDTO, MessagesAndProcessDurationDTO> allMessagesAndProcessDurationPerCodeSpec;

    public HistoryDTO(List<CodeSpecDTO> allOriginalCodeSpecs, Map<CodeSpecDTO, MessagesAndProcessDurationDTO> allMessagesAndProcessDurationPerCodeSpec) {
        this.allOriginalCodeSpecs = allOriginalCodeSpecs;
        this.allMessagesAndProcessDurationPerCodeSpec = allMessagesAndProcessDurationPerCodeSpec;
    }

    public List<CodeSpecDTO> getAllOriginalCodeSpecs() {
        return allOriginalCodeSpecs;
    }
}
