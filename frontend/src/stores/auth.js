import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/api'

function decodeJwt(token) {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    return JSON.parse(atob(payload))
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('els_token') || null)
  const user = ref(JSON.parse(localStorage.getItem('els_user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const isEmployee = computed(
    () => user.value?.role === 'EMPLOYEE' || user.value?.role === 'ADMIN'
  )
  const isApproved = computed(() => user.value?.approved === true)
  const currentUser = computed(() => user.value)

  async function login(email, password) {
    const { data } = await api.post('/users/login', { email, password })
    token.value = data.token
    localStorage.setItem('els_token', data.token)

    const claims = decodeJwt(data.token)
    const possibleId = claims?.userId ?? claims?.user_id ?? claims?.id

    if (possibleId && !isNaN(Number(possibleId))) {
      try {
        const userResp = await api.get(`/users/${possibleId}`)
        user.value = userResp.data
        localStorage.setItem('els_user', JSON.stringify(userResp.data))
      } catch {
        const fallback = {
          email: claims?.sub ?? email,
          role: claims?.role ?? claims?.roles?.[0] ?? 'CUSTOMER',
          approved: false,
        }
        user.value = fallback
        localStorage.setItem('els_user', JSON.stringify(fallback))
      }
    } else {
      const fallback = {
        email: claims?.sub ?? email,
        role: claims?.role ?? claims?.roles?.[0] ?? 'CUSTOMER',
        approved: false,
      }
      user.value = fallback
      localStorage.setItem('els_user', JSON.stringify(fallback))
    }

    return user.value
  }

  async function register(userData) {
    const { data } = await api.post('/users', userData)
    return data
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('els_token')
    localStorage.removeItem('els_user')
  }

  async function refreshUser() {
    if (!user.value?.id) return
    try {
      const { data } = await api.get(`/users/${user.value.id}`)
      user.value = data
      localStorage.setItem('els_user', JSON.stringify(data))
    } catch {
      // keep existing
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    isEmployee,
    isApproved,
    currentUser,
    login,
    register,
    logout,
    refreshUser,
  }
})
