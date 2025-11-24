package components.menu;

import java.util.Scanner;

public interface MenuCommandExecutable {
    void execute(Scanner scanner /* TODO add logic enggine */) throws Exception;
    String toString();
}
