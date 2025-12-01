package enigma.engine.logic.repository;

import enigma.component.keyboard.Keyboard;
import enigma.component.reflector.Reflector;
import enigma.component.rotor.Rotor;
import enigma.engine.generated.BTE.classes.BTEEnigma;

import java.util.Map;

public class Repository {
    private final Map<Integer, Rotor> allRotors;
    private final Map<String, Reflector> allReflectors;
    private final Keyboard keyboard;

    public Repository(Map<Integer, Rotor> allRotors, Map<String, Reflector> allReflectors, Keyboard keyboard) {
        this.allRotors = allRotors;
        this.allReflectors = allReflectors;
        this.keyboard = keyboard;
    }
    public Map<Integer, Rotor> getAllRotors() {
        return allRotors;
    }
    public Map<String, Reflector> getAllReflectors() {
        return allReflectors;
    }
    public Keyboard getKeyboard() {
        return keyboard;
    }

}
