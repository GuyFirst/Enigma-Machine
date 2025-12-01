package enigma.engine.logic.loadManager;

import enigma.component.keyboard.Keyboard;
import enigma.component.keyboard.KeyboardImpl;
import enigma.component.reflector.Reflector;
import enigma.component.reflector.ReflectorImpl;
import enigma.component.reflector.ReflectorPair;
import enigma.component.rotor.Rotor;
import enigma.component.rotor.RotorImpl;
import enigma.engine.generated.BTE.classes.*;
import enigma.engine.logic.repository.Repository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadManager {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "enigma.engine.generated.BTE.classes";

    private static BTEEnigma deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (BTEEnigma) u.unmarshal(in);
    }

    public Repository loadMachineSettingsFromXML(String filePath) throws JAXBException, FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(filePath));

        BTEEnigma bteEnigma = deserializeFrom(inputStream);

        String abcForKeyboard = bteEnigma.getABC();
        Keyboard keyboard = createKeyboard(abcForKeyboard);

        BTEReflectors bteReflectors = bteEnigma.getBTEReflectors();
        Map<String, Reflector> allReflectors = createReflectorsMap(bteReflectors);

        BTERotors bteRotors = bteEnigma.getBTERotors();
        Map<Integer, Rotor> allRotors = createRotorsMap(bteRotors, keyboard);

        return new Repository(allRotors, allReflectors, keyboard);

    }

    private Map<Integer, Rotor> createRotorsMap(BTERotors bteRotors, Keyboard keyboard) {
        Map<Integer, Rotor> rotorMap = new HashMap<>();
        List<BTERotor> listOfBTERotors = bteRotors.getBTERotor();

        for (BTERotor bteRotor : listOfBTERotors) {
            // get id and notch
            int id = bteRotor.getId();
            int notch = bteRotor.getNotch();

            // create columns
            List<BTEPositioning> btePositionings = bteRotor.getBTEPositioning();
            List<Integer> rightColumn = new ArrayList<>();
            List<Integer> leftColumn = new ArrayList<>();

            for (BTEPositioning btePositioning : btePositionings) {
                if (btePositioning.getLeft().length() == 1 && btePositioning.getRight().length() == 1) {
                    leftColumn.add(keyboard.charToIndex(btePositioning.getLeft().charAt(0)));
                    rightColumn.add(keyboard.charToIndex(btePositioning.getRight().charAt(0)));
                }
            }

            Rotor rotor = new RotorImpl(id, rightColumn, leftColumn, notch);
            rotorMap.put(id, rotor);
        }

        return rotorMap;
    }

    private Map<String, Reflector> createReflectorsMap(BTEReflectors bteReflectors) {

        Map<String, Reflector> reflectorMap = new HashMap<>();
        List<BTEReflector> listOfBTEReflectors = bteReflectors.getBTEReflector();

        for (BTEReflector bteReflector : listOfBTEReflectors) {
            List<ReflectorPair> listOfReflectorPairs = new ArrayList<>();
            String id = bteReflector.getId();
            List<BTEReflect> bteReflects = bteReflector.getBTEReflect();

            for (BTEReflect bteReflect : bteReflects) {
                int input = bteReflect.getInput();
                int output = bteReflect.getOutput();
                listOfReflectorPairs.add(new ReflectorPair(--input, --output));
            }
            Reflector reflector = new ReflectorImpl(id, listOfReflectorPairs);
            reflectorMap.put(id, reflector);
        }

        return reflectorMap;
    }

    private Keyboard createKeyboard(String abcForKeyboard) {
        Map<Character, Integer> mapFromCharToInt = createMapFromCharToInt(abcForKeyboard);
        return new KeyboardImpl(abcForKeyboard, mapFromCharToInt);
    }

    private Map<Character, Integer> createMapFromCharToInt(String abcForKeyboard) {
        abcForKeyboard = abcForKeyboard.trim();
        return java.util.stream.IntStream.range(0, abcForKeyboard.length())
            .boxed()
            .collect(java.util.stream.Collectors.toMap(
                    abcForKeyboard::charAt,            // key: character at index i
                i -> i,                                   // value: index i
                (existing, replacement) -> existing,      // keep first index on duplicates
                java.util.LinkedHashMap::new              // preserve insertion order
            ));
    }

}
