import { useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api'

const POLL_INTERVAL_MS = 3000

export default function ChatPage({ me }) {
  const { conversationId } = useParams()
  const [conversation, setConversation] = useState(null)
  const [messages, setMessages] = useState([])
  const [positionsCompact, setPositionsCompact] = useState('')
  const [text, setText] = useState('')
  const [showCipher, setShowCipher] = useState(true)
  const [error, setError] = useState('')
  const afterSeqRef = useRef(0)
  const conversationRef = useRef(null)
  const bottomRef = useRef(null)
  const navigate = useNavigate()

  const mergeMessages = useCallback((incoming) => {
    if (incoming.length === 0) return
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
      setPositionsCompact(res.currentPositionsCompact)
      // While waiting for a partner, keep refreshing so the invite banner
      // disappears as soon as the second participant joins
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
        await poll()
        timer = setInterval(poll, POLL_INTERVAL_MS)
      } catch (e) {
        setError(e.message)
      }
    })()
    return () => clearInterval(timer)
  }, [conversationId, poll])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const send = async (e) => {
    e.preventDefault()
    if (!text.trim()) return
    const outgoing = text
    setText('')
    try {
      const sent = await api.sendMessage(conversationId, outgoing)
      mergeMessages([sent])
      if (sent.seq > afterSeqRef.current) afterSeqRef.current = sent.seq
      await poll()
    } catch (e2) {
      setError(e2.message)
      setText(outgoing)
    }
  }

  const waitingForPartner = conversation && conversation.participants.length < 2

  return (
    <main className="chat-page">
      <div className="chat-header">
        <button className="link-btn" onClick={() => navigate('/')}>
          ← back
        </button>
        {conversation && (
          <>
            <span className="conv-title">
              {conversation.participants.map((p) => p.username).join(' ⇄ ')}
            </span>
            <span className="machine-state" title="rotor positions (distance to notch)">
              {conversation.machineName} · rotors: {positionsCompact || conversation.currentPositionsCompact}
            </span>
            <label className="toggle">
              <input
                type="checkbox"
                checked={showCipher}
                onChange={(e) => setShowCipher(e.target.checked)}
              />
              show ciphertext
            </label>
          </>
        )}
      </div>

      {waitingForPartner && (
        <div className="invite-banner">
          Waiting for a partner - share this invite code:{' '}
          <code className="invite-code">{conversation.inviteCode}</code>
        </div>
      )}

      <div className="messages">
        {messages.map((m) => (
          <div key={m.seq} className={`bubble ${m.senderId === me.userId ? 'mine' : 'theirs'}`}>
            <div className="bubble-meta">
              <span className="sender">{m.senderUsername}</span>
              <span className="muted small">#{m.seq}</span>
            </div>
            {showCipher && <div className="cipher">{m.ciphertext}</div>}
            <div className="plain">{m.plaintext}</div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      {error && <p className="error chat-error">{error}</p>}

      <form className="composer" onSubmit={send}>
        <input
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Type a message… (letters get encrypted, the rest passes through)"
          maxLength={500}
          autoFocus
        />
        <button className="btn" type="submit" disabled={!text.trim()}>
          encrypt + send
        </button>
      </form>
    </main>
  )
}
