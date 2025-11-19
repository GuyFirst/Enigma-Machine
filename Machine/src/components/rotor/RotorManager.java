package components.rotor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RotorManager {
    private final Map<Integer, Rotor> rotors;
    private List<Rotor> currentRotors;
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

    public void setRotorsPositions(List<Character> positions) {
        for (int i = 0; i < currentRotors.size(); i++ ) {
            currentRotors.get(i).initialRotate(charToIndex.get(positions.get(i)));
        }
    }

    public int encode(int input) {
        int signal = input;
        for (Rotor rotor : currentRotors) {
            signal = rotor.encode(signal);
        }
        return signal;
    }



}
