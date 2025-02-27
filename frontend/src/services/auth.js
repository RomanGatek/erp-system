import { api } from './api'

export const auth = {
  async login(credentials) {
    const response = await api.post('/auth/public/login', JSON.stringify(credentials))
    return response.data
  },

  async register(userData) {
    const response = await api.post('/auth/public/signup', userData)
    return response.data
  },

  async logout() {
    const response = await api.post('/auth/public/logout')
    return response.data
  },

  async forgotPassword(email) {
    const response = await api.post('/auth/public/forgot-password', { email })
    return response.data
  },

  async resetPassword(token, newPassword) {
    const response = await api.post('/auth/public/reset-password', {
      token,
      newPassword
    })
    return response.data
  }
}
