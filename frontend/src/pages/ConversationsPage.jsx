import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'

export default function ConversationsPage() {
  const [conversations, setConversations] = useState([])
  const [machines, setMachines] = useState([])
  const [selectedMachine, setSelectedMachine] = useState('')
  const [inviteCode, setInviteCode] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const load = async () => {
    try {
      const [convs, machs] = await Promise.all([api.listConversations(), api.listMachines()])
      setConversations(convs)
      setMachines(machs)
      if (machs.length > 0 && !selectedMachine) setSelectedMachine(machs[0].name)
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const create = async () => {
    setError('')
    try {
      const conv = await api.createConversation(selectedMachine)
      navigate(`/chat/${conv.id}`)
    } catch (e) {
      setError(e.message)
    }
  }

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
          <select value={selectedMachine} onChange={(e) => setSelectedMachine(e.target.value)}>
            {machines.map((m) => (
              <option key={m.name} value={m.name}>
                {m.name} ({m.abc.length} letters, {m.rotorsInUse} rotors)
              </option>
            ))}
          </select>
          <button className="btn" onClick={create} disabled={!selectedMachine}>
            create + get invite code
          </button>
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
                {c.machineName} · {c.lastSeq} messages · rotors {c.currentPositionsCompact}
              </span>
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}
