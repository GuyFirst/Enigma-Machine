import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'
import EnigmaMachine from '../components/EnigmaMachine'
import { runMachine } from '../enigma/machine'
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
  const [showSettings, setShowSettings] = useState(false)
  const [copied, setCopied] = useState(false)
  // The conversation's shared machine position, kept in step by polling
  const [currentPositions, setCurrentPositions] = useState(null)

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
      // Where the shared machine now stands - the next message starts here
      setCurrentPositions(res.currentPositions)
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
        // One machine per conversation: it stands wherever the last message
        // left it, not back at the ground setting.
        setCurrentPositions([...conv.currentPositions])
        setDialPositions([...conv.currentPositions])
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

  // Keep the idle machine showing the shared position, so it visibly turns
  // when the other participant transmits. Operator mode is left alone - there
  // the rotors are yours to set.
  useEffect(() => {
    if (!currentPositions || manualMode || anim.status !== 'idle') return
    setDialPositions((prev) =>
      prev && prev.join('') === currentPositions.join('') ? prev : [...currentPositions],
    )
  }, [currentPositions, manualMode, anim.status])

  const machineConfig = useCallback(
    (positions) => ({
      rotorIds: conversation.rotorIds,
      positions,
      reflectorId: conversation.reflectorId,
      plugs: conversation.plugs,
    }),
    [conversation],
  )

  /**
   * Parks the rotors at `next`. Clearing the animation matters: while it holds
   * a run, its positions are what the machine displays and what a run uses, so
   * they would otherwise override the setting.
   */
  const setPositions = (next) => {
    anim.reset()
    setDialPositions(next)
  }

  const dialRotor = (index, delta) => {
    const next = [...(anim.positions ?? dialPositions)]
    const at = alphabet.indexOf(next[index])
    next[index] = alphabet[(at + delta + alphabet.length) % alphabet.length]
    setPositions(next)
  }

  /** Type -> watch it go through the machine -> then decide to send. */
  const runComposer = (e) => {
    e.preventDefault()
    if (!draft.trim() || anim.isRunning) return

    // The machine runs from wherever its rotors stand: in operator mode that is
    // whatever you dialled, otherwise the conversation's shared position, which
    // is where the previous message left the rotors.
    const startPositions = manualMode ? [...displayPositions] : [...currentPositions]

    const { output, steps, endPositions } = runMachine(
      wiring,
      machineConfig(startPositions),
      draft,
    )
    setLoaded(null)
    setPending({
      ciphertext: output,
      startPositions,
      endPositions,
      expectedSeq: afterSeqRef.current,
    })
    anim.play({ steps, startPositions })
  }

  const send = async () => {
    try {
      const sent = await api.sendMessage(conversationId, pending)
      mergeMessages([sent])
      if (sent.seq > afterSeqRef.current) afterSeqRef.current = sent.seq
      // The machine stays where this message left it, ready for the next one
      setCurrentPositions([...pending.endPositions])
      setDialPositions([...pending.endPositions])
      setPending(null)
      setDraft('')
      anim.reset()
      setError('')
    } catch (e) {
      setError(e.message)
      // 409: the other side transmitted first, so this was encrypted from a
      // position the machine has already left. Re-sync and let them redo it.
      if (e.status === 409) {
        setPending(null)
        await poll()
      }
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
    const { steps } = runMachine(wiring, machineConfig(positions), message.ciphertext)
    anim.play({ steps, startPositions: positions })
  }

  const waitingForPartner = conversation && conversation.participants.length < 2
  const plugPairCount = useMemo(
    () => Object.keys(conversation?.plugs ?? {}).length / 2,
    [conversation],
  )
  const rotorsMatchKey =
    loaded && displayPositions
      ? loaded.startPositions.join('') === displayPositions.join('')
      : false

  // The conversation's settings in a form you can read out or paste elsewhere
  const settingsText = useMemo(() => {
    if (!conversation) return ''
    const pairs = Object.entries(conversation.plugs)
      .filter(([from, to]) => from < to)
      .map(([from, to]) => `${from}${to}`)
    return [
      `machine:    ${conversation.machineName}`,
      `rotors:     ${conversation.rotorIds.join(' ')}   (left to right)`,
      `reflector:  ${conversation.reflectorId}`,
      `ground:     ${conversation.initialPositions.join(' ')}`,
      `plugboard:  ${pairs.length ? pairs.join(' ') : '(none)'}`,
      `invite:     ${conversation.inviteCode}`,
    ].join('\n')
  }, [conversation])

  const copySettings = async () => {
    try {
      await navigator.clipboard.writeText(settingsText)
      setCopied(true)
      setTimeout(() => setCopied(false), 1500)
    } catch {
      setError('Could not copy — select the text manually')
    }
  }

  if (!conversation || !wiring || !displayPositions || !currentPositions) {
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
          <button className="link-btn" onClick={() => setShowSettings((s) => !s)}>
            {showSettings ? 'hide settings' : 'machine settings'}
          </button>
          <span className="muted small">
            {conversation.machineName} · rotors {conversation.rotorIds.join('-')} · reflector{' '}
            {conversation.reflectorId} · {plugPairCount} plugs
          </span>
        </div>

        {showSettings && (
          <div className="settings-card">
            <div className="settings-row">
              <span className="muted small">code book page for this conversation</span>
              <button className="link-btn" onClick={copySettings}>
                {copied ? 'copied ✓' : 'copy'}
              </button>
            </div>
            <pre className="settings-text">{settingsText}</pre>
            <span className="muted small">
              Anyone joining with the invite code receives these settings automatically.
            </span>
          </div>
        )}

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
          {/* Ciphertext only - the readable text exists nowhere but the
              machine's output tape, and only while you are running it. */}
          {messages.map((m) => {
            const mine = m.senderId === me.userId
            const inMachine = loaded?.seq === m.seq
            return (
              <div key={m.seq} className={`intercept ${mine ? 'mine' : 'theirs'}`}>
                <div className="intercept-meta">
                  <span className="sender">{m.senderUsername}</span>
                  <span className="muted small">#{m.seq}</span>
                  <span className="key-chip" title="Rotor start positions for this message">
                    key {m.startPositions.join(' ')}
                  </span>
                </div>
                <div className="cipher-text">{m.ciphertext}</div>
                <button
                  className="feed-btn"
                  onClick={() => feedIntoMachine(m)}
                  disabled={anim.isRunning}
                >
                  {inMachine ? '↓ in the machine' : '↓ feed into machine'}
                </button>
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
          <span className="machine-at" title="The conversation's shared machine position">
            machine at <strong>{currentPositions.join(' ')}</strong>
          </span>
          <span className="muted small hint">
            {manualMode
              ? 'dial the rotors yourself — a message decrypts at the position it was sent from'
              : 'one machine per conversation: it keeps turning, so each message carries on from the last'}
          </span>
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

        {loaded && manualMode && (
          <div className="loaded-strip">
            <div className="dial-guide">
              <span className="muted small">
                message #{loaded.seq} loaded — set the rotors to its key, then run:
              </span>
              <div className="dial-compare">
                <span className="dial-target">
                  key <strong>{loaded.startPositions.join(' ')}</strong>
                </span>
                <span className={`dial-current ${rotorsMatchKey ? 'match' : ''}`}>
                  rotors <strong>{displayPositions.join(' ')}</strong>
                  {rotorsMatchKey && ' ✓'}
                </span>
              </div>
            </div>
            <div className="row">
              <button
                className="link-btn"
                onClick={() => setPositions([...loaded.startPositions])}
                disabled={anim.isRunning || rotorsMatchKey}
              >
                set for me
              </button>
              <button
                className="btn"
                onClick={() => runDecrypt(loaded, displayPositions)}
                disabled={anim.isRunning}
              >
                run
              </button>
            </div>
          </div>
        )}

        {pending ? (
          <div className="pending-strip">
            <div className="pending-info">
              <span className="muted small">
                machine output · key {pending.startPositions.join(' ')}
              </span>
              <div className="pending-cipher">{pending.ciphertext}</div>
            </div>
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
          <form className="composer" onSubmit={runComposer}>
            <input
              value={draft}
              onChange={(e) => setDraft(e.target.value)}
              placeholder={
                manualMode
                  ? `Type text to run through the machine at ${displayPositions.join('')}…`
                  : `Type your message — the machine will run it from ${currentPositions.join('')}…`
              }
              maxLength={400}
              disabled={anim.isRunning}
            />
            <button className="btn" type="submit" disabled={!draft.trim() || anim.isRunning}>
              {manualMode ? 'run' : 'encrypt'}
            </button>
          </form>
        )}

        {error && <p className="error">{error}</p>}
      </section>
    </main>
  )
}
