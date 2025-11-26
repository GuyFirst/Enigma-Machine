package component.menu.command;

public class AutomaticSetupConfigurationCommand implements MenuCommandExecutable, SetupStrategy {

    @Override
    public void execute(java.util.Scanner scanner) throws Exception {
        setup();
    }

    @Override
    public void setup() throws Exception {
        // TODO implement automatic setup logic
    }

    @Override
    public String toString() {
        return "Automatic Setup Configuration";
    }

}
