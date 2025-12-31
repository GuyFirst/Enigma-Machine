# Enigma-Machine (Java 21)

A modular Java implementation of an **Enigma machine simulator**, built as a **multi-module Maven** project.

The application is a **console-based program** that simulates the mechanical behavior of the historical Enigma machine.  
Machine definitions are loaded from **XML files validated by XSD**, and the encryption process is handled by a dedicated engine layer.

---

## 🛠 Requirements

* **Java:** 21
* **Maven:** 3.8+

**Verify your environment:**

```bash
java -version
mvn -version
```

---

## 📂 Project Structure

This repository is a Maven aggregator project (packaging: `pom`) composed of the following modules:

* **`enigma-machine`**: Low-level machine components such as rotors, reflector, and internal wiring logic.
* **`enigma-loader`**: XML loading and validation layer using XSD and JAXB.
* **`enigma-engine`**: Core application logic responsible for machine orchestration, configuration management, encryption flow, and history tracking.
* **`enigma-console`**: Console-based user interface and application entry point.

---
## 🕹 Console Menu Commands

Once the application is running, the following commands are available through the main menu:

1.  **Load Machine Definition**: Load an XML file containing the machine's technical specifications (rotors, reflectors, alphabet).
2.  **Display Machine Specifications**: View the current machine setup, including available rotors, alphabet size, and the number of messages processed.
3.  **Set Initial Code (Manual)**: Manually configure the active rotors, their starting positions, the chosen reflector, and the plugboard connections.
4.  **Set Initial Code (Automatic)**: Let the system randomly select and configure the machine state.
5. **Process Input (Symmetric Encryption/Decryption)**: Process a message through the machine's current configuration. The Enigma machine is reciprocal by design, meaning the encryption and decryption processes are identical.

Example: If a specific machine state maps "HELLO" to "AKVPD", then resetting the machine to that exact same state and processing "AKVPD" will return "HELLO".
Requirement: To decrypt a message, the machine must be set to the same initial code (rotors, positions, and plugboard) used during encryption.

6.  **Reset Current Code**: Reset the rotors to their initial starting positions (the last "ground state" configured).
7.  **History and Statistics**: Display all previous encryptions and performance metrics for the current session.
8.  **Exit**: Safely terminate the application.
9.  **Save Current System State**: saves the current state of the machine (settings and history) in a file.
10.  **Load Ssystem State**: loads a machine from an existing file
---

## 🔨 Build

From the repository root, run:

```bash
mvn clean install
```
*This builds all modules in the correct dependency order.*

---

## 🚀 Run (Windows)

You can run the project end-to-end using the following methods:

### Manual Steps

```bash
# Clone the repository
git clone [https://github.com/GuyFirst/Enigma-Machine](https://github.com/GuyFirst/Enigma-Machine) ./enigma
cd enigma

# Build the project
call mvn clean install

# Run the executable
cd target
java -jar enigma-machine-ex2.jar
```

### Automation: `run.bat`
Create a file named `run.bat` in the project root with the following content:

```batch
@off
git clone [https://github.com/GuyFirst/Enigma-Machine](https://github.com/GuyFirst/Enigma-Machine) ./enigma
cd enigma
call mvn clean install
cd target
java -jar enigma-machine-ex2.jar
pause
```
*Double-click `run.bat` to build and run the application automatically.*

---

## 📦 Executable JAR

* **Location:** `target/enigma-machine-ex2.jar`
* **Main Class:** `patmal.course.enigma.main.Main`

---

## ⚙️ XML Configuration

* **XSD Schema Location:** `enigma-loader/src/main/resources/Enigma-Ex2.xsd`

All XML machine definitions are validated against this schema before runtime initialization. **Invalid configurations are rejected** before execution to ensure system stability.

---

## 🏗 Design Notes

* **Decoupling**: Strict separation between UI, engine logic, and machine mechanics.
* **UI-Agnostic**: Core logic is independent of the display layer.
* **Isolation**: XML loading is completely isolated from business logic.
* **Scalability**: The architecture allows replacing the console UI with a GUI or Web UI without modifying the engine.

---
