package enigma.component.menu.command.concrete.showSpec;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowEnigmaMachineSpecificationCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        // TODO implement Enigma machine specification display logic
        //MachineSpecificationDTO spec = engine.getMachineStatus();
        // printMachineSpecification(spec);
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
        return "Show Enigma Machine Specification";
    }
}
