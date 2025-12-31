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

Once the application is running, you can navigate through the following options:

1.  **Load Machine Definition**: Import an XML file containing the machine's technical specifications (available rotors, reflectors, and alphabet).
2.  **Display Machine Specifications**: View the current system status, including available components, alphabet size, and a count of processed messages.
3.  **Set Initial Code (Manual)**: Manually choose the active rotors, set their starting positions, select a reflector, and configure plugboard connections.
4.  **Set Initial Code (Automatic)**: Let the system generate a randomized valid configuration (random rotors, positions, reflector, and plugs).
5.  **Process Input (Symmetric Encryption/Decryption)**: Process a message through the current machine state. The Enigma is **reciprocal by design**, meaning the same process handles both encryption and decryption.
    > **Example**: If a specific configuration maps **"HELLO"** to **"AKVPD"**, then resetting the machine to that exact state and processing **"AKVPD"** will return **"HELLO"**.
    > 
    > **Note**: Successful decryption requires the machine to be at the exact same "ground state" (initial code) used during encryption.
6.  **Reset Current Code**: Reset the rotors to the starting positions of the last defined configuration.
7.  **History and Statistics**: Display all processed messages and performance data for the current session.
8.  **Save Current System State**: Persist the current machine configuration and session history into a local file.
9.  **Load System State**: Restore a previously saved session (including machine setup and history) from a file.
10. **Exit**: Securely terminate the application.
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
git clone https://github.com/GuyFirst/Enigma-Machine ./enigma
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
