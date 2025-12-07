package enigma.component.menu.command.concrete.load.system;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.Scanner;

public class LoadSystemState implements MenuCommandExecutable {

    @Override
    public void execute(Engine engine) throws Exception {
        System.out.println("Please enter the file path to load the system state from: ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
        engine.loadSystemStateFromFile(filePath);
        System.out.println("System state loaded successfully from: " + filePath);
    }

    @Override
    public String toString() {
        return "Load System State Command";
    }
}
