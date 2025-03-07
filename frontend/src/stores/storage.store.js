import { defineStore } from 'pinia'
import { api as user } from '@/services/api'
import {
  filter,
  setupSort
} from '@/utils/table-utils.js'
import { __paginate } from '@/utils/pagination.js'

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    items: [],
    loading: false,
    error: null,
    searchQuery: '',
    sorting: setupSort('product.name'),
    pagination: { currentPage: 1, perPage: 10 }
  }),
  getters: {
    filtered: (state) => filter(state, (item) => {
        const search = state.searchQuery.toLocaleLowerCase();
        return item.product.name.toLocaleLowerCase().includes(search) ||
        item.product.description.toLocaleLowerCase().includes(search)
      }
    ),
    paginateItems: (state) => __paginate(state)
  },
  actions: {
    async fetchItems() {
      try {
        const response = await user.get('/inventory')
        this.items = response.data
      } catch (err) {
        this.error = err.message
      }
    },
    async addItem(Item) {
      try {
        await user.post('/inventory', Item)
        await this.fetchItems()
      } catch (err) {
        this.error = err
      }
    },
    async updateItem(ItemData) {
      try {
        await user.put(`/inventory/${ItemData.id}`, ItemData)
        const index = this.items.findIndex(p => p.id === ItemData.id)
        if (index !== -1) {
          this.items[index] = {
            ...this.items[index],
            ...ItemData,
            id: ItemData.id
          }
        }
      } catch (err) {
        this.error = err
      }
    },
    async deleteItem(itemId) {
      try {
        await user.delete(`/inventory/${itemId}`)
        await this.fetchItems()
      } catch (err) {
        this.error = err
      }
    },
    setSearch(query) {
      this.searchQuery = query
      this.pagination.currentPage = 1 // Reset to the first page when searching
    },
    setSorting(field) {
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
