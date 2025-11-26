package component.menu.command;

public class ExitSystemCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner) throws Exception {
        System.out.println("Exiting system...");
        System.exit(0);
    }

    @Override
    public String toString() {
        return "Exit System Command";
    }
}
