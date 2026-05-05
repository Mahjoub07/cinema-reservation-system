import api from './axios';

export const createBooking = async (movieId, seats) => {
  const response = await api.post('/bookings', { movieId, seats });
  return response.data;
};

export const getMyBookings = async () => {
  const response = await api.get('/bookings/my-bookings');
  return response.data;
};

export const getUserBookings = async (userId) => {
  const response = await api.get(`/bookings/user/${userId}`);
  return response.data;
};

export const getAllBookings = async () => {
  const response = await api.get('/bookings');
  return response.data;
};

export const cancelBooking = async (bookingId) => {
  await api.put(`/bookings/${bookingId}/cancel`);
};
