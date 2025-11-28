import enigma.component.keyboard.Keyboard;
import enigma.component.keyboard.KeyboardImpl;
import enigma.component.reflector.Reflector;
import enigma.component.reflector.ReflectorImpl;
import enigma.component.reflector.ReflectorManager;
import enigma.component.reflector.ReflectorPair;
import enigma.component.rotor.RotorImpl;
import enigma.component.rotor.RotorManager;
import enigma.machine.EnigmaMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnigmaConsoleTest {

    public static void main(String[] args) {

        // --- 1. Define Alphabet (A-F, Length 6) ---
        Map<Character, Integer> charToIndex = new HashMap<>();
        charToIndex.put('A', 0);
        charToIndex.put('B', 1);
        charToIndex.put('C', 2);
        charToIndex.put('D', 3);
        charToIndex.put('E', 4);
        charToIndex.put('F', 5);
        int alphabetLength = 6;

        // --- Rotor 1 Wiring Definition ---
        List<Integer> rightColOfRotor1 = new ArrayList<>();
        List<Integer> leftColOfRotor1 = new ArrayList<>();
        // Right Column (Input points)
        rightColOfRotor1.add(0);
        rightColOfRotor1.add(1);
        rightColOfRotor1.add(2);
        rightColOfRotor1.add(3);
        rightColOfRotor1.add(4);
        rightColOfRotor1.add(5);
        // Left Column (Output points: 0->5, 1->4, 2->3, 3->2, 4->1, 5->0)
        leftColOfRotor1.add(5);
        leftColOfRotor1.add(4);
        leftColOfRotor1.add(3);
        leftColOfRotor1.add(2);
        leftColOfRotor1.add(1);
        leftColOfRotor1.add(0);

        // --- Rotor 2 Wiring Definition ---
        List<Integer> rightColOfRotor2 = new ArrayList<>();
        List<Integer> leftColOfRotor2 = new ArrayList<>();
        // Right Column (Input points)
        rightColOfRotor2.add(0);
        rightColOfRotor2.add(1);
        rightColOfRotor2.add(2);
        rightColOfRotor2.add(3);
        rightColOfRotor2.add(4);
        rightColOfRotor2.add(5);
        // Left Column (Output points: 0->4, 1->1, 2->3, 3->5, 4->2, 5->0)
        leftColOfRotor2.add(4);
        leftColOfRotor2.add(1);
        leftColOfRotor2.add(3);
        leftColOfRotor2.add(5);
        leftColOfRotor2.add(2);
        leftColOfRotor2.add(0);

        // --- 2. Rotor Instantiation ---
        RotorImpl r1 = new RotorImpl(1, rightColOfRotor1, leftColOfRotor1, 4);
        System.out.println("Rotor 1 created");

        RotorImpl r2 = new RotorImpl(1, rightColOfRotor2, leftColOfRotor2, 1);
        System.out.println("Rotor 2 created");

        Map<Integer, RotorImpl> allRotors = new HashMap<>();
        allRotors.put(1, r1);
        allRotors.put(2, r2);
        System.out.println("Rotor map initialized");

        // --- 3. Create RotorManager ---
        RotorManager manager = new RotorManager(allRotors, alphabetLength, charToIndex);
        System.out.println("Rotor Manager created");

        // --- 4. Set Rotor Order (R2 Left, R1 Right) ---
        List<Integer> rotorOrder = new ArrayList<>();
        rotorOrder.add(2); // Left
        rotorOrder.add(1); // Right
        manager.setRotorsOrder(rotorOrder);
        System.out.println("Rotor order set");

        System.out.println("Setting rotors position");
        // --- 5. Set Initial Positions (CC) ---
        List<Character> positions = new ArrayList<>();
        positions.add('C'); // Rotor 2 (Left) position
        positions.add('C'); // Rotor 1 (Right) position
        manager.setRotorsPositions(positions);

        List<ReflectorPair> rawPairsI = new ArrayList<>();
        rawPairsI.add(new ReflectorPair(0, 3));
        rawPairsI.add(new ReflectorPair(1, 4));
        rawPairsI.add(new ReflectorPair(2, 5));

        Map<String, Reflector> allReflectors = new HashMap<>();
// Use the new Reflector constructor: new Reflector(id, rawPairsList)
        allReflectors.put("I", new ReflectorImpl("I", rawPairsI));

        Reflector currentReflector = allReflectors.get("I");


        ArrayList<Character> characterArrayList = new ArrayList<>();
        ArrayList<Character> outputCharacterArrayList = new ArrayList<>();
        ArrayList<Character> outputCharacterArrayList2 = new ArrayList<>();
        characterArrayList.add('A');
        characterArrayList.add('B');
        characterArrayList.add('B');
        characterArrayList.add('A');

        ReflectorManager reflectorManager = new ReflectorManager(allReflectors, currentReflector);
        Keyboard keyboard = new KeyboardImpl("ABCDEF", charToIndex);
        EnigmaMachine testMachine = new EnigmaMachine(reflectorManager, manager, keyboard);

        System.out.println("Text to encrypt: " + characterArrayList);

        for (int i = 0; i < characterArrayList.toArray().length; i++) {
            outputCharacterArrayList.add(testMachine.encryptChar(characterArrayList.get(i)));
        }

        System.out.println("text encrypted to: " + outputCharacterArrayList);

        positions.add('C'); // Rotor 2 (Left) position
        positions.add('C'); // Rotor 1 (Right) position
        manager.setRotorsPositions(positions);

        for (int i = 0; i < characterArrayList.toArray().length; i++) {
            outputCharacterArrayList2.add(testMachine.encryptChar(outputCharacterArrayList.get(i)));
        }

        System.out.println("text decrypred back to: " + outputCharacterArrayList2);

    }
}