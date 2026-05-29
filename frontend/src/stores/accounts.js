import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/api'
import { useToastStore } from './toast'

export const useAccountsStore = defineStore('accounts', () => {
  const currentAccount = ref(null)
  const loading = ref(false)

  async function getAccountById(accountId) {
    loading.value = true
    try {
      const { data } = await api.get(`/accounts/${accountId}`)
      currentAccount.value = data
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Failed to load account')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateAccount(accountId, payload) {
    loading.value = true
    try {
      const { data } = await api.put(`/accounts/${accountId}`, payload)
      useToastStore().success('Account updated successfully')
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Failed to update account')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteAccount(accountId) {
    loading.value = true
    try {
      await api.delete(`/accounts/${accountId}`)
      useToastStore().success('Account closed successfully')
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Failed to close account')
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    currentAccount,
    loading,
    getAccountById,
    updateAccount,
    deleteAccount,
  }
})
