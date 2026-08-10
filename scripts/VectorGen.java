import patmal.course.enigma.component.reflector.Reflector;
import patmal.course.enigma.component.rotor.Rotor;
import patmal.course.enigma.engine.logic.EngineImpl;
import patmal.course.enigma.engine.logic.dto.EnigmaResult;
import patmal.course.enigma.engine.logic.dto.MachineConfiguration;
import patmal.course.enigma.engine.logic.repository.Repository;
import patmal.course.enigma.loadManager.LoadManager;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

/**
 * Generates test vectors from the Java Enigma implementation (the reference)
 * so the browser port can be verified against it. Emits JSON consumed by
 * frontend/src/enigma/machine.test.js.
 */
public class VectorGen {

    public static void main(String[] args) throws Exception {
        String outPath = args[args.length - 1];
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"machines\": [\n");

        for (int m = 0; m < args.length - 1; m++) {
            if (m > 0) json.append(",\n");
            json.append(machineJson(args[m], new Random(1234 + m)));
        }
        json.append("\n  ]\n}\n");

        try (FileWriter w = new FileWriter(outPath)) {
            w.write(json.toString());
        }
        System.out.println("Wrote " + outPath);
    }

    private static String machineJson(String xmlPath, Random rnd) throws Exception {
        Repository catalog;
        try (FileInputStream in = new FileInputStream(xmlPath)) {
            catalog = new LoadManager().loadMachineSettingsFromXML(in);
        }

        String abc = catalog.getKeyboard().toString();
        StringBuilder sb = new StringBuilder();
        sb.append("    {\n");
        sb.append("      \"name\": \"").append(catalog.getMachineName()).append("\",\n");
        sb.append("      \"wiring\": ").append(wiringJson(catalog, abc)).append(",\n");
        sb.append("      \"cases\": [\n");

        int caseCount = 60;
        for (int c = 0; c < caseCount; c++) {
            if (c > 0) sb.append(",\n");
            sb.append(caseJson(catalog, abc, rnd));
        }
        sb.append("\n      ]\n    }");
        return sb.toString();
    }

    private static String wiringJson(Repository catalog, String abc) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n        \"alphabet\": \"").append(abc).append("\",\n");
        sb.append("        \"rotorsInUse\": ").append(catalog.getNumOfUsedRotorsInMachine()).append(",\n");
        sb.append("        \"rotors\": [");

        List<Integer> rotorIds = new ArrayList<>(catalog.getAllRotors().keySet());
        Collections.sort(rotorIds);
        for (int i = 0; i < rotorIds.size(); i++) {
            Rotor rotor = catalog.getAllRotors().get(rotorIds.get(i));
            if (i > 0) sb.append(",");
            sb.append("\n          {\"id\": ").append(rotor.getId())
                    .append(", \"right\": ").append(intList(rotor.getRightColumn()))
                    .append(", \"left\": ").append(intList(rotor.getLeftColumn()))
                    .append(", \"notch\": ").append(rotor.getNotchPosition())
                    .append("}");
        }
        sb.append("\n        ],\n        \"reflectors\": [");

        List<String> reflectorIds = new ArrayList<>(catalog.getAllReflectors().keySet());
        Collections.sort(reflectorIds);
        for (int i = 0; i < reflectorIds.size(); i++) {
            Reflector reflector = catalog.getAllReflectors().get(reflectorIds.get(i));
            List<Integer> mapping = new ArrayList<>();
            for (int idx = 0; idx < abc.length(); idx++) {
                mapping.add(reflector.reflect(idx));
            }
            if (i > 0) sb.append(",");
            sb.append("\n          {\"id\": \"").append(reflectorIds.get(i))
                    .append("\", \"wiring\": ").append(intList(mapping)).append("}");
        }
        sb.append("\n        ]\n      }");
        return sb.toString();
    }

    private static String caseJson(Repository catalog, String abc, Random rnd) {
        List<Integer> rotorIds = catalog.getRandomRotorIds();
        List<Character> positions = catalog.getRandomPositionsForRotors(rotorIds.size());
        String reflectorId = catalog.getRandomReflectorId();
        Map<Character, Character> plugs = catalog.getRandomPlugboardPairs();

        int len = 1 + rnd.nextInt(40);
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < len; i++) {
            input.append(abc.charAt(rnd.nextInt(abc.length())));
        }

        EnigmaResult result = new EngineImpl().process(input.toString(), catalog,
                MachineConfiguration.builder()
                        .rotorIds(rotorIds)
                        .startingPositions(positions)
                        .reflectorId(reflectorId)
                        .plugs(plugs)
                        .build());

        StringBuilder sb = new StringBuilder();
        sb.append("        {\"rotorIds\": ").append(intList(rotorIds));
        sb.append(", \"positions\": ").append(charList(positions));
        sb.append(", \"reflectorId\": \"").append(reflectorId).append("\"");
        sb.append(", \"plugs\": ").append(plugsJson(plugs));
        sb.append(", \"input\": \"").append(input).append("\"");
        sb.append(", \"output\": \"").append(result.getOutput()).append("\"");
        sb.append(", \"endPositions\": ").append(charList(result.getNewPositions()));
        sb.append("}");
        return sb.toString();
    }

    private static String intList(List<Integer> values) {
        StringJoiner j = new StringJoiner(",", "[", "]");
        values.forEach(v -> j.add(String.valueOf(v)));
        return j.toString();
    }

    private static String charList(List<Character> values) {
        StringJoiner j = new StringJoiner(",", "[", "]");
        values.forEach(v -> j.add("\"" + v + "\""));
        return j.toString();
    }

    private static String plugsJson(Map<Character, Character> plugs) {
        StringJoiner j = new StringJoiner(",", "{", "}");
        plugs.forEach((k, v) -> j.add("\"" + k + "\":\"" + v + "\""));
        return j.toString();
    }
}
