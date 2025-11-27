package engine.logic;

import DTO.HistoryDTO;
import DTO.MachineSpecificationDTO;
import DTO.MessageDTO;
import enigmaMachine.EnigmaMachine;

import java.util.List;

public class EngineImpl implements Engine {
    private final EnigmaMachine enigmaMachine;

    public EngineImpl(EnigmaMachine enigmaMachine) {
        this.enigmaMachine = enigmaMachine;
    }

    @Override
    public void loadMachineFromXml(String xmlFilePath) {

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
