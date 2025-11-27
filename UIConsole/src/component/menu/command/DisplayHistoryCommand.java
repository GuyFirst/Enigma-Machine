package component.menu.command;

import engine.logic.Engine;

public class DisplayHistoryCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        System.out.println("Displaying history...");
    }

    @Override
    public String toString() {
        return "Display History Command";
    }
}
