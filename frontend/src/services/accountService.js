import api from '../api';

export const getAccountById = (accountId) => api.get(`/accounts/${accountId}`);

export const getAccountsForUser = async (userId) => {
  const response = await api.get(`/users/${userId}`);
  return response.data.accounts || [];
};
