package patmal.course.enigma.component.menu.command.concrete;

import patmal.course.enigma.component.menu.command.template.MenuCommandExecutable;
import patmal.course.enigma.engine.logic.Engine;

import java.util.Scanner;

public class ChangeLogLevelCommand implements MenuCommandExecutable {
    @Override
    public void execute(Engine engine) throws Exception {
        printBorders();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the new log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL):");
        String level = scanner.nextLine();
        engine.setLogLevel(level);
        System.out.println("Log level changed successfully.");
    }

    @Override
    public String toString() {
        return "Change Log Level";
    }
}
