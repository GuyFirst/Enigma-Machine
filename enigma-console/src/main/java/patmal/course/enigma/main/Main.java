package patmal.course.enigma.main;

import patmal.course.enigma.component.UIController.UIController;
import patmal.course.enigma.engine.logic.EngineImpl;

public class Main {

    /**
     * Prints the application title in a custom ASCII art style slowly,
     * using Thread.sleep() to create a dramatic introduction effect.
     */
    private static void printAsciiArtIntroduction() {
        // ASCII art provided by the user (appears to be block characters)
        // Note: The original ASCII characters were replaced with the user-provided block characters.
        String asciiArt = " _______   ________   ___  ________  _____ ______   ________          _____ ______   ________  ________  ___  ___  ___  ________   _______      \n" +
                "|\\  ___ \\ |\\   ___  \\|\\  \\|\\   ____\\|\\   _ \\  _   \\|\\   __  \\        |\\   _ \\  _   \\|\\   __  \\|\\   ____\\|\\  \\|\\  \\|\\  \\|\\   ___  \\|\\  ___ \\     \n" +
                "\\ \\   __/|\\ \\  \\\\ \\  \\ \\  \\ \\  \\___|\\ \\  \\\\\\__\\ \\  \\ \\  \\|\\  \\       \\ \\  \\\\\\__\\ \\  \\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\ \\  \\ \\  \\\\ \\  \\ \\   __/|    \n" +
                " \\ \\  \\_|/_\\ \\  \\\\ \\  \\ \\  \\ \\  \\  __\\ \\  \\\\|__| \\  \\ \\   __  \\       \\ \\  \\\\|__| \\  \\ \\   __  \\ \\  \\    \\ \\   __  \\ \\  \\ \\  \\\\ \\  \\ \\  \\_|/__  \n" +
                "  \\ \\  \\_|\\ \\ \\  \\\\ \\  \\ \\  \\ \\  \\|\\  \\ \\  \\    \\ \\  \\ \\  \\ \\  \\       \\ \\  \\    \\ \\  \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \\  \\ \\  \\\\ \\  \\ \\  \\_|\\ \\ \n" +
                "   \\ \\_______\\ \\__\\\\ \\__\\ \\__\\ \\_______\\ \\__\\    \\ \\__\\ \\__\\ \\__\\       \\ \\__\\    \\ \\__\\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\\ \\__\\ \\__\\\\ \\__\\ \\_______\\\n" +
                "    \\|_______|\\|__| \\|__|\\|__|\\|_______|\\|__|     \\|__|\\|__|\\|__|        \\|__|     \\|__|\\|__|\\|__|\\|_______|\\|__|\\|__|\\|__|\\|__| \\|__|\\|_______|\n" +
                "                                                                                                                                                                       \n" +
                "                                                                         By Guy First & Alfredo Limin                                                                  \n" +
                " \n";

        System.out.println("\n--- Initializing Enigma Simulation ---\n");
        System.out.println("Please enlarge the screen to view the ASCII art in the best way.!\n");
        // Split the art by lines and print them with a delay
        String[] lines = asciiArt.split("\n");
        for (String line : lines) {
            try {
                System.out.println(line);
                Thread.sleep(60); // Short delay (80ms) for slow effect
            } catch (InterruptedException e) {
                // If interrupted, stop sleeping and exit the loop gracefully
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Final long pause for dramatic effect before the UI starts
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        // 1. Run the ASCII art introduction sequence
        printAsciiArtIntroduction();

        UIController enigmaUIController = new UIController(new EngineImpl());
        enigmaUIController.run();
    }
}