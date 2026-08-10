import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'
import EnigmaMachine from '../components/EnigmaMachine'
import { randomPositions, runMachine } from '../enigma/machine'
import { useMachineAnimation } from '../enigma/useMachineAnimation'

const POLL_INTERVAL_MS = 3000

export default function ChatPage({ me }) {
  const { conversationId } = useParams()
  const navigate = useNavigate()

  const [conversation, setConversation] = useState(null)
  const [wiring, setWiring] = useState(null)
  const [messages, setMessages] = useState([])
  const [error, setError] = useState('')

  const [draft, setDraft] = useState('')
  const [pending, setPending] = useState(null) // encrypted, not sent yet
  const [loaded, setLoaded] = useState(null) // message sitting in the machine (manual mode)
  const [manualMode, setManualMode] = useState(false)
  const [dialPositions, setDialPositions] = useState(null)

  // Decrypted text lives only here, in memory: refresh the page and the
  // conversation is ciphertext again, exactly like a real intercept log.
  const [decrypted, setDecrypted] = useState({})

  const anim = useMachineAnimation()
  const afterSeqRef = useRef(0)
  const conversationRef = useRef(null)
  const logEndRef = useRef(null)

  const alphabet = conversation?.alphabet ?? ''
  const displayPositions = anim.positions ?? dialPositions

  const mergeMessages = useCallback((incoming) => {
    if (!incoming.length) return
    setMessages((prev) => {
      const known = new Set(prev.map((m) => m.seq))
      const merged = [...prev, ...incoming.filter((m) => !known.has(m.seq))]
      merged.sort((a, b) => a.seq - b.seq)
      return merged
    })
  }, [])

  const poll = useCallback(async () => {
    try {
      const res = await api.getMessages(conversationId, afterSeqRef.current)
      mergeMessages(res.messages)
      if (res.lastSeq > afterSeqRef.current) afterSeqRef.current = res.lastSeq
      if ((conversationRef.current?.participants?.length ?? 0) < 2) {
        const fresh = await api.getConversation(conversationId)
        conversationRef.current = fresh
        setConversation(fresh)
      }
      setError('')
    } catch (e) {
      setError(e.message)
    }
  }, [conversationId, mergeMessages])

  useEffect(() => {
    let timer
    ;(async () => {
      try {
        const conv = await api.getConversation(conversationId)
        conversationRef.current = conv
        setConversation(conv)
        setWiring(await api.getMachineWiring(conv.machineName))
        setDialPositions(Array(conv.rotorIds.length).fill(conv.alphabet[0]))
        await poll()
        timer = setInterval(poll, POLL_INTERVAL_MS)
      } catch (e) {
        setError(e.message)
      }
    })()
    return () => clearInterval(timer)
  }, [conversationId, poll])

  useEffect(() => {
    logEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const machineConfig = useCallback(
    (positions) => ({
      rotorIds: conversation.rotorIds,
      positions,
      reflectorId: conversation.reflectorId,
      plugs: conversation.plugs,
    }),
    [conversation],
  )

  const dialRotor = (index, delta) => {
    const base = anim.positions ?? dialPositions
    const next = [...base]
    const at = alphabet.indexOf(next[index])
    next[index] = alphabet[(at + delta + alphabet.length) % alphabet.length]
    anim.reset()
    setDialPositions(next)
  }

  /** Type -> watch it go through the machine -> then decide to send. */
  const encrypt = (e) => {
    e.preventDefault()
    if (!draft.trim() || anim.isRunning) return

    const startPositions = randomPositions(alphabet, conversation.rotorIds.length)
    const { output, steps } = runMachine(wiring, machineConfig(startPositions), draft)
    setLoaded(null)
    setPending({ ciphertext: output, startPositions, plaintext: draft.toUpperCase() })
    anim.play({ steps, startPositions })
  }

  const send = async () => {
    try {
      const sent = await api.sendMessage(conversationId, pending.ciphertext, pending.startPositions)
      mergeMessages([sent])
      if (sent.seq > afterSeqRef.current) afterSeqRef.current = sent.seq
      setDecrypted((prev) => ({ ...prev, [sent.seq]: pending.plaintext }))
      setPending(null)
      setDraft('')
      anim.reset()
    } catch (e) {
      setError(e.message)
    }
  }

  const discard = () => {
    setPending(null)
    anim.reset()
  }

  /** Put a received message into the machine. */
  const feedIntoMachine = (message) => {
    if (anim.isRunning) return
    setPending(null)
    setLoaded(message)

    if (manualMode) {
      // Operator mode: the rotors stay where they are - dial them yourself
      return
    }
    runDecrypt(message, message.startPositions)
  }

  const runDecrypt = (message, positions) => {
    const { output, steps } = runMachine(wiring, machineConfig(positions), message.ciphertext)
    anim.play({
      steps,
      startPositions: positions,
      onDone: () => setDecrypted((prev) => ({ ...prev, [message.seq]: output })),
    })
  }

  const waitingForPartner = conversation && conversation.participants.length < 2
  const plugPairCount = useMemo(
    () => Object.keys(conversation?.plugs ?? {}).length / 2,
    [conversation],
  )

  if (!conversation || !wiring || !displayPositions) {
    return <div className="center-screen">{error || 'Warming up the machine…'}</div>
  }

  return (
    <main className="desk">
      <section className="log-pane">
        <div className="log-header">
          <button className="link-btn" onClick={() => navigate('/')}>
            ← back
          </button>
          <span className="conv-title">
            {conversation.participants.map((p) => p.username).join(' ⇄ ')}
          </span>
          <span className="muted small">
            {conversation.machineName} · rotors {conversation.rotorIds.join('-')} · reflector{' '}
            {conversation.reflectorId} · {plugPairCount} plugs
          </span>
        </div>

        {waitingForPartner && (
          <div className="invite-banner">
            Waiting for a partner — share this invite code:{' '}
            <code className="invite-code">{conversation.inviteCode}</code>
          </div>
        )}

        <div className="log">
          {messages.length === 0 && (
            <p className="muted small">
              No traffic yet. Type below, run it through the machine, then transmit.
            </p>
          )}
          {messages.map((m) => {
            const mine = m.senderId === me.userId
            const plain = decrypted[m.seq]
            return (
              <div key={m.seq} className={`intercept ${mine ? 'mine' : 'theirs'}`}>
                <div className="intercept-meta">
                  <span className="sender">{m.senderUsername}</span>
                  <span className="muted small">
                    #{m.seq} · key {m.startPositions.join('')}
                  </span>
                </div>
                <div className="cipher-text">{m.ciphertext}</div>
                {plain ? (
                  <div className="plain-text">{plain}</div>
                ) : (
                  <button
                    className="feed-btn"
                    onClick={() => feedIntoMachine(m)}
                    disabled={anim.isRunning}
                  >
                    ↓ feed into machine
                  </button>
                )}
              </div>
            )
          })}
          <div ref={logEndRef} />
        </div>
      </section>

      <section className="machine-pane">
        <div className="machine-toolbar">
          <label className="toggle">
            <input
              type="checkbox"
              checked={manualMode}
              onChange={(e) => setManualMode(e.target.checked)}
            />
            operator mode (dial rotors yourself)
          </label>
          {anim.isRunning && (
            <button className="link-btn" onClick={anim.skip}>
              skip animation
            </button>
          )}
        </div>

        <EnigmaMachine
          alphabet={alphabet}
          rotorIds={conversation.rotorIds}
          positions={displayPositions}
          reflectorId={conversation.reflectorId}
          plugs={conversation.plugs}
          step={anim.step}
          manualMode={manualMode && !anim.isRunning}
          onDialRotor={dialRotor}
          inputTape={anim.input}
          outputTape={anim.output}
        />

        {loaded && (
          <div className="loaded-strip">
            <span className="muted small">
              in the machine: message #{loaded.seq} · key {loaded.startPositions.join('')}
            </span>
            {manualMode && (
              <button
                className="btn"
                onClick={() => runDecrypt(loaded, displayPositions)}
                disabled={anim.isRunning}
              >
                run
              </button>
            )}
          </div>
        )}

        {pending ? (
          <div className="pending-strip">
            <div className="pending-cipher">{pending.ciphertext}</div>
            <div className="row">
              <button className="btn" onClick={send} disabled={anim.isRunning}>
                transmit
              </button>
              <button className="link-btn" onClick={discard}>
                discard
              </button>
            </div>
          </div>
        ) : (
          <form className="composer" onSubmit={encrypt}>
            <input
              value={draft}
              onChange={(e) => setDraft(e.target.value)}
              placeholder="Type your message, then run it through the machine…"
              maxLength={400}
              disabled={anim.isRunning}
            />
            <button className="btn" type="submit" disabled={!draft.trim() || anim.isRunning}>
              encrypt
            </button>
          </form>
        )}

        {error && <p className="error">{error}</p>}
      </section>
    </main>
  )
}
