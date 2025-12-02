package enigma.engine.logic.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnigmaConfiguration {
    private final List<Integer> rotorIDs;
    private final List<RotorLetterAndNotch> rotorLetterAndNotch;
    private final String reflectorID;
    private final List<EnigmaMessege> messeges;

    public EnigmaConfiguration(List<Integer> rotorIDs, List<RotorLetterAndNotch> rotorLetterAndNotch, String reflectorID) {
        this.rotorIDs = rotorIDs;
        this.rotorLetterAndNotch = rotorLetterAndNotch;
        this.reflectorID = reflectorID;
        this.messeges  = new ArrayList<>();
    }

    public void addMessege(String input, String output, long time){
        this.messeges.add(new EnigmaMessege(input, output, time));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EnigmaConfiguration that = (EnigmaConfiguration) o;
        return Objects.equals(rotorIDs, that.rotorIDs) && Objects.equals(rotorLetterAndNotch, that.rotorLetterAndNotch) && Objects.equals(reflectorID, that.reflectorID) && Objects.equals(messeges, that.messeges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rotorIDs, rotorLetterAndNotch, reflectorID, messeges);
    }

    public String getReflectorID() {
        return reflectorID;
    }

    public List<RotorLetterAndNotch> getRotorLetterAndNotch() {
        return rotorLetterAndNotch;
    }

    public List<Integer> getRotorIDs() {
        return rotorIDs;
    }

    public List<EnigmaMessege> getMesseges() {
        return messeges;
    }
}
