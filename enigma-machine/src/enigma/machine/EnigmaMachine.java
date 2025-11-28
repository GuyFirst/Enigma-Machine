package enigma.machine;

import enigma.component.keyboard.Keyboard;
import enigma.component.reflector.ReflectorManager;
import enigma.component.rotor.RotorManager;

import java.util.Map;

public class EnigmaMachine implements Machine {
    private final ReflectorManager reflectorManager;
    private final RotorManager rotorManager;
    private final Keyboard keyboard;

    public EnigmaMachine(ReflectorManager reflectorManager, RotorManager rotorManager, Keyboard keyboard) {
        this.reflectorManager = reflectorManager;
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
        int outputFromReflector = reflectorManager.reflect(inputToReflector);
        int finalOutputIndex = rotorManager.encryptLetterThroughRotorsLTR(outputFromReflector);

        // Convert index back to character
        return keyboard.indexToChar(finalOutputIndex);
    }
}
