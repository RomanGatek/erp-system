import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useProductsStore } from '@/stores/products'
import { api } from '@/services/api'

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

describe('useProductsStore', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useProductsStore()
    // Reset state
    store.items = []
    store.loading = false
    store.error = null
    store.pagination = { currentPage: 1, perPage: 10 }
    store.sorting = { field: 'name', direction: 'asc' }
    store.searchQuery = ''
    vi.clearAllMocks()
  })

  it('fetchProducts: should set items on successful API call', async () => {
    const products = [
      { id: 1, name: 'Product A', price: 100 },
      { id: 2, name: 'Product B', price: 200 }
    ]
    api.get.mockResolvedValue({ data: products })

    await store.fetchProducts()

    expect(api.get).toHaveBeenCalledWith('/products')
    expect(store.items).toEqual(products)
    expect(store.error).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('fetchProducts: should set error on failed API call', async () => {
    const error = new Error('Network Error')
    api.get.mockRejectedValue(error)

    await store.fetchProducts()

    expect(api.get).toHaveBeenCalledWith('/products')
    expect(store.error).toBe(error)
    expect(store.loading).toBe(false)
  })

  it('addProduct: should call api.post and then fetch products', async () => {
    const newProduct = { name: 'Product C', price: 300 }
    const products = [{ id: 3, name: 'Product C', price: 300 }]
    api.post.mockResolvedValue({ data: {} })
    api.get.mockResolvedValue({ data: products })

    await store.addProduct(newProduct)

    expect(api.post).toHaveBeenCalledWith('/products', newProduct)
    expect(api.get).toHaveBeenCalledWith('/products')
    expect(store.items).toEqual(products)
  })

  it('updateProduct: should update an existing product', async () => {
    // Set initial items
    store.items = [{ id: 1, name: 'Product A', price: 100 }]
    const updatedData = { name: 'Product A Updated', price: 150 }
    api.put.mockResolvedValue({ data: {} })

    await store.updateProduct(1, updatedData)

    expect(api.put).toHaveBeenCalledWith('/products/1', updatedData)
    expect(store.items[0]).toEqual({ id: 1, ...updatedData })
  })

  it('deleteProduct: should call api.delete and then fetch products', async () => {
    const products = [{ id: 2, name: 'Product B', price: 200 }]
    api.delete.mockResolvedValue({ data: {} })
    api.get.mockResolvedValue({ data: products })

    await store.deleteProduct(1)

    expect(api.delete).toHaveBeenCalledWith('/products/1')
    expect(api.get).toHaveBeenCalledWith('/products')
    expect(store.items).toEqual(products)
    expect(store.error).toBeNull()
  })

  it('setSearch: should update searchQuery and reset current page', () => {
    store.pagination.currentPage = 3
    store.setSearch('Test')
    expect(store.searchQuery).toBe('Test')
    expect(store.pagination.currentPage).toBe(1)
  })

  it('setSorting: should toggle direction if same field is provided', () => {
    // Current sorting is by 'name' and 'asc'
    store.setSorting('name')
    expect(store.sorting).toEqual({ field: 'name', direction: 'desc' })
    // Toggle back
    store.setSorting('name')
    expect(store.sorting).toEqual({ field: 'name', direction: 'asc' })
  })

  it('setSorting: should set new field with asc direction if different field provided', () => {
    // Changing from 'name' to 'price'
    store.setSorting('price')
    expect(store.sorting).toEqual({ field: 'price', direction: 'asc' })
  })

  it('setPage: should update pagination current page', () => {
    store.setPage(5)
    expect(store.pagination.currentPage).toBe(5)
  })

  it('getter filtered: should return items matching the search query', () => {
    store.items = [
      { id: 1, name: 'Alpha', buyoutPrice: 100, purchasePrice: 100 },
      { id: 2, name: 'Beta', buyoutPrice: 200, purchasePrice: 200 },
      { id: 3, name: 'Gamma', buyoutPrice: 300, purchasePrice: 300 }
    ]
    // Search by name
    store.setSearch('beta')
    const filtered = store.filtered
    expect(filtered).toEqual([{ id: 2, name: 'Beta', buyoutPrice: 200, purchasePrice: 200 }])
    // Search by price
    store.setSearch('300')
    expect(store.filtered).toEqual([{ id: 3, name: 'Gamma', buyoutPrice: 300, purchasePrice: 300 }])
  })

  it('getter paginateItems: should return items for the current page', () => {
    // Create 15 products
    const products = Array.from({ length: 15 }, (_, i) => ({
      id: i + 1,
      name: `Product ${i + 1}`,
      price: (i + 1) * 10
    }))
    store.items = products
    // currentPage = 2, perPage = 10
    store.pagination = { currentPage: 2, perPage: 10 }
    const paginated = store.paginateItems
    // Expect items 11 to 15 (index 10 to 14)
    expect(paginated).toEqual(products.slice(10, 15))
  })
})
