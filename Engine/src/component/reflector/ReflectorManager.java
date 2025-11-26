package component.reflector;

import java.util.Map;

public class ReflectorManager {
    private final Map<String, Reflectable> reflectors;
    private Reflectable currentReflector;

    public ReflectorManager(Map<String, Reflectable> reflectors,  Reflectable currentReflector) {
        this.reflectors = reflectors;
        this.currentReflector = currentReflector;
    }

    private void setReflectorById(String id) {
        this.currentReflector = reflectors.get(id);
    }

    public int reflect(int inputPosition) {
        return currentReflector.reflect(inputPosition );
    }
}
