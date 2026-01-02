package patmal.course.enigma.component.rotor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RotorManager implements Serializable {
    private final List<Rotor> currentRotors;
    private final static Logger logger = LogManager.getLogger(RotorManager.class);

    public RotorManager(List<Rotor> currentRotors, List<Integer> positionIndices, List<Integer> rotorIds) {
        this.currentRotors = Objects.requireNonNull(currentRotors, "currentRotors");
        if (currentRotors.size() != rotorIds.size()) {
            throw new IllegalArgumentException("currentRotors and rotorIds must have the same size");
        }

        for (int i = 0; i < currentRotors.size(); i++) {
            currentRotors.get(i).setId(rotorIds.get(i));
        }
        logger.debug("RotorManager initialized with {} rotors.", currentRotors.size());
        logger.debug("order of rotors (from left to right): {}", rotorIds);
    }

    public int encryptLetterThroughRotorsLTR(int input) {
        int signal = input;
        for (Rotor rotor : currentRotors) {
            signal = rotor.encodeBackward(signal);
        }
        return signal;
    }

    public int encryptLetterThroughRotorsRTL(int input) {
        int signal = input;
        for (int i = currentRotors.size() - 1; i >= 0; i--) {
            Rotor rotor = currentRotors.get(i);
            signal = rotor.encodeForward(signal);
        }
        return signal;
    }

    public void moveRotorsBeforeEncodingLetter() {
        for (int i = currentRotors.size() - 1; i >= 0; i--) {
            Rotor rotor = currentRotors.get(i);
            rotor.rotate();
            if (rotor.getNotchPosition() != 0) {
                break;
            }
            logger.debug("Rotor [ID: {}] notch position at lhe top, therefore rotating next rotor", rotor.getId());
        }
    }

    public void setRotorsPositions(List<Integer> positions) {
        for (int i = 0; i < currentRotors.size(); i++) {
            Rotor rotor = currentRotors.get(i);
            rotor.setPosition(positions.get(i));
        }
    }
}
