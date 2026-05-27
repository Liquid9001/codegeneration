<template>
  <div class="row justify-content-center">
    <div class="col-lg-7">
      <div class="card shadow">
        <div class="card-body p-4">
          <h2 class="mb-4">
            Register Customer
          </h2>

          <form @submit.prevent="submit">

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">First Name</label>

                <input
                  v-model="form.first_name"
                  class="form-control"
                  required
                />
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label">Last Name</label>

                <input
                  v-model="form.last_name"
                  class="form-control"
                  required
                />
              </div>
            </div>

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
              <label class="form-label">Phone Number</label>

              <input
                v-model="form.phone_number"
                class="form-control"
                required
              />
            </div>

            <div class="mb-3">
              <label class="form-label">BSN</label>

              <input
                v-model="form.bsn"
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

            <button class="btn btn-success">
              Register
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/authApi'

const router = useRouter()

const form = reactive({
  first_name: '',
  last_name: '',
  email: '',
  password: '',
  phone_number: '',
  bsn: ''
})

const submit = async () => {
  await register(form)

  alert('Registration successful. Await employee approval.')

  router.push('/login')
}
</script>