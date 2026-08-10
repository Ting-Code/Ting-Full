import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { fetchMe, login as loginApi, type LoginData, type LoginPayload, type UserInfo } from '../api/auth'

interface AuthState {
  token: string | null
  user: UserInfo | null
  login: (payload: LoginPayload) => Promise<void>
  logout: () => void
  refreshMe: () => Promise<void>
}

const AuthContext = createContext<AuthState | null>(null)

function readUser(): UserInfo | null {
  const raw = localStorage.getItem('ting_user')
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserInfo
  } catch {
    return null
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('ting_token'))
  const [user, setUser] = useState<UserInfo | null>(() => readUser())

  const persist = useCallback((loginData: LoginData) => {
    localStorage.setItem('ting_token', loginData.token)
    const info: UserInfo = {
      id: loginData.userId,
      username: loginData.username,
      nickname: loginData.nickname,
      status: 1,
    }
    localStorage.setItem('ting_user', JSON.stringify(info))
    setToken(loginData.token)
    setUser(info)
  }, [])

  const login = useCallback(async (payload: LoginPayload) => {
    const data = await loginApi(payload)
    persist(data)
  }, [persist])

  const logout = useCallback(() => {
    localStorage.removeItem('ting_token')
    localStorage.removeItem('ting_user')
    setToken(null)
    setUser(null)
  }, [])

  const refreshMe = useCallback(async () => {
    const me = await fetchMe()
    localStorage.setItem('ting_user', JSON.stringify(me))
    setUser(me)
  }, [])

  const value = useMemo(
    () => ({ token, user, login, logout, refreshMe }),
    [token, user, login, logout, refreshMe],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
