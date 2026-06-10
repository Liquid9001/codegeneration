import api from '../api';

export const searchCustomers = (firstName, lastName) => api.get('/customers/search', {
  params: {
    firstName,
    lastName,
  },
});

export const getUsersPage = (params = {}) => api.get('/users', { params });
