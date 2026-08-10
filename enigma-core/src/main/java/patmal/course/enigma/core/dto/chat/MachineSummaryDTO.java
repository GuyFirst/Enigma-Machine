package patmal.course.enigma.core.dto.chat;

import java.util.List;

public record MachineSummaryDTO(
        String name,
        String abc,
        int rotorsInUse,
        List<Integer> availableRotorIds,
        List<String> availableReflectorIds) {
}
