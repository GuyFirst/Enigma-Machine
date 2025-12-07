package enigma.engine.logic;

//import DTO.HistoryDTO;
//import DTO.MachineSpecificationDTO;
//import DTO.MessageDTO;

import enigma.engine.DTO.history.HistoryDTO;
import enigma.engine.DTO.MachineStatusDTO;
import enigma.engine.logic.history.EnigmaMessage;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


/**
 * Defines the public contract for the Enigma Machine's core business logic.
 * All UI Commands communicate exclusively through this interface.
 */
public interface Engine {

    // --- 1. Load Machine from XML ---
    /**
     * Loads and validates machine settings from a file path.
     * @param xmlFilePath The path provided by the user.
     * @return A success message or an error description.
     */
    void loadMachineFromXml(String xmlFilePath) throws JAXBException, FileNotFoundException;

    // --- 2. Display Machine Status ---
    /**
     * Retrieves the current specification and configuration status of the machine.
     * @return MachineSpecificationDTO containing all necessary status data.
     */
    MachineStatusDTO getMachineStatus();

    // --- 3 & 4. Setup Code ---
    /**
     * Sets the machine configuration either manually or automatically using the strategy pattern.
     * @param rotorIds The selected rotor IDs (LTR).
     * @param positions The initial positions (LTR).
     * @param reflectorId The chosen reflector ID.
     * @return The new code in the required compact string format.
     */
    void setMachineCode(List<Integer> rotorIds, List<Character> positions, String reflectorId);

    /**
     * Sets a random, valid machine configuration automatically.
     * @return The new code in the required compact string format.
     */
    void setAutomaticCode();


    // --- 5. Process Input ---
    /**
     * Processes an input string, executes rotation, encrypts, and updates history.
     * @param inputString The string provided by the user.
     * @return The resulting encrypted/decrypted string.
     */
    EnigmaMessage processInput(String inputString);

    // --- 6. Reset Code ---
    /**
     * Resets the current rotor positions back to the initial code configuration.
     */
    void resetMachineToOriginalCode();

    // --- 7. Get History and Statistics ---
    /**
     * Retrieves all history of processed messages grouped by original code configurations.
     * @return HistoryDTO object containing all historical records.
     */
    HistoryDTO getHistoryAndStatistics();

    void exit();

    boolean isXMLLoaded();

    void saveCurrentSystemStateToFile(String fileName);

    void loadSystemStateFromFile(String fileName) throws IOException;
}