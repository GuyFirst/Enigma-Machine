package enigma.engine.DTO;

public class MessageDTO {
    String inputMessage;
    String outputMessage;
    long time;

    public MessageDTO(String inputMessage, String outputMessage, long time) {
        this.inputMessage = inputMessage;
        this.outputMessage = outputMessage;
        this.time = time;
    }

    public String getInputMessage() {
        return inputMessage;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public long getTime() {
        return time;
    }
}
