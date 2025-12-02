package enigma.engine.DTO.history;

import enigma.engine.logic.history.EnigmaConfiguration;

public class MachineStatusDTO {
    private final int amountOfRotorInSys;
    private final int amountOfReflectorsInSys;
    private final int amountOfMsgsTillNow;
    private final EnigmaConfiguration currentConfig;
    private final EnigmaConfiguration initialConfig;

    public MachineStatusDTO(int amountOfRotorInSys, int amountOfReflectorsInSys, int amountOfMsgsTillNow, EnigmaConfiguration currentConfig, EnigmaConfiguration initialConfig) {
        this.amountOfRotorInSys = amountOfRotorInSys;
        this.amountOfReflectorsInSys = amountOfReflectorsInSys;
        this.amountOfMsgsTillNow = amountOfMsgsTillNow;
        this.currentConfig = currentConfig;
        this.initialConfig = initialConfig;
    }

    public int getAmountOfRotorInSys() {
        return amountOfRotorInSys;
    }

    public int getAmountOfReflectorsInSys() {
        return amountOfReflectorsInSys;
    }

    public int getAmountOfMsgsTillNow() {
        return amountOfMsgsTillNow;
    }

    public EnigmaConfiguration getCurrentConfig() {
        return currentConfig;
    }

    public EnigmaConfiguration getInitialConfig() {
        return initialConfig;
    }

}
