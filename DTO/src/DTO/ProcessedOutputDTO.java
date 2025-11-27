package DTO;

import java.util.List;

public class ProcessedOutputDTO {
    private final List<Integer> processedOutputPositions;

    public  ProcessedOutputDTO(List<Integer> processedOutputPositions) {
        this.processedOutputPositions = processedOutputPositions;
    }

    public List<Integer> getProcessedOutputPositions() {
        return processedOutputPositions;
    }

}
