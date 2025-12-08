package enigma.component.menu.command.concrete.process;

import enigma.component.UIController.UIController;
import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;
import enigma.engine.logic.history.EnigmaMessage;

import java.util.Scanner;

public class ProcessInputCommand implements MenuCommandExecutable {

    @Override
    public void execute( Engine engine) throws Exception {
        printBorders();
        if(!UIController.isMachineLoaded)
            throw new IllegalStateException("Machine configuration is not loaded. Please load the machine configuration before processing input.");
        System.out.println("please write the input to process:");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine().toUpperCase();
        try{
            EnigmaMessage msg = engine.processInput(userInput);
            System.out.println("the message " + msg.getInput() + " was processed to: " + msg.getOutput());
        } catch (Exception e){
            System.out.println("Error during processing input: " + e.getMessage());
            throw e;
        }
        printBorders();
    }
    @Override
    public String toString() {
        return "Process Input Command";
    }

}
