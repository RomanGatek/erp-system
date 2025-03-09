import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { useInventoryStore } from '@/stores/inventory.store.js'
import { api } from '@/services/api.js'

// Mock the dependencies
vi.mock('@/services/api', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

vi.mock('../errors', () => ({
  useErrorStore: () => ({
    handle: vi.fn()
  })
}))

vi.mock('../notifier', () => ({
  useNotifier: () => ({
    success: vi.fn(),
    error: vi.fn(),
    info: vi.fn(),
    warning: vi.fn()
  })
}))

describe('Inventory Store', () => {
  let store

  beforeEach(() => {
    // Create a fresh pinia instance for each test
    setActivePinia(createPinia())
    store = useInventoryStore()

    // Clear all mocks before each test
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.resetAllMocks()
  })

  describe('state', () => {
    it('should have default state', () => {
      expect(store.loading).toBe(false)
      expect(store.stockOrders).toEqual([])
      expect(store.selectedOrder).toBeNull()
    })
  })

  describe('actions', () => {
    it('should fetch stock orders successfully', async () => {
      const mockStockOrders = [
        { id: 1, productId: 101, quantity: 10 },
        { id: 2, productId: 102, quantity: 5 }
      ]
      api.get.mockResolvedValueOnce({ data: mockStockOrders })

      await store.fetchStockOrders()

      expect(api.get).toHaveBeenCalledWith('/inventory/orders')
      expect(store.stockOrders).toEqual(mockStockOrders)
      expect(store.loading).toBe(false)
    })

    it('should handle fetch stock orders error', async () => {
      const error = new Error('Network error')
      api.get.mockRejectedValueOnce(error)

      await store.fetchStockOrders()

      expect(api.get).toHaveBeenCalledWith('/inventory/orders')
      expect(store.stockOrders).toEqual([])
      expect(store.loading).toBe(false)
      // We can't directly test error handling since it's using an external store
    })

    it('should create stock order successfully', async () => {
      const mockOrderData = {
        productId: '101',
        quantity: '10',
        expectedDeliveryDate: '2023-12-31',
        supplier: 'Test Supplier',
        purchasePrice: '99.99',
        notes: 'Test notes'
      }

      const mockApiResponse = {
        id: 1,
        productId: 101,
        quantity: 10,
        expectedDeliveryDate: '2023-12-31',
        supplier: 'Test Supplier',
        purchasePrice: 99.99,
        notes: 'Test notes'
      }

      api.post.mockResolvedValueOnce({ data: mockApiResponse })
      // Mock the fetchStockOrders to avoid additional API calls
      store.fetchStockOrders = vi.fn()

      const result = await store.createStockOrder(mockOrderData)

      expect(api.post).toHaveBeenCalledWith('/inventory/orders', {
        productId: 101, // Should be converted to number
        quantity: 10, // Should be converted to number
        expectedDeliveryDate: '2023-12-31',
        supplier: 'Test Supplier',
        purchasePrice: 99.99, // Should be converted to float
        notes: 'Test notes'
      })

      expect(store.fetchStockOrders).toHaveBeenCalled()
      expect(result).toEqual(mockApiResponse)
      expect(store.loading).toBe(false)
    })

    it('should handle create stock order error', async () => {
      const mockOrderData = {
        productId: '101',
        quantity: '10',
        expectedDeliveryDate: '2023-12-31',
        supplier: 'Test Supplier',
        purchasePrice: '99.99',
        notes: 'Test notes'
      }

      const error = new Error('API error')
      api.post.mockRejectedValueOnce(error)

      const result = await store.createStockOrder(mockOrderData)

      expect(api.post).toHaveBeenCalledWith('/inventory/orders', expect.any(Object))
      expect(result).toBeNull()
      expect(store.loading).toBe(false)
    })

    it('should update stock order status successfully', async () => {
      const orderId = 1
      const newStatus = 'DELIVERED'
      const mockApiResponse = {
        id: orderId,
        status: newStatus
      }

      api.put.mockResolvedValueOnce({ data: mockApiResponse })
      // Mock the fetchStockOrders to avoid additional API calls
      store.fetchStockOrders = vi.fn()

      const result = await store.updateStockOrderStatus(orderId, newStatus)

      expect(api.put).toHaveBeenCalledWith(`/inventory/orders/${orderId}/status`, { status: newStatus })
      expect(store.fetchStockOrders).toHaveBeenCalled()
      expect(result).toEqual(mockApiResponse)
      expect(store.loading).toBe(false)
    })

    it('should handle update stock order status error', async () => {
      const orderId = 1
      const newStatus = 'DELIVERED'
      const error = new Error('API error')

      api.put.mockRejectedValueOnce(error)

      const result = await store.updateStockOrderStatus(orderId, newStatus)

      expect(api.put).toHaveBeenCalledWith(`/inventory/orders/${orderId}/status`, { status: newStatus })
      expect(result).toBeNull()
      expect(store.loading).toBe(false)
    })

    it('should delete stock order successfully', async () => {
      const orderId = 1

      api.delete.mockResolvedValueOnce({})
      // Mock the fetchStockOrders to avoid additional API calls
      store.fetchStockOrders = vi.fn()

      const result = await store.deleteStockOrder(orderId)

      expect(api.delete).toHaveBeenCalledWith(`/inventory/orders/${orderId}`)
      expect(store.fetchStockOrders).toHaveBeenCalled()
      expect(result).toBe(true)
      expect(store.loading).toBe(false)
    })

    it('should handle delete stock order error', async () => {
      const orderId = 1
      const error = new Error('API error')

      api.delete.mockRejectedValueOnce(error)

      const result = await store.deleteStockOrder(orderId)

      expect(api.delete).toHaveBeenCalledWith(`/inventory/orders/${orderId}`)
      expect(result).toBe(false)
      expect(store.loading).toBe(false)
    })
  })
})
