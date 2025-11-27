package component.menu.command;

import engine.logic.Engine;

public class ExitSystemCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        System.out.println("Exiting system...");
        System.exit(0);
    }

    @Override
    public String toString() {
        return "Exit System Command";
    }
}
