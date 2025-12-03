package main;

import enigma.component.UIController.UIController;
import enigma.engine.logic.EngineImpl;
import enigma.engine.logic.history.HistoryManager;

public class Main {
    public static void main(String[] args) {
        UIController enigmaUIController = new UIController(new EngineImpl(new HistoryManager()));
        enigmaUIController.run();
    }
}
