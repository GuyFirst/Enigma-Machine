package enigma.component.keyboard;

public interface Keyboard {
    int charToIndex(char c);
    char indexToChar(int index);
    int getAlphabetLength();

    boolean isCharInKeyboard(char c);
}
