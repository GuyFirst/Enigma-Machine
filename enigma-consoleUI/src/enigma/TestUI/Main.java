package enigma.TestUI;

import enigma.component.UIController.UIController;
import enigma.engine.logic.Engine;
import enigma.engine.logic.EngineImpl;

public class Main {
    public static void main(String[] args) {
        Engine engine = new EngineImpl();
        UIController uiController = new UIController(engine);
        uiController.start();
    }
}
