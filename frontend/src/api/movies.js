import api from './axios';

export const getAllMovies = async () => {
  const response = await api.get('/movies');
  return response.data;
};

export const getMovieById = async (id) => {
  const response = await api.get(`/movies/${id}`);
  return response.data;
};

export const searchMovies = async (title) => {
  const response = await api.get('/movies/search', { params: { title } });
  return response.data;
};

export const getMoviesByGenre = async (genre) => {
  const response = await api.get(`/movies/genre/${genre}`);
  return response.data;
};

export const addMovie = async (movieData) => {
  const response = await api.post('/movies', movieData);
  return response.data;
};

export const updateMovie = async (id, movieData) => {
  const response = await api.put(`/movies/${id}`, movieData);
  return response.data;
};

export const deleteMovie = async (id) => {
  await api.delete(`/movies/${id}`);
};

export const uploadPoster = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  const response = await api.post('/movies/upload-poster', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};
