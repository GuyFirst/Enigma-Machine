package enigma.component.menu.command.concrete.loadXML;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.logic.Engine;

import java.io.File;
import java.util.Scanner;

public class LoadXMLConfigurationCommand implements MenuCommandExecutable {
    @Override
    public void execute(Engine engine) throws Exception {

        System.out.println("Please insert the exact path for the XML desired to be loaded.");
        Scanner pathScanner = new Scanner(System.in);
        String path = pathScanner.nextLine();
        if(!path.endsWith(".xml")){
            System.out.println("Invalid file format. Please provide a valid XML file path.");
            return;
        }
        File file = new File(path);
        if(!file.exists() || !file.isFile()){
            System.out.println("The file does not exist. Please provide a valid file path.");
            return;
        System.out.println("Loading XML configuration from: " + path);
        try {
            engine.loadMachineFromXml(path);
            System.out.println("XML configuration loaded successfully.");
        } catch (Exception e) {
            System.out.println("Error loading XML configuration: " + e.getMessage());
        }

    }

    @Override
    public String toString() {
        return "Read XML Configuration";
    }
}
