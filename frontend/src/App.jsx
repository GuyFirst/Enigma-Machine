import { useCallback, useEffect, useState } from 'react'
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import { api, ApiError } from './api'
import { currentAuth, isDevAuth, signOut, supabase } from './auth'
import LoginPage from './pages/LoginPage'
import ConversationsPage from './pages/ConversationsPage'
import ChatPage from './pages/ChatPage'
import UsernameSetup from './pages/UsernameSetup'

export default function App() {
  const [auth, setAuth] = useState(undefined) // undefined = loading, null = signed out
  const [profile, setProfile] = useState(undefined)
  const navigate = useNavigate()

  const refreshAuth = useCallback(async () => {
    const a = await currentAuth()
    setAuth(a)
    if (!a) {
      setProfile(null)
      return
    }
    try {
      setProfile(await api.getProfile())
    } catch (e) {
      // 404 = authenticated but no username chosen yet
      setProfile(e instanceof ApiError && e.status === 404 ? null : null)
    }
  }, [])

  useEffect(() => {
    refreshAuth()
    if (!isDevAuth) {
      const { data: sub } = supabase.auth.onAuthStateChange(() => refreshAuth())
      return () => sub.subscription.unsubscribe()
    }
  }, [refreshAuth])

  const handleSignOut = async () => {
    await signOut()
    await refreshAuth()
    navigate('/')
  }

  if (auth === undefined) {
    return <div className="center-screen">Loading…</div>
  }

  if (!auth) {
    return <LoginPage onLoggedIn={refreshAuth} />
  }

  if (!profile) {
    return <UsernameSetup onDone={refreshAuth} onSignOut={handleSignOut} />
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <span className="brand">⚙ ENIGMA CHAT</span>
        <span className="topbar-right">
          <span className="username">{profile.username}</span>
          <button className="link-btn" onClick={handleSignOut}>
            sign out
          </button>
        </span>
      </header>
      <Routes>
        <Route path="/" element={<ConversationsPage />} />
        <Route path="/chat/:conversationId" element={<ChatPage me={profile} />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  )
}
