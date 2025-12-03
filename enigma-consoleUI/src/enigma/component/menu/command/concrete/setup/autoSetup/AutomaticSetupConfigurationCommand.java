package enigma.component.menu.command.concrete.setup.autoSetup;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

public class AutomaticSetupConfigurationCommand implements MenuCommandExecutable {

    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        try{
            engine.setAutomaticCode();
            System.out.println("Automatic setup completed successfully.");
        }catch (IllegalStateException e){
            System.out.println("Automatic setup failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during automatic setup: " + e.getMessage());
        }
    }


    @Override
    public String toString() {
        return "Automatic Setup Configuration";
    }

}
