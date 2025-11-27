package DTO;

public class MachineSpecificationDTO {
    private final int numOfRotorsInSystem;
    private final int numOfReflectorsInSystem;
    private final int numOfReceivedMessages;
    private final CodeSpecDTO initialCodeSpec;
    private final CodeSpecDTO currentCodeSpec;

    public MachineSpecificationDTO(int numOfRotors, int numOfReflectors, int numOfReceivedMessages,
                                   CodeSpecDTO initialCodeSpec, CodeSpecDTO currentCodeSpec) {
        this.numOfRotorsInSystem = numOfRotors;
        this.numOfReflectorsInSystem = numOfReflectors;
        this.numOfReceivedMessages = numOfReceivedMessages;
        this.initialCodeSpec = initialCodeSpec;
        this.currentCodeSpec = currentCodeSpec;
    }

    public int getNumOfRotorsInSystem() {
        return numOfRotorsInSystem;
    }

    public int getNumOfReflectorsInSystem() {
        return numOfReflectorsInSystem;
    }

    public int getNumOfReceivedMessages() {
        return numOfReceivedMessages;
    }

    public CodeSpecDTO getInitialCodeSpec() {
        return initialCodeSpec;
    }

    public CodeSpecDTO getCurrentCodeSpec() {
        return currentCodeSpec;
    }

}
