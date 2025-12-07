package enigma.machine;

import enigma.component.keyboard.Keyboard;
import enigma.component.reflector.Reflector;
import enigma.component.rotor.RotorManager;

import java.io.Serializable;

public class MachineImpl implements Machine, Serializable {
    private final Reflector reflector;
    private final RotorManager rotorManager;
    private final Keyboard keyboard;

    public MachineImpl(Reflector reflector, RotorManager rotorManager, Keyboard keyboard) {
        this.reflector = reflector;
        this.rotorManager = rotorManager;
        this.keyboard = keyboard;
    }

    @Override
    public char encryptChar(char inputChar) {
        // Convert character to index
        int charIndex = keyboard.charToIndex(inputChar);

        // Move rotors before encoding
        rotorManager.moveRotorsBeforeEncodingLetter();

        // Pass through rotors and reflector
        int inputToReflector = rotorManager.encryptLetterThroughRotorsRTL(charIndex);
        int outputFromReflector = reflector.reflect(inputToReflector);
        int finalOutputIndex = rotorManager.encryptLetterThroughRotorsLTR(outputFromReflector);

        // Convert index back to character
        return keyboard.indexToChar(finalOutputIndex);
    }
}
