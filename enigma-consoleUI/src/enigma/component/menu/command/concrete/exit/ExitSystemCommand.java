package enigma.component.menu.command.concrete.exit;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

public class ExitSystemCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner, Engine engine) throws Exception {
        System.out.println("Exiting system...");
        engine.exit();
    }

    @Override
    public String toString() {
        return "Exit System Command";
    }
}
