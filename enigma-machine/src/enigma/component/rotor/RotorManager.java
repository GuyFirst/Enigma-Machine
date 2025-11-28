package enigma.component.rotor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RotorManager {
    private final Map<Integer, Rotor> rotors;
    private final List<Rotor> currentRotors = new ArrayList<>();
    private final Map<Character, Integer> charToIndex; //TODO - a static member that appears somewhere in the system

    public RotorManager(Map<Integer, Rotor> rotors, int alphabetLength, Map<Character, Integer> charToIndex) {
        this.rotors = rotors;
        this.charToIndex = charToIndex;
    }

    public void setRotorsOrder(List<Integer> rotorIds) {
        for (Integer id : rotorIds) {
            currentRotors.add(rotors.get(id));
        }
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

    public void setRotorsPositions(List<Character> positions) {
        for (Rotor rotor : currentRotors) {
            rotor.setPosition(charToIndex.get(positions.get(currentRotors.indexOf(rotor))));
        }
    }
}