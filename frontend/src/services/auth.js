import axios from 'axios';

export async function login(username, password) {
  const response = await axios.post('/public/login', { username, password });
  return response.data.token;
}

export async function register(username, email, password) {
  await axios.post('/public/register', { username, email, password });
}