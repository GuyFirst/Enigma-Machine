import java.util.List;

public class MessageDTO {
    private final List<Integer> processedOutputPositions;
    private final List<Integer> processedInputPositions;

    public MessageDTO(List<Integer> processedOutputPositions, List<Integer> processedInputPositions) {
        this.processedOutputPositions = processedOutputPositions;
        this.processedInputPositions = processedInputPositions;
    }

    public List<Integer> getProcessedOutputPositions() {
        return processedOutputPositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageDTO that = (MessageDTO) o;

        if (!processedOutputPositions.equals(that.processedOutputPositions)) return false;
        return processedInputPositions.equals(that.processedInputPositions);
    }

    @Override
    public int hashCode() {
        int result = processedOutputPositions.hashCode();
        result = 31 * result + processedInputPositions.hashCode();
        return result;
    }
}
