package enigma.component.menu.command.concrete.showHistory;

import enigma.component.menu.command.template.MenuCommandExecutable;
import enigma.engine.DTO.history.HistoryDTO;
import enigma.engine.logic.Engine;
import enigma.engine.logic.history.EnigmaConfiguration;
import enigma.engine.logic.history.EnigmaMessege;

public class DisplayHistoryCommand implements MenuCommandExecutable {

    @Override
    public void execute(Engine engine) throws Exception {
        HistoryDTO history = engine.getHistoryAndStatistics();
        System.out.println("----- Machine Usage History -----");
        for(EnigmaConfiguration config : history.getHistory()) {
            System.out.println(config);
            if(config.getMesseges().isEmpty()) {
                System.out.println("  No messages processed with this configuration.");
            } else {
                System.out.println("  Processed Messages:");
                int numOfMsgs = 1;
                for(EnigmaMessege message : config.getMesseges()) {

                    System.out.println(numOfMsgs + ". " + message);
                }
            }
        }

    }

    @Override
    public String toString() {
        return "Display History Command";
    }
}
