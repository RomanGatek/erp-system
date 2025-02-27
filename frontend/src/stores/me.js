import { defineStore } from 'pinia'
import { user as api } from '@/services/api' 
import axios from 'axios'

export const useMeStore = defineStore('me', {
  state: () => ({
    user: null,
    error: null
  }),
  actions: {
    async fetchMe(token) {
      try {
        const response = await api.get('/auth/user/me', 
          { headers: { Authorization: `Bearer ${token || localStorage.getItem('token')}` }})
        this.user = response.data
      } catch (err) {
        this.error = err
      }
    },
    clearUser() {
      this.user = null
      this.error = null
    },
    async updateProfile(profileData) {
      try {
        const response = await axios.put('/api/me', profileData)
        this.user = response.data
        return response.data
      } catch (error) {
        throw error
      }
    }
  }
})