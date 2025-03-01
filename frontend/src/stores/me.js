import { defineStore } from 'pinia'
import { user as api } from '@/services/api'
import { ref } from 'vue'

export const useMeStore = defineStore('me', {
  state: () => ({
    user: ref(null),
    error: null
  }),
  actions: {
    async fetchMe() {
      try {
        const response = await api.get('/me')
        this.user = response.data
      } catch (err) {
        this.error = err
      }
    },
    clearUser() {
      this.user = null
      this.error = null
    },
    async logout() {
      localStorage.removeItem('token')
      this.user = null
      this.error = null
    },
    async updateProfile(profileData) {
      const response = await api.put('/me', profileData)
      this.user = response.data
      return response.data
    },
    async updatePassword(pass) {
      const response = await api.post('/me/change-password', { password: pass })
      this.user = { password: response.data, ...this.user }
    },
    async updateAvatar(formData) {
      const response = await api.post('/me/avatar', formData)
      this.user = response.data
      return response.data
    }
  }
})
