package patmal.course.enigma.component.menu.command.template;


import patmal.course.enigma.engine.logic.Engine;

public interface MenuCommandExecutable {
    void execute(Engine engine) throws Exception;
    default void printBorders() { System.out.println(" "); }
    String toString();
}
