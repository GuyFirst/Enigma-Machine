package patmal.course.enigma.engine.logic;

import patmal.course.enigma.component.plugboard.PlugboardImpl;
import patmal.course.enigma.component.rotor.Rotor;
import patmal.course.enigma.component.rotor.RotorManager;
import patmal.course.enigma.engine.logic.dto.MachineConfiguration;
import patmal.course.enigma.engine.logic.repository.Repository;
import patmal.course.enigma.machine.Machine;
import patmal.course.enigma.machine.MachineImpl;

import java.util.List;

/**
 * Builds a ready-to-run Machine from the fixed catalog (Repository) and a
 * per-use configuration. Rotors are CLONED from the catalog blueprints so that
 * stepping them during encryption never mutates the shared catalog objects.
 */
public final class MachineFactory {
    private MachineFactory() {
    }

    public static Machine build(Repository repository, MachineConfiguration config) {
        List<Rotor> rotors = config.getRotorIds().stream()
                .map(id -> repository.getAllRotors().get(id).cloneRotor())
                .toList();

        List<Integer> positionIndices = config.getStartingPositions().stream()
                .map(c -> repository.getKeyboard().charToIndex(c))
                .toList();

        RotorManager rotorManager = new RotorManager(rotors, positionIndices, config.getRotorIds());

        return new MachineImpl(
                repository.getAllReflectors().get(config.getReflectorId()),
                rotorManager,
                repository.getKeyboard(),
                new PlugboardImpl(config.getPlugs())
        );
    }
}
