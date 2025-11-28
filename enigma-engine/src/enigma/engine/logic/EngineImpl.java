package enigma.engine.logic;

import enigma.machine.Machine;

import java.util.List;

public class EngineImpl implements Engine {
    private Machine machine;
    // private LoadManager loadManager;
    // private HistoryManager historyManager;
    // private Repository repository;

    @Override
    public void loadMachineFromXml(String xmlFilePath) {
        // when repo is ready this.machine = createMachine(...);
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
