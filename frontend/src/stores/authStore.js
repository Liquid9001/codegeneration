import { defineStore } from 'pinia'
import * as authApi from '../api/authApi'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token'),
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),

  getters: {
    isAuthenticated: state => !!state.token,
    isEmployee: state => state.user?.role === 'EMPLOYEE'
  },

  actions: {
    async login(credentials) {
      const data = await authApi.login(credentials)

      this.token = data.token

      localStorage.setItem('token', data.token)

      let payload = {}

      try {
        payload = JSON.parse(atob(data.token.split('.')[1]))
      } catch {
        payload = {
          role: 'CUSTOMER'
        }
      }

      this.user = payload

      localStorage.setItem('user', JSON.stringify(payload))
    },

    logout() {
      this.token = null
      this.user = null

      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})