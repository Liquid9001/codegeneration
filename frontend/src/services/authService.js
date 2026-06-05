import { jwtDecode } from 'jwt-decode';
import api from '../api';

export const loginUser = async (email, password) => {
  const response = await api.post('/users/login', { email, password });
  const token = response.data.token;
  const decodedToken = jwtDecode(token);
  const userId = decodedToken.userId;
  const userResponse = await api.get(`/users/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return {
    token,
    decodedToken,
    user: userResponse.data,
  };
};

export const getUserWithAccounts = (userId) => api.get(`/users/${userId}`);
