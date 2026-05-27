<template>
  <div class="row justify-content-center mt-5">
    <div class="col-md-5">
      <div class="card shadow">
        <div class="card-body p-4">
          <h2 class="mb-4 text-center">
            ELS Bank Login
          </h2>

          <div
            v-if="error"
            class="alert alert-danger"
          >
            {{ error }}
          </div>

          <form @submit.prevent="submit">
            <div class="mb-3">
              <label class="form-label">Email</label>

              <input
                v-model="form.email"
                type="email"
                class="form-control"
                required
              />
            </div>

            <div class="mb-3">
              <label class="form-label">Password</label>

              <input
                v-model="form.password"
                type="password"
                class="form-control"
                required
              />
            </div>

            <button
              class="btn btn-primary w-100"
              :disabled="loading"
            >
              Login
            </button>
          </form>

          <router-link
            to="/register"
            class="btn btn-link w-100 mt-3"
          >
            Create Account
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const form = reactive({
  email: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

const authStore = useAuthStore()
const router = useRouter()

const submit = async () => {
  loading.value = true
  error.value = ''

  try {
    await authStore.login(form)

    if (authStore.isEmployee) {
      router.push('/employee')
    } else {
      router.push('/dashboard')
    }

  } catch (e) {
    error.value = 'Invalid credentials'
  }

  loading.value = false
}
</script>