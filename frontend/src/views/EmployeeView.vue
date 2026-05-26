<template>
  <div>
    <h1 class="mb-4">
      Employee Dashboard
    </h1>

    <div class="card shadow-sm">
      <div class="card-body">
        <h4 class="mb-3">
          Pending Customers
        </h4>

        <table class="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Approved</th>
              <th></th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="user in pendingUsers"
              :key="user.id"
            >
              <td>
                {{ user.first_name }}
                {{ user.last_name }}
              </td>

              <td>{{ user.email }}</td>

              <td>{{ user.approved }}</td>

              <td>
                <button
                  class="btn btn-success btn-sm"
                  @click="approve(user.id)"
                >
                  Approve
                </button>
              </td>
            </tr>
          </tbody>
        </table>

      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { approveUser, getUsers } from '../api/userApi'

const users = ref([])

const pendingUsers = computed(() =>
  users.value.filter(u => !u.approved)
)

onMounted(async () => {
  users.value = await getUsers()
})

const approve = async userId => {
  await approveUser(userId)

  users.value = users.value.map(u => {
    if (u.id === userId) {
      u.approved = true
    }

    return u
  })
}
</script>