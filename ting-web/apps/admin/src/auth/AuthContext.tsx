import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { ROLE_ADMIN, type LoginData, type LoginPayload, type UserInfo } from '@ting/shared'
import { fetchMe, login as loginApi } from '../api/auth'

interface AuthState {
  token: string | null
  user: UserInfo | null
  isAdmin: boolean
  login: (payload: LoginPayload) => Promise<void>
  logout: () => void
  refreshMe: () => Promise<void>
}

const AuthContext = createContext<AuthState | null>(null)

function readUser(): UserInfo | null {
  const raw = localStorage.getItem('ting_user')
  if (!raw) return null
  try {
    const user = JSON.parse(raw) as UserInfo
    if (!user.roles) user.roles = []
    return user
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
      roles: loginData.roles || [],
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
    if (!me.roles) me.roles = []
    localStorage.setItem('ting_user', JSON.stringify(me))
    setUser(me)
  }, [])

  const isAdmin = Boolean(user?.roles?.includes(ROLE_ADMIN))

  const value = useMemo(
    () => ({ token, user, isAdmin, login, logout, refreshMe }),
    [token, user, isAdmin, login, logout, refreshMe],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
