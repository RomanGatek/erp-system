import { defineStore } from 'pinia'
import {user as api} from '@/services/api' // Import your axios instance

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    items: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchItems() {
      this.loading = true
      try {
        const response = await api.get('/inventory')
        this.items = response.data
      } catch (error) {
        this.error = error.response?.data?.message || error.message
      } finally {
        this.loading = false
      }
    },

    async addItem(item) {
      try {
        await api.post('/inventory', item)
        await this.fetchItems()
      } catch (error) {
        this.error = error.response?.data?.message || error.message
      }
    },

    async updateItem(item) {
      try {
        await api.put(`/inventory/${item.id}`, item)
        await this.fetchItems()
      } catch (error) {
        this.error = error.response?.data?.message || error.message
      }
    },

    async deleteItem(itemId) {
      try {
        await api.delete(`/inventory/${itemId}`)
        await this.fetchItems()
      } catch (error) {
        this.error = error.response?.data?.message || error.message
      }
    },
  },
})
