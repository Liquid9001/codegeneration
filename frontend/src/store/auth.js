import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: localStorage.getItem('user')
      ? JSON.parse(localStorage.getItem('user'))
      : null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    currentUser: (state) => state.user
  },
  actions: {
    login(token, user) {
      this.token = token;
      localStorage.setItem('token', token);
      this.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    },
    logout() {
      this.token = null;
      localStorage.removeItem('token');
      this.user = null;
      localStorage.removeItem('user');
    }
  }
});
