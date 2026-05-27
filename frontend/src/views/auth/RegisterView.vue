<template>
  <div class="els-login-wrapper">
    <div class="card shadow-lg" style="width: 100%; max-width: 540px; border: none; border-radius: 1rem;">
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
          <h1 class="h4 fw-bold mb-1">Open an ELS Bank Account</h1>
          <p class="text-muted small">Fill in your details to register</p>
        </div>

        <!-- Error alert -->
        <div v-if="errorMessage" class="alert alert-danger alert-dismissible" role="alert">
          {{ errorMessage }}
          <button type="button" class="btn-close" @click="errorMessage = ''" aria-label="Close"></button>
        </div>

        <!-- Success -->
        <div v-if="registered" class="alert alert-success" role="alert">
          <strong>Registration successful!</strong> Your account is pending employee approval.
          You can <router-link to="/login">log in</router-link> now.
        </div>

        <form v-if="!registered" @submit.prevent="handleRegister" novalidate>
          <!-- Name row -->
          <div class="row g-3 mb-3">
            <div class="col-sm-6">
              <label for="firstName" class="form-label fw-medium">First name</label>
              <input
                id="firstName"
                v-model="form.first_name"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': errors.first_name }"
                placeholder="Jan"
                autocomplete="given-name"
                required
              />
              <div v-if="errors.first_name" class="invalid-feedback">First name is required.</div>
            </div>
            <div class="col-sm-6">
              <label for="lastName" class="form-label fw-medium">Last name</label>
              <input
                id="lastName"
                v-model="form.last_name"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': errors.last_name }"
                placeholder="de Vries"
                autocomplete="family-name"
                required
              />
              <div v-if="errors.last_name" class="invalid-feedback">Last name is required.</div>
            </div>
          </div>

          <!-- Email -->
          <div class="mb-3">
            <label for="regEmail" class="form-label fw-medium">Email address</label>
            <input
              id="regEmail"
              v-model="form.email"
              type="email"
              class="form-control"
              :class="{ 'is-invalid': errors.email }"
              placeholder="jan@example.com"
              autocomplete="email"
              required
            />
            <div v-if="errors.email" class="invalid-feedback">
              {{ errors.email }}
            </div>
          </div>

          <!-- Password -->
          <div class="mb-3">
            <label for="regPassword" class="form-label fw-medium">Password</label>
            <div class="input-group">
              <input
                id="regPassword"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-control"
                :class="{ 'is-invalid': errors.password }"
                placeholder="Min. 8 characters"
                autocomplete="new-password"
                required
              />
              <button type="button" class="btn btn-outline-secondary" @click="showPassword = !showPassword" :aria-label="showPassword ? 'Hide password' : 'Show password'">
                {{ showPassword ? '🙈' : '👁' }}
              </button>
              <div v-if="errors.password" class="invalid-feedback">{{ errors.password }}</div>
            </div>
          </div>

          <!-- Phone + BSN -->
          <div class="row g-3 mb-3">
            <div class="col-sm-6">
              <label for="phone" class="form-label fw-medium">Phone number</label>
              <input
                id="phone"
                v-model="form.phone_number"
                type="tel"
                class="form-control"
                :class="{ 'is-invalid': errors.phone_number }"
                placeholder="0612345678"
                autocomplete="tel"
                required
              />
              <div v-if="errors.phone_number" class="invalid-feedback">Enter a valid phone number (digits only).</div>
            </div>
            <div class="col-sm-6">
              <label for="bsn" class="form-label fw-medium">BSN number</label>
              <input
                id="bsn"
                v-model="form.bsn"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': errors.bsn }"
                placeholder="123456789"
                maxlength="9"
                required
              />
              <div v-if="errors.bsn" class="invalid-feedback">Enter a valid 9-digit BSN.</div>
            </div>
          </div>

          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            {{ loading ? 'Registering…' : 'Create Account' }}
          </button>
        </form>

        <hr class="my-4" />
        <p class="text-center text-muted small mb-0">
          Already have an account?
          <router-link to="/login" class="fw-medium">Sign in</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()

const form = reactive({
  first_name: '',
  last_name: '',
  email: '',
  password: '',
  phone_number: '',
  bsn: '',
})

const errors = reactive({})
const loading = ref(false)
const registered = ref(false)
const errorMessage = ref('')
const showPassword = ref(false)

function validate() {
  Object.keys(errors).forEach((k) => delete errors[k])
  if (!form.first_name) errors.first_name = true
  if (!form.last_name) errors.last_name = true
  if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
    errors.email = 'Please enter a valid email address.'
  if (!form.password || form.password.length < 8)
    errors.password = 'Password must be at least 8 characters.'
  if (!form.phone_number || !/^\d{8,15}$/.test(form.phone_number))
    errors.phone_number = true
  if (!form.bsn || !/^\d{8,9}$/.test(form.bsn))
    errors.bsn = true
  return Object.keys(errors).length === 0
}

async function handleRegister() {
  errorMessage.value = ''
  if (!validate()) return

  loading.value = true
  try {
    await auth.register({
      first_name: form.first_name,
      last_name: form.last_name,
      email: form.email,
      password: form.password,
      phone_number: parseInt(form.phone_number),
      bsn: parseInt(form.bsn),
    })
    registered.value = true
  } catch (err) {
    errorMessage.value =
      err.response?.status === 400
        ? err.response?.data?.message || 'Registration failed. Email may already be in use.'
        : 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
