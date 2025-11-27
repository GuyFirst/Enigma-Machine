package component.menu.command;

import engine.logic.Engine;

public class ProcessInputCommand implements MenuCommandExecutable {

    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        System.out.println("Processing input...");
    }
    @Override
    public String toString() {
        return "Process Input Command";
    }

}
