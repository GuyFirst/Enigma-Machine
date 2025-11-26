# 🧩 Enigma Machine Simulator (current state on prod branch)

This project implements a functional software simulation of the historical Enigma encryption machine, built upon modern Java architecture principles. This forms the foundational core competency exercise for the development of complex, modular systems, leveraging concepts required for the "Audio-DNA Exchange" platform (specifically, complex data modeling and efficient processing).

---

## 👥 Developers

This project is developed by:

* **Guy First**
* **Alfredo Limin**

---

## 📋 Exercise 1: Console Application Overview

This application implements the electromechanical logic of the Enigma machine using Java 21, accessible via a modular console interface. The primary design goal is strict adherence to the **Open/Closed Principle (OCP)** and **Separation of Concerns (SoC)**, utilizing interfaces extensively to prepare for future Maven and Spring deployments.

### 🏛️ Architecture & Modularity

The system is separated into passive Logic Modules (Engine) and an active UI Module (Console):

| Module | Core Responsibility | Key Logic |
| :--- | :--- | :--- |
| **UIConsole** (Active) | Handles all I/O (`Scanner`, `System.out.println`), displays the menu, and dispatches commands. Contains the `Main` class and the `UIController`. | Command Pattern, Menu Display. |
| **Machine** (Passive) | The core business logic layer. Contains the `EnigmaEngine` implementation and all component managers. | Rotor Offset Logic, Enigma History. |
| **components** (Library) | Holds all component definitions (`Rotor`, `Reflector`, `Keyboard`) and DTOs. | Relative Mapping Formula. |

### 🧭 Key Design Patterns Used

1.  **Command Pattern:** Used in the `UIConsole` to map numeric menu inputs (1-8) to specific executable classes, ensuring that adding or removing features does not require modifying the central `processInput` loop (OCP).
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

The following commands are available via the console menu:

1.  **Load Machine from XML**
2.  **Display Machine Status**
3.  **Manual Code Setup**
4.  **Automatic Code Setup**
5.  **Process Input (Encrypt/Decrypt)**
6.  **Reset to Original Code**
7.  **Get History and Statistics**
8.  **Exit System**

---

## 💡 Technical Notes on Component Implementation

### Rotor Logic (List Shifting Model)

The `Rotor` implementation uses the **List Shifting Model** (instead of Offset calculation) where the rotor's movement is physically represented by modifying the underlying data structure:

1.  **Wiring Representation:** The rotor's wiring is stored in two lists (`rightColumn` and `leftColumn`) representing the physical contacts.
2.  **Rotation (`rotate()`):** Movement is simulated by removing the top element of both lists and adding it to the end, effectively rotating the wheel's wiring relative to the viewing window.
3.  **Encoding Efficiency:** The encoding methods (`encodeForward` / `encodeBackward`) use **list lookup** (`List.get(index)`) followed by **linear search** (`List.indexOf(value)`). This makes the encoding process $\mathcal{O}(N)$ (linear time complexity) where $N$ is the alphabet size. we''ll change it to be calculate in a better time complexity.
4.  **Notch Alignment:** The `notchPosition` is decremented during rotation to track its current position relative to the viewing window.

### Reflector Logic

The `Reflector` is implemented using a single `Map<Integer, Integer>` built to store both the $\text{Input} \to \text{Output}$ and the reciprocal $\text{Output} \to \text{Input}$ mappings simultaneously in the constructor, simplifying runtime access and ensuring symmetry.
