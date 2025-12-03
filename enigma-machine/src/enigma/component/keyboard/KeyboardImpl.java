package enigma.component.keyboard;

import java.util.HashMap;
import java.util.Map;

public class KeyboardImpl implements Keyboard {
    private final String alphabetString;
    private final Map<Character, Integer> mapFromCharToInt;
    private final Map<Integer, Character> mapFromIntToChar;

    public KeyboardImpl(String alphabetString, Map<Character, Integer> mapFromCharToInt) {
        this.alphabetString = alphabetString.trim();
        this.mapFromCharToInt = mapFromCharToInt;
        this.mapFromIntToChar = createIntToCharMap();
    }

    private Map<Integer, Character> createIntToCharMap() {
        Map<Integer, Character> intToCharMap = new HashMap<>();
        for (Map.Entry<Character, Integer> entry : mapFromCharToInt.entrySet()) {
            intToCharMap.put(entry.getValue(), entry.getKey());
        }
        return intToCharMap;
    }

    @Override
    public int charToIndex(char c) {
        return mapFromCharToInt.get(c);
    }

    @Override
    public char indexToChar(int index) {
        return mapFromIntToChar.get(index);
    }

    @Override
    public int getAlphabetLength() {
        return alphabetString.length();
    }

    @Override
    public boolean isCharInKeyboard(char c) {
        for(char ch : alphabetString.toCharArray()){
            if(ch == c)
                return true;
        }
        return false;
    }

    private boolean isValidChar(char c) {
        return alphabetString.indexOf(c) != -1;
    }

}
