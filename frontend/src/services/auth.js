import api from './api.js'
import { AxiosError } from 'axios'

export async function login(email, password) {

  password = 'password123'
  email = 'admin@example.com'

  const response = await exceptionHandler(api.post('/auth/public/login', { email, password }));
  return response.data === undefined ? false : response.data
}

export async function register(username, email, password) {
  await api.post('/auth/public/login', { username, email, password });
}


async function exceptionHandler(response) {
  try {
    return await response;
  } catch (e) {
    if (e instanceof AxiosError) {
      console.error(e.response);
    }
  }
}
