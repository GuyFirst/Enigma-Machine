package enigma.component.menu.command.concrete.save.system;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.Scanner;

public class SaveCurrentSystemState implements MenuCommandExecutable {
    @Override
    public void execute(Engine engine) throws Exception {
        printBorders();
        System.out.println("Please choose where to save the current system state (file path): ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
        engine.saveCurrentSystemStateToFile(filePath);
        System.out.println("System state saved successfully to: " + filePath);
        printBorders();
    }

    @Override
    public String toString() {
        return "Save Current System State Command";
    }
}
