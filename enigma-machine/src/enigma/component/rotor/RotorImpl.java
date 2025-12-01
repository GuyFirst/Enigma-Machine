package enigma.component.rotor;

import java.util.List;

public class RotorImpl implements Rotor{
    private final List<Integer> rightColumn;
    private final List<Integer> leftColumn;
    private int notchPosition;
    private int alphabetLength;

    public RotorImpl(int id, List<Integer> rightColumn, List<Integer> leftColumn, int notchPosition, int alphabetLength) {
        this.alphabetLength = alphabetLength;
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
        return (position % alphabetLength + alphabetLength) % alphabetLength;
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