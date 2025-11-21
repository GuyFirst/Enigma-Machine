package components.rotor;

import java.util.List;

public class Rotor {
//    private final Map<Integer, Integer> forwardWiring;
//    private final Map<Integer, Integer> backwardWiring;
//    private int offsetForInputCalculation;
//    private int notchDistanceFromTop;
//    private final int ALPHABET_LENGTH;
//
//    public Rotor(int id, Map<Integer, Integer> wiring, int notchDistanceFromTop, int ALPHABET_LENGTH) {
//        this.forwardWiring = wiring;
//        this.backwardWiring = calculateBackwordWiring(wiring);
//        this.notchDistanceFromTop = notchDistanceFromTop;
//        this.ALPHABET_LENGTH = ALPHABET_LENGTH;
//    }
//
//    private Map<Integer, Integer> calculateBackwordWiring(Map<Integer, Integer> wiring) {
//        Map<Integer, Integer> backwardWiring = new HashMap<>();
//        for (Map.Entry<Integer, Integer> entry : wiring.entrySet()) {
//            backwardWiring.put(entry.getValue(), entry.getKey());
//        }
//        return backwardWiring;
//    }
//
//    public int encodeForward(int letterRepresentation) {
//        int convertedLetterRepresentation = makePositionInBounds(letterRepresentation + offsetForInputCalculation);
//        System.out.println("convertedLetterRepresentation: " + convertedLetterRepresentation);
//        return convertedLetterRepresentation;
//    }
//
//    public int encodeBackward(int letterRepresentation) {
//        int convertedLetterRepresentation = makePositionInBounds(letterRepresentation + offsetForInputCalculation);
//        System.out.println("convertedLetterRepresentation: " + convertedLetterRepresentation);
//        return convertedLetterRepresentation;
//    }
//
//    void rotate() {
//        System.out.print(forwardWiring);
//        System.out.print("notch distance from the top (before):" + notchDistanceFromTop + "\n");
////        // אורך האלפבית N הוא ALPHABET_LENGTH
////        int N = ALPHABET_LENGTH;
////        Map<Integer, Integer> newMapping = new HashMap<>();
////
////        for (Map.Entry<Integer, Integer> entry : wiring.entrySet()) {
////            // תיקון עבור newKey:
////            int newKey = (entry.getKey() - 1);
////            newKey = ((newKey % N) + N) % N;
////
////            // תיקון עבור newValue:
////            int newValue = (entry.getValue() - 1);
////            newValue = ((newValue % N) + N) % N;
////            newMapping.put(newKey, newValue);
////
////        }
////
////        wiring = newMapping;
////
////        // תיקון עבור notchDistanceFromTop:
////        this.notchDistanceFromTop = (this.notchDistanceFromTop - 1);
//        this.notchDistanceFromTop = makePositionInBounds(this.notchDistanceFromTop - 1);
//        this.offsetForInputCalculation = makePositionInBounds(this.offsetForInputCalculation + 1);
//
//        //System.out.print(forwardWiring);
//        System.out.print("notch distance from the top (after):" + notchDistanceFromTop + "\n");
//    }
//
//    public int getNotchDistanceFromTop() {
//        return notchDistanceFromTop;
//    }
//
//    public void setPosition(Integer position) {
//        notchDistanceFromTop = makePositionInBounds(this.notchDistanceFromTop - position);
//        offsetForInputCalculation = position;
//
//        System.out.print(forwardWiring);
//        System.out.print("notch: " + notchDistanceFromTop + "\n");
//        System.out.print("position: " + offsetForInputCalculation + "\n");
//    }
//
//    private int makePositionInBounds(int position) {
//        if (position < 0) {
//            position += ALPHABET_LENGTH;
//        }
//        return position % ALPHABET_LENGTH;
//    }
    private final int id;
    private final List<Integer> rightColumn;
    private final List<Integer> leftColumn;
    private int notchPosition;
    private final int ALPHABET_LENGTH = 6;

    public Rotor(int id, List<Integer> rightColumn, List<Integer> leftColumn,  int notchPosition) {
        this.id = id;
        this.rightColumn = rightColumn;
        this.leftColumn = leftColumn;
        this.notchPosition = makePositionInBounds(notchPosition - 1);
    }

    void rotate() {
        int topLetterFroRightColumn = rightColumn.removeFirst();
        int topLetterFromLeftColumn = leftColumn.removeFirst();

        rightColumn.add(topLetterFroRightColumn);
        leftColumn.add(topLetterFromLeftColumn);

        notchPosition = makePositionInBounds(notchPosition - 1);
    }

    void setPosition(int position) {
        while (rightColumn.getFirst() != position) {
            rotate();
        }
    }

    public int encodeForward(int entryLocation) {
        int resultedLetter = rightColumn.get(entryLocation);
        return leftColumn.indexOf(resultedLetter);
    }

    public int encodeBackward(int entryLocation) {
        int resultedLetter = leftColumn.get(entryLocation);
        return rightColumn.indexOf(resultedLetter);
    }

    private int makePositionInBounds(int position) {
        if (position < 0) {
            position += ALPHABET_LENGTH;
        }
        return position % ALPHABET_LENGTH;
    }

    public int getNotchPosition() {
        return notchPosition;
    }
}