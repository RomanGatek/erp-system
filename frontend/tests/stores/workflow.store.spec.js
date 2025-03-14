import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { useWorkflowStore } from '@/stores/workflow.store.js'

// Mock API functions
const mockGetAll = vi.fn()
const mockGetById = vi.fn()
const mockConfirm = vi.fn()
const mockCancel = vi.fn()
const mockDelete = vi.fn()

// Mock the API service
vi.mock('@/services/api', () => {
  return {
    default: {
      orders: () => ({
        getAll: mockGetAll,
        getById: mockGetById,
        confirm: mockConfirm,
        cancel: mockCancel,
        delete: mockDelete
      })
    }
  }
})

// Mock notifier
const mockSuccess = vi.fn()
const mockError = vi.fn()
const mockInfo = vi.fn()
const mockWarning = vi.fn()

vi.mock('./notifier.store.js', () => ({
  useNotifier: () => ({
    success: mockSuccess,
    error: mockError,
    info: mockInfo,
    warning: mockWarning
  })
}))

describe('Workflow Store', () => {
  let store

  beforeEach(() => {
    // Create a fresh pinia instance for each test
    setActivePinia(createPinia())
    store = useWorkflowStore()

    // Clear all mocks before each test
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.resetAllMocks()
  })

  describe('state', () => {
    it('should have default state', () => {
      expect(store.items).toEqual([])
      expect(store.loading).toBe(false)
      expect(store.error).toBeNull()
      expect(store.selectedOrder).toBeNull()
      expect(store.searchQuery).toBe('')
      expect(store.type).toBe('all')
    })
  })

  describe('getters', () => {
    beforeEach(() => {
      // Setup test data
      store.items = [
        { id: 1, status: 'PENDING', orderItems: [{ name: 'Product A' }], orderTime: '2023-01-01T10:00:00Z' },
        { id: 2, status: 'CONFIRMED', orderItems: [{ name: 'Product B' }], orderTime: '2023-01-02T10:00:00Z' },
        { id: 3, status: 'CANCELED', orderItems: [{ name: 'Product C' }], orderTime: '2023-01-03T10:00:00Z' },
        { id: 4, status: 'PENDING', orderItems: [{ name: 'Product D' }], orderTime: '2023-01-04T10:00:00Z' },
        { id: 5, status: 'CONFIRMED', orderItems: [{ name: 'Product E' }], orderTime: '2023-01-05T10:00:00Z' }
      ]
    })

    it('should filter pending orders', () => {
      expect(store.pendingOrders).toHaveLength(2)
      expect(store.pendingOrders.every(order => order.status === 'PENDING')).toBe(true)
    })

    it('should filter confirmed orders', () => {
      expect(store.confirmedOrders).toHaveLength(2)
      expect(store.confirmedOrders.every(order => order.status === 'CONFIRMED')).toBe(true)
    })

    it('should filter canceled orders', () => {
      expect(store.canceledOrders).toHaveLength(1)
      expect(store.canceledOrders.every(order => order.status === 'CANCELED')).toBe(true)
    })

    it('should filter orders by status type', () => {
      store.type = 'pending'
      expect(store.filtered).toHaveLength(2)
      expect(store.filtered.every(order => order.status === 'PENDING')).toBe(true)

      store.type = 'confirmed'
      expect(store.filtered).toHaveLength(2)
      expect(store.filtered.every(order => order.status === 'CONFIRMED')).toBe(true)

      store.type = 'canceled'
      expect(store.filtered).toHaveLength(1)
      expect(store.filtered.every(order => order.status === 'CANCELED')).toBe(true)
    })

    it('should filter orders by search query', () => {
      store.searchQuery = 'Product A'
      expect(store.filtered).toHaveLength(1)
      expect(store.filtered[0].orderItems[0].name).toBe('Product A')
    })

    it('should sort filtered orders correctly', () => {
      // Default sorting is by orderTime desc
      expect(store.sortedAndFiltered[0].id).toBe(5)
      expect(store.sortedAndFiltered[4].id).toBe(1)

      // Change sorting to asc
      store.sorting.direction = 'asc'
      expect(store.sortedAndFiltered[0].id).toBe(1)
      expect(store.sortedAndFiltered[4].id).toBe(5)

      // Change sorting field
      store.sorting.field = 'orderItems'
      store.sorting.direction = 'desc'
      expect(store.sortedAndFiltered).toHaveLength(5) // Same length, just sorted
    })

    it('should paginate sorted and filtered orders', () => {
      store.pagination.perPage = 2
      store.pagination.currentPage = 1
      expect(store.paginatedOrders).toHaveLength(2)
      expect(store.paginatedOrders[0].id).toBe(5)
      expect(store.paginatedOrders[1].id).toBe(4)

      store.pagination.currentPage = 2
      expect(store.paginatedOrders).toHaveLength(2)
      expect(store.paginatedOrders[0].id).toBe(3)
      expect(store.paginatedOrders[1].id).toBe(2)

      store.pagination.currentPage = 3
      expect(store.paginatedOrders).toHaveLength(1)
    })
  })

  describe('actions', () => {
    it('should fetch orders successfully', async () => {
      const mockOrders = [
        { id: 1, status: 'PENDING', orderItems: [] },
        { id: 2, status: 'CONFIRMED', orderItems: [] }
      ]
      mockGetAll.mockResolvedValue([mockOrders, null])

      await store.fetchOrders()

      expect(mockGetAll).toHaveBeenCalled()
      expect(store.items).toEqual(mockOrders)
      expect(store.loading).toBe(false)
      expect(store.error).toBeNull()
    })

    it('should handle fetch orders error', async () => {
      const error = new Error('Network error')
      mockGetAll.mockResolvedValue([null, error])

      await store.fetchOrders()

      expect(mockGetAll).toHaveBeenCalled()
      expect(store.items).toEqual(null)
      expect(store.loading).toBe(false)
      expect(store.error).toBe(error)
    })

    it('should get order by id successfully', async () => {
      const mockOrder = { id: 1, status: 'PENDING', orderItems: [] }
      mockGetById.mockResolvedValue([mockOrder, null])

      const result = await store.getOrderById(1)

      expect(mockGetById).toHaveBeenCalledWith(1)
      expect(result).toEqual(mockOrder)
      expect(store.selectedOrder).toEqual(mockOrder)
      expect(store.loading).toBe(false)
    })

    it('should handle get order by id error', async () => {
      const error = new Error('Order not found')
      mockGetById.mockResolvedValue([null, error])

      const result = await store.getOrderById(999)

      expect(mockGetById).toHaveBeenCalledWith(999)
      expect(result).toBeNull()
      expect(store.selectedOrder).toBeNull()
      expect(store.loading).toBe(false)
      expect(store.error).toBe(error)
    })

    it('should approve a SELL order successfully', async () => {
      const orderId = 1
      const comment = 'Approved'
      const mockOrder = {
        id: orderId,
        status: 'PENDING',
        orderType: 'SELL',
        orderItems: [
          { name: 'Product', stockedQuantity: 10, needQuantity: 5 }
        ]
      }

      // Setup initial state
      store.items = [mockOrder]

      // Setup mocks
      mockConfirm.mockResolvedValue([
        { id: orderId, status: 'CONFIRMED' },
        null
      ])

      const result = await store.approveOrder(orderId, comment)

      expect(mockConfirm).toHaveBeenCalledWith(orderId, comment)
      expect(result).toBe(true)
      expect(store.items[0].status).toBe('CONFIRMED')
      expect(store.items[0].comment).toBe(comment)
      expect(store.loading).toBe(false)
    })

    it('should not approve SELL order with insufficient stock', async () => {
      const orderId = 1
      const comment = 'Approved'
      const mockOrder = {
        id: orderId,
        status: 'PENDING',
        orderType: 'SELL',
        orderItems: [
          { name: 'Product', stockedQuantity: 5, needQuantity: 10 }
        ]
      }

      // Setup initial state
      store.items = [mockOrder]

      const result = await store.approveOrder(orderId, comment)

      expect(mockConfirm).not.toHaveBeenCalled()
      expect(result).toBe(false)
      expect(store.items[0].status).toBe('PENDING')
      expect(store.loading).toBe(false)
    })

    it('should approve a PURCHASE order successfully', async () => {
      const orderId = 1
      const comment = 'Approved'
      const mockOrder = {
        id: orderId,
        status: 'PENDING',
        orderType: 'PURCHASE',
        orderItems: []
      }

      // Setup initial state
      store.items = [mockOrder]

      // Setup mocks
      mockConfirm.mockResolvedValue([
        { id: orderId, status: 'CONFIRMED' },
        null
      ])

      const result = await store.approveOrder(orderId, comment)

      expect(mockConfirm).toHaveBeenCalledWith(orderId, comment)
      expect(result).toBe(true)
      expect(store.items[0].status).toBe('CONFIRMED')
      expect(store.items[0].comment).toBe(comment)
      expect(store.loading).toBe(false)
    })

    it('should reject an order successfully', async () => {
      const orderId = 1
      const comment = 'Rejected'
      const mockOrder = {
        id: orderId,
        status: 'PENDING',
        orderItems: []
      }

      // Setup initial state
      store.items = [mockOrder]

      // Setup mocks
      mockCancel.mockResolvedValue([
        { id: orderId, status: 'CANCELED' },
        null
      ])

      const result = await store.rejectOrder(orderId, comment)

      expect(mockCancel).toHaveBeenCalledWith(orderId, comment)
      expect(result).toBe(true)
      expect(store.items[0].status).toBe('CANCELED')
      expect(store.items[0].comment).toBe(comment)
      expect(store.loading).toBe(false)
    })

    it('should delete an order successfully', async () => {
      const orderId = 1
      const mockOrder = {
        id: orderId,
        status: 'PENDING',
        orderItems: []
      }

      // Setup initial state
      store.items = [mockOrder]

      // Setup mocks
      mockDelete.mockResolvedValue([{}, null])

      const result = await store.deleteOrder(orderId)

      expect(mockDelete).toHaveBeenCalledWith(orderId)
      expect(result).toBe(true)
      expect(store.items).toEqual([])
      expect(store.loading).toBe(false)
    })

    it('should set search query and reset page', () => {
      store.pagination.currentPage = 3
      store.setSearch('test query')

      expect(store.searchQuery).toBe('test query')
      expect(store.pagination.currentPage).toBe(1)
    })

    it('should set sorting correctly', () => {
      // Same field should toggle direction
      store.sorting = { field: 'orderTime', direction: 'asc' }
      store.setSorting('orderTime')
      expect(store.sorting).toEqual({ field: 'orderTime', direction: 'desc' })

      // Different field should set to asc
      store.setSorting('id')
      expect(store.sorting).toEqual({ field: 'id', direction: 'asc' })
    })

    it('should set page number', () => {
      store.setPage(5)
      expect(store.pagination.currentPage).toBe(5)
    })

    it('should clear error', () => {
      store.error = 'Some error'
      store.clearError()
      expect(store.error).toBeNull()
    })
  })
})
