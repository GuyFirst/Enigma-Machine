package enigma.engine.logic;

import enigma.component.reflector.Reflector;
import enigma.component.rotor.Rotor;
import enigma.component.rotor.RotorManager;
import enigma.engine.DTO.history.HistoryDTO;
import enigma.engine.DTO.MachineStatusDTO;
import enigma.engine.logic.history.EnigmaConfiguration;
import enigma.engine.logic.history.EnigmaMessage;
import enigma.engine.logic.history.HistoryManager;
import enigma.engine.logic.history.RotorLetterAndNotch;
import enigma.engine.logic.loadManager.LoadManager;
import enigma.engine.logic.repository.Repository;
import enigma.machine.MachineImpl;
import enigma.machine.Machine;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class EngineImpl implements Engine {
    private Machine machine;
    private final LoadManager loadManager;
    private Repository repository;
    private HistoryManager historyManager;
    private EnigmaConfiguration initialConfig;
    private EnigmaConfiguration currentConfig;
    public static int NUM_OF_MINIMUM_ROTOR_IN_SYSTEM = 3;

    public EngineImpl() {
        this.historyManager = new HistoryManager();
        this.loadManager = new LoadManager();
    }

    @Override
    public void loadMachineFromXml(String xmlFilePath) throws JAXBException, FileNotFoundException {
        this.repository = loadManager.loadMachineSettingsFromXML(xmlFilePath);
    }

    @Override
    public MachineStatusDTO getMachineStatus() {
        if(!isXMLLoaded())
            throw new IllegalStateException("XML file is not loaded. Please configure the system via XML configuration or loading an existing state via file.");
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
        if (!repository.getAllReflectors().containsKey(reflectorId)) {
            throw new IllegalArgumentException("Invalid reflector ID: " + reflectorId);
        }
        Reflector reflector = repository.getAllReflectors().get(reflectorId);
        List<Rotor> currentRotors = new ArrayList<>();
        for (Integer rotorId : rotorIds) {
            if (!repository.getAllRotors().containsKey(rotorId)) {
                throw new IllegalArgumentException("Invalid rotor ID: " + rotorId);
            }
            Rotor rotor = repository.getAllRotors().get(rotorId);
            currentRotors.add(rotor);
        }

        List<Integer> positionIndices = new ArrayList<>();
        for (Character posChar : positions) {
            if (!repository.getKeyboard().isValidChar(posChar)) {
                throw new IllegalArgumentException("Invalid rotor position: " + posChar);
            }
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
        if(!isXMLLoaded())
            throw new IllegalStateException("Machine is not loaded yet. Please load an XML file first.");
        // random parameters
        List<Integer> randomRotorIds = repository.getRandomRotorIds();
        List<Character> randomRotorStartPositions = repository.getRandomPositionsForRotors(randomRotorIds.size());
        String randomReflectorId = repository.getRandomReflectorId();

        setMachineCode(randomRotorIds, randomRotorStartPositions, randomReflectorId);
    }

    @Override
    public EnigmaMessage processInput(String inputString) {

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
        historyManager.addMessageToConfiguration(inputString, outputString, time, initialConfig);

        return new EnigmaMessage(inputString, outputString.toString(), time);
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

    @Override
    public boolean isXMLLoaded() {
        return this.repository != null;
    }

    @Override
    public void saveCurrentSystemStateToFile(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(machine);
            out.writeObject(historyManager);
            out.writeObject(repository);
            out.writeObject(initialConfig);
            out.writeObject(currentConfig);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadSystemStateFromFile(String fileName) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.machine = (Machine) in.readObject();
            this.historyManager = (HistoryManager)in.readObject();
            this.repository = (Repository) in.readObject();
            this.initialConfig = (EnigmaConfiguration) in.readObject();
            this.currentConfig = (EnigmaConfiguration) in.readObject();

        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to load system state from file: " + e.getMessage(), e);
        }

    }

    public boolean isMachineLoaded() {
        return this.machine != null;
    }

    @Override
    public void exit() {
        System.exit(404);
    }
}
