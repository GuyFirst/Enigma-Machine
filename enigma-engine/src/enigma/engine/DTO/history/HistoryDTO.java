package enigma.engine.DTO.history;

import enigma.engine.logic.history.EnigmaConfiguration;

import java.util.List;

public class HistoryDTO {
    private final List<EnigmaConfiguration> history;

    public HistoryDTO(List<EnigmaConfiguration> history) {
        this.history = history;
    }

    public List<EnigmaConfiguration> getHistory() {
        return history;
    }
}
