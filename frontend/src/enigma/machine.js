/**
 * Enigma machine - browser implementation.
 *
 * This is a faithful port of the Java engine (enigma-logic/enigma-machine).
 * It exists in JS because the animation IS the computation: the UI needs the
 * per-character intermediate state (rotor positions, signal path, lamp) which
 * a "return the final string" API cannot provide. It also means plaintext
 * never leaves the browser.
 *
 * The Java implementation is the reference: `npm test` replays vectors
 * generated from it and asserts this code agrees character for character.
 *
 * Rotor model (same as Java): a rotor is two index arrays, `right` and `left`,
 * that rotate together. Stepping = cyclic shift of both by one. The letter in
 * the rotor's window is `right[0]`.
 */

/** Wraps any integer into [0, n). */
function mod(value, n) {
  return ((value % n) + n) % n
}

class Rotor {
  /**
   * @param {{id:number, right:number[], left:number[], notch:number}} spec
   *        wiring as loaded (unrotated); `notch` is a 0-based index into that
   *        same orientation
   */
  constructor(spec) {
    this.id = spec.id
    this.right = [...spec.right]
    this.left = [...spec.left]
    this.notch = spec.notch
    this.size = this.right.length
  }

  rotate() {
    this.right.push(this.right.shift())
    this.left.push(this.left.shift())
    // The notch travels with the ring, so it counts down as the rotor turns
    this.notch = mod(this.notch - 1, this.size)
  }

  /** Turns the rotor until `index` shows in its window. */
  setPosition(index) {
    let guard = 0
    while (this.right[0] !== index) {
      this.rotate()
      if (++guard > this.size) {
        throw new Error(`Position ${index} not reachable on rotor ${this.id}`)
      }
    }
  }

  get topIndex() {
    return this.right[0]
  }

  /** Signal travelling right-to-left (keyboard towards reflector). */
  encodeForward(index) {
    return this.left.indexOf(this.right[index])
  }

  /** Signal travelling left-to-right (reflector back towards the lamps). */
  encodeBackward(index) {
    return this.right.indexOf(this.left[index])
  }
}

export class EnigmaMachine {
  /**
   * @param {object} wiring machine wiring from the server
   *        ({alphabet, rotors:[{id,right,left,notch}], reflectors:[{id,wiring}]})
   * @param {object} config {rotorIds:number[], positions:string[],
   *        reflectorId:string, plugs:Record<string,string>}
   *        rotorIds/positions are ordered left-to-right, as displayed.
   */
  constructor(wiring, config) {
    this.alphabet = wiring.alphabet
    this.size = this.alphabet.length

    const rotorSpecs = new Map(wiring.rotors.map((r) => [r.id, r]))
    this.rotors = config.rotorIds.map((id) => {
      const spec = rotorSpecs.get(id)
      if (!spec) throw new Error(`Unknown rotor id: ${id}`)
      return new Rotor(spec)
    })

    const reflector = wiring.reflectors.find((r) => r.id === config.reflectorId)
    if (!reflector) throw new Error(`Unknown reflector id: ${config.reflectorId}`)
    this.reflector = reflector.wiring

    this.plugs = config.plugs || {}
    this.setPositions(config.positions)
  }

  charToIndex(char) {
    return this.alphabet.indexOf(char)
  }

  indexToChar(index) {
    return this.alphabet[index]
  }

  isValidChar(char) {
    return this.alphabet.includes(char)
  }

  /** @param {string[]} positions letters, left-to-right */
  setPositions(positions) {
    positions.forEach((letter, i) => this.rotors[i].setPosition(this.charToIndex(letter)))
  }

  /** Current window letters, left-to-right. */
  getPositions() {
    return this.rotors.map((r) => this.indexToChar(r.topIndex))
  }

  plugSubstitute(char) {
    return this.plugs[char] ?? char
  }

  /**
   * Advances the rotors the way a keypress does: the rightmost always turns,
   * and each turn carries into the next rotor only while the notch lines up.
   */
  step() {
    for (let i = this.rotors.length - 1; i >= 0; i--) {
      this.rotors[i].rotate()
      if (this.rotors[i].notch !== 0) break
    }
  }

  /**
   * Encrypts one character, returning it plus everything the animation needs.
   * Characters outside the alphabet pass through untouched and do NOT step the
   * rotors - that is what lets free-form chat text (spaces, punctuation) work
   * while keeping the machine reciprocal.
   */
  encryptChar(char) {
    const positionsBefore = this.getPositions()

    if (!this.isValidChar(char)) {
      return {
        inputChar: char,
        outputChar: char,
        passthrough: true,
        positionsBefore,
        positionsAfter: positionsBefore,
        path: null,
      }
    }

    const afterPlugIn = this.plugSubstitute(char)
    let signal = this.charToIndex(afterPlugIn)

    this.step()
    const positionsAfter = this.getPositions()

    const forward = []
    for (let i = this.rotors.length - 1; i >= 0; i--) {
      const inIndex = signal
      signal = this.rotors[i].encodeForward(signal)
      forward.push({ rotorId: this.rotors[i].id, in: inIndex, out: signal })
    }

    const reflectorIn = signal
    signal = this.reflector[signal]
    const reflectorOut = signal

    const backward = []
    for (let i = 0; i < this.rotors.length; i++) {
      const inIndex = signal
      signal = this.rotors[i].encodeBackward(signal)
      backward.push({ rotorId: this.rotors[i].id, in: inIndex, out: signal })
    }

    const beforePlugOut = this.indexToChar(signal)
    const outputChar = this.plugSubstitute(beforePlugOut)

    return {
      inputChar: char,
      outputChar,
      passthrough: false,
      positionsBefore,
      positionsAfter,
      path: {
        plugIn: { from: char, to: afterPlugIn },
        forward,
        reflector: { in: reflectorIn, out: reflectorOut },
        backward,
        plugOut: { from: beforePlugOut, to: outputChar },
      },
    }
  }

  /**
   * Runs a whole message. Returns the result plus the per-character steps that
   * drive the animation. Consumes the machine's state (rotors are left where
   * the message ended), so build a fresh instance per run.
   */
  process(text) {
    const steps = []
    let output = ''
    for (const char of text.toUpperCase()) {
      const step = this.encryptChar(char)
      steps.push(step)
      output += step.outputChar
    }
    return { output, steps, endPositions: this.getPositions() }
  }
}

/** Convenience: one-shot run without keeping the instance around. */
export function runMachine(wiring, config, text) {
  return new EnigmaMachine(wiring, config).process(text)
}

/** Random start positions for a new message key. */
export function randomPositions(alphabet, count) {
  return Array.from(
    { length: count },
    () => alphabet[Math.floor(Math.random() * alphabet.length)],
  )
}
