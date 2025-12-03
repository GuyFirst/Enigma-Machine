package enigma.component.menu.command.concrete.showSpec;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.DTO.MachineStatusDTO;
import enigma.engine.logic.Engine;
import enigma.engine.logic.history.EnigmaConfiguration;


public class ShowEnigmaMachineSpecificationCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        try{
            System.out.println("Fetching Enigma Machine Specification...");
            MachineStatusDTO machineStatus = engine.getMachineStatus();
            displayMachineSpecification(machineStatus);
        } catch (IllegalStateException e){
            System.out.println("Error fetching machine specification: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void displayMachineSpecification(MachineStatusDTO machineStatus) {
        System.out.println("--- Enigma Machine Specification ---");
        System.out.println("Amount of rotors in the system: " + machineStatus.getAmountOfRotorInSys());
        System.out.println("Amount of reflectors in the system: " + machineStatus.getAmountOfReflectorsInSys());
        System.out.println("Number of messages that have been processed: " + machineStatus.getAmountOfMsgsTillNow());
        if(machineStatus.isMachineLoaded()){
            EnigmaConfiguration initialConfig = machineStatus.getInitialConfig();
            EnigmaConfiguration currentConfig = machineStatus.getCurrentConfig();
            StringBuilder initialConfigStr = new StringBuilder();
            StringBuilder currentConfigStr = new StringBuilder();
            System.out.println("Initial Enigma Machine Configuration: ");
            displayMachineConfiguration(initialConfig, initialConfigStr);
            System.out.println("Current Enigma Machine Configuration: ");
            displayMachineConfiguration(currentConfig, currentConfigStr);
        } else {
            System.out.println("The Enigma Machine is currently not loaded with a configuration.");
        }
    }

    private void displayMachineConfiguration(EnigmaConfiguration config, StringBuilder configStr) {
        appendRotorsToStr(config, configStr);
        appendRotorsAndNotchesToStr(config, configStr);
        appendReflectorToStr(config, configStr);
    }

    private void appendReflectorToStr(EnigmaConfiguration initialConfig, StringBuilder initialConfigStr) {

        initialConfigStr.append("<");
        initialConfigStr.append(initialConfig.getReflectorID());
        initialConfigStr.append(">");
    }

    private void appendRotorsAndNotchesToStr(EnigmaConfiguration initialConfig, StringBuilder initialConfigStr) {

        initialConfigStr.append("<");

        for(int i = 0; i < initialConfig.getRotorLetterAndNotch().size(); i++) {
            initialConfigStr.append(initialConfig.getRotorLetterAndNotch().get(i).getLetter());
            initialConfigStr.append("(");
            initialConfigStr.append(initialConfig.getRotorLetterAndNotch().get(i).getNotchPos());
            initialConfigStr.append(")");
            if(i != initialConfig.getRotorLetterAndNotch().size() - 1){
                initialConfigStr.append(",");
            }
        }

        initialConfigStr.append(">");
    }

    private void appendRotorsToStr(EnigmaConfiguration initialConfig, StringBuilder initialConfigStr) {

        initialConfigStr.append("<");
        for (int i = 0; i < initialConfig.getRotorIDs().size(); i++) {
            initialConfigStr.append(initialConfig.getRotorIDs().get(i));
            if(i != initialConfig.getRotorIDs().size() - 1){
                initialConfigStr.append(",");
            }
        }

        initialConfigStr.append(">");
    }

    @Override
    public String toString() {
        return "Show Enigma Machine Specification";
    }
}
