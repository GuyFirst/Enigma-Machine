package DTO;

import java.util.List;
import java.util.Map;

public class CodeSpecDTO {
    private final int reflectorId;
    private final List<Integer> currentRotorIds;
    private final Map<Integer, Integer> rotorPositionOnWindow;
    private final Map<Integer, Integer> notchPositionFromWindow;

    public CodeSpecDTO(int reflectorId, List<Integer> currentRotorIds,
                       Map<Integer, Integer> rotorPositionOnWindow,
                       Map<Integer, Integer> notchPositionFromWindow) {
        this.reflectorId = reflectorId;
        this.currentRotorIds = currentRotorIds;
        this.rotorPositionOnWindow = rotorPositionOnWindow;
        this.notchPositionFromWindow = notchPositionFromWindow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodeSpecDTO that = (CodeSpecDTO) o;

        if (reflectorId != that.reflectorId) return false;
        if (!currentRotorIds.equals(that.currentRotorIds)) return false;
        if (!rotorPositionOnWindow.equals(that.rotorPositionOnWindow)) return false;
        return notchPositionFromWindow.equals(that.notchPositionFromWindow);
    }

    @Override
    public int hashCode() {
        int result = reflectorId;
        result = 31 * result + currentRotorIds.hashCode();
        result = 31 * result + rotorPositionOnWindow.hashCode();
        result = 31 * result + notchPositionFromWindow.hashCode();
        return result;
    }

    public int getReflectorId() {
        return reflectorId;
    }

    public List<Integer> getCurrentRotorIds() {
        return currentRotorIds;
    }

    public Map<Integer, Integer> getRotorPositionOnWindow() {
        return rotorPositionOnWindow;
    }

    public Map<Integer, Integer> getNotchPositionFromWindow() {
        return notchPositionFromWindow;
    }

}
