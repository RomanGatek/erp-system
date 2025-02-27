import { defineStore } from 'pinia'
import { user as api } from '@/services/api' 

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
    }
  }
})