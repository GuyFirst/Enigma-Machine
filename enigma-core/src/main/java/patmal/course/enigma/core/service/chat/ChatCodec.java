package patmal.course.enigma.core.service.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serializes conversation code parts to/from the flat DB columns:
 * rotor ids "2,3" - positions "E,B" - plugs "A|C,B|E" (each pair stored once).
 */
final class ChatCodec {
    private ChatCodec() {
    }

    static String joinInts(List<Integer> values) {
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    static List<Integer> parseInts(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).map(Integer::parseInt).toList();
    }

    static String joinChars(List<Character> values) {
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    static List<Character> parseChars(String csv) {
        return Arrays.stream(csv.split(",")).map(String::trim).map(s -> s.charAt(0)).toList();
    }

    static String joinPlugs(Map<Character, Character> plugs) {
        // The map holds both directions (A->C and C->A); keep each pair once
        return plugs.entrySet().stream()
                .filter(e -> e.getKey() < e.getValue())
                .map(e -> e.getKey() + "|" + e.getValue())
                .collect(Collectors.joining(","));
    }

    static Map<Character, Character> parsePlugs(String csv) {
        Map<Character, Character> plugs = new LinkedHashMap<>();
        if (csv == null || csv.isBlank()) {
            return plugs;
        }
        for (String pair : csv.split(",")) {
            char a = pair.charAt(0);
            char b = pair.charAt(2);
            plugs.put(a, b);
            plugs.put(b, a);
        }
        return plugs;
    }

    static List<String> plugsAsPairList(String csv) {
        if (csv == null || csv.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(csv.split(",")).toList();
    }
}
