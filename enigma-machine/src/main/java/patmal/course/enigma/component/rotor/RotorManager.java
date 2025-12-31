package patmal.course.enigma.component.rotor;

import java.io.Serializable;
import java.util.List;

public class RotorManager implements Serializable {
    private final List<Rotor> currentRotors;

    public RotorManager(List<Rotor> currentRotors, List<Integer> positionIndices) {
        this.currentRotors = currentRotors;
        setRotorsPositions(positionIndices);
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
        }
    }

    public void setRotorsPositions(List<Integer> positions) {
        for (int i = 0; i < currentRotors.size(); i++) {
            Rotor rotor = currentRotors.get(i);
            rotor.setPosition(positions.get(i));
        }
    }
}