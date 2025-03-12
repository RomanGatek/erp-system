import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useProductsStore } from '@/stores/products.store.js'

// Mock API functions
const mockGetAll = vi.fn()
const mockAdd = vi.fn()
const mockUpdate = vi.fn()
const mockDelete = vi.fn()

// Mock the API service
vi.mock('@/services/api', () => {
  return {
    default: {
      products: () => ({
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
      { id: 1, name: 'Product A', buyoutPrice: 100, purchasePrice: 80 },
      { id: 2, name: 'Product B', buyoutPrice: 200, purchasePrice: 160 }
    ]
    mockGetAll.mockResolvedValue([products, null])

    await store.fetchProducts()

    expect(mockGetAll).toHaveBeenCalled()
    expect(store.items).toEqual(products)
    expect(store.error).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('fetchProducts: should set error on failed API call', async () => {
    const error = new Error('Network Error')
    mockGetAll.mockResolvedValue([null, error])

    await store.fetchProducts()

    expect(mockGetAll).toHaveBeenCalled()
    expect(store.error).toBe(error)
    expect(store.loading).toBe(false)
  })

  it('addProduct: should call api.products().add and then fetch products', async () => {
    const newProduct = { 
      name: 'Product C', 
      buyoutPrice: 300, 
      purchasePrice: 240,
      productCategory: { name: 'Category A' }
    }
    const products = [
      { id: 3, name: 'Product C', buyoutPrice: 300, purchasePrice: 240 }
    ]
    
    mockAdd.mockResolvedValue([{}, null])
    mockGetAll.mockResolvedValue([products, null])

    await store.addProduct(newProduct)

    expect(mockAdd).toHaveBeenCalledWith({ 
      ...newProduct, 
      productCategory: newProduct.productCategory.name 
    })
    expect(mockGetAll).toHaveBeenCalled()
    expect(store.items).toEqual(products)
  })

  it('updateProduct: should update an existing product', async () => {
    // Set initial items
    store.items = [{ id: 1, name: 'Product A', buyoutPrice: 100, purchasePrice: 80 }]
    const updatedData = { name: 'Product A Updated', buyoutPrice: 150, purchasePrice: 120 }
    const updatedProduct = { id: 1, ...updatedData }
    
    mockUpdate.mockResolvedValue([updatedProduct, null])
    mockGetAll.mockResolvedValue([[updatedProduct], null])

    await store.updateProduct(1, updatedData)

    expect(mockUpdate).toHaveBeenCalledWith(1, updatedData)
    expect(mockGetAll).toHaveBeenCalled()
    expect(store.items[0]).toEqual(updatedProduct)
  })

  it('deleteProduct: should call api.products().delete and then fetch products', async () => {
    const products = [{ id: 2, name: 'Product B', buyoutPrice: 200, purchasePrice: 160 }]
    
    mockDelete.mockResolvedValue([{}, null])
    mockGetAll.mockResolvedValue([products, null])

    await store.deleteProduct(1)

    expect(mockDelete).toHaveBeenCalledWith(1)
    expect(mockGetAll).toHaveBeenCalled()
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
      { id: 1, name: 'Alpha', buyoutPrice: 100, purchasePrice: 80 },
      { id: 2, name: 'Beta', buyoutPrice: 200, purchasePrice: 160 },
      { id: 3, name: 'Gamma', buyoutPrice: 300, purchasePrice: 240 }
    ]
    // Search by name
    store.setSearch('beta')
    const filtered = store.filtered
    expect(filtered).toEqual([{ id: 2, name: 'Beta', buyoutPrice: 200, purchasePrice: 160 }])
    // Search by price
    store.setSearch('300')
    expect(store.filtered).toEqual([{ id: 3, name: 'Gamma', buyoutPrice: 300, purchasePrice: 240 }])
  })

  it('getter paginateItems: should return items for the current page', () => {
    // Create 15 products
    const products = Array.from({ length: 15 }, (_, i) => ({
      id: i + 1,
      name: `Product ${i + 1}`,
      buyoutPrice: (i + 1) * 10,
      purchasePrice: (i + 1) * 8
    }))
    store.items = products
    // currentPage = 2, perPage = 10
    store.pagination = { currentPage: 2, perPage: 10 }
    const paginated = store.paginateItems
    // Expect items 11 to 15 (index 10 to 14)
    expect(paginated).toEqual(products.slice(10, 15))
  })
})
