import { defineStore } from 'pinia'
import api from '@/services/api'
import { ref } from 'vue'
import { AxiosError } from 'axios'

export const useMeStore = defineStore('me', {
  state: () => ({
    /**@type {import('../../types').User} */
    user: ref(null),
    /**@type {AxiosError} */
    error: null
  }),
  actions: {
    /**
     * Get current authenticated user
     * @returns {Promise<import('../../types').User>}
     */
    async fetchMe() {
      [this.user, this.error] = await api.me().current()
      if (this.error) {
        const [{ accessToken, refreshToken }, error]  = await api.me().refreshToken()
        if (error){
          this.error = error
          return null
        };
        localStorage.setItem('token', accessToken)
        localStorage.setItem('refresh-token', refreshToken)

        const [user, e] = await api.me().current()

        if (e) {
          this.error = e
          return null
        }

        if (user) {
          this.user = user
          this.error = null
          return this.user
        }
      }
      return this.user
    },
    /**
     * update user profile
     * @param {import('@/services/api').UserUpdate} profileData 
     * @returns {Promise<import('@/services/api').User>}
     */
    async updateProfile(profileData) {

      const [user, error] = await api.me().updateProfile({
        username: profileData.username,
        firstName: profileData.firstName,
        lastName: profileData.lastName,
        roles: profileData.roles.map(role => role.name),
        avatar: profileData.avatar
      })

      if (error) {
        this.error = error
        return null
      }
      this.user = user

      return this.user
    },
    /**
     * update user password
     * @param {string} pass 
     * @returns {Promise<import('@/services/api').User>}
     */
    async updatePassword(pass) {
      const [user, error] = await api.me().updatePassword(pass)
      if (error) {
        this.error = error
        return null
      }
      this.user = user
      return this.user
    },
    /**
     * update user avatar
     * @param {FormData} formData 
     * @returns {Promise<import('@/services/api').User>}
     */
    async updateAvatar(formData) {
      [this.user, this.error] = await api.me().updateAvatar(formData)
      return this.user
    },
    logout() {
      localStorage.removeItem('token')
      this.clearUser()
    },
    clearUser() {
      [this.user, this.error] = [null, null]
    },
  }
})
