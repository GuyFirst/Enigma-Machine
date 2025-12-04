package enigma.engine.logic.history;

public class EnigmaMessage {
    private final String input;
    private final String output;
    private final long time;

    public EnigmaMessage(String input, String output, long time) {
        this.input = input;
        this.output = output;
        this.time = time;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "<" + input + ">" + " --> " + "<" + output + ">" + " (" + time + " nano-seconds)";
    }

}
