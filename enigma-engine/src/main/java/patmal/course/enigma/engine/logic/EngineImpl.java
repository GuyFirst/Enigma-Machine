package patmal.course.enigma.engine.logic;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import patmal.course.enigma.component.keyboard.Keyboard;
import patmal.course.enigma.component.plugboard.PlugboardImpl;
import patmal.course.enigma.component.reflector.Reflector;
import patmal.course.enigma.component.rotor.Rotor;
import patmal.course.enigma.component.rotor.RotorManager;
import patmal.course.enigma.engine.DTO.MachineStatusDTO;
import patmal.course.enigma.engine.DTO.history.HistoryDTO;
import patmal.course.enigma.engine.logic.history.EnigmaConfiguration;
import patmal.course.enigma.engine.logic.history.EnigmaMessage;
import patmal.course.enigma.engine.logic.history.HistoryManager;
import patmal.course.enigma.engine.logic.loadManager.LoadManager;
import patmal.course.enigma.engine.logic.repository.Repository;
import patmal.course.enigma.machine.Machine;
import patmal.course.enigma.machine.MachineImpl;
import patmal.course.enigma.engine.logic.history.RotorLetterAndNotch;


import java.io.*;
import java.util.*;



public class EngineImpl implements Engine {
    private Machine machine;
    private final LoadManager loadManager;
    private Repository repository;
    private HistoryManager historyManager;
    private EnigmaConfiguration initialConfig;
    private EnigmaConfiguration currentConfig;
    public static int numOfUsedRotorsInMachine;
    private Map<Character , Character> plugBoardConfig;
    public static final Logger logger = LogManager.getLogger(EngineImpl.class);

    public EngineImpl() {
        this.historyManager = new HistoryManager();
        this.loadManager = new LoadManager();
    }

    @Override
    public void loadMachineFromXml(String xmlFilePath) throws JAXBException, FileNotFoundException {
        logger.info("loading machine configuration from XML file: {}", xmlFilePath);
        this.repository = loadManager.loadMachineSettingsFromXML(xmlFilePath);
        logger.info("machine configuration loaded successfully from XML file: {}", xmlFilePath);
    }

    @Override
    public MachineStatusDTO getMachineStatus() {
        if(!isXMLLoaded())
            throw new IllegalStateException("XML file is not loaded. Please configure the system via XML configuration or loading an existing state via file.");
        int amountOfRotorInSys = repository.getAllRotors().size();
        int amountOfReflectorsInSys = repository.getAllReflectors().size();
        int amountOfMsgsTillNow = historyManager.getMsgsAmount();
        EnigmaConfiguration currentConfig = this.currentConfig;
        EnigmaConfiguration initialConfig = this.initialConfig;
        boolean isMachineLoaded = isMachineLoaded();

        return new MachineStatusDTO(isMachineLoaded, amountOfRotorInSys, amountOfReflectorsInSys, amountOfMsgsTillNow, currentConfig, initialConfig);
    }


    @Override
    public void setMachineCode(List<Integer> rotorIds, List<Character> positions, String reflectorId, Map<Character, Character> plugBoardConfig) {
        
        logger.info("Setting machine code with reflector ID: {}, rotor IDs: {}, positions of rotors: {}, plugboard config: {}",
                reflectorId, rotorIds, positions, formatPlugBoardConfig(plugBoardConfig));
        
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

        if(plugBoardConfigNotValid(plugBoardConfig, repository.getKeyboard())) {
            throw new IllegalArgumentException("Invalid plugBoard configuration.");
        }

        RotorManager rotorManager = new RotorManager(currentRotors, positionIndices, rotorIds);
        this.plugBoardConfig = plugBoardConfig;
        this.machine = new MachineImpl(reflector, rotorManager, repository.getKeyboard(), new PlugboardImpl(plugBoardConfig));

        logger.debug("adding machine configuration into history...");
        logger.debug("setting initial code and current code as the provided configuration...");
        this.initialConfig = this.currentConfig = addConfigToHistory(currentRotors, rotorIds, positions, reflectorId, plugBoardConfig);

        logger.info("Machine code set successfully.");
    }

    private boolean plugBoardConfigNotValid(Map<Character, Character> plugBoardConfig, Keyboard keyboard) {
        Set<Character> validatedChars = new HashSet<>();

        for (Map.Entry<Character, Character> entry : plugBoardConfig.entrySet()) {
            char charA = entry.getKey();
            char charB = entry.getValue();
            // Since the map contains A->B and B->A, we skip the entry where the key is alphabetically larger.
            if (charA > charB) {
                continue;
            }

            if (!keyboard.isValidChar(charA)) {
                logger.error("Invalid plugboard configuration. Character outside of alphabet: {}", charA);
                return true;
            }
            if (!keyboard.isValidChar(charB)) {
                logger.error("Invalid plugboard configuration. Character outside of alphabet: {}", charB);
                return true;
            }
            // 2. Check for self-mapping (A -> A)
            if (charA == charB) {
                logger.error("Invalid plugboard configuration. Self-mapping detected for character: {}", charA);
                // This case should ideally be caught in the UI layer, but checked here for safety.
                return true;
            }

            if(validatedChars.contains(charA)) {
                logger.error("Invalid plugboard configuration. Character already mapped: {}", charA);
                return true;
            }

            if(validatedChars.contains(charB)) {
                logger.error("Invalid plugboard configuration. Character already mapped: {}", charB);
                return true;
            }

            // Mark the connection as validated and used
            validatedChars.add(charA);
            validatedChars.add(charB);
            logger.trace("Validated plugboard connection: {} <-> {}", charA, charB);
        }

        // If the loop completes without returning true, the configuration is valid.
        return false;
    }

    private EnigmaConfiguration addConfigToHistory(List<Rotor> currentRotors, List<Integer> rotorIds, List<Character> positions, String reflectorId, Map<Character, Character> plugBoardConfig) {

        List<RotorLetterAndNotch> rotorLetterAndNotches = new ArrayList<>();
        for (int i = 0; i < currentRotors.size(); i++){
            Rotor rotor = currentRotors.get(i);
            int notchPos = rotor.getNotchPosition();
            char topLetter = positions.get(i);
            rotorLetterAndNotches.add(new RotorLetterAndNotch(topLetter, notchPos));
        }

        EnigmaConfiguration configuration = new EnigmaConfiguration(rotorIds, rotorLetterAndNotches, reflectorId, plugBoardConfig);
        historyManager.addConfiguration(configuration);
        return configuration;
    }

    @Override
    public void setAutomaticCode() {
        logger.info("Setting automatic machine code...");
        if(!isXMLLoaded())
            throw new IllegalStateException("Machine is not loaded yet. Please load an XML file first.");
        // random parameters
        List<Integer> randomRotorIds = repository.getRandomRotorIds();
        logger.debug("Randomly selected rotor IDs: {}", randomRotorIds);

        List<Character> randomRotorStartPositions = repository.getRandomPositionsForRotors(randomRotorIds.size());
        logger.debug("Randomly selected rotor starting positions: {}", randomRotorStartPositions);
        String randomReflectorId = repository.getRandomReflectorId();
        logger.debug("Randomly selected reflector ID: {}", randomReflectorId);
        Map<Character, Character> plugBoardConfig = createRandomPlugBoardConfig();
        logger.debug("Randomly generated plugboard configuration: {}", formatPlugBoardConfig(plugBoardConfig));
        setMachineCode(randomRotorIds, randomRotorStartPositions, randomReflectorId, plugBoardConfig);
    }

    private Map<Character, Character> createRandomPlugBoardConfig() {

        String alphabet = repository.getKeyboard().toString();
        Random random = new Random();
        Map<Character, Character> plugboardMap = new HashMap<>();

        List<Character> availableChars = new ArrayList<>();
        for (char c : alphabet.toCharArray()) {
            availableChars.add(c);
        }

        int maxNumOfPairs = availableChars.size() / 2;
        int randomNumOfPairs = random.nextInt(maxNumOfPairs + 1); // +1 to include the maximum

        logger.debug("Generating Plugboard with {} pairs...", randomNumOfPairs);
        // 4. Loop N times to create the pairs
        for (int i = 0; i < randomNumOfPairs; i++) {

            int indexA = random.nextInt(availableChars.size());
            Character charA = availableChars.remove(indexA);

            int indexB = random.nextInt(availableChars.size());
            Character charB = availableChars.remove(indexB);

            plugboardMap.put(charA, charB);
            plugboardMap.put(charB, charA);
            logger.trace("Created plugboard pair: {} <-> {}", charA, charB);

        }
        return plugboardMap;
    }

    @Override
    public EnigmaMessage processInput(String inputString) {

        logger.info("Processing input string: {}", inputString);
        if(!isStringAbleToBeCrypt(inputString)) {
            throw new IllegalArgumentException("Input string contains invalid characters not present in the machine's keyboard.");
        }

        StringBuilder outputString = new StringBuilder();
        long startTime = System.nanoTime();

        for (char inputChar : inputString.toCharArray()) {
            char outputChar = machine.encryptChar(inputChar);
            outputString.append(outputChar);
            logger.debug("Encrypted character: {} --> {}", inputChar, outputChar);
        }
        long endTime = System.nanoTime();
        long time = endTime - startTime;
        this.currentConfig = getCurrentConfig();
        historyManager.addMessageToConfiguration(inputString, outputString, time, initialConfig);

        logger.info("Input processing complete. Output string: {}, Time taken (ns): {}", outputString, time);
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
        return new EnigmaConfiguration(rotorIds, rotorLetterAndNotches, reflectorId, this.plugBoardConfig);
    }

    @Override
    public void resetMachineToOriginalCode() {
        logger.info("Resetting machine to original code...");
        String reflectorId = initialConfig.getReflectorID();
        List<Integer> rotorIds = initialConfig.getRotorIDs();
        List<RotorLetterAndNotch> rotorLetterAndNotches = initialConfig.getRotorLetterAndNotch();
        List<Character> positions = new ArrayList<>();
        for (RotorLetterAndNotch rotorLetterAndNotch : rotorLetterAndNotches){
            positions.add(rotorLetterAndNotch.getLetter());
        }

        setMachineCode(rotorIds, positions, reflectorId, this.plugBoardConfig);
        logger.info("Machine reset to original code successfully.");
    }

    @Override
    public HistoryDTO getHistoryAndStatistics() {
        logger.info("Retrieving history and statistics...");
        List<EnigmaConfiguration> history = historyManager.getHistory();
        return new HistoryDTO(history);
    }

    @Override
    public boolean isXMLLoaded() {
        return this.repository != null;
    }

    @Override
    public void saveCurrentSystemStateToFile(String fileName) {
        logger.info("Saving current system state to file: {}", fileName);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            logger.debug("writing machine to file...");
            out.writeObject(machine);
            logger.debug("writing history manager to file...");
            out.writeObject(historyManager);
            logger.debug("writing repository to file...");
            out.writeObject(repository);
            logger.debug("writing initial configurations to file...");
            out.writeObject(initialConfig);
            logger.debug("writing current configurations to file...");
            out.writeObject(currentConfig);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("System state saved successfully to file: {}", fileName);
    }

    @Override
    public void loadSystemStateFromFile(String fileName) throws IOException {
        logger.info("Loading system state from file: {}", fileName);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            logger.debug("reading machine state from file...");
            this.machine = (Machine) in.readObject();
            logger.debug("reading history manager state from file...");
            this.historyManager = (HistoryManager)in.readObject();
            logger.debug("reading repository state from file...");
            this.repository = (Repository) in.readObject();
            logger.debug("reading initial configuration from file...");
            this.initialConfig = (EnigmaConfiguration) in.readObject();
            logger.debug("reading current configuration from file...");
            this.currentConfig = (EnigmaConfiguration) in.readObject();

        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to load system state from file: " + e.getMessage(), e);
        }
        logger.info("System state loaded successfully from file: {}", fileName);
    }

    public boolean isMachineLoaded() {
        return this.machine != null;
    }

    @Override
    public void exit() {
        System.exit(404);
    }

    @Override
    public void setLogLevel(String level) {
        Level newLevel = Level.toLevel(level.toUpperCase(), null);
        if (newLevel == null) {
            logger.error("Invalid log level provided: {}", level);
            throw new IllegalArgumentException("Invalid log level: " + level);
        }
        
        LoggerContext ctx = (LoggerContext) LogManager.getContext(EngineImpl.class.getClassLoader(), false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(newLevel);
        ctx.updateLoggers();
        
        logger.info("Log level changed to: {}", newLevel);
    }

    private String formatPlugBoardConfig(Map<Character, Character> plugBoardConfig) {
        if (plugBoardConfig == null || plugBoardConfig.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        Set<Character> processed = new HashSet<>();
        List<Character> keys = new ArrayList<>(plugBoardConfig.keySet());
        Collections.sort(keys);

        boolean first = true;
        for (Character key : keys) {
            if (processed.contains(key)) {
                continue;
            }
            Character value = plugBoardConfig.get(key);
            processed.add(key);
            if (value != null) {
                processed.add(value);
            }

            if (!first) {
                sb.append(", ");
            }
            sb.append(key).append("=").append(value);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}