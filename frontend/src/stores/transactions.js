import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../services/api'
import { useToastStore } from './toast'

export const useTransactionsStore = defineStore('transactions', () => {
  const transactions = ref([])
  const currentTransaction = ref(null)
  const loading = ref(false)

  async function getAllTransactions(filters = {}) {
    loading.value = true
    try {
      const params = {}
      if (filters.offset !== undefined) params.offset = filters.offset
      if (filters.limit !== undefined) params.limit = filters.limit
      if (filters.startDate) params.startDate = filters.startDate
      if (filters.endDate) params.endDate = filters.endDate
      if (filters.minAmount !== undefined && filters.minAmount !== '') params.minAmount = filters.minAmount
      if (filters.maxAmount !== undefined && filters.maxAmount !== '') params.maxAmount = filters.maxAmount
      if (filters.iban) params.iban = filters.iban

      const { data } = await api.get('/transactions', { params })
      transactions.value = data
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Failed to load transactions')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function getTransactionById(transactionId) {
    loading.value = true
    try {
      const { data } = await api.get(`/transactions/${transactionId}`)
      currentTransaction.value = data
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Transaction not found')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function createTransaction(payload) {
    loading.value = true
    try {
      const { data } = await api.post('/transactions', payload)
      useToastStore().success('Transaction completed successfully')
      return data
    } catch (err) {
      useToastStore().error(err.response?.data?.message || 'Transaction failed')
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    transactions,
    currentTransaction,
    loading,
    getAllTransactions,
    getTransactionById,
    createTransaction,
  }
})
