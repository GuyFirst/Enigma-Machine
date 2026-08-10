package patmal.course.enigma.core.service.chat;

import org.springframework.stereotype.Service;
import patmal.course.enigma.component.keyboard.Keyboard;
import patmal.course.enigma.engine.logic.MachineFactory;
import patmal.course.enigma.engine.logic.dto.MachineConfiguration;
import patmal.course.enigma.engine.logic.repository.Repository;
import patmal.course.enigma.machine.Machine;

import java.util.List;
import java.util.Map;

/**
 * Chat-oriented wrapper around the Enigma engine. Unlike the raw engine, it
 * PASSES THROUGH characters that are not in the machine's alphabet (spaces,
 * punctuation, digits...) without stepping the rotors - so free-form chat text
 * works. Since passthrough is applied identically on encrypt and decrypt, the
 * Enigma reciprocity property is preserved: running the ciphertext through the
 * machine at the same starting positions yields the plaintext back.
 */
@Service
public class ChatCryptoService {

    public record Result(String text, List<Character> newPositions) {
    }

    public Result process(Repository catalog,
                          List<Integer> rotorIds,
                          List<Character> positions,
                          String reflectorId,
                          Map<Character, Character> plugs,
                          String input) {
        MachineConfiguration config = MachineConfiguration.builder()
                .rotorIds(rotorIds)
                .startingPositions(positions)
                .reflectorId(reflectorId)
                .plugs(plugs)
                .build();
        Machine machine = MachineFactory.build(catalog, config);
        Keyboard keyboard = catalog.getKeyboard();

        StringBuilder out = new StringBuilder(input.length());
        for (char c : input.toUpperCase().toCharArray()) {
            out.append(keyboard.isValidChar(c) ? machine.encryptChar(c) : c);
        }
        return new Result(out.toString(), machine.getRotorManager().getCurrentPositions());
    }
}
