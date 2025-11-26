package component.menu.command;

public class ManualSetupConfigurationCommand implements MenuCommandExecutable, SetupStrategy {

    @Override
    public void execute(java.util.Scanner scanner) throws Exception {
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
