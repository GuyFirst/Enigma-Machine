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
import java.util.*;

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

        List<BTERotor> listOfBTERotors = bteRotors.getBTERotor();
        if(listOfBTERotors.size() < RotorImpl.NUM_OF_MINIMUM_ROTOR_IN_SYSTEM){
            throw new IllegalArgumentException("The machine must contain at least three rotors.");
        }
        Map<Integer, Rotor> rotorMap = new HashMap<>();
        Set<Integer> idSet = new HashSet<>();
        for (BTERotor bteRotor : listOfBTERotors) {
            // get id and notch
            int id = bteRotor.getId();
            if(idSet.contains(id)){
                throw new IllegalArgumentException("Rotor ID must be unique, but got a duplicate ID: " + id);
            }
            idSet.add(id); // to ensure no duplicate IDs
            int notch = bteRotor.getNotch();
            if(notch < 0 || notch >= keyboard.getAlphabetLength()){
                int size = keyboard.getAlphabetLength() - 1;
                throw new IllegalArgumentException("Rotor notch must be between 0 and the length of the abc size minus one, which is currently " + size + ", but got " + notch + " for Rotor ID " + id + ".");
            } //maybe off by one bug?


            // create columns
            List<BTEPositioning> btePositioning = bteRotor.getBTEPositioning();
            List<Integer> rightColumn = new ArrayList<>();
            List<Integer> leftColumn = new ArrayList<>();

            List<Character> abcInLeftColumn = new ArrayList<>(keyboard.toString()
                    .chars()
                    .mapToObj(c -> (char) c)
                    .toList());

            List<Character> abcInRightColumn = new ArrayList<>(keyboard.toString()
                    .chars()
                    .mapToObj(c -> (char) c)
                    .toList());

            for (BTEPositioning btePosition : btePositioning) {
                if (btePosition.getLeft().length() == 1 && btePosition.getRight().length() == 1) {
                    if(!keyboard.isValidChar(btePosition.getLeft().charAt(0))){
                        throw new IllegalArgumentException("Rotor ID " + id + " character in the left column '" + btePosition.getLeft().charAt(0) + "' is not in the keyboard allowed characters, which is currently: " + keyboard.toString() + ".");
                    }
                    if(!keyboard.isValidChar(btePosition.getRight().charAt(0))){
                        throw new IllegalArgumentException("Rotor ID " + id + " character in the right column '" + btePosition.getRight().charAt(0) + "' is not in the keyboard allowed characters, which is currently: " + keyboard.toString() + ".");
                    }
                    leftColumn.add(keyboard.charToIndex(btePosition.getLeft().charAt(0)));
                    if(!abcInLeftColumn.contains(btePosition.getLeft().charAt(0))){
                        throw new IllegalArgumentException("Rotor ID " + id + " character in the left column '" + btePosition.getLeft().charAt(0) + "' is mapped more than once.");
                    } else {
                        abcInLeftColumn.remove((Character) btePosition.getLeft().charAt(0));
                    }
                    rightColumn.add(keyboard.charToIndex(btePosition.getRight().charAt(0)));
                    if(!abcInRightColumn.contains(btePosition.getRight().charAt(0))){
                        throw new IllegalArgumentException("Rotor ID " + id + " character in the right column '" + btePosition.getRight().charAt(0) + "' is mapped more than once.");
                    } else {
                        abcInRightColumn.remove((Character) btePosition.getRight().charAt(0));
                    }
                }
            }

            int alphabet_length = keyboard.getAlphabetLength();
            Rotor rotor = new RotorImpl(rightColumn, leftColumn, notch, alphabet_length);
            rotorMap.put(id, rotor);
        }

        for(int i = 1; i <= rotorMap.size(); i++){
            if(!rotorMap.containsKey(i)){
                throw new IllegalArgumentException("Rotor IDs must be consecutive integers starting from 1 to " + rotorMap.size() + ", but missing ID: " + i);
            }
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