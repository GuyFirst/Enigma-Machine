package components.rotor;

import java.util.Map;

public class Rotor {
    private Map<Integer, Integer> wiring;
    private int notchDistanceFromTop;
    private final int ALPHABET_LENGTH;

    public Rotor(int id, Map<Integer, Integer> wiring, int notchDistanceFromTop, int ALPHABET_LENGTH) {
        this.wiring = wiring;
        this.notchDistanceFromTop = notchDistanceFromTop;
        this.ALPHABET_LENGTH = ALPHABET_LENGTH;
    }

    public int encode(int input) {
            return wiring.get(input);
    }

    private void rotate() {
        for (Map.Entry<Integer, Integer> entry : wiring.entrySet()) {
            int newKey = (entry.getKey() - 1) % ALPHABET_LENGTH;
            int newValue = (entry.getValue() - 1) % ALPHABET_LENGTH;
            wiring.put(newKey, newValue);
        }
        this.notchDistanceFromTop = (this.notchDistanceFromTop - 1) % ALPHABET_LENGTH;
    }

    public void initialRotate(int position) {
        while (wiring.get(0) != position) {
            rotate();
        }
    }

}