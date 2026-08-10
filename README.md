# Enigma Chat

A web app where two users correspond through a simulated WWII Enigma machine: each
conversation shares one machine and one rotor state, messages are encrypted on send and
decrypted on read, and the database only ever stores ciphertext. Built on a Java 21 /
Spring Boot backend (originally the "ex3" stage of a multi-stage course project) with a
React frontend.

**The chat only ever carries ciphertext.** You type a message, watch it go through an
animated machine letter by letter, and only then transmit the result. The recipient sees
gibberish until they run it back through their own machine. The plaintext never reaches
the server - encryption and decryption both happen in the browser.

### One machine per conversation

Starting a conversation means setting a machine up: the rotor order, the reflector, the
plug cables and the ground position the rotors start at. That setup is the conversation's
code book page, and the invite code is what hands it to the other person.

From then on there is **one machine and it keeps turning**. Encrypting a message advances
the rotors, and the next message - from either participant - carries on from exactly
where the previous one stopped. Send `J` from `AAA` and the rotors sit at `AAB`; the next
message starts there. The server owns that shared position, so both operators always see
the same machine.

Two people can't type on one machine at once, and the server enforces that: a message
encrypted from a position the machine has already left is rejected (HTTP 409) so it can
be re-encrypted rather than silently corrupting the conversation.

Each message also records the position it was encrypted at, which is what lets you reread
an old message, decrypt them out of order, or come back after a refresh.

Decryption is automatic by default. Flip on **operator mode** and the rotors stay where
they are: you dial them in yourself, and a wrong setting gives you gibberish.

**Other features:** user accounts (Supabase in prod, dev-identity mode locally), preset
machines (a full 26-letter "Enigma I" with historical rotor wirings + a small demo
machine), polling for near-real-time delivery. Decrypted text is held in memory only, so
a page refresh returns the conversation to ciphertext.

### Where the cipher runs

The machine exists twice on purpose. Java (`enigma-logic/`) is the reference implementation
and still powers the original course API. The browser port (`frontend/src/enigma/machine.js`)
exists because the animation *is* the computation - it needs the per-character rotor
positions, signal path and lamp state that a "return the final string" API cannot give -
and because it keeps plaintext on the client.

They are kept in agreement by generated test vectors:

```bash
powershell -ExecutionPolicy Bypass -File scripts/generate-vectors.ps1   # Java -> vectors.json
cd frontend && npm test                                                 # asserts the port matches
```

- Chat API: `/api/*` (see `enigma-api/.../controller/chat/`), React app in `frontend/`
- Deploying to the cloud (Supabase + Render + Vercel, all free): see [DEPLOYMENT.md](DEPLOYMENT.md)
- Local dev: `run-server.bat` + `cd frontend && npm run dev` - no cloud accounts needed

Everything below documents the underlying Enigma server the chat is built on.

---

## How it works, in short

An Enigma machine encrypts one character at a time by passing it through, in order:
a **plugboard** (swaps some letter pairs), a set of **rotors** (each rotor is a fixed
letter-substitution wiring; after every character the rightmost rotor turns one position,
occasionally causing the next rotor to turn too — like an odometer), a **reflector**
(bounces the signal back through the rotors on a different path), the rotors again in
reverse, and the plugboard again. Because the reflector guarantees no letter maps to
itself and every step is its own inverse, the machine is *reciprocal*: encrypting a
message and then running the ciphertext back through the machine **from the exact same
starting position** decrypts it. There's no separate "decrypt mode" — it's the same
operation both ways.

The rotor wiring, reflector wiring, and alphabet are not hard-coded — they're loaded at
runtime from an XML file you upload, validated against a schema (JAXB).

## Architecture

The project is a multi-module Maven build. Each module has one job:

```
enigma-app      Spring Boot entry point (the only module you actually run)
enigma-api      REST controllers - HTTP in, DTOs out
enigma-core     Services that orchestrate everything + DTOs + the AI chat feature
enigma-dal      Database persistence (JPA entities + repositories)
enigma-logic    The actual Enigma machine, split into 4 sub-modules:
  enigma-machine    the cipher itself: keyboard, plugboard, rotor, reflector
  enigma-loader     parses & validates the machine-definition XML
  enigma-engine     builds a machine from a config and runs text through it
  enigma-sessions   in-memory session state (which rotors, current positions, etc.)
```

**The key design decision:** the cipher engine itself is *stateless*. Every time you
process a message, a fresh machine is built from two things: the machine's fixed
"catalog" (all available rotors/reflectors, loaded once from XML and stored in the DB)
and the *session's* current state (which rotors are in use, their current positions,
the reflector, the plugs). The session — held in memory, not the DB — is what actually
carries the rotors turning from one message to the next, exactly like a real physical
machine keeps its rotor positions between keystrokes.

### Request flow

```
1. POST /enigma/load            upload an XML machine definition -> saved to DB
2. POST /enigma/session          pick a loaded machine by name -> get a session ID
3. PUT  /enigma/config/manual     set exact rotors/positions/reflector/plugs
   or   /enigma/config/automatic  ...or let the server pick randomly
4. POST /enigma/process           encrypt/decrypt text (rotors advance & persist state)
5. GET  /enigma/history           see everything processed so far
6. POST /enigma/ai                ask a question in plain English about the history
                                   (translated to SQL by an LLM, restricted to read-only)
```

### Database

Uses an **embedded H2 in-memory database** — no external DB service to install or run.
The schema is created fresh from the JPA entities every time the app starts
(`ddl-auto: create-drop`), and all data (machines, rotors, reflectors, processing
history) lives only for the lifetime of that run. This keeps the whole project a
single `java -jar` away from running, with nothing else to set up.

### AI chat assistant

`POST /enigma/ai` accepts a free-text question (e.g. "how many messages did the sanity
machine process?"), asks an LLM to translate it into a read-only SQL query against the
processing-history schema, runs it, and asks the LLM to summarize the result back in
plain English. It requires an OpenAI API key (`AI_API_KEY` env var) to actually answer
questions — without one, the server still starts and every other endpoint works fine,
this one just won't produce real answers.

## Setup & running it

**Requirements:** JDK 21, Maven. That's it — no database to install.

```bash
git clone https://github.com/GuyFirst/Enigma-Machine.git
cd Enigma-Machine
mvn clean install -DskipTests
```

Run it:

```bash
java -jar enigma-app/target/enigma-machine-server-ex3.jar
```

(or use `run-server.bat` on Windows, which sets `JAVA_HOME` and a placeholder
`AI_API_KEY` for you). The server starts on `http://localhost:8080`.

To actually get answers from `/enigma/ai`, set a real key first:

```bash
export AI_API_KEY=sk-...          # bash
$env:AI_API_KEY = "sk-..."        # PowerShell
```

### Trying it out

`test-api.ps1` runs the full flow end-to-end against a running server — loads the
sample machine (`enigma-logic/enigma-loader/src/main/resources/ex3-sanity-small.xml`,
a tiny 6-letter A-F alphabet machine), creates a session, sets a random code, encrypts
a message, decrypts it back, and prints the history:

```powershell
powershell -ExecutionPolicy Bypass -File test-api.ps1
```

Or drive it manually with curl - example machine load:

```bash
curl -X POST http://localhost:8080/enigma/load \
  -F "file=@enigma-logic/enigma-loader/src/main/resources/ex3-sanity-small.xml"
```

### Frontend

`web for enigma/` is a small separate React + Vite app - a chat UI for the `/enigma/ai`
endpoint only (not a full admin UI for the machine itself). To run it:

```bash
cd "web for enigma"
npm install
npm run dev
```
