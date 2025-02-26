import { guest, user } from './api'

export async function login(email, password) {
  password = 'P&ssw0rd123@'
  email = 'admin@example.com'


  const response = await guest.post('/auth/public/login', { email, password })
  return response === undefined ? false : response
}

export async function register(username, email, password) {
  await user.post('/auth/public/register', { username, email, password })
}
