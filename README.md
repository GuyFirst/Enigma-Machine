# 🧩 Enigma Machine Simulator (current state on prod branch)

This project implements a functional software simulation of the historical Enigma encryption machine, built upon modern Java architecture principles.

---

## 👥 Developers

This project is developed by:

* **Guy First**
* **Alfredo Limin**

---

## 📋 Exercise 1: Console Application Overview

This application implements the electromechanical logic of the Enigma machine using Java 21, accessible via a modular console interface. The primary design goal is strict adherence to the **Open/Closed Principle (OCP)** and **Separation of Concerns (SoC)**, utilizing interfaces extensively to prepare for future Maven and Spring deployments.

### 🏛️ Architecture & Modularity

The system is strictly separated into three logical modules:

| Module | Core Responsibility | Key Components |
| :--- | :--- | :--- |
| **3. enigma-consoleUI** (Active) | Handles all User I/O (`Scanner`, `System.out.println`), displays the menu, and dispatches commands. | `Main`, `UI-controller`, `Menu` |
| **2. enigma-engine** (Passive/Logic) | Manages system state, configuration loading, history, and command execution logic. | `EngineImpl`, `LoadManager`, `HistoryManager` |
| **1. Enigma-machine** (Passive/Library) | Core business logic library. Implements component mechanics and signal processing. | `Rotor`, `Reflector`, `Keyboard`, `RotorManager` |

---

## 💻 System Architecture and Module Breakdown

The implementation is structured into three modules, ensuring clear separation of concerns.

### 1. Core Module: `Enigma-machine`

This module is the foundational library containing all necessary classes to assemble, configure, and execute the electromechanical logic of the Enigma machine. Its primary function is the transformation of a numerical input index into a corresponding output index.

* **Note:** The Plugboard component is not yet developed.

#### Key Classes:

* **`Reflector`:** Implements the fixed electrical reversal of the signal path based on XML configuration.
* **`Keyboard`:** Manages the mapping between character input (ABC) and the machine's internal numerical indexing.
* **`Rotor`:** Implements the core substitution mapping and rotation capability of a single rotor.
* **`RotorManager`:** Centralizes and manages the cascading rotation logic between multiple `Rotor` components.

### 2. Logic Module: `enigma-engine`

This module serves as the primary execution layer, managing the system state, configuration, and command processing. The central coordination point is the **`EngineImpl`** class.

#### Key Data Members of `EngineImpl`:

* **`loadManager`:** Responsible for loading, validating (logical reasonableness), and transferring configuration data from the XML (via JAXB) to internal classes.
* **`machine`:** Represents the actively running Enigma Machine instance (Reflector, active Rotor set, and Keyboard).
    * **Note:** Although the XML configuration may be loaded after the initial command, the actual machine instance is not set up until the user provides the specific configuration details (rotors, starting positions, etc.), **whether manually or automatically.**
* **`repository`:** A centralized store for all available component definitions (all rotors, all reflectors, and the system keyboard).
* **`HistoryManager`:** Records the state and operation history to support Command 7.
* **`enigma configuration (initial and current)`:** Tracks the specific configuration (rotors, starting positions, reflector). `Initial` is the state set by the user (manual or automatic), and `Current` updates dynamically after each operation due to rotor rotation.
* **State Management:** The `EngineImpl` includes logic to support **Save State** and **Load State** commands. This functionality serializes and deserializes the full working state of the system, including the current `machine` configuration, the `repository`, and the execution `HistoryManager`.

### 3. UI Module: `enigma-consoleUI`

This module is responsible for the user interface, command presentation, and I/O. It acts as the bridge between the user and the system's logic.

#### Key Components:

* **`UI-controller`:** Manages the main application loop, presenting options, handling user selection, and providing error feedback.
* **`menu`:** Holds all command options as an array of the **`MenuCommandExecutable`** interfaces. Each concrete command implements the interface's `execute` function.
* **`engine`:** An interface to the **`EngineImpl`**, allowing the `UI-controller` to call and execute each of the ten required system commands.

---

### 🧭 Key Design Patterns Used

1.  **Command Pattern:** Used in the `UIConsole` to map numeric menu inputs (1-10) to specific executable classes, ensuring that adding or removing features does not require modifying the central `processInput` loop (**OCP**).
2.  **Strategy Pattern:** Used for code setup (`SetupStrategy` interface) to abstract the implementation details of *how* the code is set (Manual vs. Automatic).
3.  **Chain of Responsibility (Conceptual):** The signal processing within the `EnigmaMachine` coordinates the flow of electricity through the `RotorManager` and `ReflectorManager` sequentially.

---

## 🛠️ Usage and Execution

### Prerequisites

1.  Java Development Kit (JDK) **21** or higher.
2.  Apache Maven (or a standard IntelliJ project setup capable of building JARs).

### 🚀 Running the Application (Exercise 1)

1.  **Clone the repository:**
    ```bash
    git clone [Your GitHub Repository URL Here]
    ```
2.  **Compile and Run (Standard JAR execution):**
    * The project should be built to produce a runnable JAR file containing both the UI and Engine modules.
    * Execute the application from the command line:
        ```bash
        java -jar enigma-machine-ex1.jar
        ```

### 📜 Available Commands (Menu Display)

The following **10 commands** are available via the console menu:

1.  **Load Machine from XML**
2.  **Display Machine Status**
3.  **Manual Code Setup**
4.  **Automatic Code Setup**
5.  **Save State of Machine**
6.  **Load State of Machine**
7.  **Process Input (Encrypt/Decrypt)**
8.  **Reset to Original Code**
9.  **Get History and Statistics**
10. **Exit System**

---

## 💡 Technical Notes on Component Implementation

### Rotor Logic (List Shifting Model)

The `Rotor` implementation uses the **List Shifting Model** (instead of Offset calculation) where the rotor's movement is physically represented by modifying the underlying data structure:

1.  **Wiring Representation:** The rotor's wiring is stored in two lists (`rightColumn` and `leftColumn`) representing the physical contacts.
2.  **Rotation (`rotate()`):** Movement is simulated by removing the top element of both lists and adding it to the end, effectively rotating the wheel's wiring relative to the viewing window.
3.  **Encoding Efficiency:** The encoding methods (`encodeForward` / `encodeBackward`) use **list lookup** (`List.get(index)`) followed by **linear search** (`List.indexOf(value)`). This makes the encoding process $\mathcal{O}(N)$ (linear time complexity) where $N$ is the alphabet size. we'll change it to be calculated in a better time complexity.
4.  **Notch Alignment:** The `notchPosition` is decremented during rotation to track its current position relative to the viewing window.

### Reflector Logic

The `Reflector` is implemented using a single `Map<Integer, Integer>` built to store both the $\text{Input} \to \text{Output}$ and the reciprocal $\text{Output} \to \text{Input}$ mappings simultaneously in the constructor, simplifying runtime access and ensuring symmetry.
