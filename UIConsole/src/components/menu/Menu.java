package components.menu;

import java.util.List;

public class Menu {
    private final List<MenuCommandExecutable> MenuCommands;

    public Menu(List<MenuCommandExecutable> menuCommands) {
        this.MenuCommands = menuCommands;
    }

    public void displayMenu() {
        System.out.println("===== components.menu.Menu =====");
        for (int i = 0; i < MenuCommands.size(); i++) {
            System.out.println((i + 1) + ". " + MenuCommands.get(i).toString());
        }
        System.out.println("================");
    }

    public List<MenuCommandExecutable> getMenuCommands() {
        return MenuCommands;
    }
}
