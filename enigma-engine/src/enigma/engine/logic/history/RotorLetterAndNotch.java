package enigma.engine.logic.history;

public class RotorLetterAndNotch {
    private final char letter;
    private final int notch;

    public RotorLetterAndNotch(char letter, int notch) {
        this.letter = letter;
        this.notch = notch;
    }

    public char getLetter() {
        return letter;
    }

    public int getNotch() {
        return notch;
    }

}
