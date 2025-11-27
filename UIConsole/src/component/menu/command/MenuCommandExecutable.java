package component.menu.command;

import engine.logic.Engine;

import java.util.Scanner;

public interface MenuCommandExecutable {
    void execute(Scanner scanner, Engine engine) throws Exception;
    String toString();
}
