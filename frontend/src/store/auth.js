import { defineStore } from 'pinia';
import { jwtDecode } from 'jwt-decode';

const isExpiredToken = (token) => {
  if (!token) {
    return true;
  }

  try {
    const decodedToken = jwtDecode(token);

    if (!decodedToken.exp) {
      return false;
    }

    return decodedToken.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
};

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: localStorage.getItem('user')
      ? JSON.parse(localStorage.getItem('user'))
      : null
  }),
  getters: {
    isAuthenticated: (state) => !!state.token && !isExpiredToken(state.token),
    isLoggedIn: (state) => !!state.token && !isExpiredToken(state.token),
    currentUser: (state) => state.user,
    isTokenExpired: (state) => isExpiredToken(state.token),
    userRole: (state) => state.user?.role || null,
    isEmployeeOrAdmin: (state) => state.user?.role === 'EMPLOYEE' || state.user?.role === 'ADMIN',
    isApproved: (state) => !!state.user?.approved
  },
  actions: {
    login(token, user) {
      this.token = token;
      localStorage.setItem('token', token);
      this.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    },
    clearAuth() {
      this.token = null;
      localStorage.removeItem('token');
      this.user = null;
      localStorage.removeItem('user');
    },
    logout() {
      this.clearAuth();
    },
    ensureValidSession() {
      if (this.token && isExpiredToken(this.token)) {
        this.clearAuth();
        return false;
      }

      return !!this.token;
    }
  }
});
