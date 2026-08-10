
<img width="855" height="799" alt="image" src="https://github.com/user-attachments/assets/6b6e37ea-adeb-4deb-b70c-d3bdd0dcd745" />


# Enigma Chat

A messaging app where the only way to read a message is to work the machine yourself.

Two people share one simulated WWII Enigma. You type a message, watch it go through the
machine letter by letter — rotors clicking round, lamps lighting up — and only then
transmit the result. Your partner receives gibberish. To read it they have to feed it
back through their own machine.

**Plaintext never leaves the browser.** The cipher runs client-side, so the server
receives, stores and serves ciphertext only — it has no way to recover what was written.
The chat log shows ciphertext even to the person who sent it.

Java 21 · Spring Boot · React · deployable free on Supabase + Render + Vercel.

---

## How a conversation works

**1. Set up the machine.** Starting a conversation means configuring an Enigma: rotor
order, reflector, plugboard cables, and the ground position the rotors rest at. That
setup is the conversation's code book page.

**2. Hand it over.** The invite code gives the other person exactly that machine. Without
it their machine is wired differently and produces nothing but noise.

**3. The machine keeps turning.** There is one machine per conversation and it never
resets. Encrypting advances the rotors, and the next message — from either person —
carries on from where the last one stopped. Send `J` from `AAA` and the rotors sit at
`AAB`; the next message begins there. The server owns that shared position, so both
operators always see the same machine, and it visibly turns on your screen when your
partner transmits.

**4. Read by operating.** Feeding a received message into the machine plays the
decryption back through the rotors. Automatic by default; flip on **operator mode** and
the rotors stay put so you dial them in yourself — a wrong setting gives you gibberish,
same as it would have in 1942.

Two people can't type on one machine at once, and the server enforces it: a message
encrypted from a position the machine has already left is rejected (HTTP 409) so it can
be re-encrypted rather than quietly corrupting the conversation. Each message also
records the position it started from, which is what lets you reread old messages,
decrypt out of order, or come back after a refresh.

## How Enigma works

Each character passes through a **plugboard** (swaps some letter pairs), a stack of
**rotors** (each a fixed letter substitution), a **reflector** (bounces the signal back
along a different path), the rotors again in reverse, and the plugboard once more.

Before every character the rightmost rotor steps one position. When its notch comes
round it carries into the next rotor — an odometer, except the carry happens at the
notch letter rather than at a wrap from Z to A.

Because the reflector guarantees no letter ever maps to itself and every stage is its own
inverse, the machine is **reciprocal**: running ciphertext back through it from the same
starting position returns the plaintext. There is no separate decrypt mode — it is the
same operation both ways, which is why one machine does both jobs here.

Rotor wirings, reflectors and the alphabet aren't hard-coded. They come from XML machine
definitions validated against a schema, so new machines can be added without touching
code. Two are seeded at startup: a full 26-letter **Enigma I** using the historical
rotor wirings, and a small 6-letter machine that makes the mechanics easy to follow.

## Architecture

Multi-module Maven build; each module has one job.

```
enigma-app      Spring Boot entry point, security config, preset seeding
enigma-api      REST controllers - HTTP in, DTOs out
enigma-core     Services, DTOs, chat domain
enigma-dal      JPA entities and repositories
enigma-logic    the machine itself:
  enigma-machine    the cipher: keyboard, plugboard, rotor, reflector
  enigma-loader     parses and validates machine-definition XML
  enigma-engine     builds a machine from a configuration and runs text through it
  enigma-sessions   session state for the legacy API
frontend/       React app: operator desk, machine setup, browser-side cipher
```

### The cipher exists twice, on purpose

Java (`enigma-logic/`) is the reference implementation. The browser port
(`frontend/src/enigma/machine.js`) exists for two reasons: the animation **is** the
computation — it needs the per-character rotor positions, signal path and lamp state that
a "return the final string" API can't provide — and running it client-side is what keeps
plaintext away from the server.

They are held in agreement by generated test vectors, so the two can't silently drift:

```bash
powershell -ExecutionPolicy Bypass -File scripts/generate-vectors.ps1   # Java -> vectors.json
cd frontend && npm test                                                 # asserts the port matches
```

The suite replays 120 vectors across both machines, checking ciphertext *and* final rotor
positions, plus reciprocity, passthrough of non-alphabet characters, and that a wrong
start position fails.

### Data

Locally an embedded **H2 in-memory** database — nothing to install, and the schema is
built from the JPA entities at startup. The `prod` profile switches to PostgreSQL
(Supabase) with `ddl-auto: update`.

Messages are stored as ciphertext plus the rotor position they were encrypted at. There
is no column anywhere that holds readable text.

### Auth

Locally, an `X-Dev-User` header identifies you — no accounts, no cloud, just pick a name.
In production, Spring Security validates Supabase JWTs on `/api/**`.

## Running it locally

**Requirements:** JDK 21 and Maven. No database, no cloud accounts.

```bash
git clone https://github.com/GuyFirst/Enigma-Machine.git
cd Enigma-Machine
mvn clean install -DskipTests
```

Backend (`http://localhost:8080`):

```bash
java -jar enigma-app/target/enigma-chat.jar
```

Frontend (`http://localhost:5173`):

```bash
cd frontend
npm install
npm run dev
```

Open it in two browsers — or one normal window and one incognito — sign in as two
different names, create a conversation with one and join with the invite code from the
other. On Windows `run-server.bat` starts the backend with `JAVA_HOME` already set.

### Tests

```bash
cd frontend && npm test                                       # cipher, against Java-generated vectors
powershell -ExecutionPolicy Bypass -File test-chat-api.ps1    # chat API end to end
powershell -ExecutionPolicy Bypass -File test-api.ps1         # legacy API end to end
```

## Deploying

Supabase (Postgres + auth) · Render (backend) · Vercel (frontend), all on free tiers.
Step by step in **[DEPLOYMENT.md](DEPLOYMENT.md)**.

## The original API

This started as a course exercise: a console Enigma, then a modular Maven library, then a
REST server. That server is still here and still works, under `/enigma/*` — upload an XML
machine, open a session, set a code manually or randomly, process text, read the history.
There is also `/enigma/ai`, which answers plain-English questions about the processing
history by having an LLM write a read-only SQL query (needs `AI_API_KEY`; everything else
runs fine without it). `web for enigma/` is the small React client for that endpoint.

The chat app is built on the same engine rather than replacing it.
