package enigma.component.reflector;

import java.util.Map;

public class ReflectorManager {
    private final Reflector currentReflector;

    public ReflectorManager(Reflector currentReflector) {
        this.currentReflector = currentReflector;
    }

    public int reflect(int inputPosition) {
        return currentReflector.reflect(inputPosition );
    }
}
