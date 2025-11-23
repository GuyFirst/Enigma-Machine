import components.reflector.Reflectable;
import components.reflector.Reflector;
import components.reflector.ReflectorPair;
import components.rotor.Rotor;
import components.rotor.RotorManager;

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
        Rotor r1 = new Rotor(1, rightColOfRotor1, leftColOfRotor1, 4);
        System.out.println("Rotor 1 created");

        Rotor r2 = new Rotor(1, rightColOfRotor2, leftColOfRotor2, 1);
        System.out.println("Rotor 2 created");

        Map<Integer, Rotor> allRotors = new HashMap<>();
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


        ArrayList<Character> characterArrayList = new ArrayList<>();
        characterArrayList.add('B');
        characterArrayList.add('D');

        for (int i = 0; i < 2; i++) {


            // --- 6. Rotation Cycle (Pre-encoding step) ---
            manager.moveRotorsBeforeEncodingLetter();

            // --- 7. Forward Pass (RTL) ---
            int inputIndex = charToIndex.get(characterArrayList.get(i)); // B is the effective input (e.g., after Plugboard B|C)

            System.out.println("--- Encoding Letter");
            System.out.println("Input Index: " + characterArrayList.get(i) + " " + inputIndex);

            int signal = inputIndex;
            signal = manager.encryptLetterThroughRotorsRTL(signal);

            // --- 8. Output and LTR Pass ---
            char outputChar = getChar(signal, charToIndex);
            System.out.println("Output Index after RTL: " + signal);
            System.out.println("Encoded Character before reflector: " + outputChar);


            //HERE WE ARE ADDING A REFLECTOR
            List<ReflectorPair> rawPairsI = new ArrayList<>();
            rawPairsI.add(new ReflectorPair(1, 4));
            rawPairsI.add(new ReflectorPair(2, 5));
            rawPairsI.add(new ReflectorPair(3, 6));

// Reflector 'II' Pairs (1->5, 2->6, 3->4).
            List<ReflectorPair> rawPairsII = new ArrayList<>();
            rawPairsII.add(new ReflectorPair(1, 5));
            rawPairsII.add(new ReflectorPair(2, 6));
            rawPairsII.add(new ReflectorPair(3, 4));


// --- 2. Instantiate Reflector Objects ---
            Map<String, Reflectable> allReflectors = new HashMap<>();
// Use the new Reflector constructor: new Reflector(id, rawPairsList)
            allReflectors.put("I", new Reflector("I", rawPairsI));
            allReflectors.put("II", new Reflector("II", rawPairsII));


// --- 3. Reflector Manager Initialization and Usage ---
// Assuming you have a ReflectorManager that holds all available reflectors.
// You must choose which one to use for the current machine configuration (e.g., Reflector "I").

// Choose Reflector "I" for the simulation
            Reflectable currentReflector = allReflectors.get("I");

// --- 4. Simulation of Reflector Pass ---
            System.out.println("// --- Reflector Pass ---");
// signal will be the output from the RTL pass (e.g., Index 1)
            signal = currentReflector.reflect(signal);
            System.out.println("Signal after reflector: Index " + signal);

            // --- 9. Backward Pass (LTR) ---
            signal = manager.encryptLetterThroughRotorsLTR(signal);

            char outputChar2 = getChar(signal, charToIndex);
            System.out.println("Output Index after LTR: " + signal);
            System.out.println("Final Encoded Character: " + outputChar2);
        }

    }

    private static char getChar(int index, Map<Character, Integer> charToIndex) {
        for (Map.Entry<Character, Integer> entry : charToIndex.entrySet()) {
            if (entry.getValue().equals(index)) {
                return entry.getKey();
            }
        }
        return '?';
    }
}