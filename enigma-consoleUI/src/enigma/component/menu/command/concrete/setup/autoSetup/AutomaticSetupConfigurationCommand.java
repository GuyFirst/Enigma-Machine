package enigma.component.menu.command.concrete.setup.autoSetup;

import enigma.component.UIController.UIController;
import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

public class AutomaticSetupConfigurationCommand implements MenuCommandExecutable {

    @Override
    public void execute(Engine engine) throws Exception {
        printBorders();
        if (!engine.isXMLLoaded()) {
            throw new IllegalStateException("XML file is not loaded. Please configure the system via XML configuration or loading an existing state via file.");
        }
        try{
            engine.setAutomaticCode();
            System.out.println("Automatic setup completed successfully.");
            UIController.isMachineLoaded = true;
        }catch (IllegalStateException e){
            System.out.println("Automatic setup failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during automatic setup: " + e.getMessage());
        }
        printBorders();
    }


    @Override
    public String toString() {
        return "Automatic Setup Configuration";
    }

}
