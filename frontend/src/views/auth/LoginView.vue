<template>
  <div class="els-login-wrapper">
    <div class="els-login-card card shadow-lg">
      <div class="card-body p-4 p-sm-5">
        <!-- Logo & heading -->
        <div class="text-center mb-4">
          <div class="d-inline-flex align-items-center justify-content-center bg-primary rounded-3 mb-3" style="width: 56px; height: 56px;">
            <svg width="32" height="32" viewBox="0 0 28 28" fill="none" aria-hidden="true">
              <path d="M4 18 L14 8 L24 18" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
              <rect x="8" y="18" width="4" height="5" rx="1" fill="white" />
              <rect x="16" y="18" width="4" height="5" rx="1" fill="white" />
              <rect x="6" y="17" width="16" height="2" rx="1" fill="white" />
            </svg>
          </div>
          <h1 class="h4 fw-bold mb-1">Welcome to ELS Bank</h1>
          <p class="text-muted small">Sign in to your account</p>
        </div>

        <!-- Error alert -->
        <div v-if="errorMessage" class="alert alert-danger alert-dismissible" role="alert">
          {{ errorMessage }}
          <button type="button" class="btn-close" @click="errorMessage = ''" aria-label="Close"></button>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleLogin" novalidate>
          <div class="mb-3">
            <label for="email" class="form-label fw-medium">Email address</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              class="form-control"
              :class="{ 'is-invalid': v$.email.$error }"
              placeholder="you@example.com"
              autocomplete="email"
              aria-describedby="emailHelp"
              required
            />
            <div v-if="v$.email.$error" class="invalid-feedback">
              Please enter a valid email address.
            </div>
          </div>

          <div class="mb-4">
            <div class="d-flex justify-content-between align-items-center mb-1">
              <label for="password" class="form-label fw-medium mb-0">Password</label>
            </div>
            <div class="input-group">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-control"
                :class="{ 'is-invalid': v$.password.$error }"
                placeholder="Enter your password"
                autocomplete="current-password"
                required
              />
              <button
                type="button"
                class="btn btn-outline-secondary"
                :aria-label="showPassword ? 'Hide password' : 'Show password'"
                @click="showPassword = !showPassword"
              >
                {{ showPassword ? '🙈' : '👁' }}
              </button>
              <div v-if="v$.password.$error" class="invalid-feedback">
                Password is required.
              </div>
            </div>
          </div>

          <button
            type="submit"
            class="btn btn-primary w-100"
            :disabled="loading"
            aria-label="Sign in"
          >
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            {{ loading ? 'Signing in…' : 'Sign In' }}
          </button>
        </form>

        <hr class="my-4" />

        <p class="text-center text-muted small mb-0">
          Don't have an account?
          <router-link to="/register" class="fw-medium">Create account</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({ email: '', password: '' })
const loading = ref(false)
const errorMessage = ref('')
const showPassword = ref(false)

// Simple inline validation state
const v$ = reactive({
  email: { $error: false },
  password: { $error: false },
})

function validate() {
  v$.email.$error = !form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)
  v$.password.$error = !form.password
  return !v$.email.$error && !v$.password.$error
}

async function handleLogin() {
  errorMessage.value = ''
  if (!validate()) return

  loading.value = true
  try {
    const user = await auth.login(form.email, form.password)
    if (auth.isEmployee) {
      router.push('/employee')
    } else if (auth.isApproved) {
      router.push('/dashboard')
    } else {
      router.push('/welcome')
    }
  } catch (err) {
    errorMessage.value =
      err.response?.status === 401
        ? 'Invalid email or password. Please try again.'
        : err.response?.data?.message || 'Login failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
