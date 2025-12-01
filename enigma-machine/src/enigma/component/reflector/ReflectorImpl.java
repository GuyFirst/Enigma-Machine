package enigma.component.reflector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectorImpl implements Reflector {

    private final String id;
    private final Map<Integer, Integer> wiring;

    public ReflectorImpl(String id, List<ReflectorPair> rawPairs) {
        this.id = id;
        Map<Integer, Integer> finalWiring = new HashMap<>();

        for (ReflectorPair pair : rawPairs) {
            int input = pair.getInput();
            int output = pair.getOutput();

            // Adds the mapping: Input -> Output
            finalWiring.put(input, output);

            // Adds the reciprocal mapping: Output -> Input
            finalWiring.put(output, input);
        }

        this.wiring = Collections.unmodifiableMap(finalWiring);
    }

    public String getId() {
        return id;
    }

    @Override
    public int reflect(int inputIndex) {
        if (!wiring.containsKey(inputIndex)) {
            // This should ideally not happen if the input is a valid alphabet index,
            // but handles cases where the reflector is incomplete.
            throw new IllegalArgumentException("Reflector mapping not found for index: " + inputIndex);
        }
        return wiring.get(inputIndex);
    }
}