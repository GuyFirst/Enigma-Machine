package enigma.component.menu.command.template;

import enigma.engine.logic.Engine;

import java.util.Scanner;

public interface MenuCommandExecutable {
    void execute(Engine engine) throws Exception;

    String toString();
}
