import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'

export default function ConversationsPage() {
  const [conversations, setConversations] = useState([])
  const [inviteCode, setInviteCode] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    api.listConversations().then(setConversations).catch((e) => setError(e.message))
  }, [])

  const join = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const conv = await api.joinConversation(inviteCode)
      navigate(`/chat/${conv.id}`)
    } catch (e2) {
      setError(e2.message)
    }
  }

  return (
    <main className="page">
      <section className="card">
        <h2>Start a conversation</h2>
        <div className="row">
          <button className="btn" onClick={() => navigate('/new')}>
            set up a machine
          </button>
          <span className="muted small">
            choose the rotors, reflector, plugboard and ground position
          </span>
        </div>
        <form className="row" onSubmit={join}>
          <input
            placeholder="or paste an invite code…"
            value={inviteCode}
            onChange={(e) => setInviteCode(e.target.value.toUpperCase())}
          />
          <button className="btn" type="submit" disabled={!inviteCode.trim()}>
            join
          </button>
        </form>
        {error && <p className="error">{error}</p>}
      </section>

      <section className="card">
        <h2>Your conversations</h2>
        {conversations.length === 0 && (
          <p className="muted">None yet - create one and share the invite code.</p>
        )}
        <ul className="conv-list">
          {conversations.map((c) => (
            <li key={c.id} onClick={() => navigate(`/chat/${c.id}`)}>
              <span className="conv-title">
                {c.participants.map((p) => p.username).join(' ⇄ ') || c.machineName}
              </span>
              <span className="muted small">
                {c.machineName} · {c.lastSeq} messages · rotors {c.rotorIds.join('-')} · reflector{' '}
                {c.reflectorId}
              </span>
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}
