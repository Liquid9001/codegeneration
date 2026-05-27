import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/api'
import { useToastStore } from './toast'

export const useUsersStore = defineStore('users', () => {
  const users = ref([])
  const currentViewUser = ref(null)
  const pendingUsers = ref([])
  const searchResults = ref([])
  const loading = ref(false)
  const totalUsers = ref(0)

  async function getAllUsers(offset = 0, limit = 20) {
    loading.value = true
    try {
      const { data } = await api.get('/users', { params: { offset, limit } })
      users.value = data
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Failed to load users')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function getPendingUsers() {
    loading.value = true
    try {
      const { data } = await api.get('/users', { params: { offset: 0, limit: 200 } })
      pendingUsers.value = data.filter((u) => !u.approved)
      return pendingUsers.value
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Failed to load pending users')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function getUserById(userId) {
    loading.value = true
    try {
      const { data } = await api.get(`/users/${userId}`)
      currentViewUser.value = data
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'User not found')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function approveUser(userId) {
    try {
      const { data } = await api.post(`/users/${userId}/approve`)
      useToastStore().success('User approved and accounts created successfully')
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Failed to approve user')
      throw err
    }
  }

  async function searchCustomers(firstName, lastName) {
    loading.value = true
    try {
      const { data } = await api.get('/customers/search', {
        params: { firstName, lastName },
      })
      searchResults.value = data
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Search failed')
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    users,
    currentViewUser,
    pendingUsers,
    searchResults,
    loading,
    totalUsers,
    getAllUsers,
    getPendingUsers,
    getUserById,
    approveUser,
    searchCustomers,
  }
})
