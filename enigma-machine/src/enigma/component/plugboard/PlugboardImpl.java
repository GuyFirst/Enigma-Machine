package enigma.component.plugboard;

import java.util.Map;

public class PlugboardImpl implements  Plugboard {
    private final Map<Character, Character> wiringMap;

    public PlugboardImpl(Map<Character,Character> wiringMap) {
        this.wiringMap = wiringMap;
    }

    @Override
    public char substitute(char inputChar) {
        return wiringMap.getOrDefault(inputChar, inputChar);
    }

    @Override
    public Map<Character, Character> getWiringMap() {
        return wiringMap;
    }
}


