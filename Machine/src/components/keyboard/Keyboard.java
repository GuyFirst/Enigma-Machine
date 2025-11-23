package components.keyboard;

import java.util.Map;

public class Keyboard {
    private final String alphabetString;
    private final Map<Character, Integer> mapFromCharToInt;
    private final Map<Integer, Character> mapFromIntToChar;

    public Keyboard(int alphabetLength, String alphabetString, int length, Map<Character, Integer> mapFromCharToInt, Map<Integer, Character> mapFromIntToChar) {
        this.alphabetString = alphabetString;
        this.mapFromCharToInt = mapFromCharToInt;
        this.mapFromIntToChar = mapFromIntToChar;
    }
    public int charToIndex(char c) {
        return mapFromCharToInt.get(c);
    }
    public char indexToChar(int index) {
        return mapFromIntToChar.get(index);
    }

    private boolean isValidChar(char c) {
        return alphabetString.indexOf(c) != -1;
    }

}
