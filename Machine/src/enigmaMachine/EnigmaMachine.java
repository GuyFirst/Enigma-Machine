package enigmaMachine;

import components.keyboard.AlphabetComponent;
import components.keyboard.Keyboard;
import components.reflector.Reflectable;
import components.reflector.Reflector;
import components.reflector.ReflectorManager;
import components.rotor.RotorManager;

public class EnigmaMachine {
    private final ReflectorManager reflectorManager;
    private final RotorManager rotorManager;
    private final AlphabetComponent keyboard;

    public EnigmaMachine(ReflectorManager reflectorManager, RotorManager rotorManager, AlphabetComponent keyboard) {
        this.reflectorManager = reflectorManager;
        this.rotorManager = rotorManager;
        this.keyboard = keyboard;
    }

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
