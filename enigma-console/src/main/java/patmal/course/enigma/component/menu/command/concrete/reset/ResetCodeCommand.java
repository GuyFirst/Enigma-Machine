package patmal.course.enigma.component.menu.command.concrete.reset;


import patmal.course.enigma.component.UIController.UIController;
import patmal.course.enigma.component.menu.command.template.MenuCommandExecutable;
import patmal.course.enigma.engine.logic.Engine;

public class ResetCodeCommand implements MenuCommandExecutable {

    @Override
    public void execute(Engine engine) throws Exception {
        printBorders();
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
        printBorders();
    }
    @Override
    public String toString() {
        return "Reset Code Command";
    }
}
