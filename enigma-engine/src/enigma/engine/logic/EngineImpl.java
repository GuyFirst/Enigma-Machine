package enigma.engine.logic;

import enigma.component.rotor.Rotor;
import enigma.engine.generated.BTE.classes.BTEEnigma;
import enigma.engine.logic.load.manager.LoadManager;
import enigma.engine.logic.repository.Repository;
import enigma.machine.Machine;

import java.util.List;
import java.util.Map;

public class EngineImpl implements Engine {
    private Machine machine;
    private LoadManager loadManager;
    // private HistoryManager historyManager;
    private Repository repository;

    public EngineImpl() {
        this.loadManager = new LoadManager();
        // this.historyManager = new HistoryManager();
        // this.repository = new Repository();
    }
    @Override
    public void loadMachineFromXml(String xmlFilePath) {
        try {
            this.repository = loadManager.loadMachineSettingsFromXML(xmlFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public MachineSpecificationDTO getMachineStatus() {
        return null;
    }

    @Override
    public void setMachineCode(List<Integer> rotorIds, List<Character> positions, String reflectorId) {

    }

    @Override
    public void setAutomaticCode() {

    }

    @Override
    public MessageDTO processInput(String inputString) {
        return null;
    }

    @Override
    public void resetMachineToOriginalCode() {

    }

    @Override
    public HistoryDTO getHistoryAndStatistics() {
        return null;
    }

    @Override
    public boolean isMachineLoaded() {
        return false;
    }
}
