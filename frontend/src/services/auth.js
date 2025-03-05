import { api } from './api'

/**
 * @typedef {{accessToken: string, refreshToken: string}} LoginResponse
 */

export const auth = {
  /**
   * Authenticates a user with credentials
   * @param {{email: string, password: string}} credentials User credentials
   * @returns {Promise<string|null>} Access token if login successful, null otherwise
   */
  async login(credentials) {
    const response = await api.post('/auth/public/login', JSON.stringify(credentials))
    /** @type {LoginResponse} */
    const data = response.data
    return data?.accessToken ?? null
  },

  /**
   * Registers a new user
   * @param {{email: string, password: string, name: string}} userData User registration data
   * @returns {Promise<Object>} Registered user data
   */
  async register(userData) {
    const response = await api.post('/auth/public/signup', userData)
    return response.data
  },

  /**
   * Logs out the current user
   * @returns {Promise<Object>} Logout result
   */
  async logout() {
    const response = await api.post('/auth/public/logout')
    return response.data
  },

  /**
   * Initiates password reset for the given email
   * @param {string} email User email
   * @returns {Promise<Object>} Password reset request result
   */
  async forgotPassword(email) {
    const response = await api.post('/auth/public/forgot-password', { email })
    return response.data
  },

  /**
   * Resets password using token
   * @param {string} token Reset password token
   * @param {string} newPassword New password
   * @returns {Promise<Object>} Password reset result
   */
  async resetPassword(token, newPassword) {
    const response = await api.post('/auth/public/reset-password', {
      token,
      newPassword
    })
    return response.data
  }
}
