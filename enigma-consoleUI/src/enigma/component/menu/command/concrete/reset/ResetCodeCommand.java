package enigma.component.menu.command.concrete.reset;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

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
