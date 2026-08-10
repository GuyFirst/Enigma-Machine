import { useMemo } from 'react'

// The historical Enigma keyboard/lampboard layout, used when the machine's
// alphabet is the full A-Z. Other alphabets (like the 6-letter demo machine)
// fall back to simple rows.
const HISTORICAL_ROWS = ['QWERTZUIO', 'ASDFGHJK', 'PYXCVBNML']

function keyRows(alphabet) {
  const isStandard = [...alphabet].sort().join('') === 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
  if (isStandard) return HISTORICAL_ROWS.map((row) => [...row])

  const perRow = Math.min(9, Math.ceil(alphabet.length / Math.ceil(alphabet.length / 9)))
  const rows = []
  for (let i = 0; i < alphabet.length; i += perRow) {
    rows.push([...alphabet.slice(i, i + perRow)])
  }
  return rows
}

/**
 * The machine itself. Purely presentational: it renders whatever position /
 * lamp / key state it is handed, so the same component shows an idle machine,
 * a live animation, or a manually dialled one.
 */
export default function EnigmaMachine({
  alphabet,
  rotorIds,
  positions,
  reflectorId,
  plugs = {},
  step = null,
  manualMode = false,
  onDialRotor,
  inputTape = '',
  outputTape = '',
}) {
  const rows = useMemo(() => keyRows(alphabet), [alphabet])
  const litChar = step && !step.passthrough ? step.outputChar : null
  const pressedChar = step && !step.passthrough ? step.inputChar : null

  const plugPairs = useMemo(
    () =>
      Object.entries(plugs)
        .filter(([from, to]) => from < to)
        .map(([from, to]) => `${from}${to}`),
    [plugs],
  )

  return (
    <div className="machine">
      <div className="machine-lid">
        <span className="machine-label">ENIGMA</span>
        <span className="machine-sub">
          reflector {reflectorId} · rotors {rotorIds.join('-')}
        </span>
      </div>

      <div className="rotor-bank">
        {positions.map((letter, i) => (
          <div className="rotor" key={i}>
            <span className="rotor-id">{rotorIds[i]}</span>
            {manualMode && (
              <button
                className="dial"
                type="button"
                aria-label={`rotor ${rotorIds[i]} up`}
                onClick={() => onDialRotor?.(i, 1)}
              >
                ▲
              </button>
            )}
            <div className="rotor-window">
              <span key={letter} className="rotor-letter">
                {letter}
              </span>
            </div>
            {manualMode && (
              <button
                className="dial"
                type="button"
                aria-label={`rotor ${rotorIds[i]} down`}
                onClick={() => onDialRotor?.(i, -1)}
              >
                ▼
              </button>
            )}
          </div>
        ))}
      </div>

      <div className="lampboard">
        {rows.map((row, r) => (
          <div className="lamp-row" key={r}>
            {row.map((char) => (
              <span key={char} className={`lamp ${litChar === char ? 'lit' : ''}`}>
                {char}
              </span>
            ))}
          </div>
        ))}
      </div>

      <div className="keyboard">
        {rows.map((row, r) => (
          <div className="key-row" key={r}>
            {row.map((char) => (
              <span key={char} className={`key ${pressedChar === char ? 'pressed' : ''}`}>
                {char}
              </span>
            ))}
          </div>
        ))}
      </div>

      <div className="plugboard">
        <span className="plug-label">Steckerbrett</span>
        {plugPairs.length === 0 ? (
          <span className="muted small">no plugs</span>
        ) : (
          plugPairs.map((pair) => (
            <span className="plug" key={pair}>
              {pair[0]}<span className="plug-cable" />{pair[1]}
            </span>
          ))
        )}
      </div>

      <div className="tapes">
        <div className="tape">
          <span className="tape-label">in</span>
          <span className="tape-text">{inputTape || ' '}</span>
        </div>
        <div className="tape out">
          <span className="tape-label">out</span>
          <span className="tape-text">{outputTape || ' '}</span>
        </div>
      </div>
    </div>
  )
}
