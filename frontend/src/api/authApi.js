import api from './axios'

export const login = async payload => {
  const response = await api.post('/users/login', payload)
  return response.data
}

export const register = async payload => {
  const response = await api.post('/users', payload)
  return response.data
}