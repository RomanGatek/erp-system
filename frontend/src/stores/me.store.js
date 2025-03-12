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
      return this.user
    },
    /**
     * update user profile
     * @param {import('@/services/api').UserUpdate} profileData 
     * @returns {Promise<import('@/services/api').User>}
     */
    async updateProfile(profileData) {
      [this.user, this.error] = await api.me().updateProfile(profileData)
      return this.user
    },
    /**
     * update user password
     * @param {string} pass 
     * @returns {Promise<import('@/services/api').User>}
     */
    async updatePassword(pass) {
      [this.user, this.error] = await api.me().updatePassword(pass)
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
