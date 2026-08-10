package patmal.course.enigma.engine.logic;

import org.springframework.stereotype.Service;
import patmal.course.enigma.engine.logic.dto.EnigmaResult;
import patmal.course.enigma.engine.logic.dto.MachineConfiguration;
import patmal.course.enigma.engine.logic.repository.Repository;
import patmal.course.enigma.machine.Machine;

import java.util.List;

@Service
public class EngineImpl implements Engine {

    public EnigmaResult process(String input, Repository repository, MachineConfiguration config) {
        // Build machine from provided config
        Machine machine = buildMachine(repository, config);

        StringBuilder output = new StringBuilder();
        long startTime = System.nanoTime();

        // Encrypt
        for (char c : input.toCharArray()) {
            output.append(machine.encryptChar(c));
        }

        long duration = System.nanoTime() - startTime;

        // Capture the new positions after rotors moved
        List<Character> newPositions = machine.getRotorManager().getCurrentPositions();

        return new EnigmaResult(input, output.toString(), newPositions, duration);
    }

    private Machine buildMachine(Repository repository, MachineConfiguration config) {
        // Extracted to MachineFactory so other flows (e.g. chat) can build machines too
        return MachineFactory.build(repository, config);
    }
}