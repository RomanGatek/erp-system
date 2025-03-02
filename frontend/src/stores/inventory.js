import { defineStore } from 'pinia'
import { user } from '@/services/api'
import { notify } from '@kyvg/vue3-notification'

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    items: [],
    loading: false,
    error: null,
    searchQuery: '',
    sorting: { field: 'product.name', direction: 'asc' },
    pagination: { currentPage: 1, perPage: 10 }
  }),
  getters: {
    filteredItems: (state) => {
      let filtered = [...state.items]

      if (state.searchQuery) {
        const searchValue = state.searchQuery.toLocaleLowerCase()
        filtered = filtered.filter(item =>
          item.product.name.toLocaleLowerCase().includes(searchValue) ||
          item.product.description.toLocaleLowerCase().includes(searchValue)
        )
      }

      if (state.sorting.field !== 'actions') {
        filtered.sort((a, b) => {
          const aVal = a[state.sorting.field]
          const bVal = b[state.sorting.field]
          const direction = state.sorting.direction === 'asc' ? 1 : -1
          return aVal > bVal ? direction : -direction
        })
      }

      return filtered
    },
    paginateItems: (state) => {
      const start = (state.pagination.currentPage - 1) * state.pagination.perPage
      const end = start + state.pagination.perPage
      return state.filteredItems.slice(start, end)
    }
  },
  actions: {
    async fetchItems() {
      try {
        const response = await user.get('/inventory')
        this.items = response.data
        this.error = null
      } catch (err) {
        console.log(err)
        this.error = err.message
      }
    },
    async addItem(Item) {
      try {
        await user.post('/inventory', Item)
        await this.fetchItems()
        notify({
          type: 'success',
          text: 'Product was successfully added',
          duration: 5000,
          speed: 500
        })
      } catch (err) {
        console.error(err)
        this.error = err
      }
    },
    async updateProduct(id, ItemData) {
      try {
        await user.put(`/inventory/${id}`, ItemData)
        const index = this.products.findIndex(p => p.id === id)
        if (index !== -1) {
          this.items[index] = {
            ...this.items[index],
            ...ItemData,
            id
          }
        }
        notify({
          type: 'success',
          text: 'Product was successfully updated',
          duration: 5000,
          speed: 500
        })
        this.error = null
      } catch (err) {
        this.error = err
      }
    },
    async deleteProduct(itemId) {
      try {
        await user.delete(`/iventory/${itemId}`)
        await this.fetchItems()
        notify({
          type: 'success',
          text: 'Product was successfully deleted',
          duration: 5000,
          speed: 500
        })
        this.error = null
      } catch (err) {
        this.error = err
      }
    },


    setSearch(query) {
      this.searchQuery = query
      this.pagination.currentPage = 1 // Reset to the first page when searching
    },
    setSorting(field) {
      console.log(field)
      if (this.sorting.field === field) {
        this.sorting.direction = this.sorting.direction === 'asc' ? 'desc' : 'asc'
      } else {
        this.sorting.field = field
        this.sorting.direction = 'asc'
      }
    },
    setPage(page) {
      this.pagination.currentPage = page
    }
  }
})
