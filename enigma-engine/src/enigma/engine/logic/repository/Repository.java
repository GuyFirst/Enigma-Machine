package enigma.engine.logic.repository;

import enigma.component.keyboard.Keyboard;
import enigma.component.reflector.Reflector;
import enigma.component.rotor.Rotor;
import enigma.component.rotor.RotorImpl;

import java.util.*;

public class Repository {
    private final Map<Integer, Rotor> allRotors;
    private final Map<String, Reflector> allReflectors;
    private final Keyboard keyboard;
    private final Random randomGenerator;
    public Repository(Map<Integer, Rotor> allRotors, Map<String, Reflector> allReflectors, Keyboard keyboard) {
        this.allRotors = allRotors;
        this.allReflectors = allReflectors;
        this.keyboard = keyboard;
        this.randomGenerator = new Random();
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

    public String getRandomReflectorId() {
        Object[] reflectorIds = allReflectors.keySet().toArray();
        return (String) reflectorIds[randomGenerator.nextInt(reflectorIds.length)];
    }

    public List<Integer> getRandomRotorIds() {
        List<Integer> available = new ArrayList<>(allRotors.keySet());
        if (available.isEmpty()) {
            return new ArrayList<>();
        }

        // we will use it later
        // int maxRotors = available.size();
        // int randomNumOfRotors = randomGenerator.nextInt(maxRotors) + 1; // 1..maxRotors
        int numOfRotors = RotorImpl.NUM_OF_MINIMUM_ROTOR_IN_SYSTEM;


        Collections.shuffle(available, randomGenerator);
        return new ArrayList<>(available.subList(0, numOfRotors));
    }


    public List<Character> getRandomPositionsForRotors(int size) {
        List<Character> randomRotorStartingPositions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int randomIndex = randomGenerator.nextInt(keyboard.getAlphabetLength());
            char randomChar = keyboard.indexToChar(randomIndex);
            randomRotorStartingPositions.add(randomChar);
        }
        return randomRotorStartingPositions;
    }
}
