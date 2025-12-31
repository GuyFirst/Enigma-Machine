package patmal.course.enigma.component.menu.command.concrete.setup.manualSetup;


import patmal.course.enigma.component.UIController.UIController;
import patmal.course.enigma.component.menu.command.template.MenuCommandExecutable;
import patmal.course.enigma.engine.logic.Engine;
import patmal.course.enigma.engine.logic.EngineImpl;

import java.util.*;

public class ManualSetupConfigurationCommand implements MenuCommandExecutable {

    private final Map<String, String> romanMapping = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in); // Use a single Scanner instance

    public ManualSetupConfigurationCommand() {
        romanMapping.put("1", "I");
        romanMapping.put("2", "II");
        romanMapping.put("3", "III");
        romanMapping.put("4", "IV");
        romanMapping.put("5", "V");
    }

    /**
     * Handles user input for Rotor IDs. Returns Optional.empty() if user chooses to return to menu.
     */
    private Optional<List<Integer>> rotorIdSetupHandler(int numOfUsedRotorsInMachine) {
        boolean isValidRotorIds = false;
        List<Integer> rotorIds = new ArrayList<>();
        do {
            System.out.println("Please write the IDs of the rotors you want to use in the following format: 1,2,3 (most left, ... , most right)");
            System.out.println("make sure to write exactly " + numOfUsedRotorsInMachine + " unique rotor IDs:");
            String rotorsIdFromUser = scanner.nextLine();

            try {
                rotorIds = Arrays.stream(rotorsIdFromUser.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .toList();

                Set<Integer> uniqueIds = new HashSet<>(rotorIds);
                if (uniqueIds.size() != rotorIds.size()) {
                    System.out.println(rotorsIdFromUser + " contains duplicate rotor IDs (numbers must be unique).");
                    System.out.println("Press 0 to return to menu");
                    System.out.println("Press any other key to try again");
                    String userDecision = scanner.nextLine();
                    if (userDecision.trim().equals("0")) {
                        return Optional.empty(); // Return empty Optional to signal exit
                    } else {
                        continue;
                    }
                }
                if (rotorIds.size() != numOfUsedRotorsInMachine) {
                    System.out.println("You must enter exactly " + numOfUsedRotorsInMachine + " rotor IDs.");
                    System.out.println("Press 0 to return to menu");
                    System.out.println("Press any other key to try again");
                    String userDecision = scanner.nextLine();
                    if (userDecision.trim().equals("0")) {
                        return Optional.empty(); // Return empty Optional to signal exit
                    } else {
                        continue;
                    }
                }
                isValidRotorIds = true;
            } catch (NumberFormatException e) {
                System.out.println(rotorsIdFromUser + " is not a valid format (must be comma-separated numbers).");
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.trim().equals("0")) {
                    return Optional.empty(); // Return empty Optional to signal exit
                }
            }
        } while (!isValidRotorIds);
        return Optional.of(rotorIds);
    }

    /**
     * Handles user input for Rotor Start Positions. Returns Optional.empty() if user chooses to return to menu.
     */
    private Optional<List<Character>> rotorPositionSetupHandler() {
        boolean isValidRotorPositions = false;
        List<Character> rotorPositions = new ArrayList<>();
        do {
            System.out.println("Please write the positions of the rotors you want to use in the following format: 4D8A (most left, ... , most right):");
            String rotorsPositionsFromUser = scanner.nextLine().toUpperCase();

            // Basic validation: positions string should not be empty
            if (rotorsPositionsFromUser.isEmpty()) {
                System.out.println("Positions cannot be empty.");
                continue;
            }

            if (rotorsPositionsFromUser.length() != EngineImpl.NUM_OF_USED_ROTORS_IN_MACHINE) {
                System.out.println("You must enter exactly " + EngineImpl.NUM_OF_USED_ROTORS_IN_MACHINE + " rotor positions.");
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.trim().equals("0")) {
                    return Optional.empty(); // Return empty Optional to signal exit
                }
                continue;
            }
            try {
                rotorPositions = rotorsPositionsFromUser.chars()
                        .mapToObj(c -> (char) c)
                        .toList();
                isValidRotorPositions = true;
            } catch (Exception e) {
                // This catch block is mostly theoretical for char conversion but kept for consistency
                System.out.println("An unexpected error occurred: " + e.getMessage());
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.trim().equals("0")) {
                    return Optional.empty(); // Return empty Optional to signal exit
                }
            }

        } while (!isValidRotorPositions);
        return Optional.of(rotorPositions);
    }

    /**
     * Handles user input for Reflector ID. Returns Optional.empty() if user chooses to return to menu.
     */
    private Optional<String> reflectorIdSetupHandler() {

        String reflectorId = "";
        boolean isValidReflectorId = false;
        do {
            System.out.println("\nPlease select the reflector you want to use (decimal number):");
            for (Map.Entry<String, String> entry : romanMapping.entrySet()) {
                System.out.println(entry.getKey() + ". " + entry.getValue());
            }
            String reflectorFromUser = scanner.nextLine().trim(); // Trim input here
            if (!romanMapping.containsKey(reflectorFromUser)) {
                System.out.println(reflectorFromUser + " is not a valid selection.");
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.trim().equals("0")) {
                    return Optional.empty(); // Return empty Optional to signal exit
                }
            } else {
                reflectorId = romanMapping.get(reflectorFromUser);
                isValidReflectorId = true;
            }
        } while (!isValidReflectorId);

        return Optional.of(reflectorId);
    }


    @Override
    public void execute(Engine engine) throws Exception {
        // First check if machine is loaded
        // NOTE: The method name 'isXMLLoaded' should probably be 'isRepositoryLoaded' or similar,
        // assuming it checks if the repository (holding all rotors/reflectors) is ready.
        printBorders();
        if (!engine.isXMLLoaded()) {
            throw new IllegalStateException("XML file is not loaded. Please configure the system via XML configuration or loading an existing state via file.");
        }

        do {
            Optional<List<Integer>> optionalRotorIds = rotorIdSetupHandler(EngineImpl.NUM_OF_USED_ROTORS_IN_MACHINE);
            if (optionalRotorIds.isEmpty()) {
                return; // Exit command if user chose '0'
            }
            List<Integer> rotorIds = optionalRotorIds.get();


            Optional<List<Character>> optionalRotorPositions = rotorPositionSetupHandler();
            if (optionalRotorPositions.isEmpty()) {
                return; // Exit command if user chose '0'
            }
            List<Character> rotorPositions = optionalRotorPositions.get();


            Optional<String> optionalReflectorId = reflectorIdSetupHandler();
            if (optionalReflectorId.isEmpty()) {
                return; // Exit command if user chose '0'
            }
            String reflectorId = optionalReflectorId.get();

            Optional<Map<Character, Character>> optionalPlugBoardMapping = plugBoardSetupHandler();
            if (optionalPlugBoardMapping.isEmpty()) {
                return; // Exit command if user chose '0'
            }
            Map<Character, Character> plugBoardMapping = optionalPlugBoardMapping.get();
            try {
                // All Optional values are present, proceed with setup
                engine.setMachineCode(rotorIds, rotorPositions, reflectorId, plugBoardMapping);
                UIController.isMachineLoaded = true;
                System.out.println("Manual setup completed successfully.");
            } catch (Exception e) {
                // Setup failed (e.g., mismatched rotor/position counts, invalid characters, etc.)
                System.out.println("\n--- ERROR! ---");
                System.out.println("Error during manual setup: " + e.getMessage());
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.trim().equals("0")) {
                    return;
                }
            }
        } while (!UIController.isMachineLoaded);
        printBorders();
    }

    public Optional<Map<Character, Character>> plugBoardSetupHandler() {

        Map<Character, Character> plugBoard = null;
        boolean isValidConfiguration = false;

        do {
            System.out.println("Please enter the PlugBoard connections as a continuous string of pairs (e.g., ABCEFG). ");
            System.out.println("An empty string means NO connections. Press Enter to finish.");

            String inputPlugs = scanner.nextLine().toUpperCase().trim();

            // 1. Handle empty input (valid, no connections)
            if (inputPlugs.isEmpty()) {
                plugBoard = new HashMap<>();
                break;
            }

            try {
                // Attempt to validate and process the input string
                plugBoard = processAndValidatePlugs(inputPlugs);
                isValidConfiguration = true;

            } catch (IllegalArgumentException e) {
                // Validation failed: print error and ask for retry/exit
                System.out.println("\n--- Input Error ---");
                System.out.println("Error: " + e.getMessage());
                System.out.println("Press 0 to return to the main menu.");
                System.out.println("Press any other key to try again.");

                String userDecision = scanner.nextLine();
                if (userDecision.trim().equals("0")) {
                    return Optional.empty(); // Signal user exit
                }

            }

        } while (!isValidConfiguration);

        return Optional.of(plugBoard);
    }

    private Map<Character, Character> processAndValidatePlugs(String inputPlugs) throws IllegalArgumentException {

        // 1. Validation: Even Length
        if (inputPlugs.length() % 2 != 0) {
            throw new IllegalArgumentException("The PlugBoard input must have an even number of characters (pairs).");
        }

        Set<Character> usedChars = new HashSet<>();
        Map<Character, Character> plugboardMap = new HashMap<>();

        for (int i = 0; i < inputPlugs.length(); i += 2) {
            char charA = inputPlugs.charAt(i);
            char charB = inputPlugs.charAt(i + 1);

            // Validation: No Self-Mapping (A -> A)
            if (charA == charB) {
                throw new IllegalArgumentException("A character cannot be connected to itself ('" + charA + "').");
            }

            // Check for character repetition
            if (usedChars.contains(charA) || usedChars.contains(charB)) {
                // Find which character was repeated for a specific error message
                char repeatedChar = usedChars.contains(charA) ? charA : charB;
                throw new IllegalArgumentException("Character '" + repeatedChar + "' was repeated. A character can be used in at most one connection.");
            }

            // Mark characters as used
            usedChars.add(charA);
            usedChars.add(charB);

            // Create bidirectional connection (A <-> B)
            plugboardMap.put(charA, charB);
            plugboardMap.put(charB, charA);
        }

        return plugboardMap;
    }


    @Override
    public String toString() {
        return "Manual Setup Configuration";
    }
}