import {createPinia, setActivePinia} from 'pinia'
import {beforeEach, describe, expect, it, vi} from 'vitest'
import {useInventoryStore} from '@/stores/storage.store.js'

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
    return state.filtered ? state.filtered.slice(start, start + state.pagination.perPage) : state.items.slice(start, start + state.pagination.perPage)
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
    mockGetAll.mockResolvedValue([items, null])

    await store.fetchItems()

    expect(mockGetAll).toHaveBeenCalled()
    expect(store.items).toEqual(items)
    expect(store.error).toBeNull()
  })

  it('fetchItems: should set error on failed API call', async () => {
    const error = new Error('Network Error')
    mockGetAll.mockResolvedValue([null, error])

    await store.fetchItems()

    expect(mockGetAll).toHaveBeenCalled()
    expect(store.error).toBe(error)
  })


  it('addItem: should handle error when adding item fails', async () => {
    const newItem = {
      product: { id: 3, name: 'Product C', description: 'Desc C' },
      stockedAmount: 5
    }
    const expectedApiCall = {
      productId: newItem.product.id,
      stockedAmount: newItem.stockedAmount
    }
    const error = new Error('Failed to add item')
    mockAdd.mockResolvedValue([null, error])

    await store.addItem(newItem)

    expect(mockAdd).toHaveBeenCalledWith(expectedApiCall)
    expect(store.error).toBe(error)
  })

  it('updateItem: should update an existing item', async () => {
    // Set initial items
    store.items = [{ id: 1, product: { id: 1, name: 'Product A', description: 'Desc A' }, stockedAmount: 5 }]
    const updatedData = {
      id: 1,
      product: { id: 2 },
      stockedAmount: 10
    }
    const expectedApiCall = {
      id: 1,
      productId: 2,
      stockedAmount: 10
    }
    mockUpdate.mockResolvedValue([{}, null])

    await store.updateItem(updatedData)

    expect(mockUpdate).toHaveBeenCalledWith(expectedApiCall)
    expect(store.items[0].stockedAmount).toBe(10)
    expect(store.error).toBeNull()
  })


  it('updateItem: should not update when item is not found', async () => {
    store.items = [{ id: 1, product: { name: 'Product A', description: 'Desc A' }, stockedAmount: 5 }]
    const updatedData = { id: 999, stockedAmount: 10 }
    mockUpdate.mockResolvedValue([{}, null])

    await store.updateItem(updatedData)

    expect(mockUpdate).toHaveBeenCalledWith(updatedData)
    expect(store.error).toBeNull()
    // Items array should remain unchanged
    expect(store.items).toEqual([{ id: 1, product: { name: 'Product A', description: 'Desc A' }, stockedAmount: 5 }])
  })


  it('deleteItem: should call api.inventory().delete and then fetch items', async () => {
    const items = [{ id: 2, product: { name: 'Product B', description: 'Desc B' } }]
    mockDelete.mockResolvedValue([{}, null])
    mockGetAll.mockResolvedValue([items, null])

    await store.deleteItem(1)

    expect(mockDelete).toHaveBeenCalledWith(1)
    expect(mockGetAll).toHaveBeenCalled()
    expect(store.items).toEqual(items)
    expect(store.error).toBeNull()
  })

  it('deleteItem: should handle error when deleting item fails', async () => {
    const error = new Error('Failed to delete item')
    mockDelete.mockResolvedValue([null, error])

    // Nastavíme mockGetAll, aby vracel prázdné pole položek
    mockGetAll.mockResolvedValue([[], null])

    await store.deleteItem(1)

    expect(mockDelete).toHaveBeenCalledWith(1)
    expect(store.error).toBeNull()
    expect(mockGetAll).toHaveBeenCalled()
  })

  it('setSearch: should update searchQuery and reset currentPage', () => {
    store.pagination.currentPage = 3
    store.setSearch('Test')
    expect(store.searchQuery).toBe('Test')
    expect(store.pagination.currentPage).toBe(1)
  })

  it('setSearch: should handle empty search query', () => {
    store.searchQuery = 'Previous search'
    store.pagination.currentPage = 3
    store.setSearch('')
    expect(store.searchQuery).toBe('')
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

  it('setPage: should handle invalid page numbers', () => {
    store.setPage(-1)
    expect(store.pagination.currentPage).toBe(-1) // Store doesn't validate page numbers

    store.setPage(0)
    expect(store.pagination.currentPage).toBe(0)

    // NaN test - v implementaci se NaN nekonvertuje na 0
    store.setPage(parseInt('not a number'))
    expect(store.pagination.currentPage).toBeNaN()
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

  it('getter filtered: should return all items when search query is empty', () => {
    const items = [
      { id: 1, product: { name: 'Alpha', description: 'First product' } },
      { id: 2, product: { name: 'Beta', description: 'Second product' } },
      { id: 3, product: { name: 'Gamma', description: 'Third product' } }
    ]
    store.items = items
    store.setSearch('')
    expect(store.filtered).toEqual(items)
  })

  it('getter filtered: should handle case insensitive search', () => {
    store.items = [
      { id: 1, product: { name: 'Alpha', description: 'First product' } },
      { id: 2, product: { name: 'Beta', description: 'Second product' } },
      { id: 3, product: { name: 'Gamma', description: 'Third product' } }
    ]
    // Case insensitive search
    store.setSearch('BETA')
    expect(store.filtered).toEqual([{ id: 2, product: { name: 'Beta', description: 'Second product' } }])

    store.setSearch('tHiRd')
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

  it('getter paginateItems: should handle empty pages', () => {
    // Create 5 items
    store.items = Array.from({length: 5}, (_, i) => ({
      id: i + 1,
      product: {name: `Product ${i + 1}`, description: `Description ${i + 1}`}
    }))
    // currentPage = 2, perPage = 10 (should be empty)
    store.pagination = { currentPage: 2, perPage: 10 }
    const paginated = store.paginateItems
    // Expect empty array
    expect(paginated).toEqual([])
  })

  it('getter paginateItems: should handle last page with fewer items', () => {
    // Create 12 items
    const items = Array.from({ length: 12 }, (_, i) => ({
      id: i + 1,
      product: { name: `Product ${i + 1}`, description: `Description ${i + 1}` }
    }))
    store.items = items
    // currentPage = 2, perPage = 10 (should have 2 items)
    store.pagination = { currentPage: 2, perPage: 10 }
    const paginated = store.paginateItems
    // Expect items 11 to 12 (indices 10 to 11)
    expect(paginated).toEqual(items.slice(10, 12))
  })
})
