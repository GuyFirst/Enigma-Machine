package enigma.engine.logic;

import enigma.component.reflector.Reflector;
import enigma.component.rotor.Rotor;
import enigma.component.rotor.RotorManager;
import enigma.engine.logic.loadManager.LoadManager;
import enigma.engine.logic.repository.Repository;
import enigma.machine.MachineImpl;
import enigma.machine.Machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EngineImpl implements Engine {
    private Machine machine;
    private LoadManager loadManager;
    public Repository repository;
    // private HistoryManager historyManager;

    public static void main(String[] args) {
        EngineImpl engine = new EngineImpl();
        engine.loadMachineFromXml("enigma-engine/src/enigma/resource/ex1-sanity-small.xml");
        Repository repo = engine.repository;
        Map<Integer, Rotor> rotors = repo.getAllRotors();
        Map<String, Reflector> reflectors = repo.getAllReflectors();


        String input = "AABBCCDDEEFF";

        engine.setAutomaticCode();

        System.out.println("Machine is set. \n Input String: " + input + "\n");
        String output = engine.processInput(input);

        System.out.println("Output String: " + output + "\n");

    }

    public EngineImpl() {
        this.loadManager = new LoadManager();
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
    public void getMachineStatus() {
        //return null;
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

    }

    @Override
    public void setAutomaticCode() {
        // random parameters
        List<Integer> randomRotorIds = repository.getRandomRotorIds();
        List<Character> randomRotorStartPositions = repository.getRandomPositionsForRotors(randomRotorIds.size());
        String randomReflectorId = repository.getRandomReflectorId();

        setMachineCode(randomRotorIds, randomRotorStartPositions, randomReflectorId);
    }

    @Override
    public String processInput(String inputString) {
        StringBuilder outputString = new StringBuilder();
        for (char inputChar : inputString.toCharArray()) {
            char outputChar = machine.encryptChar(inputChar);
            outputString.append(outputChar);
        }
        return outputString.toString();
    }

    @Override
    public void resetMachineToOriginalCode() {

    }

    @Override
    public void getHistoryAndStatistics() {
        //return null;
    }

    @Override
    public boolean isMachineLoaded() {
        return this.machine != null;
    }

    @Override
    public void exit() {
        System.out.println("Machine has been exited. please get a life");
        System.exit(404);
    }
}
