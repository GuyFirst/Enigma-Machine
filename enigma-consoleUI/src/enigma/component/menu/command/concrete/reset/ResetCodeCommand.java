package enigma.component.menu.command.concrete.reset;

import enigma.component.UIController.UIController;
import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

public class ResetCodeCommand implements MenuCommandExecutable {

    @Override
    public void execute(Engine engine) throws Exception {
        if(!UIController.isMachineLoaded)
            throw new IllegalStateException("Machine configuration is not loaded. Please load the machine configuration before processing input.");
        System.out.println("Resetting code to default state...");
        try{
            engine.resetMachineToOriginalCode();
            System.out.println("Code has been reset to default state.");
        } catch (Exception e){
            System.out.println("Error during resetting code: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public String toString() {
        return "Reset Code Command";
    }
}
