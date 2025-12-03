package enigma.component.menu.command.concrete.showHistory;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.Scanner;

public class DisplayHistoryCommand implements MenuCommandExecutable {

//    @Override
//    public void execute(Scanner scanner, Engine engine) throws Exception {
//        System.out.println("Displaying history...");
//    }

    @Override
    public void execute(Engine engine) throws Exception {

    }

    @Override
    public String toString() {
        return "Display History Command";
    }
}
