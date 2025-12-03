package enigma.component.menu.command.concrete.setup.manualSetup;

import enigma.component.UIController.UIController;
import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

public class ManualSetupConfigurationCommand implements MenuCommandExecutable {

    @Override
    public void execute( Engine engine) throws Exception {



       // UIController.isMachineLoaded = true;
    }


    @Override
    public String toString() {
        return "Manual Setup Configuration";
    }
}
