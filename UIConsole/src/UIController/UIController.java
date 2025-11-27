package UIController;

import component.menu.*;
import component.menu.command.*;
import engine.logic.Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UIController {
    private final Menu menu;
    private final Engine engine;
    //TODO add engine reference

    public UIController(Engine engine) {
        menu = createMenu();
        this.engine = engine;
    }

    private Menu createMenu() {
        List<MenuCommandExecutable> commands = new ArrayList<>();

        // 1. Load Machine from XML
        commands.add(new LoadXMLConfigurationCommand());

        // 2. Display Machine Status / Specification
        commands.add(new ShowEnigmaMachineSpecificationCommand());

        // 3. Manual Code Setup
        commands.add(new ManualSetupConfigurationCommand());

        // 4. Automatic Code Setup
        commands.add(new AutomaticSetupConfigurationCommand());

        // 5. Process Input (Encrypt/Decrypt)
        commands.add(new ProcessInputCommand());

        // 6. Reset to Original Code
        commands.add(new ResetCodeCommand());

        // 7. Get History and Statistics
        commands.add(new DisplayHistoryCommand());

        // 8. Exit System
        commands.add(new ExitSystemCommand());

        return new Menu(commands);
    }

    public void start() {

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please select an option from the menu:");
            menu.displayMenu();
            int choice = scanner.nextInt();
            try {
                menu.getMenuCommands().get(choice - 1).execute(scanner, engine);

            } catch (Exception e) {
               System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }










}

