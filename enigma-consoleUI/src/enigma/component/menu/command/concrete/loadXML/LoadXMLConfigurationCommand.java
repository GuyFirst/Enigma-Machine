package enigma.component.menu.command.concrete.loadXML;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.Scanner;

public class LoadXMLConfigurationCommand implements MenuCommandExecutable {
    @Override
    public void execute(Scanner scanner, Engine engine) throws Exception {
        engine.loadMachineFromXml(/* TODO: path */);
    }
    @Override
    public String toString() {
        return "Read XML Configuration";
    }
}
