import { useState } from 'react'
import { devLogin, isDevAuth, supabase } from '../auth'

export default function LoginPage({ onLoggedIn }) {
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  return (
    <div className="center-screen">
      <div className="card login-card">
        <h1 className="brand big">⚙ ENIGMA CHAT</h1>
        <p className="muted">
          Encrypted correspondence over a simulated Enigma machine.
        </p>
        {isDevAuth ? <DevLogin onLoggedIn={onLoggedIn} setError={setError} /> : (
          <SupabaseLogin onLoggedIn={onLoggedIn} setError={setError} busy={busy} setBusy={setBusy} />
        )}
        {error && <p className="error">{error}</p>}
      </div>
    </div>
  )
}

function DevLogin({ onLoggedIn, setError }) {
  const [name, setName] = useState('')

  const login = (identity) => {
    if (!identity.trim()) {
      setError('Enter an identity name')
      return
    }
    devLogin(identity)
    onLoggedIn()
  }

  return (
    <>
      <p className="muted small">
        DEV MODE - no Supabase configured. Pick a local identity:
      </p>
      <div className="row">
        <button className="btn" onClick={() => login('alice')}>alice</button>
        <button className="btn" onClick={() => login('bob')}>bob</button>
      </div>
      <form
        className="row"
        onSubmit={(e) => {
          e.preventDefault()
          login(name)
        }}
      >
        <input
          placeholder="or any identity name…"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <button className="btn" type="submit">enter</button>
      </form>
    </>
  )
}

function SupabaseLogin({ onLoggedIn, setError, busy, setBusy }) {
  const [mode, setMode] = useState('signin')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const fn =
        mode === 'signin'
          ? supabase.auth.signInWithPassword({ email, password })
          : supabase.auth.signUp({ email, password })
      const { error } = await fn
      if (error) throw error
      onLoggedIn()
    } catch (err) {
      setError(err.message || 'Authentication failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <form className="col" onSubmit={submit}>
      <input
        type="email"
        placeholder="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
        minLength={6}
      />
      <button className="btn" type="submit" disabled={busy}>
        {mode === 'signin' ? 'Sign in' : 'Create account'}
      </button>
      <button
        type="button"
        className="link-btn"
        onClick={() => setMode(mode === 'signin' ? 'signup' : 'signin')}
      >
        {mode === 'signin' ? 'No account? Sign up' : 'Have an account? Sign in'}
      </button>
    </form>
  )
}
