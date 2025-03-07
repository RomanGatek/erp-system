import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useInventoryStore } from '@/stores/storage.store.js'
import { api as user } from '@/services/api'

// Stub external utilities so our tests are isolated
vi.mock('@/utils/table-utils.js', () => ({
  filter: (state, predicate) => state.items.filter(predicate),
  setupSort: (defaultField) => ({ field: defaultField, direction: 'asc' })
}))

vi.mock('@/utils/pagination.js', () => ({
  __paginate: (state) => {
    const start = (state.pagination.currentPage - 1) * state.pagination.perPage
    return state.items.slice(start, start + state.pagination.perPage)
  }
}))

// Mock the API service
vi.mock('@/services/api', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

describe('useInventoryStore', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useInventoryStore()
    // Reset initial state
    store.items = []
    store.loading = false
    store.error = null
    store.searchQuery = ''
    store.pagination = { currentPage: 1, perPage: 10 }
    store.sorting = { field: 'product.name', direction: 'asc' }
    vi.clearAllMocks()
  })

  it('fetchItems: should set items on successful API call', async () => {
    const items = [
      { id: 1, product: { name: 'Product A', description: 'Desc A' } },
      { id: 2, product: { name: 'Product B', description: 'Desc B' } }
    ]
    user.get.mockResolvedValue({ data: items })

    await store.fetchItems()

    expect(user.get).toHaveBeenCalledWith('/inventory')
    expect(store.items).toEqual(items)
    expect(store.error).toBeNull()
  })

  it('fetchItems: should set error on failed API call', async () => {
    const error = new Error('Network Error')
    user.get.mockRejectedValue(error)

    await store.fetchItems()

    expect(user.get).toHaveBeenCalledWith('/inventory')
    expect(store.error).toBe(error.message)
  })

  it('addItem: should call api.post and then fetch items', async () => {
    const newItem = { product: { name: 'Product C', description: 'Desc C' } }
    const items = [{ id: 3, product: { name: 'Product C', description: 'Desc C' } }]
    user.post.mockResolvedValue({ data: {} })
    user.get.mockResolvedValue({ data: items })

    await store.addItem(newItem)

    expect(user.post).toHaveBeenCalledWith('/inventory', newItem)
    expect(user.get).toHaveBeenCalledWith('/inventory')
    expect(store.items).toEqual(items)
  })

  it('updateItem: should update an existing item', async () => {
    // Set initial items
    store.items = [{ id: 1, product: { name: 'Product A', description: 'Desc A' } }]
    const updatedData = { id: 1, product: { name: 'Updated Product A', description: 'Updated Desc A' } }
    user.put.mockResolvedValue({ data: {} })

    await store.updateItem(updatedData)

    expect(user.put).toHaveBeenCalledWith('/inventory/1', updatedData)
    expect(store.items[0]).toEqual(updatedData)
  })

  it('deleteItem: should call api.delete and then fetch items', async () => {
    const items = [{ id: 2, product: { name: 'Product B', description: 'Desc B' } }]
    user.delete.mockResolvedValue({ data: {} })
    user.get.mockResolvedValue({ data: items })

    await store.deleteItem(1)

    expect(user.delete).toHaveBeenCalledWith('/inventory/1')
    expect(user.get).toHaveBeenCalledWith('/inventory')
    expect(store.items).toEqual(items)
  })

  it('setSearch: should update searchQuery and reset currentPage', () => {
    store.pagination.currentPage = 3
    store.setSearch('Test')
    expect(store.searchQuery).toBe('Test')
    expect(store.pagination.currentPage).toBe(1)
  })

  it('setSorting: should toggle direction if same field is provided', () => {
    // Initially, sorting is by 'product.name' in asc direction
    store.setSorting('product.name')
    expect(store.sorting).toEqual({ field: 'product.name', direction: 'desc' })
    store.setSorting('product.name')
    expect(store.sorting).toEqual({ field: 'product.name', direction: 'asc' })
  })

  it('setSorting: should set new field with asc direction if different field provided', () => {
    store.setSorting('product.description')
    expect(store.sorting).toEqual({ field: 'product.description', direction: 'asc' })
  })

  it('setPage: should update currentPage', () => {
    store.setPage(5)
    expect(store.pagination.currentPage).toBe(5)
  })

  it('getter filtered: should return items matching search query', () => {
    store.items = [
      { id: 1, product: { name: 'Alpha', description: 'First product' } },
      { id: 2, product: { name: 'Beta', description: 'Second product' } },
      { id: 3, product: { name: 'Gamma', description: 'Third product' } }
    ]
    // Search by product name
    store.setSearch('beta')
    expect(store.filtered).toEqual([{ id: 2, product: { name: 'Beta', description: 'Second product' } }])
    // Search by product description
    store.setSearch('third')
    expect(store.filtered).toEqual([{ id: 3, product: { name: 'Gamma', description: 'Third product' } }])
  })

  it('getter paginateItems: should return items for the current page', () => {
    // Create 15 items
    const items = Array.from({ length: 15 }, (_, i) => ({
      id: i + 1,
      product: { name: `Product ${i + 1}`, description: `Description ${i + 1}` }
    }))
    store.items = items
    // currentPage = 2, perPage = 10
    store.pagination = { currentPage: 2, perPage: 10 }
    const paginated = store.paginateItems
    // Expect items 11 to 15 (indices 10 to 14)
    expect(paginated).toEqual(items.slice(10, 15))
  })
})
