import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests if it exists
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth endpoints
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

// CV endpoints
export const cvAPI = {
  create: (data) => api.post('/cv', data),
  getMyCV: () => api.get('/cv/me'),
  update: (id, data) => api.put(`/cv/${id}`, data),
  addEducation: (cvId, data) => api.post(`/cv/${cvId}/education`, data),
  addExperience: (cvId, data) => api.post(`/cv/${cvId}/experience`, data),
};

// Directory endpoints (public)
export const directoryAPI = {
  search: (keyword) => api.get('/directory/search', { params: { q: keyword } }),
  getProfile: (userId) => api.get(`/directory/profile/${userId}`),
  joinDirectory: () => api.post('/directory/me/join'),
  leaveDirectory: () => api.delete('/directory/me/leave'),
  getMyEntry: () => api.get('/directory/me'),
};

export default api;