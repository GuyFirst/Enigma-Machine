package patmal.course.enigma.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import patmal.course.enigma.service.LoadService;

import java.io.InputStream;

/**
 * Seeds the preset Enigma machines into the DB on startup so the chat app
 * always has machines to offer. Idempotent: an already-present machine throws
 * the LoadService duplicate error, which we treat as "nothing to do".
 */
@Component
public class PresetMachineSeeder implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(PresetMachineSeeder.class);
    private static final String[] PRESETS = {
            "preset-machines/enigma-i.xml",
            "preset-machines/sanity-small.xml"
    };

    private final LoadService loadService;

    public PresetMachineSeeder(LoadService loadService) {
        this.loadService = loadService;
    }

    @Override
    public void run(String... args) {
        for (String preset : PRESETS) {
            try (InputStream in = new ClassPathResource(preset).getInputStream()) {
                String name = loadService.handleXmlImport(in);
                logger.info("Seeded preset machine '{}' from {}", name, preset);
            } catch (IllegalArgumentException e) {
                logger.info("Preset {} already present ({})", preset, e.getMessage());
            } catch (Exception e) {
                logger.error("Failed to seed preset {}: {}", preset, e.getMessage());
            }
        }
    }
}
