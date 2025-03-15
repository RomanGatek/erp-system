import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { useInventoryStore } from '@/stores'

// Mock API functions
const mockGetAll = vi.fn()
const mockAdd = vi.fn()
const mockUpdate = vi.fn()
const mockDelete = vi.fn()

// Mock the API service
vi.mock('@/services/api', () => {
  return {
    default: {
      inventory: () => ({
        getAll: mockGetAll,
        add: mockAdd,
        update: mockUpdate,
        delete: mockDelete
      })
    }
  }
})

// Mock utils
vi.mock('@/utils', () => ({
  filter: (state, predicate) => state.items.filter(predicate),
  setupSort: (defaultField) => ({ field: defaultField, direction: 'asc' }),
  paginateViaState: (state) => {
    const start = (state.pagination.currentPage - 1) * state.pagination.perPage
    return state.items.slice(start, start + state.pagination.perPage)
  }
}))

describe('Inventory Store', () => {
  let store

  beforeEach(() => {
    // Create a fresh pinia instance for each test
    setActivePinia(createPinia())
    store = useInventoryStore()

    // Reset store state
    store.items = []
    store.loading = false
    store.error = null
    store.searchQuery = ''
    store.pagination = { currentPage: 1, perPage: 10 }
    store.sorting = { field: 'product.name', direction: 'asc' }

    // Clear all mocks before each test
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.resetAllMocks()
  })

  describe('state', () => {
    it('should have default state', () => {
      expect(store.loading).toBe(false)
      expect(store.items).toEqual([])
      expect(store.error).toBeNull()
      expect(store.searchQuery).toBe('')
      expect(store.currentFilter).toBe(undefined)
      expect(store.sorting).toEqual({ field: 'product.name', direction: 'asc' })
      expect(store.pagination).toEqual({ currentPage: 1, perPage: 10 })
    })
  })

  describe('getters', () => {
    beforeEach(() => {
      // Setup test data
      store.items = [
        { id: 1, product: { name: 'Product A', description: 'Description A' }, stockedAmount: 10 },
        { id: 2, product: { name: 'Product B', description: 'Description B' }, stockedAmount: 5 },
        { id: 3, product: { name: 'Product C', description: 'Description C' }, stockedAmount: 0 }
      ]
    })

    it('should filter items by search query', () => {
      store.searchQuery = 'Product A'
      expect(store.filtered).toHaveLength(1)
      expect(store.filtered[0].product.name).toBe('Product A')

      store.searchQuery = 'Description B'
      expect(store.filtered).toHaveLength(1)
      expect(store.filtered[0].product.name).toBe('Product B')
    })

    it('should paginate filtered items', () => {
      store.pagination.perPage = 2
      store.pagination.currentPage = 1
      expect(store.paginateItems).toHaveLength(2)
      expect(store.paginateItems[0].id).toBe(1)
      expect(store.paginateItems[1].id).toBe(2)

      store.pagination.currentPage = 2
      expect(store.paginateItems).toHaveLength(1)
      expect(store.paginateItems[0].id).toBe(3)
    })
  })

  describe('actions', () => {
    it('should fetch items successfully', async () => {
      const mockItems = [
        { id: 1, product: { name: 'Product A', description: 'Description A' }, stockedAmount: 10 },
        { id: 2, product: { name: 'Product B', description: 'Description B' }, stockedAmount: 5 }
      ]
      mockGetAll.mockResolvedValue([mockItems, null])

      await store.fetchItems()

      expect(mockGetAll).toHaveBeenCalled()
      expect(store.items).toEqual(mockItems)
      expect(store.error).toBeNull()
    })


    it('should add item successfully', async () => {
      const newItem = {
        product: { id: 1, name: 'New Product' },
        stockedAmount: 10
      }
      const mockItems = [
        {
          "id": 1,
          "stockedAmount": 10,
        },
        {
          "id": 2,
          "stockedAmount": 10,
        }
      ]
      mockAdd.mockResolvedValue([mockItems, null])

      await store.addItem(newItem)

      expect(store.items).toEqual([mockItems])
      expect(store.error).toBeNull()
    })

    it('should update item successfully', async () => {
      // Setup initial state
      store.items = [
        { id: 1, product: { name: 'Product A' }, stockedAmount: 10 },
        { id: 2, product: { name: 'Product B' }, stockedAmount: 5 }
      ]

      const updatedItem = {
        id: 1,
        stockedAmount: 15
      }
      mockUpdate.mockResolvedValue([{}, null])

      await store.updateItem(updatedItem)

      expect(mockUpdate).toHaveBeenCalledWith(updatedItem)
      expect(store.items[0].stockedAmount).toBe(15)
      expect(store.error).toBeNull()
    })

    it('should delete item successfully', async () => {
      const itemId = 1
      const mockItems = [
        { id: 2, product: { name: 'Product B' }, stockedAmount: 5 }
      ]
      mockDelete.mockResolvedValue([{}, null])
      mockGetAll.mockResolvedValue([mockItems, null])

      await store.deleteItem(itemId)

      expect(mockDelete).toHaveBeenCalledWith(itemId)
      expect(mockGetAll).toHaveBeenCalled()
      expect(store.items).toEqual(mockItems)
      expect(store.error).toBeNull()
    })

    it('should set search query and reset page', () => {
      store.pagination.currentPage = 3
      store.setSearch('test query')

      expect(store.searchQuery).toBe('test query')
      expect(store.pagination.currentPage).toBe(1)
    })

    it('should set sorting correctly', () => {
      // Same field should toggle direction
      store.sorting = { field: 'product.name', direction: 'asc' }
      store.setSorting('product.name')
      expect(store.sorting).toEqual({ field: 'product.name', direction: 'desc' })

      // Different field should set to asc
      store.setSorting('stockedAmount')
      expect(store.sorting).toEqual({ field: 'stockedAmount', direction: 'asc' })
    })

    it('should set page number', () => {
      store.setPage(5)
      expect(store.pagination.currentPage).toBe(5)
    })
  })
})
