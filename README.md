# Enigma-Machine (Java 21)

A modular Java implementation of an **Enigma machine simulator**, built as a **multi-module Maven** project.

The application is a **console-based program** that simulates the mechanical behavior of the historical Enigma machine.  
Machine definitions are loaded from **XML files validated by XSD**, and the encryption process is handled by a dedicated engine layer.

---

## Requirements

- Java 21
- Maven 3.8+

Verify your environment:

```bash
java -version
mvn -version
Project Structure
This repository is a Maven aggregator project (packaging: pom) composed of the following modules:

enigma-machine
Low-level machine components such as rotors, reflector, and internal wiring logic.

enigma-loader
XML loading and validation layer using XSD and JAXB.

enigma-engine
Core application logic responsible for machine orchestration, configuration management, encryption flow, and history tracking.

enigma-console
Console-based user interface and application entry point.

Build
From the repository root:

Bash

mvn clean install
This builds all modules in the correct dependency order.

Run (Windows – run.bat style)
You can run the project end-to-end using the following commands.

Manual steps
Bash

git clone [https://github.com/GuyFirst/Enigma-Machine](https://github.com/GuyFirst/Enigma-Machine) ./enigma
cd enigma
call mvn clean install
cd target
java -jar enigma-machine-ex2.jar
Optional: run.bat
Create a file named run.bat in the project root with the following content:

Code snippet

@echo off
git clone [https://github.com/GuyFirst/Enigma-Machine](https://github.com/GuyFirst/Enigma-Machine) ./enigma
cd enigma
call mvn clean install
cd target
java -jar enigma-machine-ex2.jar
pause
Double-click run.bat to build and run the application.

Executable JAR
The runnable JAR is generated at: target/enigma-machine-ex2.jar

Main class: patmal.course.enigma.main.Main

XML Configuration
XSD schema location: enigma-loader/src/main/resources/Enigma-Ex2.xsd

All XML machine definitions are validated against this schema before runtime initialization.

Invalid configurations are rejected before execution.

Design Notes
Strict separation between UI, engine logic, and machine mechanics.

Core logic is UI-agnostic.

XML loading is isolated from business logic.

Architecture allows replacing the console UI without modifying the engine.

Author
Guy First B.Sc. Computer Science

Java backend & architecture-oriented developer
