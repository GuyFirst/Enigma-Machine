package patmal.course.enigma.component.rotor;

import java.io.Serializable;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RotorImpl implements Rotor, Serializable {
    private final List<Integer> rightColumn;
    private final List<Integer> leftColumn;
    private int notchPosition;
    private final int alphabetLength;
    private int id = -1;
    private static final Logger logger = LogManager.getLogger(RotorImpl.class);

    public RotorImpl(List<Integer> rightColumn, List<Integer> leftColumn, int notchPosition, int alphabetLength) {
        this.alphabetLength = alphabetLength;
        this.rightColumn = rightColumn;
        this.leftColumn = leftColumn;
        this.notchPosition = makePositionInBounds(notchPosition - 1);
        logger.debug("Rotor initialized with alphabet length: {}", alphabetLength);
    }

    @Override
    public void setId(int id) {
        this.id = id;
        logger.debug("Rotor id set to {}", id);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setPosition(int position) {
        // INFO: Logging a major state change request
        logger.debug("Rotor [ID: {}] - Setting top index to: {}...", id, position);
        while (rightColumn.getFirst() != position) {
            rotate();
        }
        logger.debug("Rotor [ID: {}] - top index have been set to: {}.", id, position);
    }
    @Override
    public int encodeForward(int entryLocation) {
        // Step 1: Get the value (letter) from the right column at the entry index
        int mappedValue = rightColumn.get(entryLocation);

        // Step 2: Search for that value's position in the left column
        int outputIndex = leftColumn.indexOf(mappedValue);

        // The best log format for this process:
        // It captures the exact 'Search' logic you described.
        logger.trace("Rotor [ID: {}] | FORWARD | In-Index: {} -> Value: {} -> Out-Index (Found in Left): {}",
                id, entryLocation, mappedValue, outputIndex);

        return outputIndex;
    }

    @Override
    public int encodeBackward(int entryLocation) {
        // For backward: signal comes from the left and exits from the right
        int mappedValue = leftColumn.get(entryLocation);
        int outputIndex = rightColumn.indexOf(mappedValue);

        logger.trace("Rotor [ID: {}] | BACKWARD | In-Index: {} -> Value: {} -> Out-Index (Found in Right): {}",
                id, entryLocation, mappedValue, outputIndex);

        return outputIndex;
    }

    private int makePositionInBounds(int position) {
        int boundedPosition = (position % alphabetLength + alphabetLength) % alphabetLength;
        // DEBUG: Log mathematical adjustments
        if(position != boundedPosition)
            logger.debug("Position bounds check: {} adjusted to {}", position, boundedPosition);

        return boundedPosition;
    }

    @Override
    public int getNotchPosition() {
        return notchPosition;
    }

    @Override
    public void rotate() {
        logger.debug("Rotating rotor [ID: {}].... Current top index: {}", id, rightColumn.getFirst());

        int topLetterFroRightColumn = rightColumn.removeFirst();
        int topLetterFromLeftColumn = leftColumn.removeFirst();

        rightColumn.add(topLetterFroRightColumn);
        leftColumn.add(topLetterFromLeftColumn);

        notchPosition = makePositionInBounds(notchPosition - 1);

        // DEBUG: Logged if the notch reaches the active position (example condition)
        if (notchPosition == 0) {
            logger.debug("Rotor [ID: {}] notch has reached the zero position.", id);
        }

        logger.debug("Rotor[ID: {}] rotated. New top index: {}", id, rightColumn.getFirst());
    }

    @Override
    public int getTopLetter() {
        return rightColumn.getFirst();
    }
}