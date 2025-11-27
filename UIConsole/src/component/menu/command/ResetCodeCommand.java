package component.menu.command;

import engine.logic.Engine;

public class ResetCodeCommand implements MenuCommandExecutable {

    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        System.out.println("Resetting code to default state...");
    }
    @Override
    public String toString() {
        return "Reset Code Command";
    }
}
