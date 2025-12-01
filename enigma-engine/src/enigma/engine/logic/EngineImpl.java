package enigma.engine.logic;

import enigma.component.reflector.Reflector;
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

    }

    @Override
    public void setAutomaticCode() {

    }

    @Override
    public void processInput(String inputString) {
        //return null;
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
