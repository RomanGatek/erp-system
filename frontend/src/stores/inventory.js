import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { user as api } from '@/services/api'

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    items: [],
    error: null,
    searchQuery: '',
    sorting: { field: 'productName', direction: 'asc' },
    pagination: { currentPage: 1, perPage: 10 }
  }),
  actions: {
    async fetchItems() {
      try {
        const response = await api.get('/inventory');
        this.items = response.data;
        this.error = null;
      } catch (err) {
        this.error = err.message;
        // Notify user on error
      }
    },
    // Other actions for addItem, updateItem, deleteItem...
  },
  getters: {
    filteredItems: (state) => {
      if (!state.searchQuery) return state.items
      
      return state.items.filter(item => 
        item.productName.toLowerCase().includes(state.searchQuery.toLowerCase()) ||
        item.quantity.toString().includes(state.searchQuery) ||
        item.location.toLowerCase().includes(state.searchQuery.toLowerCase())
      )
    },
    paginatedItems: (state) => {
      const sorted = [...state.filteredItems].sort((a, b) => {
        const aValue = a[state.sorting.field]
        const bValue = b[state.sorting.field]
        
        if (state.sorting.direction === 'asc') {
          return aValue > bValue ? 1 : -1
        }
        return aValue < bValue ? 1 : -1
      })

      const start = (state.pagination.currentPage - 1) * state.pagination.perPage
      const end = start + state.pagination.perPage

      return sorted.slice(start, end)
    }
  }
});
