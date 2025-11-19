package components.rotor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RotorManager {
    private final Map<Integer, Rotor> rotors;
    private List<Rotor> currentRotors = new ArrayList<>();
    private final int ALPHABET_LENGTH;
    private final Map<Character, Integer> charToIndex; //TODO - a static member that appears somewhere in the system

    public RotorManager(Map<Integer, Rotor> rotors, int alphabetLength, Map<Character, Integer> charToIndex) {
        this.rotors = rotors;
        ALPHABET_LENGTH = alphabetLength;
        this.charToIndex = charToIndex;
    }

    public void setRotorsOrder(List<Integer> rotorIds) {
        for (Integer id : rotorIds) {
            currentRotors.add(rotors.get(id));
        }
    }

//    public void setRotorsPositions(List<Character> positions) {
//        for (int i = 0; i < currentRotors.size(); i++ ) {
//            currentRotors.get(i).setCurrentWindowLetter(charToIndex.get(positions.get(i)));
//            currentRotors.get(i).initialRotate(charToIndex.get(positions.get(i)));
//        }
//    }

    public int encryptLetterThroughRotorsLTR(int input) {
        int signal = input;
        for (Rotor rotor : currentRotors) {
            signal = rotor.encode(signal);
        }
        return signal;
    }

    public int encryptLetterThroughRotorsRTL(int input) {
        currentRotors = currentRotors.reversed();
        int signal = encryptLetterThroughRotorsLTR(input);
        currentRotors = currentRotors.reversed();
        return signal;
    }

    public void moveRotorsBeforeEncodingLetter() {
        for (int i = currentRotors.size() - 1; i >= 0; i--) {
            Rotor rotor = currentRotors.get(i);
            rotor.rotate();
            if (rotor.getNotchDistanceFromTop() != 0) {
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
