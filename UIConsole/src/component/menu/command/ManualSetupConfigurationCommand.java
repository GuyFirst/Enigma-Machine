package component.menu.command;

import engine.logic.Engine;

public class ManualSetupConfigurationCommand implements MenuCommandExecutable, SetupStrategy {

    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        setup();
    }

    @Override
    public void setup() throws Exception {
        // TODO implement manual setup logic
    }

    @Override
    public String toString() {
        return "Manual Setup Configuration";
    }
}
