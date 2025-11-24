package components.menu;

public class ResetCodeCommand implements MenuCommandExecutable{

    @Override
    public void execute(java.util.Scanner scanner) throws Exception {
        System.out.println("Resetting code to default state...");
    }
    @Override
    public String toString() {
        return "Reset Code Command";
    }
}
