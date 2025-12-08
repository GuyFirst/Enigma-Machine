package enigma.component.menu.command.concrete.showSpec;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.DTO.MachineStatusDTO;
import enigma.engine.logic.Engine;



public class ShowEnigmaMachineSpecificationCommand implements MenuCommandExecutable {
    @Override
    public void execute( Engine engine) throws Exception {
        printBorders();
        try{
            System.out.println("Fetching Enigma Machine Specification...");
            MachineStatusDTO machineStatus = engine.getMachineStatus();
            displayMachineSpecification(machineStatus);
        } catch (IllegalStateException e){
            System.out.println("Error fetching machine specification: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            printBorders();
        }
    }

    private void displayMachineSpecification(MachineStatusDTO machineStatus) {
        System.out.println("--- Enigma Machine Specification ---");
        System.out.println("Amount of rotors in the system: " + machineStatus.getAmountOfRotorInSys());
        System.out.println("Amount of reflectors in the system: " + machineStatus.getAmountOfReflectorsInSys());
        System.out.println("Number of messages that have been processed: " + machineStatus.getAmountOfMsgsTillNow());
        if(machineStatus.isMachineLoaded()){
            System.out.println("Initial Enigma Machine Configuration: ");
            System.out.println(machineStatus.getInitialConfig());
            System.out.println("Current Enigma Machine Configuration: ");
            System.out.println(machineStatus.getCurrentConfig());
        } else {
            System.out.println("The Enigma Machine is currently not loaded with a configuration.");
        }
    }

    @Override
    public String toString() {
        return "Show Enigma Machine Specification";
    }
}
