package enigma.engine.logic;

import enigma.component.reflector.Reflector;
import enigma.component.reflector.ReflectorManager;
import enigma.component.rotor.Rotor;
import enigma.component.rotor.RotorManager;
import enigma.engine.generated.BTE.classes.BTEEnigma;
import enigma.engine.logic.load.manager.LoadManager;
import enigma.engine.logic.repository.Repository;
import enigma.machine.EnigmaMachine;
import enigma.machine.Machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        for (Map.Entry<Integer, Rotor> entry : rotors.entrySet()) {
           System.out.println("Rotor ID: " + entry.getKey());
           System.out.println("Rotor Details: " + entry.getValue());
        }

        for (Map.Entry<String, Reflector> entry : reflectors.entrySet()) {
            System.out.println("Reflector ID: " + entry.getKey());
            System.out.println("Reflector Details: " + entry.getValue());
        }


        String input = "AABBCCDDEEFF";
        List<Integer> rotorIds = new ArrayList<>();
        rotorIds.add(3);
        rotorIds.add(2);
        rotorIds.add(1);

        List<Character> positions = new ArrayList<>();
        positions.add('C');
        positions.add('C');
        positions.add('C');
        String reflectorId = "I";
        engine.setMachineCode(rotorIds, positions, reflectorId);

        System.out.println("Machine is set. \n Input String: " + input + "\n");
        engine.processInput(input);

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

        ReflectorManager reflectorManager = new ReflectorManager(repository.getAllReflectors().get(reflectorId));

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
        this.machine = new EnigmaMachine(reflectorManager, rotorManager, repository.getKeyboard());

    }

    @Override
    public void setAutomaticCode() {

    }

    @Override
    public void processInput(String inputString) {
        StringBuilder outputString = new StringBuilder();
        for (char inputChar : inputString.toCharArray()) {
            char outputChar = machine.encryptChar(inputChar);
            outputString.append(outputChar);
        }
        System.out.println("Output String: " + outputString.toString());
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
        return false;
    }
}
