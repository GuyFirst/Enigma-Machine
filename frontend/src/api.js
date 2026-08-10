import { currentAuth } from './auth'

const BASE = import.meta.env.VITE_API_URL || ''

class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.status = status
  }
}

async function request(method, path, body) {
  const auth = await currentAuth()
  if (!auth) throw new ApiError(401, 'Not signed in')

  const res = await fetch(`${BASE}${path}`, {
    method,
    headers: {
      ...auth.headers,
      ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (res.status === 204) return null
  let data = null
  try {
    data = await res.json()
  } catch {
    /* non-JSON body */
  }
  if (!res.ok) {
    throw new ApiError(res.status, data?.error || `Request failed (${res.status})`)
  }
  return data
}

export const api = {
  getProfile: () => request('GET', '/api/profile'),
  setUsername: (username) => request('PUT', '/api/profile', { username }),
  listMachines: () => request('GET', '/api/machines'),
  getMachineWiring: (name) => request('GET', `/api/machines/${encodeURIComponent(name)}/wiring`),
  listConversations: () => request('GET', '/api/conversations'),
  getConversation: (id) => request('GET', `/api/conversations/${id}`),
  // setup: {machineName, rotorIds, reflectorId, plugPairs, initialPositions}
  createConversation: (setup) => request('POST', '/api/conversations', setup),
  joinConversation: (inviteCode) => request('POST', '/api/conversations/join', { inviteCode }),
  getMessages: (conversationId, afterSeq = 0) =>
    request('GET', `/api/conversations/${conversationId}/messages?afterSeq=${afterSeq}`),
  // The browser encrypts; only ciphertext and its message key leave the client
  sendMessage: (conversationId, ciphertext, startPositions) =>
    request('POST', `/api/conversations/${conversationId}/messages`, {
      ciphertext,
      startPositions,
    }),
}

export { ApiError }
