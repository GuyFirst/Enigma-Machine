# Deploying Enigma Chat (all free tiers)

Stack: **Supabase** (Postgres + auth) · **Render** (Spring Boot backend) · **Vercel** (React frontend).
All three have free tiers that don't require a credit card. Account creation must be done
by you; everything else is wired up by the config files already in this repo.

## 1. Supabase (database + user accounts)

1. Sign up at https://supabase.com and create a new project (pick a region near you,
   note the database password you choose).
2. Collect four values:
   - **DB connection**: Project Settings → Database → Connection string (URI). Convert to JDBC form:
     `jdbc:postgresql://<host>:5432/postgres?sslmode=require` plus the `postgres` username
     and your DB password. (If you use the pooled connection string, keep port 6543 and
     add `?sslmode=require` too.)
   - **JWKS URI**: `https://<project-ref>.supabase.co/auth/v1/.well-known/jwks.json`
     (project-ref is in your project URL).
   - **Project URL** and **anon public key**: Project Settings → API (for the frontend).
3. Authentication → Providers → Email: enabled by default. For a demo you may want to
   disable "Confirm email" so signups work instantly.

## 2. Render (backend)

1. Sign up at https://render.com with your GitHub account.
2. New → **Blueprint** → connect `GuyFirst/Enigma-Machine`. Render reads `render.yaml`
   and creates the `enigma-chat-backend` docker service.
3. Fill the env vars it prompts for:

   | Var | Value |
   |---|---|
   | `SPRING_DATASOURCE_URL` | the JDBC URL from step 1 |
   | `SPRING_DATASOURCE_USERNAME` | `postgres` |
   | `SPRING_DATASOURCE_PASSWORD` | your Supabase DB password |
   | `SUPABASE_JWKS_URI` | the JWKS URI from step 1 |
   | `ALLOWED_ORIGINS` | fill after step 3: `https://<your-app>.vercel.app` |
   | `AI_API_KEY` | optional (legacy /enigma/ai only) - leave blank |

4. Deploy. First build takes a few minutes. Your backend URL will be
   `https://enigma-chat-backend-XXXX.onrender.com` - verify `GET /healthz` returns `{"status":"ok"}`.

   **Free-tier note:** the service sleeps after ~15 min idle; the first request after
   that takes ~30-60s to wake up.

## 3. Vercel (frontend)

1. Sign up at https://vercel.com with your GitHub account.
2. Add New → Project → import `GuyFirst/Enigma-Machine`.
3. **Root Directory: `frontend`** (important). Framework preset: Vite.
4. Environment variables:

   | Var | Value |
   |---|---|
   | `VITE_API_URL` | your Render backend URL (no trailing slash) |
   | `VITE_SUPABASE_URL` | Supabase Project URL |
   | `VITE_SUPABASE_ANON_KEY` | Supabase anon public key |

5. Deploy → you get `https://<your-app>.vercel.app`.
6. Go back to Render and set `ALLOWED_ORIGINS` to exactly that origin.

## 4. Smoke test

Open the Vercel URL in two browsers (or one normal + one incognito), sign up two
users, set usernames, create a conversation with one, join with the invite code from
the other, and chat. The DB only ever stores ciphertext - check the `chat_messages`
table in Supabase's Table Editor to see it.

## Local development (no cloud needed)

Backend: `run-server.bat` (H2 in-memory, X-Dev-User auth).
Frontend: `cd frontend && npm install && npm run dev` → http://localhost:5173
(dev-identity mode is automatic when Supabase env vars are absent).
API smoke tests: `test-chat-api.ps1` (chat) and `test-api.ps1` (legacy course API).
