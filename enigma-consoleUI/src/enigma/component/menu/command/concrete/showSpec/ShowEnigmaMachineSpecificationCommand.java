package enigma.component.menu.command.concrete.showSpec;

import DTO.CodeSpecDTO;
import DTO.MachineSpecificationDTO;
import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowEnigmaMachineSpecificationCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        // TODO implement Enigma machine specification display logic
        MachineSpecificationDTO spec = engine.getMachineStatus();
        printMachineSpecification(spec);
    }

    private void printMachineSpecification(MachineSpecificationDTO spec) {

        System.out.println("\n===== ENIGMA MACHINE SPECIFICATION =====");

        // General System Specs
        // 1. Total Rotors available
        System.out.println("Total Rotors in System (Max available): " + spec.getNumOfRotorsInSystem());
        // 2. Total Reflectors available
        System.out.println("Total Reflectors in System: " + spec.getNumOfReflectorsInSystem());
        // 3. Total Messages Processed
        System.out.println("Total Encrypted Messages since load: " + spec.getNumOfReceivedMessages());

        // Initial Code Configuration (4)
        CodeSpecDTO initialCode = spec.getInitialCodeSpec();
        if (initialCode != null) {
            System.out.println("\n--- Initial (Original) Code Configuration ---");
            // The method must return the compact format for sections 4 and 5
            System.out.println(formatCodeSpec(initialCode));
        } else {
            System.out.println("\n--- Initial Code Configuration: Not yet set (Use Option 3 or 4) ---");
        }

        // Current Code Configuration (5)
        CodeSpecDTO currentCode = spec.getCurrentCodeSpec();
        if (currentCode != null) {
            System.out.println("\n--- Current Code Configuration (Active) ---");
            // The method must return the compact format for sections 4 and 5
            System.out.println(formatCodeSpec(currentCode));
        } else {
            System.out.println("\n--- Current Code Configuration: Not Active ---");
        }

        System.out.println("==========================================");
    }

    /**
     * Formats the CodeSpecDTO into the required compact string format:
     * <Rotors ID, Order><Positions (Char, Distance)><Reflector ID>
     */
    private String formatCodeSpec(CodeSpecDTO codeSpec) {
        if (codeSpec == null) {
            return "Configuration Data Not Available.";
        }

        StringBuilder sb = new StringBuilder();

        // The list of rotor IDs (currentRotorIds) is ordered Left-to-Right (LTR).
        List<Integer> rotorIds = codeSpec.getCurrentRotorIds();

        // Maps are keyed by the ROTOR ID (Integer).
        Map<Integer, Integer> rotorPositionOnWindow = codeSpec.getRotorPositionOnWindow();
        Map<Integer, Integer> notchDistanceMap = codeSpec.getNotchPositionFromWindow();


        // --- 1. <Rotors ID, Order> (e.g., <45, 27, 94>) ---
        // Rotors must be printed LTR (Left to Right), separated by commas.
        String rotorsSection = rotorIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        sb.append("<").append(rotorsSection).append(">");


        // --- 2. <Positions (Char, Distance)> (e.g., <A(2), O(5), !(20)>) ---
        sb.append("<");

        // Build the list of Position(Distance) strings, ordered LTR.
        // This relies on the Engine/Keyboard to convert the Index (Integer) back to a Character.
        String positionsSection = rotorIds.stream()
                .map(rotorId -> {
                    // Get the current position index for this rotor ID.
                    Integer positionIndex = rotorPositionOnWindow.get(rotorId);
                    // Get the notch distance for this rotor ID.
                    Integer notchDistance = notchDistanceMap.get(rotorId);

                    // --- CRITICAL ASSUMPTION ---
                    // We must convert the position index (Integer) back to a Character.
                    // Since charToIndex/indexToChar logic resides in the Keyboard/Engine,
                    // we simulate this conversion here using a placeholder 'CHAR' if the DTO only holds the index.
                    char positionChar;
                    if (positionIndex != null) {
                        // This is where the Keyboard/Alphabet mapping logic must be used.
                        // For now, we simulate the conversion based on index:
                        positionChar = (char) ('A' + positionIndex); // Placeholder: Assumes A-Z alphabet and small index.
                    } else {
                        positionChar = '?';
                    }

                    return positionChar + "(" + notchDistance + ")";
                })
                .collect(Collectors.joining(","));

        sb.append(positionsSection).append(">");


        // --- 3. <Reflector ID> (e.g., <III>) ---
        // Reflector ID is an Integer (1-5) in the DTO, needs conversion to Roman numeral (I-V).
        String reflectorRoman = convertToRoman(codeSpec.getReflectorId());
        sb.append("<").append(reflectorRoman).append(">");

        return sb.toString();
    }

    /**
     * Utility method to convert an integer (1-5) to a Roman numeral (I-V).
     */
    private String convertToRoman(int number) {
        if (number < 1 || number > 5) return String.valueOf(number);
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return "";
        }
    }

    @Override
    public String toString() {
        return "2. Show Enigma Machine Specification";
    }
}
