package enigma.component.rotor;

import java.util.List;

public class RotorImpl implements Rotor{
    private final int id;
    private final List<Integer> rightColumn;
    private final List<Integer> leftColumn;
    private int notchPosition;
    private final int ALPHABET_LENGTH = 6;

    public RotorImpl(int id, List<Integer> rightColumn, List<Integer> leftColumn, int notchPosition) {
        this.id = id;
        this.rightColumn = rightColumn;
        this.leftColumn = leftColumn;
        this.notchPosition = makePositionInBounds(notchPosition - 1);
    }
    @Override
    public void setPosition(int position) {
        while (rightColumn.getFirst() != position) {
            rotate();
        }
    }

    @Override
    public int encodeForward(int entryLocation) {
        int resultedLetter = rightColumn.get(entryLocation);
        return leftColumn.indexOf(resultedLetter);
    }
    @Override
    public int encodeBackward(int entryLocation) {
        int resultedLetter = leftColumn.get(entryLocation);
        return rightColumn.indexOf(resultedLetter);
    }

    private int makePositionInBounds(int position) {
        return (position % ALPHABET_LENGTH + ALPHABET_LENGTH) % ALPHABET_LENGTH;
    }
    @Override
    public int getNotchPosition() {
        return notchPosition;
    }
    @Override
    public void rotate() {
        int topLetterFroRightColumn = rightColumn.removeFirst();
        int topLetterFromLeftColumn = leftColumn.removeFirst();

        rightColumn.add(topLetterFroRightColumn);
        leftColumn.add(topLetterFromLeftColumn);

        notchPosition = makePositionInBounds(notchPosition - 1);
    }
}