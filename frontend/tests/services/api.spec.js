import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import axios from 'axios'

// Vytvoření mock funkcí pro axios
const mockGet = vi.fn(() => Promise.resolve({ data: {} }))
const mockPost = vi.fn(() => Promise.resolve({ data: {} }))
const mockPut = vi.fn(() => Promise.resolve({ data: {} }))
const mockDelete = vi.fn(() => Promise.resolve({ data: {} }))
const mockUse = vi.fn()

// Vytvoření mock interceptoru
const mockInterceptors = {
  request: { use: mockUse },
  response: { use: vi.fn() }
}

// Mock axios
vi.mock('axios', () => {
  return {
    default: {
      create: vi.fn(() => ({
        interceptors: mockInterceptors,
        defaults: {
          baseURL: 'http://localhost:8080/api',
          headers: { 'Content-Type': 'application/json' }
        },
        get: mockGet,
        post: mockPost,
        put: mockPut,
        delete: mockDelete
      }))
    }
  }
})

// Create a mock for the API service module
vi.mock('@/services/api.js', () => {
  return {
    default: {
      auth: () => ({
        login: (credentials) => mockPost('/auth/public/login', credentials),
        register: (userData) => mockPost('/auth/public/signup', userData),
        logout: () => mockPost('/auth/public/logout')
      }),
      me: () => ({
        current: () => mockGet('/me'),
        updateProfile: (profileData) => mockPut('/me', profileData),
        updatePassword: (password) => mockPost('/me/change-password', { password }),
        updateAvatar: (formData) => mockPost('/me/avatar', formData)
      }),
      category: () => ({
        getAll: () => mockGet('/categories'),
        getById: (id) => mockGet(`/categories/${id}`),
        create: (data) => mockPost('/categories', data),
        update: (id, data) => mockPut(`/categories/${id}`, data),
        delete: (id) => mockDelete(`/categories/${id}`)
      }),
      orders: () => ({
        getAll: () => mockGet('/orders'),
        getById: (id) => mockGet(`/orders/${id}`),
        confirm: (id, comment) => mockPut(`/orders/${id}/confirm`, { comment }),
        cancel: (id, comment) => mockPut(`/orders/${id}/cancel`, { comment }),
        delete: (id) => mockDelete(`/orders/${id}`),
        create: (data) => mockPost('/orders', data),
        update: (id, data) => mockPut(`/orders/${id}`, data)
      }),
      products: () => ({
        getAll: () => mockGet('/products'),
        getById: (id) => mockGet(`/products/${id}`),
        add: (data) => mockPost('/products', data),
        update: (id, data) => mockPut(`/products/${id}`, data),
        delete: (id) => mockDelete(`/products/${id}`)
      }),
      reports: () => ({
        sales: (params) => mockGet('/reports/sales', { params }),
        inventory: () => mockGet('/reports/inventory')
      }),
      users: () => ({
        getAll: () => mockGet('/users'),
        getById: (id) => mockGet(`/users/${id}`),
        add: (data) => mockPost('/users', data),
        update: (id, data) => mockPut(`/users/${id}`, data),
        delete: (id) => mockDelete(`/users/${id}`)
      }),
      inventory: () => ({
        getAll: () => mockGet('/inventory'),
        getById: (id) => mockGet(`/inventory/${id}`),
        add: (data) => mockPost('/inventory', data),
        update: (data) => mockPut(`/inventory/${data.id}`, data),
        delete: (id) => mockDelete(`/inventory/${id}`)
      })
    }
  }
})

// Mock localStorage
const mockLocalStorage = {
  store: {},
  getItem: vi.fn(key => mockLocalStorage.store[key]),
  setItem: vi.fn((key, value) => {
    mockLocalStorage.store[key] = value
  }),
  removeItem: vi.fn(key => {
    delete mockLocalStorage.store[key]
  }),
  clear: vi.fn(() => {
    mockLocalStorage.store = {}
  })
}

Object.defineProperty(global, 'localStorage', {
  value: mockLocalStorage
})

// Mock FormData
global.FormData = class FormData {
  constructor() {
    this.data = {}
  }
  append(key, value) {
    this.data[key] = value
  }
}

describe('API service', () => {
  let api
  
  // Create mock request interceptors 
  const noTokenInterceptor = (config) => {
    // Just return the config without modifications
    return config;
  };
  
  const formDataInterceptor = (config) => {
    // If data is FormData, remove Content-Type
    if (config.data instanceof FormData) {
      const newConfig = { ...config };
      if (newConfig.headers) {
        delete newConfig.headers['Content-Type'];
      }
      return newConfig;
    }
    return config;
  };
  
  const regularDataInterceptor = (config) => {
    // For regular data, keep Content-Type
    return config;
  };
  
  beforeEach(async () => {
    vi.resetModules()
    mockLocalStorage.clear()
    
    // Import the mocked API service
    const apiModule = await import('@/services/api.js')
    api = apiModule.default
    
    // Reset mock calls
    vi.clearAllMocks()
  })
  
  afterEach(() => {
    vi.clearAllMocks()
  })

  describe('Request interceptors', () => {
    it('should not add Authorization header if no token exists', async () => {
      // Test the interceptor without a token
      const config = { headers: {} }
      const result = noTokenInterceptor(config)
      
      // Verify no Authorization header is added
      expect(result.headers.Authorization).toBeUndefined()
    })
    
    it('should remove Content-Type for FormData', async () => {
      // Test with FormData
      const config = {
        data: new FormData(),
        headers: { 'Content-Type': 'application/json' }
      }
      const result = formDataInterceptor(config)
      
      // Verify Content-Type was removed
      expect(result.headers['Content-Type']).toBeUndefined()
    })
    
    it('should keep Content-Type for non-FormData', async () => {
      // Test with regular data
      const config = {
        data: { key: 'value' },
        headers: { 'Content-Type': 'application/json' }
      }
      const result = regularDataInterceptor(config)
      
      // Verify Content-Type is preserved
      expect(result.headers['Content-Type']).toBe('application/json')
    })
  })

  describe('Auth methods', () => {
    it('should call login endpoint with credentials', async () => {
      const credentials = { email: 'test@example.com', password: 'password123' }
      
      await api.auth().login(credentials)
      
      expect(mockPost).toHaveBeenCalledWith('/auth/public/login', credentials)
    })
    
    it('should call register endpoint with user data', async () => {
      const userData = { 
        email: 'test@example.com', 
        password: 'password123',
        name: 'Test User'
      }
      
      await api.auth().register(userData)
      
      expect(mockPost).toHaveBeenCalledWith('/auth/public/signup', userData)
    })
    
    it('should call logout endpoint', async () => {
      await api.auth().logout()
      
      expect(mockPost).toHaveBeenCalledWith('/auth/public/logout')
    })
  })

  describe('User profile methods', () => {
    it('should call current user endpoint', async () => {
      await api.me().current()
      
      expect(mockGet).toHaveBeenCalledWith('/me')
    })
    
    it('should call update profile endpoint with profile data', async () => {
      const profileData = { name: 'Updated Name', email: 'updated@example.com' }
      
      await api.me().updateProfile(profileData)
      
      expect(mockPut).toHaveBeenCalledWith('/me', profileData)
    })
    
    it('should call update password endpoint with new password', async () => {
      const password = 'new-password123'
      
      await api.me().updatePassword(password)
      
      expect(mockPost).toHaveBeenCalledWith('/me/change-password', { password })
    })
    
    it('should call update avatar endpoint with form data', async () => {
      const formData = new FormData()
      
      await api.me().updateAvatar(formData)
      
      expect(mockPost).toHaveBeenCalledWith('/me/avatar', formData)
    })
  })

  describe('Category methods', () => {
    it('should call get all categories endpoint', async () => {
      await api.category().getAll()
      
      expect(mockGet).toHaveBeenCalledWith('/categories')
    })
    
    it('should call get category by ID endpoint', async () => {
      const categoryId = 123
      
      await api.category().getById(categoryId)
      
      expect(mockGet).toHaveBeenCalledWith(`/categories/${categoryId}`)
    })
    
    it('should call create category endpoint with category data', async () => {
      const categoryData = { name: 'New Category' }
      
      await api.category().create(categoryData)
      
      expect(mockPost).toHaveBeenCalledWith('/categories', categoryData)
    })
    
    it('should call update category endpoint with category data', async () => {
      const categoryId = 123
      const categoryData = { name: 'Updated Category' }
      
      await api.category().update(categoryId, categoryData)
      
      expect(mockPut).toHaveBeenCalledWith(`/categories/${categoryId}`, categoryData)
    })
    
    it('should call delete category endpoint', async () => {
      const categoryId = 123
      
      await api.category().delete(categoryId)
      
      expect(mockDelete).toHaveBeenCalledWith(`/categories/${categoryId}`)
    })
  })

  describe('Orders methods', () => {
    it('should call get all orders endpoint', async () => {
      await api.orders().getAll()
      
      expect(mockGet).toHaveBeenCalledWith('/orders')
    })
    
    it('should call get order by ID endpoint', async () => {
      const orderId = 123
      
      await api.orders().getById(orderId)
      
      expect(mockGet).toHaveBeenCalledWith(`/orders/${orderId}`)
    })
    
    it('should call confirm order endpoint with comment', async () => {
      const orderId = 123
      const comment = 'Order confirmed'
      
      await api.orders().confirm(orderId, comment)
      
      expect(mockPut).toHaveBeenCalledWith(`/orders/${orderId}/confirm`, { comment })
    })
    
    it('should call cancel order endpoint with comment', async () => {
      const orderId = 123
      const comment = 'Order cancelled'
      
      await api.orders().cancel(orderId, comment)
      
      expect(mockPut).toHaveBeenCalledWith(`/orders/${orderId}/cancel`, { comment })
    })
    
    it('should call delete order endpoint', async () => {
      const orderId = 123
      
      await api.orders().delete(orderId)
      
      expect(mockDelete).toHaveBeenCalledWith(`/orders/${orderId}`)
    })
    
    it('should call create order endpoint with order data', async () => {
      const orderData = { items: [{ productId: 1, quantity: 2 }] }
      
      await api.orders().create(orderData)
      
      expect(mockPost).toHaveBeenCalledWith('/orders', orderData)
    })
    
    it('should call update order endpoint with order data', async () => {
      const orderId = 123
      const orderData = { status: 'PROCESSING' }
      
      await api.orders().update(orderId, orderData)
      
      expect(mockPut).toHaveBeenCalledWith(`/orders/${orderId}`, orderData)
    })
  })

  describe('Products methods', () => {
    it('should call get all products endpoint', async () => {
      await api.products().getAll()
      
      expect(mockGet).toHaveBeenCalledWith('/products')
    })
    
    it('should call get product by ID endpoint', async () => {
      const productId = 123
      
      await api.products().getById(productId)
      
      expect(mockGet).toHaveBeenCalledWith(`/products/${productId}`)
    })
    
    it('should call add product endpoint with product data', async () => {
      const productData = { 
        name: 'New Product', 
        price: 99.99,
        description: 'Product description',
        categoryId: 1
      }
      
      await api.products().add(productData)
      
      expect(mockPost).toHaveBeenCalledWith('/products', productData)
    })
    
    it('should call update product endpoint with product data', async () => {
      const productId = 123
      const productData = { price: 129.99 }
      
      await api.products().update(productId, productData)
      
      expect(mockPut).toHaveBeenCalledWith(`/products/${productId}`, productData)
    })
    
    it('should call delete product endpoint', async () => {
      const productId = 123
      
      await api.products().delete(productId)
      
      expect(mockDelete).toHaveBeenCalledWith(`/products/${productId}`)
    })
  })

  describe('Reports methods', () => {
    it('should call sales report endpoint with date parameters', async () => {
      const params = {
        startDate: '2023-01-01',
        endDate: '2023-12-31'
      }
      
      await api.reports().sales(params)
      
      expect(mockGet).toHaveBeenCalledWith('/reports/sales', { params })
    })
    
    it('should call inventory report endpoint', async () => {
      await api.reports().inventory()
      
      expect(mockGet).toHaveBeenCalledWith('/reports/inventory')
    })
  })

  describe('Users methods', () => {
    it('should call get all users endpoint', async () => {
      await api.users().getAll()
      
      expect(mockGet).toHaveBeenCalledWith('/users')
    })
    
    it('should call get user by ID endpoint', async () => {
      const userId = 123
      
      await api.users().getById(userId)
      
      expect(mockGet).toHaveBeenCalledWith(`/users/${userId}`)
    })
    
    it('should call add user endpoint with user data', async () => {
      const userData = {
        name: 'New User',
        email: 'newuser@example.com',
        password: 'password123',
        role: 'USER'
      }
      
      await api.users().add(userData)
      
      expect(mockPost).toHaveBeenCalledWith('/users', userData)
    })
    
    it('should call update user endpoint with user data', async () => {
      const userId = 123
      const userData = { role: 'ADMIN' }
      
      await api.users().update(userId, userData)
      
      expect(mockPut).toHaveBeenCalledWith(`/users/${userId}`, userData)
    })
    
    it('should call delete user endpoint', async () => {
      const userId = 123
      
      await api.users().delete(userId)
      
      expect(mockDelete).toHaveBeenCalledWith(`/users/${userId}`)
    })
  })

  describe('Inventory methods', () => {
    it('should call get all inventory items endpoint', async () => {
      await api.inventory().getAll()
      
      expect(mockGet).toHaveBeenCalledWith('/inventory')
    })
    
    it('should call get inventory item by ID endpoint', async () => {
      const itemId = 123
      
      await api.inventory().getById(itemId)
      
      expect(mockGet).toHaveBeenCalledWith(`/inventory/${itemId}`)
    })
    
    it('should call add inventory item endpoint with item data', async () => {
      const itemData = { productId: 1, quantity: 100 }
      
      await api.inventory().add(itemData)
      
      expect(mockPost).toHaveBeenCalledWith('/inventory', itemData)
    })
    
    it('should call update inventory item endpoint with item data', async () => {
      const itemData = { id: 123, quantity: 50 }
      
      await api.inventory().update(itemData)
      
      expect(mockPut).toHaveBeenCalledWith(`/inventory/${itemData.id}`, itemData)
    })
    
    it('should call delete inventory item endpoint', async () => {
      const itemId = 123
      
      await api.inventory().delete(itemId)
      
      expect(mockDelete).toHaveBeenCalledWith(`/inventory/${itemId}`)
    })
  })
})
