package component.reflector;

// This class represents a single one-way mapping from the XML (e.g., 1 -> 4)
// It can be implemented as a simple DTO/Record.

public class ReflectorPair {
    private final int input;
    private final int output;

    public ReflectorPair(int input, int output) {
        this.input = input;
        this.output = output;
    }

    public int getInput() {
        return input;
    }

    public int getOutput() {
        return output;
    }

    // In a final project, this would likely be a JAXB-generated class.
}