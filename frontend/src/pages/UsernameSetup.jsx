import { useState } from 'react'
import { api } from '../api'

export default function UsernameSetup({ onDone, onSignOut }) {
  const [username, setUsername] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await api.setUsername(username)
      onDone()
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="center-screen">
      <div className="card login-card">
        <h2>Choose your operator name</h2>
        <p className="muted small">3-20 characters: letters, digits, underscore.</p>
        <form className="row" onSubmit={submit}>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="e.g. station_x"
            autoFocus
          />
          <button className="btn" type="submit" disabled={busy}>
            set
          </button>
        </form>
        {error && <p className="error">{error}</p>}
        <button className="link-btn" onClick={onSignOut}>
          sign out
        </button>
      </div>
    </div>
  )
}
