package enigma.component.menu.command.concrete.loadXML;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.util.Scanner;

public class LoadXMLConfigurationCommand implements MenuCommandExecutable {
    @Override
    public void execute(Engine engine) throws Exception {

        System.out.println("Please insert the exact path for the XML desired to be loaded.");
        Scanner pathScanner = new Scanner(System.in);
        String path = pathScanner.nextLine();
        System.out.println("Loading XML configuration from: " + path);
        engine.loadMachineFromXml(path);
        System.out.println("XML configuration loaded successfully.");
    }
    @Override
    public String toString() {
        return "Read XML Configuration";
    }
}
