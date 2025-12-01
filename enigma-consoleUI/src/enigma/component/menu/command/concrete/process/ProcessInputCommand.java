package enigma.component.menu.command.concrete.process;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

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
