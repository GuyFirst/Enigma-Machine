package enigma.engine.logic;

import enigma.component.reflector.Reflector;
import enigma.component.rotor.Rotor;
import enigma.component.rotor.RotorManager;
import enigma.engine.DTO.history.HistoryDTO;
import enigma.engine.DTO.MachineStatusDTO;
import enigma.engine.logic.history.EnigmaConfiguration;
import enigma.engine.logic.history.EnigmaMessege;
import enigma.engine.logic.history.HistoryManager;
import enigma.engine.logic.history.RotorLetterAndNotch;
import enigma.engine.logic.loadManager.LoadManager;
import enigma.engine.logic.repository.Repository;
import enigma.machine.MachineImpl;
import enigma.machine.Machine;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class EngineImpl implements Engine {
    private Machine machine;
    private final LoadManager loadManager;
    private Repository repository;
    private final HistoryManager historyManager;
    private EnigmaConfiguration initialConfig;
    private EnigmaConfiguration currentConfig;

    public EngineImpl(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.loadManager = new LoadManager();
    }

    @Override
    public void loadMachineFromXml(String xmlFilePath) throws JAXBException, FileNotFoundException {
        this.repository = loadManager.loadMachineSettingsFromXML(xmlFilePath);
    }

    @Override
    public MachineStatusDTO getMachineStatus() {
        if(!isXMLneLoaded())
            throw new IllegalStateException("Machine is not loaded yet. Please load an XML file first.");
        int amountOfRotorInSys = repository.getAllRotors().size();
        int amountOfReflectorsInSys = repository.getAllReflectors().size();
        int amountOfMsgsTillNow = historyManager.getMsgsAmount();
        EnigmaConfiguration currentConfig = this.currentConfig;
        EnigmaConfiguration initialCondig = this.initialConfig;
        boolean isMachineLoaded = isMachineLoaded();

        return new MachineStatusDTO(isMachineLoaded, amountOfRotorInSys, amountOfReflectorsInSys, amountOfMsgsTillNow, currentConfig, initialCondig);
    }


    @Override
    public void setMachineCode(List<Integer> rotorIds, List<Character> positions, String reflectorId) {

        //ReflectorManager reflectorManager = new ReflectorManager(repository.getAllReflectors().get(reflectorId));
        Reflector reflector = repository.getAllReflectors().get(reflectorId);
        List<Rotor> currentRotors = new ArrayList<>();
        for (Integer rotorId : rotorIds) {
            Rotor rotor = repository.getAllRotors().get(rotorId);
            currentRotors.add(rotor);
        }

        List<Integer> positionIndices = new ArrayList<>();
        for (Character posChar : positions) {
            int index = repository.getKeyboard().charToIndex(posChar);
            positionIndices.add(index);
        }

        RotorManager rotorManager = new RotorManager(currentRotors, positionIndices);
        this.machine = new MachineImpl(reflector, rotorManager, repository.getKeyboard());
        this.initialConfig = this.currentConfig = addConfigToHistory(currentRotors, rotorIds, positions, reflectorId);

    }

    private EnigmaConfiguration addConfigToHistory(List<Rotor> currentRotors, List<Integer> rotorIds, List<Character> positions, String reflectorId) {

        List<RotorLetterAndNotch> rotorLetterAndNotches = new ArrayList<>();
        for (int i = 0; i < currentRotors.size(); i++){
            Rotor rotor = currentRotors.get(i);
            int notchPos = rotor.getNotchPosition();
            char topLetter = positions.get(i);
            rotorLetterAndNotches.add(new RotorLetterAndNotch(topLetter, notchPos));
        }

        EnigmaConfiguration configuration = new EnigmaConfiguration(rotorIds, rotorLetterAndNotches, reflectorId);
        historyManager.addConfiguration(configuration);
        return configuration;
    }

    @Override
    public void setAutomaticCode() {
        if(!isXMLneLoaded())
            throw new IllegalStateException("Machine is not loaded yet. Please load an XML file first.");
        // random parameters
        List<Integer> randomRotorIds = repository.getRandomRotorIds();
        List<Character> randomRotorStartPositions = repository.getRandomPositionsForRotors(randomRotorIds.size());
        String randomReflectorId = repository.getRandomReflectorId();

        setMachineCode(randomRotorIds, randomRotorStartPositions, randomReflectorId);
    }

    @Override
    public EnigmaMessege processInput(String inputString) {

        if(!isStringAbleToBeCrypt(inputString)) {
            throw new IllegalArgumentException("Input string contains invalid characters not present in the machine's keyboard.");
        }

        StringBuilder outputString = new StringBuilder();
        long startTime = System.nanoTime();
        for (char inputChar : inputString.toCharArray()) {
            char outputChar = machine.encryptChar(inputChar);
            outputString.append(outputChar);
        }
        long endTime = System.nanoTime();
        long time = endTime - startTime;
        this.currentConfig = getCurrentConfig();
        historyManager.addMessegeToConfiguration(inputString, outputString, time, initialConfig);

        return new EnigmaMessege(inputString, outputString.toString(), time);
    }

    private boolean isStringAbleToBeCrypt(String inputString) {
        for (char c : inputString.toCharArray()) {
            if (!repository.getKeyboard().isValidChar(c)) {
                return false;
            }
        }
        return true;
    }

    private EnigmaConfiguration getCurrentConfig() {
        List<Integer> rotorIds = initialConfig.getRotorIDs();
        String reflectorId = initialConfig.getReflectorID();
        List<RotorLetterAndNotch> rotorLetterAndNotches = new ArrayList<>();
        for(int rotorId : rotorIds){
            Rotor rotor = repository.getAllRotors().get(rotorId);
            int notchPos = rotor.getNotchPosition();
            char topLetter = repository.getKeyboard().indexToChar(rotor.getTopLetter());
            rotorLetterAndNotches.add(new RotorLetterAndNotch(topLetter, notchPos));
        }
        return new EnigmaConfiguration(rotorIds, rotorLetterAndNotches, reflectorId);
    }

    @Override
    public void resetMachineToOriginalCode() {
        String reflectorId = initialConfig.getReflectorID();
        List<Integer> rotorIds = initialConfig.getRotorIDs();
        List<RotorLetterAndNotch> rotorLetterAndNotches = initialConfig.getRotorLetterAndNotch();
        List<Character> positions = new ArrayList<>();
        for (RotorLetterAndNotch rotorLetterAndNotch : rotorLetterAndNotches){
            positions.add(rotorLetterAndNotch.getLetter());
        }
        setMachineCode(rotorIds, positions, reflectorId);
    }

    @Override
    public HistoryDTO getHistoryAndStatistics() {
        List<EnigmaConfiguration> history = historyManager.getHistory();
        return new HistoryDTO(history);
    }

    public boolean isXMLneLoaded() {
        return this.repository != null;
    }

    public boolean isMachineLoaded() {
        return this.machine != null;
    }

    @Override
    public void exit() {
        System.exit(404);
    }
}
