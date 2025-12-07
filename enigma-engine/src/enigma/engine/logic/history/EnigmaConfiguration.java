package enigma.engine.logic.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnigmaConfiguration implements Serializable {
    private final List<Integer> rotorIDs;
    private final List<RotorLetterAndNotch> rotorLetterAndNotch;
    private final String reflectorID;
    private final List<EnigmaMessage> messages;

    public EnigmaConfiguration(List<Integer> rotorIDs, List<RotorLetterAndNotch> rotorLetterAndNotch, String reflectorID) {
        this.rotorIDs = rotorIDs;
        this.rotorLetterAndNotch = rotorLetterAndNotch;
        this.reflectorID = reflectorID;
        this.messages = new ArrayList<>();
    }

    public void addMessage(String input, String output, long time){
        this.messages.add(new EnigmaMessage(input, output, time));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EnigmaConfiguration that = (EnigmaConfiguration) o;
        return Objects.equals(rotorIDs, that.rotorIDs) && Objects.equals(rotorLetterAndNotch, that.rotorLetterAndNotch) && Objects.equals(reflectorID, that.reflectorID) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rotorIDs, rotorLetterAndNotch, reflectorID, messages);
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

    public List<EnigmaMessage> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        StringBuilder configStr = new StringBuilder();
        appendRotorsToStr(configStr);
        appendRotorsAndNotchesToStr(configStr);
        appendReflectorToStr(configStr);
        return configStr.toString();
    }

    private void appendReflectorToStr(StringBuilder ConfigStr) {

        ConfigStr.append("<");
        ConfigStr.append(this.getReflectorID());
        ConfigStr.append(">");
    }

    private void appendRotorsAndNotchesToStr(StringBuilder ConfigStr) {

        ConfigStr.append("<");

        for(int i = 0; i < this.getRotorLetterAndNotch().size(); i++) {
            ConfigStr.append(this.getRotorLetterAndNotch().get(i).getLetter());
            ConfigStr.append("(");
            ConfigStr.append(this.getRotorLetterAndNotch().get(i).getNotchPos());
            ConfigStr.append(")");
            if(i != this.getRotorLetterAndNotch().size() - 1){
                ConfigStr.append(",");
            }
        }

        ConfigStr.append(">");
    }

    private void appendRotorsToStr(StringBuilder ConfigStr) {

        ConfigStr.append("<");
        for (int i = 0; i < this.getRotorIDs().size(); i++) {
            ConfigStr.append(this.getRotorIDs().get(i));
            if(i != this.getRotorIDs().size() - 1){
                ConfigStr.append(",");
            }
        }

        ConfigStr.append(">");
    }
}
