package enigma.machine;

import enigma.component.keyboard.Keyboard;
import enigma.component.plugboard.Plugboard;
import enigma.component.reflector.Reflector;
import enigma.component.rotor.RotorManager;

import java.io.Serializable;
import java.util.Map;

public class MachineImpl implements Machine, Serializable {
    private final Reflector reflector;
    private final RotorManager rotorManager;
    private final Keyboard keyboard;
    private final Plugboard plugboard;

    public MachineImpl(Reflector reflector, RotorManager rotorManager, Keyboard keyboard, Plugboard plugboard) {
        this.reflector = reflector;
        this.rotorManager = rotorManager;
        this.keyboard = keyboard;
        this.plugboard = plugboard;
    }

    @Override
    public char encryptChar(char inputChar) {
        // Pass through plug board
        inputChar = plugboard.substitute(inputChar);
        // Convert character to index
        int charIndex = keyboard.charToIndex(inputChar);

        // Move rotors before encoding
        rotorManager.moveRotorsBeforeEncodingLetter();

        // Pass through rotors and reflector
        int inputToReflector = rotorManager.encryptLetterThroughRotorsRTL(charIndex);
        int outputFromReflector = reflector.reflect(inputToReflector);
        int finalOutputIndex = rotorManager.encryptLetterThroughRotorsLTR(outputFromReflector);

        // pass through plug board again
        char outputChar = keyboard.indexToChar(finalOutputIndex);
        outputChar = plugboard.substitute(outputChar);
        finalOutputIndex = keyboard.charToIndex(outputChar);

        // Convert index back to character
        return keyboard.indexToChar(finalOutputIndex);
    }

}
