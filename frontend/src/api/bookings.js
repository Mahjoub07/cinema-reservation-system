import api from './axios';

export const createBooking = async (movieId, seatNumbers, showTime) => {
  const response = await api.post('/bookings', {
    movieId,
    seats: seatNumbers.length,
    seatNumbers,
    showTime
  });
  return response.data;
};

export const getBookedSeats = async (movieId, showTime) => {
  const response = await api.get(`/bookings/seats/${movieId}`, {
    params: { showtime: showTime }
  });
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

export const downloadTicket = async (bookingId) => {
  const response = await api.get(`/bookings/${bookingId}/ticket`, {
    responseType: 'blob'
  });
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `ticket-${bookingId}.pdf`);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

export const verifyBooking = async (bookingId) => {
  const response = await api.get(`/bookings/verify/${bookingId}`);
  return response.data;
};
