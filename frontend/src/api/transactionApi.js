import api from './axios'

export const getTransactions = async () => {
  const response = await api.get('/transactions')
  return response.data
}

export const createTransaction = async payload => {
  const response = await api.post('/transactions', payload)
  return response.data
}