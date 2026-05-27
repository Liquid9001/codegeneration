<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-8 col-lg-6">
        <div class="card shadow-sm text-center p-4">
          <div class="card-body">
            <!-- Icon -->
            <div class="d-inline-flex align-items-center justify-content-center bg-warning-subtle rounded-circle mb-4" style="width: 80px; height: 80px; margin: 0 auto;">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#f59e0b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
            </div>

            <h2 class="h4 fw-bold mb-2">Account Pending Approval</h2>
            <p class="text-muted mb-3">
              Welcome, <strong>{{ userName }}</strong>! Your account has been registered successfully.
            </p>
            <p class="text-muted small mb-4">
              An ELS Bank employee will review and approve your account shortly. Once approved,
              you'll have access to your checking and savings accounts, transfers, and more.
            </p>

            <div class="alert alert-info small text-start" role="status">
              <strong>What happens next?</strong>
              <ul class="mb-0 mt-2 ps-3">
                <li>An employee reviews your registration</li>
                <li>Your checking and savings accounts are created</li>
                <li>You'll be able to start banking immediately</li>
              </ul>
            </div>

            <button class="btn btn-outline-secondary mt-3" @click="checkStatus">
              <span v-if="checking" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
              {{ checking ? 'Checking…' : 'Check approval status' }}
            </button>

            <div class="mt-3">
              <button class="btn btn-link text-muted small" @click="handleLogout">
                Sign out
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useToastStore } from '../stores/toast'

const auth = useAuthStore()
const toast = useToastStore()
const router = useRouter()

const checking = ref(false)

const userName = computed(() => {
  const u = auth.currentUser
  if (!u) return 'there'
  if (u.firstName && u.lastName) return `${u.firstName} ${u.lastName}`
  return u.email || 'there'
})

async function checkStatus() {
  checking.value = true
  try {
    await auth.refreshUser()
    if (auth.isApproved) {
      toast.success('Your account has been approved! Welcome to ELS Bank.')
      router.push('/dashboard')
    } else {
      toast.info('Your account is still pending approval. Please check back later.')
    }
  } catch {
    toast.error('Could not check account status.')
  } finally {
    checking.value = false
  }
}

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>
