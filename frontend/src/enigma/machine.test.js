import { describe, expect, it } from 'vitest'
import { EnigmaMachine, runMachine } from './machine'
import vectors from './vectors.json'

// vectors.json is generated from the Java implementation (the reference) by
// scripts/generate-vectors - see README. These tests are what keeps the browser
// port honest: if the two ever diverge, they fail here.

describe.each(vectors.machines)('$name', ({ wiring, cases }) => {
  it('matches the Java implementation on every vector', () => {
    const mismatches = []
    for (const testCase of cases) {
      const { output, endPositions } = runMachine(wiring, testCase, testCase.input)
      if (output !== testCase.output) {
        mismatches.push(`input=${testCase.input} expected=${testCase.output} got=${output}`)
      }
      if (endPositions.join(',') !== testCase.endPositions.join(',')) {
        mismatches.push(
          `input=${testCase.input} end positions expected=${testCase.endPositions} got=${endPositions}`,
        )
      }
    }
    expect(mismatches).toEqual([])
  })

  it('is reciprocal: re-running the ciphertext from the same start recovers the input', () => {
    for (const testCase of cases) {
      const { output } = runMachine(wiring, testCase, testCase.output)
      expect(output).toBe(testCase.input)
    }
  })

  it('emits one trace step per character, with the lamp matching the output', () => {
    const testCase = cases[0]
    const { steps, output } = runMachine(wiring, testCase, testCase.input)
    expect(steps).toHaveLength(testCase.input.length)
    expect(steps.map((s) => s.outputChar).join('')).toBe(output)
    for (const step of steps) {
      expect(step.path.forward).toHaveLength(testCase.rotorIds.length)
      expect(step.path.backward).toHaveLength(testCase.rotorIds.length)
      // Enigma can never encrypt a letter to itself
      expect(step.outputChar).not.toBe(step.inputChar)
    }
  })

  it('passes non-alphabet characters through without stepping the rotors', () => {
    const testCase = cases[0]
    const machine = new EnigmaMachine(wiring, testCase)
    const positionsBefore = machine.getPositions()
    const space = machine.encryptChar(' ')

    expect(space.outputChar).toBe(' ')
    expect(space.passthrough).toBe(true)
    expect(machine.getPositions()).toEqual(positionsBefore)
  })

  it('keeps free-form text (spaces and punctuation) reciprocal', () => {
    const testCase = cases[0]
    const text = `${testCase.input.slice(0, 5)} ${testCase.input.slice(5, 10)}, 42!`
    const { output } = runMachine(wiring, testCase, text)
    const roundTrip = runMachine(wiring, testCase, output).output
    expect(roundTrip).toBe(text.toUpperCase())
  })

  it('produces different output from a different start position', () => {
    const testCase = cases[0]
    const shifted = {
      ...testCase,
      positions: testCase.positions.map((p) =>
        wiring.alphabet[(wiring.alphabet.indexOf(p) + 1) % wiring.alphabet.length],
      ),
    }
    const correct = runMachine(wiring, testCase, testCase.output).output
    const wrong = runMachine(wiring, shifted, testCase.output).output
    expect(correct).toBe(testCase.input)
    expect(wrong).not.toBe(testCase.input)
  })
})
