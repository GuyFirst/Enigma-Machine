package enigma.component.rotor;

public interface Rotor {
    void setPosition(int position);
    int encodeForward(int entryLocation);
    int encodeBackward(int entryLocation);
    int getNotchPosition();
    void rotate();
    int getTopLetter();
}
