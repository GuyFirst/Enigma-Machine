import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'

/**
 * Setting up the machine before the first message, the way an operator would
 * from a code book page: pick the rotor order, the reflector, the plug cables
 * and the ground position. Whatever is chosen here is what the other side
 * receives through the invite code.
 */
export default function NewConversationPage() {
  const [machines, setMachines] = useState([])
  const [machineName, setMachineName] = useState('')
  const [wiring, setWiring] = useState(null)

  const [rotorIds, setRotorIds] = useState([])
  const [reflectorId, setReflectorId] = useState('')
  const [positions, setPositions] = useState([])
  const [plugPairs, setPlugPairs] = useState([])
  const [plugA, setPlugA] = useState('')
  const [plugB, setPlugB] = useState('')

  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    api
      .listMachines()
      .then((list) => {
        setMachines(list)
        if (list.length) setMachineName(list[0].name)
      })
      .catch((e) => setError(e.message))
  }, [])

  // Loading a machine resets the whole setup to a sane default for it
  useEffect(() => {
    if (!machineName) return
    let cancelled = false
    api
      .getMachineWiring(machineName)
      .then((w) => {
        if (cancelled) return
        setWiring(w)
        setRotorIds(w.rotors.slice(0, w.rotorsInUse).map((r) => r.id))
        setReflectorId(w.reflectors[0]?.id ?? '')
        setPositions(Array(w.rotorsInUse).fill(w.alphabet[0]))
        setPlugPairs([])
        setPlugA('')
        setPlugB('')
      })
      .catch((e) => !cancelled && setError(e.message))
    return () => {
      cancelled = true
    }
  }, [machineName])

  const alphabet = wiring?.alphabet ?? ''
  const pluggedLetters = useMemo(
    () => new Set(plugPairs.flatMap((p) => [p[0], p[1]])),
    [plugPairs],
  )
  const duplicateRotor = new Set(rotorIds).size !== rotorIds.length
  const maxPlugs = Math.floor(alphabet.length / 2)

  const setRotorAt = (slot, id) => {
    setRotorIds((prev) => prev.map((cur, i) => (i === slot ? id : cur)))
  }

  const setPositionAt = (slot, letter) => {
    setPositions((prev) => prev.map((cur, i) => (i === slot ? letter : cur)))
  }

  const addPlug = () => {
    if (!plugA || !plugB || plugA === plugB) return
    setPlugPairs((prev) => [...prev, `${plugA}${plugB}`])
    setPlugA('')
    setPlugB('')
  }

  const removePlug = (pair) => setPlugPairs((prev) => prev.filter((p) => p !== pair))

  const randomise = () => {
    const shuffled = [...wiring.rotors].sort(() => Math.random() - 0.5)
    setRotorIds(shuffled.slice(0, wiring.rotorsInUse).map((r) => r.id))
    setReflectorId(wiring.reflectors[Math.floor(Math.random() * wiring.reflectors.length)].id)
    setPositions(
      Array.from({ length: wiring.rotorsInUse }, () =>
        alphabet[Math.floor(Math.random() * alphabet.length)],
      ),
    )
    const letters = [...alphabet].sort(() => Math.random() - 0.5)
    const count = Math.floor(Math.random() * (maxPlugs + 1))
    setPlugPairs(
      Array.from({ length: count }, (_, i) => `${letters[i * 2]}${letters[i * 2 + 1]}`),
    )
  }

  const create = async () => {
    setError('')
    setBusy(true)
    try {
      const conv = await api.createConversation({
        machineName,
        rotorIds,
        reflectorId,
        plugPairs,
        initialPositions: positions,
      })
      navigate(`/chat/${conv.id}`)
    } catch (e) {
      setError(e.message)
      setBusy(false)
    }
  }

  if (!wiring) {
    return <div className="center-screen">{error || 'Loading machines…'}</div>
  }

  return (
    <main className="page">
      <section className="card">
        <div className="setup-head">
          <h2>Set up the machine</h2>
          <button className="link-btn" onClick={() => navigate('/')}>
            cancel
          </button>
        </div>
        <p className="muted small">
          These settings are this conversation&apos;s code book page. Whoever joins with the
          invite code gets exactly this machine — without it they cannot read a word.
        </p>

        <label className="field">
          <span className="field-label">machine</span>
          <select value={machineName} onChange={(e) => setMachineName(e.target.value)}>
            {machines.map((m) => (
              <option key={m.name} value={m.name}>
                {m.name} ({m.abc.length} letters, {m.rotorsInUse} rotors)
              </option>
            ))}
          </select>
        </label>

        <div className="field">
          <span className="field-label">rotors &amp; ground position (left to right)</span>
          <div className="rotor-picker">
            {rotorIds.map((id, slot) => (
              <div className="rotor-slot" key={slot}>
                <select value={id} onChange={(e) => setRotorAt(slot, Number(e.target.value))}>
                  {wiring.rotors.map((r) => (
                    <option key={r.id} value={r.id}>
                      rotor {r.id}
                    </option>
                  ))}
                </select>
                <select
                  className="letter-select"
                  value={positions[slot]}
                  onChange={(e) => setPositionAt(slot, e.target.value)}
                >
                  {[...alphabet].map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>
            ))}
          </div>
          {duplicateRotor && <p className="error">Each rotor can only be used once.</p>}
        </div>

        <label className="field">
          <span className="field-label">reflector</span>
          <select value={reflectorId} onChange={(e) => setReflectorId(e.target.value)}>
            {wiring.reflectors.map((r) => (
              <option key={r.id} value={r.id}>
                reflector {r.id}
              </option>
            ))}
          </select>
        </label>

        <div className="field">
          <span className="field-label">
            plugboard — {plugPairs.length} of {maxPlugs} cables
          </span>
          <div className="plug-editor">
            {plugPairs.map((pair) => (
              <span className="plug-tag" key={pair}>
                {pair[0]}—{pair[1]}
                <button type="button" onClick={() => removePlug(pair)} aria-label={`remove ${pair}`}>
                  ✕
                </button>
              </span>
            ))}
            {plugPairs.length === 0 && <span className="muted small">no cables</span>}
          </div>
          <div className="row">
            <select
              className="letter-select"
              value={plugA}
              onChange={(e) => setPlugA(e.target.value)}
            >
              <option value="">–</option>
              {[...alphabet]
                .filter((c) => !pluggedLetters.has(c))
                .map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
            </select>
            <select
              className="letter-select"
              value={plugB}
              onChange={(e) => setPlugB(e.target.value)}
            >
              <option value="">–</option>
              {[...alphabet]
                .filter((c) => !pluggedLetters.has(c) && c !== plugA)
                .map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
            </select>
            <button
              className="btn"
              type="button"
              onClick={addPlug}
              disabled={!plugA || !plugB || plugPairs.length >= maxPlugs}
            >
              add cable
            </button>
          </div>
        </div>

        <div className="row setup-actions">
          <button className="link-btn" type="button" onClick={randomise}>
            randomise everything
          </button>
          <button className="btn" onClick={create} disabled={busy || duplicateRotor}>
            create conversation
          </button>
        </div>

        {error && <p className="error">{error}</p>}
      </section>
    </main>
  )
}
