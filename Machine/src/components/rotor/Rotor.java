package components.rotor;

import java.util.HashMap;
import java.util.List;
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

    void rotate() {
        System.out.print(wiring);
        System.out.print("notch distance from the top:" + notchDistanceFromTop + "\n");

        // אורך האלפבית N הוא ALPHABET_LENGTH
        int N = ALPHABET_LENGTH;
        Map<Integer, Integer> newMapping = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : wiring.entrySet()) {
            // תיקון עבור newKey:
            int newKey = (entry.getKey() - 1);
            newKey = ((newKey % N) + N) % N;

            // תיקון עבור newValue:
            int newValue = (entry.getValue() - 1);
            newValue = ((newValue % N) + N) % N;
            newMapping.put(newKey, newValue);

        }

        wiring = newMapping;

        // תיקון עבור notchDistanceFromTop:
        this.notchDistanceFromTop = (this.notchDistanceFromTop - 1);
        this.notchDistanceFromTop = ((this.notchDistanceFromTop % N) + N) % N; // שימוש בנוסחה

        System.out.print(wiring);
        System.out.print("notch distance from the top:" + notchDistanceFromTop + "\n");
    }

    public int getNotchDistanceFromTop() {
        return notchDistanceFromTop;
    }

    public void setPosition(Integer position) {
        int numOfMoves = position - wiring.get(0);
        notchDistanceFromTop = (this.notchDistanceFromTop - numOfMoves);
    }
}