package components.rotor;

import java.util.Map;

public class Rotor {
    private final Map<Integer, Integer> wiring;
    private int offsetForInputCalculation;
    private int notchDistanceFromTop;
    private final int ALPHABET_LENGTH;

    public Rotor(Map<Integer, Integer> wiring, int notchDistanceFromTop, int ALPHABET_LENGTH) {
        this.wiring = wiring;
        this.notchDistanceFromTop = notchDistanceFromTop;
        this.ALPHABET_LENGTH = ALPHABET_LENGTH;
    }

    public int encode(int letterRepresentation) {
        int convertedLetterRepresentation = makePositionInBounds(letterRepresentation + offsetForInputCalculation);
        return wiring.get(convertedLetterRepresentation);
    }

    void rotate() {
        System.out.print(wiring);
        System.out.print("notch distance from the top:" + notchDistanceFromTop + "\n");
//        // אורך האלפבית N הוא ALPHABET_LENGTH
//        int N = ALPHABET_LENGTH;
//        Map<Integer, Integer> newMapping = new HashMap<>();
//
//        for (Map.Entry<Integer, Integer> entry : wirings.entrySet()) {
//            // תיקון עבור newKey:
//            int newKey = (entry.getKey() - 1);
//            newKey = ((newKey % N) + N) % N;
//
//            // תיקון עבור newValue:
//            int newValue = (entry.getValue() - 1);
//            newValue = ((newValue % N) + N) % N;
//            newMapping.put(newKey, newValue);
//
//        }
//
//        wirings = newMapping;
//
//        // תיקון עבור notchDistanceFromTop:
//        this.notchDistanceFromTop = (this.notchDistanceFromTop - 1);
        this.notchDistanceFromTop = makePositionInBounds(this.notchDistanceFromTop - 1);
        this.offsetForInputCalculation = makePositionInBounds(this.offsetForInputCalculation + 1);

        System.out.print(wiring);
        System.out.print("notch distance from the top:" + notchDistanceFromTop + "\n");
    }

    public int getNotchDistanceFromTop() {
        return notchDistanceFromTop;
    }

    public void setPosition(Integer position) {
        notchDistanceFromTop = makePositionInBounds(this.notchDistanceFromTop - position);
        offsetForInputCalculation = position;
    }

    private int makePositionInBounds(int position) {
        if (position < 0) {
            position += ALPHABET_LENGTH;
        }
        return position % ALPHABET_LENGTH;
    }
}