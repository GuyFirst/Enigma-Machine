package patmal.course.enigma.core.dto.chat;

import java.util.List;

/**
 * Full machine wiring, sent to the browser so it can run the Enigma itself.
 * Rotor columns are given in their as-loaded orientation; `notch` is a 0-based
 * index into that same orientation. `reflectors[].wiring[i]` is the index that
 * alphabet position i reflects to.
 */
public record MachineWiringDTO(
        String name,
        String alphabet,
        int rotorsInUse,
        List<RotorWiring> rotors,
        List<ReflectorWiring> reflectors) {

    public record RotorWiring(int id, List<Integer> right, List<Integer> left, int notch) {
    }

    public record ReflectorWiring(String id, List<Integer> wiring) {
    }
}
