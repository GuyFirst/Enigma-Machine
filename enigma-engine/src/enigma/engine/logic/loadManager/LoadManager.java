package enigma.engine.logic.loadManager;

import enigma.component.keyboard.Keyboard;
import enigma.component.keyboard.KeyboardImpl;
import enigma.component.reflector.Reflector;
import enigma.component.reflector.ReflectorImpl;
import enigma.component.reflector.ReflectedPositionsPair;
import enigma.component.reflector.RomanValues;
import enigma.component.rotor.Rotor;
import enigma.component.rotor.RotorImpl;
import enigma.engine.generated.BTE.classes.*;
import enigma.engine.logic.repository.Repository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

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
        InputStream inputStream = new FileInputStream(filePath); // I deleted the new file because IntelliJ said its redundant maybe bug

        BTEEnigma bteEnigma = deserializeFrom(inputStream);

        String abcForKeyboard = bteEnigma.getABC();
        if (abcForKeyboard.trim().length() % 2 == 1) {
            throw new IllegalArgumentException("The ABC length must be even, but got: " + abcForKeyboard.length() + "chars.");
        }
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
            List<BTEPositioning> btePositioning = bteRotor.getBTEPositioning();
            List<Integer> rightColumn = new ArrayList<>();
            List<Integer> leftColumn = new ArrayList<>();

            for (BTEPositioning btePosition : btePositioning) {
                if (btePosition.getLeft().length() == 1 && btePosition.getRight().length() == 1) {
                    leftColumn.add(keyboard.charToIndex(btePosition.getLeft().charAt(0)));
                    rightColumn.add(keyboard.charToIndex(btePosition.getRight().charAt(0)));
                }
            }
            int alphabet_length = keyboard.getAlphabetLength();
            Rotor rotor = new RotorImpl(rightColumn, leftColumn, notch, alphabet_length);
            rotorMap.put(id, rotor);
        }

        return rotorMap;
    }

    private Map<String, Reflector> createReflectorsMap(BTEReflectors bteReflectors) {

        Map<String, Reflector> reflectorMap = new HashMap<>();
        List<BTEReflector> listOfBTEReflectors = bteReflectors.getBTEReflector();

        RomanValues.clear();
        for (BTEReflector bteReflector : listOfBTEReflectors) {
            List<ReflectedPositionsPair> listOfReflectedPositionsPairs = new ArrayList<>();
            String id = bteReflector.getId();
            if(!RomanValues.romanValues.containsKey(id)){
                throw new IllegalArgumentException("Reflector ID must be a roman value, but got: " + id);
            } else if (RomanValues.checkIfUsed(id)){
                throw new IllegalArgumentException("Reflector ID must be unique, but got a duplicate ID: " + id);
            }
            RomanValues.markAsUsed(id);
             // to ensure no duplicate IDs

            List<BTEReflect> bteReflects = bteReflector.getBTEReflect();

            for (BTEReflect bteReflect : bteReflects) {
                int input = bteReflect.getInput();
                int output = bteReflect.getOutput();
                if(input == output){
                    throw new IllegalArgumentException("Reflector cannot map a position to itself, but got map between " + input + " and " + output + ".");
                }
                listOfReflectedPositionsPairs.add(new ReflectedPositionsPair(--input, --output)); // convert to zero-based index
            }
            Reflector reflector = new ReflectorImpl(id, listOfReflectedPositionsPairs);
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