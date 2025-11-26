package component.menu.command;

public class DisplayHistoryCommand implements MenuCommandExecutable {
    @Override
    public void execute(java.util.Scanner scanner) throws Exception {
        System.out.println("Displaying history...");
    }

    @Override
    public String toString() {
        return "Display History Command";
    }
}
