import { createClient } from '@supabase/supabase-js'

// Auth adapter: real Supabase auth when the project env vars are configured,
// otherwise local dev-identity mode (X-Dev-User header understood by the
// backend's non-prod profile). Lets the app run fully offline in dev.

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY

export const supabase =
  supabaseUrl && supabaseAnonKey ? createClient(supabaseUrl, supabaseAnonKey) : null

export const isDevAuth = supabase === null

const DEV_IDS_KEY = 'enigma.devIdentities'
const DEV_ACTIVE_KEY = 'enigma.devActiveIdentity'

// Fixed UUIDs for the demo identities so they match test-chat-api.ps1
const WELL_KNOWN = {
  alice: '11111111-1111-1111-1111-111111111111',
  bob: '22222222-2222-2222-2222-222222222222',
}

export function devIdentityFor(name) {
  const key = name.trim().toLowerCase()
  if (WELL_KNOWN[key]) return WELL_KNOWN[key]
  const stored = JSON.parse(localStorage.getItem(DEV_IDS_KEY) || '{}')
  if (!stored[key]) {
    stored[key] = crypto.randomUUID()
    localStorage.setItem(DEV_IDS_KEY, JSON.stringify(stored))
  }
  return stored[key]
}

export function devLogin(name) {
  const userId = devIdentityFor(name)
  localStorage.setItem(DEV_ACTIVE_KEY, JSON.stringify({ name: name.trim(), userId }))
  return { userId, name: name.trim() }
}

export function devLogout() {
  localStorage.removeItem(DEV_ACTIVE_KEY)
}

export function devCurrentIdentity() {
  const raw = localStorage.getItem(DEV_ACTIVE_KEY)
  return raw ? JSON.parse(raw) : null
}

/** Returns { userId, headers } for the current session, or null if signed out. */
export async function currentAuth() {
  if (isDevAuth) {
    const identity = devCurrentIdentity()
    return identity ? { userId: identity.userId, headers: { 'X-Dev-User': identity.userId } } : null
  }
  const { data } = await supabase.auth.getSession()
  const session = data?.session
  if (!session) return null
  return {
    userId: session.user.id,
    headers: { Authorization: `Bearer ${session.access_token}` },
  }
}

export async function signOut() {
  if (isDevAuth) {
    devLogout()
  } else {
    await supabase.auth.signOut()
  }
}
