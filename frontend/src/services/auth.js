import { guest, user } from './api'

export async function login(email, password) {
  const response = await guest.post('/auth/public/login', { email, password })
  return response === undefined ? false : response.data
}

export async function register(username, email, password) {
  await user.post('/auth/public/register', { username, email, password })
}
