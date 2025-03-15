import { defineStore } from 'pinia'
import api from '@/services/api'
import {
  filter,
  setupSort,
  paginateViaState
} from '@/utils'

let _;

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    items: [],
    loading: false,
    error: null,
    searchQuery: '',
    pagination: { currentPage: 1, perPage: 10 },
    sorting: setupSort('product.name'),
  }),
  getters: {
    filtered: (state) => filter(state, (item) => {
        const search = state.searchQuery.toLocaleLowerCase();
        return item.product.name.toLocaleLowerCase().includes(search) ||
        item.product.description.toLocaleLowerCase().includes(search)
      }
    ),
    paginateItems: (state) => paginateViaState(state)
  },
  actions: {
    async fetchItems() {
      [this.items, this.error] = await api.inventory().getAll()
    },
    async addItem(Item) {
      [_, this.error] = await api.inventory().add({
        productId: Item.product.id,
        stockedAmount: Item.stockedAmount,
      })
      if (!this.error) this.items.push(_)
    },
    async updateItem(ItemData) {
      var _;
      [_, this.error] = await api.inventory().update({
        id: ItemData.id,
        productId: ItemData.product?.id,
        stockedAmount: ItemData.stockedAmount,
      })

      if (!this.error) {
        const index = this.items.findIndex(p => p.id === ItemData.id)
        if (index !== -1) {
          this.items[index] = {
            ...this.items[index],
            ...ItemData,
            id: ItemData.id,
            stockedAmount: ItemData.stockedAmount ?? 0,
          }
        }
      }
    },
    async deleteItem(itemId) {
      var _;
      [_, this.error] = await api.inventory().delete(itemId)
      await this.fetchItems()
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
