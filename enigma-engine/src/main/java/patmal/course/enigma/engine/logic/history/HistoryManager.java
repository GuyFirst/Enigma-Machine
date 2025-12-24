package patmal.course.enigma.engine.logic.history;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager implements Serializable {
    private final List<EnigmaConfiguration> configurations = new ArrayList<>();

    public List<EnigmaConfiguration> getHistory() {
        return configurations;
    }

    public void addConfiguration(EnigmaConfiguration configuration) {
        for (EnigmaConfiguration config : configurations) {
            if (config.equals(configuration)) {
                return;
            }
        }
        configurations.add(configuration);
    }

    public void addMessageToConfiguration(String inputString, StringBuilder outputString, long time, EnigmaConfiguration currentConfig) {

        for (EnigmaConfiguration configuration : configurations){
            if (configuration.equals(currentConfig)) {
                configuration.addMessage(inputString, outputString.toString(), time);
            }
        }

    }

    public int getMsgsAmount() {
        int sum = 0;
        for (EnigmaConfiguration configuration : configurations){
            sum += configuration.getMessages().size();
        }
        return sum;
    }
}
