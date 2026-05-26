import api from './axios'

export const getUser = async userId => {
  const response = await api.get(`/users/${userId}`)
  return response.data
}

export const getUsers = async () => {
  const response = await api.get('/users')
  return response.data
}

export const approveUser = async userId => {
  const response = await api.post(`/users/${userId}/approve`)
  return response.data
}