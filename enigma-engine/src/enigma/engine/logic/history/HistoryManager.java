package enigma.engine.logic.history;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private final List<EnigmaConfiguration> configurations = new ArrayList<>();

    public List<EnigmaConfiguration> getHistory() {
        return configurations;
    }

    public void addConfiguration(EnigmaConfiguration configuration) {
        if (!configurations.contains(configuration)) {
            configurations.add(configuration);
        }
    }

    public void addMessegeToConfiguration(String inputString, StringBuilder outputString, long time, EnigmaConfiguration currentConfig) {

        for (EnigmaConfiguration configuration : configurations){
            if (configuration.equals(currentConfig)) {
                configuration.addMessege(inputString, outputString.toString(), time);
            }
        }

    }

    public int getMsgsAmount() {
        int sum = 0;
        for (EnigmaConfiguration configuration : configurations){
            sum += configuration.getMesseges().size();
        }
        return sum;
    }
}
