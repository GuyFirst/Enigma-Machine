package components.reflector;

import java.util.Map;

public class ReflectorManager {
    private final Map<String, Reflector> reflectors;
    private Reflector currentReflector;

    public ReflectorManager(Map<String, Reflector> reflectors,  Reflector currentReflector) {
        this.reflectors = reflectors;
        this.currentReflector = currentReflector;
    }

    private void setReflectorById(String id) {
        this.currentReflector = reflectors.get(id);
    }

    public int reflect(int inputPosition) {
        return currentReflector.reflect(inputPosition);
    }
}
