import api from './axios';

export const getDashboardStats = async () => {
  const response = await api.get('/admin/stats');
  return response.data;
};

export const getAllUsers = async () => {
  const response = await api.get('/admin/users');
  return response.data;
};

export const getAllBookingsAdmin = async () => {
  const response = await api.get('/admin/bookings');
  return response.data;
};

export const promoteUser = async (userId) => {
  const response = await api.post(`/admin/users/promote/${userId}`);
  return response.data;
};

export const demoteUser = async (userId) => {
  const response = await api.post(`/admin/users/demote/${userId}`);
  return response.data;
};

export const deleteUser = async (userId) => {
  const response = await api.delete(`/admin/users/${userId}`);
  return response.data;
};

export const createAdmin = async (adminData) => {
  const response = await api.post('/admin/create-admin', adminData);
  return response.data;
};
