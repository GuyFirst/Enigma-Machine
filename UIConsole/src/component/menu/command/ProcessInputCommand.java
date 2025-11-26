package component.menu.command;

public class ProcessInputCommand implements MenuCommandExecutable {

    @Override
    public void execute(java.util.Scanner scanner) throws Exception {
        System.out.println("Processing input...");
    }
    @Override
    public String toString() {
        return "Process Input Command";
    }

}
