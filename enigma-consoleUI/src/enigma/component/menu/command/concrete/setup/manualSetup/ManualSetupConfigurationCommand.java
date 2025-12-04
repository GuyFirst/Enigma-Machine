package enigma.component.menu.command.concrete.setup.manualSetup;

import enigma.component.UIController.UIController;
import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.*;
import java.util.stream.Collectors;

public class ManualSetupConfigurationCommand implements MenuCommandExecutable {

    Map<String, String> romanMapping = new HashMap<>();

    public ManualSetupConfigurationCommand() {
        romanMapping.put("1", "I");
        romanMapping.put("2", "II");
        romanMapping.put("3", "III");
        romanMapping.put("4", "IV");
        romanMapping.put("5", "V");
    }

    private List<Integer> rotorIdSetupHandler() {
            Scanner scanner = new Scanner(System.in);
            boolean isValidRotorIds = false;
            List<Integer> rotorIds = new ArrayList<>();
            do {
                // get rotor ids like this: 1,2,3 - the most right rotor will be 3 the most left will be 1
                System.out.println("please write the ids of the rotors you want to use in the following format: 1,2,3 (most left, ... , most right):");
                String rotorsIdFromUser = scanner.nextLine();

                try {
                    rotorIds = Arrays.stream(rotorsIdFromUser.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .toList();
                    isValidRotorIds = true;
                } catch (Exception e) {
                    System.out.println(rotorsIdFromUser + " is not a valid format");
                    System.out.println("Press 0 to return to menu");
                    System.out.println("Press any other key to try again");
                    String userDecision = scanner.nextLine();
                    if (userDecision.equals("0")) {
                        return null;
                    }
                }
            } while (!isValidRotorIds);
        return rotorIds;
    }



    private List<Character> rotorPositionSetupHandler(){
        boolean isValidRotorPositions = false;
        Scanner scanner = new Scanner(System.in);
        List<Character> rotorPositions = new ArrayList<>();
        do {
            // get rotor positions: 4D8A - A is the position of the most right rotor
            System.out.println("please write the positions of the rotors you want to use in the following format: 4D8A (most left, ... , most right):");
            String rotorsPositionsFromUser = scanner.nextLine().toUpperCase();
            try{
                rotorPositions = rotorsPositionsFromUser.chars()
                        .mapToObj(c -> (char) c)
                        .toList();
                isValidRotorPositions = true;
            }catch (Exception e){
                System.out.println(rotorsPositionsFromUser + " is not a valid format");
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.equals("0")) {
                    return null;
                }
            }

        } while (!isValidRotorPositions);
        return rotorPositions;
    }

    private String reflectorIdSetupHandler(){
        //get id of reflector in roman letter - need to present to user the roman letters and the user will put decimal number
        System.out.println("please select the reflector you want to use (decimal number):");
        for (Map.Entry<String, String> entry : romanMapping.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
        Scanner scanner = new Scanner(System.in);
        String reflectorId = "";
        String reflectorFromUser = "";
        boolean isValidReflectorId = false;
        do {
            reflectorFromUser = scanner.nextLine().toUpperCase();
            if (!romanMapping.containsKey(reflectorFromUser)) {
                System.out.println(reflectorFromUser + " is not a valid format");
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.equals("0")) {
                    return null;
                }
            } else {
                isValidReflectorId = true;
            }
        } while (!isValidReflectorId);

        reflectorId = romanMapping.get(reflectorFromUser);
        return reflectorId;
    }


    @Override
    public void execute(Engine engine) throws Exception {
        // first check if machine is loaded
        if (!engine.isXMLLoaded()) {
            throw new IllegalStateException("Machine repository is not loaded. Please load the machine configuration before setting up the machine.");
        }

        do {
            List<Integer> rotorIds = rotorIdSetupHandler();
            if (rotorIds == null) {
                return;
            }
            List<Character> rotorPositions = rotorPositionSetupHandler();
            if (rotorPositions == null) {
                return;
            }
            String reflectorId = reflectorIdSetupHandler();
            if (reflectorId == null) {
                return;
            }

            try {
                engine.setMachineCode(rotorIds, rotorPositions, reflectorId);
                UIController.isMachineLoaded = true;
                System.out.println("Manual setup completed successfully.");
            } catch (Exception e) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Error during manual setup: " + e.getMessage());
                System.out.println("Press 0 to return to menu");
                System.out.println("Press any other key to try again");
                String userDecision = scanner.nextLine();
                if (userDecision.equals("0")) {
                    return;
                }
            }
        } while (!UIController.isMachineLoaded);
    }


    @Override
    public String toString() {
        return "Manual Setup Configuration";
    }
}
