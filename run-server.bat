@echo off
REM Starts the Enigma Spring Boot server.
REM Uses an embedded H2 in-memory database - no external DB service needed,
REM and no setup step required before running this.

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM The AI chat endpoint (/enigma/ai) needs a real OpenAI API key to actually answer questions.
REM Everything else (load/session/config/process/history) works without it -
REM but Spring will refuse to start at all if AI_API_KEY is not set, since it's a required property.
if "%AI_API_KEY%"=="" set AI_API_KEY=placeholder-not-a-real-key

echo Starting Enigma server on http://localhost:8080 ...
java -jar "%~dp0enigma-app\target\enigma-chat.jar"
