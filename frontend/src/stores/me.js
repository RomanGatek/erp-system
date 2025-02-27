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
        const response = await api.get('/auth/user/me')
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
      try {
        const response = await api.put('auth/user/me', profileData)
        this.user = response.data
        return response.data
      } catch (error) {
        throw error
      }
    },
    async updatePassword(pass) {
      try {
        const response = await api.post('/auth/user/me/change-password', { password: pass })
        this.user = { password: response.data, ...this.user }
      } catch (error) {
        throw error
      }
    },
    async updateAvatar(formData) {
      try {
        const response = await api.post('/auth/user/me/avatar', formData)
        this.user = response.data
        return response.data
      } catch (error) {
        throw error
      }
    }
  }
})