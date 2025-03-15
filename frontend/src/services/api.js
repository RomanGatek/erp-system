import axios, { AxiosError } from 'axios'
import { notify } from '@kyvg/vue3-notification'
import { jwtDecode } from 'jwt-decode'
import router from '@/router'

class Api {
  #axios
  #cache = new Map()
  #healthCheckTimeout = null
  #pendingRequests = new Map()
  #isConnected = true
  #lastHealthCheck = 0
  
  // Health check with debouncing (only check once every 30 seconds)
  async #checkConnection() {
    const now = Date.now()
    // Return cached result if checked recently
    if (now - this.#lastHealthCheck < 30000 && this.#isConnected) {
      return this.#isConnected
    }
    
    this.#lastHealthCheck = now
    
    // Clear existing timeout
    if (this.#healthCheckTimeout) {
      clearTimeout(this.#healthCheckTimeout)
    }
    
    try {
      // Use AbortController for timeout control
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), 5000) // 5-second timeout
      
      await fetch(this.#axios.defaults.checkHealthURL, {
        headers: {
          'Content-Type': 'application/json'
        },
        mode: 'no-cors',
        method: 'HEAD',
        signal: controller.signal
      })
      
      clearTimeout(timeoutId)
      this.#isConnected = true
      return true
    } catch (error) {
      this.#isConnected = false
      
      console.error('🔴 API connection failed:', error.message)
      notify({
        title: 'API connection',
        text: 'connection to api endpoint failed',
        type: 'error',
        duration: 2000,
        speed: 1000
      })
      return false
    }
  }
  
  // Adds request cancellation functionality
  #createCancelToken(requestId) {
    const source = axios.CancelToken.source()
    this.#pendingRequests.set(requestId, source)
    return source.token
  }
  
  #removePendingRequest(requestId) {
    this.#pendingRequests.delete(requestId)
  }
  
  // Cancel a specific pending request
  cancelRequest(requestId) {
    const source = this.#pendingRequests.get(requestId)
    if (source) {
      source.cancel('Request cancelled')
      this.#pendingRequests.delete(requestId)
    }
  }
  
  // Cancel all pending requests
  cancelAllRequests() {
    this.#pendingRequests.forEach(source => source.cancel('Operation cancelled'))
    this.#pendingRequests.clear()
  }

  constructor() {
    this.#axios = axios.create({
      baseURL: 'http://localhost:8080/api',
      checkHealthURL: 'http://localhost:8080/actuator/health',
      withCredentials: true,
      headers: { 'Content-Type': 'application/json' },
      timeout: 10000 // Add a default timeout
    })

    this.#axios.interceptors.request.use(
      async (config) => {
        // Only check connection for non-health endpoints
        if (!await this.#checkConnection()) {
          throw new Error('API is not available')
        }

        const token = localStorage.getItem('token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        if (config.data instanceof FormData) {
          delete config.headers['Content-Type']
        }
        
        // Add request cancellation support
        if (config.requestId) {
          config.cancelToken = this.#createCancelToken(config.requestId)
        }
        
        return config
      },
      (error) => Promise.reject(error)
    )
    
    // Add response interceptor to handle cache
    this.#axios.interceptors.response.use(
      (response) => {
        // If this is a cacheable request
        if (response.config.cache) {
          const cacheKey = `${response.config.method}:${response.config.url}`
          this.#cache.set(cacheKey, {
            data: response.data,
            timestamp: Date.now(),
            maxAge: response.config.cacheMaxAge || 60000 // Default: 1 minute
          })
        }
        
        // Clean up pending request
        if (response.config.requestId) {
          this.#removePendingRequest(response.config.requestId)
        }
        
        return response
      },
      (error) => {
        // Clean up pending request on error too
        if (error.config?.requestId) {
          this.#removePendingRequest(error.config.requestId)
        }
        return Promise.reject(error)
      }
    )

    // Only log in development
    if (process.env.NODE_ENV !== 'production') {
      console.log('✅ API instance created')
      console.log('🔗 Base URL:', this.#axios.defaults.baseURL)
      console.log('🔑 Authorization Bearer')
      console.log('🔦 Endpoints discovered')
    }
  }
  
  // Cached request method
  async #cachedRequest(config) {
    const cacheKey = `${config.method}:${config.url}`
    
    // Check if we have a valid cached response
    if (config.cache && this.#cache.has(cacheKey)) {
      const cachedItem = this.#cache.get(cacheKey)
      const now = Date.now()
      
      if (now - cachedItem.timestamp < cachedItem.maxAge) {
        return { data: cachedItem.data }
      } else {
        // Remove expired cache
        this.#cache.delete(cacheKey)
      }
    }
    
    // Proceed with actual request
    return this.#axios(config)
  }
  
  // Clear entire cache or specific endpoint
  clearCache(endpoint = null) {
    if (endpoint) {
      // Delete specific cache entries that match the endpoint
      this.#cache.forEach((value, key) => {
        if (key.includes(endpoint)) {
          this.#cache.delete(key)
        }
      })
    } else {
      // Clear all cache
      this.#cache.clear()
    }
  }

  /**
   * Auth management operations
   */
  auth() {
    return {
      /**
     * Authenticates a user with credentials
     * @param {{email: string, password: string}} credentials User credentials
     * @returns {Promise<string|null>} Access token if login successful, null otherwise
     */
      login: async (credentials) => {
        try {
          const response = await this.#axios.post('/auth/public/login', credentials)
          /** @type {LoginResponse} */
          const data = response.data
          return [data, null]
        } catch (e) {
          return [{}, e]
        }
      },

      /**
       * Registers a new user
       * @param {{email: string, password: string, name: string}} userData User registration data
       * @returns {Promise<Object>} Registered user data
       */
      register: async (userData) => {
        try {
          const response = await this.#axios.post('/auth/public/signup', userData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },

      /**
       * Logs out the current user
       * @returns {Promise<Object>} Logout result
       */
      logout: async() => {
        try {
          const response = await this.#axios.post('/auth/public/logout')
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },

      /**
       * Initiates password reset for the given email
       * @param {string} email User email
       * @returns {Promise<Object>} Password reset request result
       */
      forgotPassword: async (email) => {
        try {
          const response = await api.post('/auth/public/forgot-password', { email })
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },

      /**
       * Resets password using token
       * @param {string} token Reset password token
       * @param {string} newPassword New password
       * @returns {Promise<Object>} Password reset result
       */
      resetPassword: async (token, newPassword) => {
        try {
          const response = await api.post('/auth/public/reset-password', {
            token,
            newPassword
          })
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      }
    }
  }

  /**
   * User profile related operations
   */
  me() {
    return {
      /**
       * Gets the current user's profile information
       * @returns {Promise<[import('../../types').User, AxiosError]>} Current user's profile data
       */
      current: async () => {
        try {
          const response = await this.#axios.get('/me')
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Updates the current user's profile information
       * @param {import('../../types').UserUpdate} profileData - Updated profile data
       * @returns {Promise<[import('../../types').User, AxiosError]>} Updated user profile data
       */
      updateProfile: async (profileData) => {
        try {
          const response = await this.#axios.put('/me', profileData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Changes the current user's password
       * @param {string} password - New password
       * @returns {Promise<[import('../../types').User, AxiosError]>} Password update result
       */
      updatePassword: async (password) => {
        try {
          const response = await this.#axios.post('/me/change-password', { password })
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Updates the current user's avatar
       * @param {FormData} formData - Form data containing the avatar image
       * @returns {Promise<[import('../../types').User, AxiosError]>} Avatar update result
       */
      updateAvatar: async (formData) => {
        try {
          const response = await this.#axios.post('/me/avatar', formData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      refreshToken: async () => {
        try {
          const refreshToken = localStorage.getItem('refresh-token')
          if (!refreshToken) {
            notify({
              title: 'Refresh token',
              text: 'No refresh token found, please login again',
              type: 'error',
              duration: 3500,
              speed: 1000
            })
            router.push('/auth')
            return null;
          }

          const decoded = jwtDecode(refreshToken)
          if (decoded.exp * 1000 < Date.now()) {
            notify({
              title: 'Refresh token',
              text: 'Refresh token has expired, please login again',
              type: 'error',
              duration: 3500,
              speed: 1000
            })
            router.push('/auth')
            return null;
          }
          const response = await axios.get('http://localhost:8080/api/me/renew', { 
            params: { refreshToken }, 
            headers: { 
              Authorization: `Bearer ${refreshToken}`,
              'Content-Type': 'application/json'
            } 
          })

          if (response.status === 200) {
            const { accessToken, refreshToken } = response.data
            notify({
              title: 'Authorization',
              text: 'Reauthorization successful',
              type: 'info',
              duration: 2000,
              speed: 1000
            })
            return [{ accessToken, refreshToken }, null]
          }
        } catch (e) {
          return [null, e]
        }
      }
    }
  }

  category() {
    return {
      getAll: async (options = {}) => {
        try {
          const response = await this.#cachedRequest({
            method: 'get',
            url: '/categories',
            cache: options.cache !== false,
            cacheMaxAge: options.cacheMaxAge || 300000, // 5 minutes default
            requestId: options.requestId
          })
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      getById: async (id) => {
        try {
          const response = await this.#axios.get(`/categories/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      create: async (categoryData) => {
        try {
          const response = await this.#axios.post('/categories', categoryData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      update: async (id, categoryData) => {
        try {
          const response = await this.#axios.put(`/categories/${id}`, categoryData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      delete: async (id) => {
        try {
          const response = await this.#axios.delete(`/categories/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      }
    }
  }

    /**
   * Order management operations
   * Object containing user management methods
   */
  orders() {
    return {
      /**
       * Fetches all orders
       * @returns {Promise<[import('../../types').Order[], AxiosError]>} List of orders
       */
      getAll: async () => {
        try {
          const response = await this.#axios.get('/orders')
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Fetches a single order by ID
       * @param {number} id - Order ID
       * @returns {Promise<[import('../../types').Order, AxiosError]>} Order data
       */
      getById: async (id) => {
        try {
          const response = await this.#axios.get(`/orders/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Confirm an order
       * @param {number} orderId - Order ID
       * @param {string} comment - Approval comment
       * @returns {Promise<[import('../../types').Order, AxiosError]>} Approval result
       */
      confirm: async (orderId, comment) => {
        try {
          const response = await this.#axios.put(`/orders/${orderId}/confirm`, comment)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Rejects an order
       * @param {number} orderId - Order ID
       * @param {string} comment - Rejection comment
       * @returns {Promise<[import('../../types').Order, AxiosError]>} Rejection result
       */
      cancel: async (orderId, comment) => {
        try {
          const response = await this.#axios.put(`/orders/${orderId}/cancel`,comment)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * deletes an order
       * @param {number} orderId 
       * @returns {string}
       */
      delete: async (orderId) => {
        try {
          const response = await this.#axios.delete(`/orders/${orderId}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Creates a new order
       * @param {Partial<import('../../types').Order>} orderData 
       * @returns {Promise<[import('../../types').Order, AxiosError]>}
       */
      create: async (orderData) => {
        try {
          const response = await this.#axios.post('/orders', orderData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * updates an existing order
       * @param {number} orderId 
       * @param {Partial<import('../../types').Order>} orderData 
       * @returns {Promise<[import('../../types').Order, AxiosError]>}
       */
      update: async (orderId, orderData) => {
        try {
          const response = await this.#axios.put(`/orders/${orderId}`, orderData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      }
    }
  }

    /**
   * Product management operations
   * Object containing user management methods
   */
  products() {
    return {
      /**
       * Fetches all products
       * @returns {Promise<Object[]>} List of products
       */
      getAll: async () => {
        try {
          const response = await this.#axios.get('/products')
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Fetches a single product by ID
       * @param {number} id - Product ID
       * @returns {Promise<Object>} Product data
       */
      getById: async (id) => {
        try {
          const response = await this.#axios.get(`/products/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Creates a new product
       * @param {Object} productData - Product data
       * @param {string} productData.name - Product name
       * @param {number} productData.price - Product price
       * @param {string} productData.description - Product description
       * @param {number} productData.categoryId - Category ID
       * @returns {Promise<Object>} Created product data
       */
      add: async (productData) => {
        try {
          const response = await this.#axios.post('/products', productData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Updates an existing product
       * @param {number} id - Product ID
       * @param {Object} productData - Updated product data
       * @param {string} [productData.name] - Product name
       * @param {number} [productData.price] - Product price
       * @param {string} [productData.description] - Product description
       * @param {number} [productData.categoryId] - Category ID
       * @returns {Promise<Object>} Updated product data
       */
      update: async (id, productData) => {
        try {
          const response = await this.#axios.put(`/products/${id}`, productData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Deletes a product
       * @param {number} id - Product ID
       * @returns {Promise<void>} Deletion result
       */
      delete: async (id) => {
        try {
          const response = await this.#axios.delete(`/products/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      }
    }
  }

  /**
   * Report generation and management
   * @returns {Object} Object containing report methods
   */
  reports() {
    return {
      /**
       * Generates a sales report for a given period
       * @param {Object} params - Report parameters
       * @param {string} params.startDate - Start date in ISO format
       * @param {string} params.endDate - End date in ISO format
       * @returns {Promise<Object>} Generated report data
       */
      sales: async (params) => {
        try {
          const response = await this.#axios.get('/reports/sales', { params })
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Generates an inventory report
       * @returns {Promise<Object>} Current inventory report data
       */
      inventory: async () => {
        try {
          const response = await this.#axios.get('/reports/inventory')
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      }
    }
  }

  /**
   * User management operations
   * Object containing user management methods
   */
  users() {
    return {
      /**
       * Fetches all users
       * @returns {Promise<Object[]>} List of users
       */
      getAll: async () => {
        try {
          const response = await this.#axios.get('/users')
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Fetches a single user by ID
       * @param {number} id - User ID
       * @returns {Promise<Object>} User data
       */
      getById: async (id) => {
        try {
          const response = await this.#axios.get(`/users/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Creates a new user
       * @param {Object} userData - User data
       * @param {string} userData.name - User's name
       * @param {string} userData.email - User's email
       * @param {string} userData.password - User's password
       * @param {string} userData.role - User's role
       * @returns {Promise<Object>} Created user data
       */
      add: async (userData) => {
        try {
          const response = await this.#axios.post('/users', userData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Updates an existing user
       * @param {number} id - User ID
       * @param {Object} userData - Updated user data
       * @param {string} [userData.name] - User's name
       * @param {string} [userData.email] - User's email
       * @param {string} [userData.role] - User's role
       * @returns {Promise<Object>} Updated user data
       */
      update: async (id, userData) => {
        try {
          const response = await this.#axios.put(`/users/${id}`, userData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Deletes a user
       * @param {number} id - User ID
       * @returns {Promise<void>} Deletion result
       */
      delete: async (id) => {
        try {
          const response = await this.#axios.delete(`/users/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      }
    }
  }

  /**
   * Inventory management operations
   * Object containing inventory management methods
   */
  inventory() {
    return {
      /**
       * Fetches all inventory items
       * @returns {Promise<Object[]>} List of inventory items
       */
      getAll: async () => {
        try {
          const response = await this.#axios.get('/inventory')
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * Fetches a single inventory item by ID
       * @param {number} id - Inventory item ID
       * @returns {Promise<Object>} Inventory item data
       */
      getById: async (id) => {
        try {
          const response = await this.#axios.get(`/inventory/${id}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      /**
       * 
       * @param {*} Item 
       * @returns {Promise<?>}
       */
      add : async (Item) => {
        try {
          const response = await this.#axios.post('/inventory', Item)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      update: async (ItemData) => {
        try {
          const response = await this.#axios.put(`/inventory/${ItemData.id}`, ItemData)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      },
      delete: async (itemId) => {
        try {
          const response = await this.#axios.delete(`/inventory/${itemId}`)
          return [response.data, null]
        } catch (e) {
          return [null, e]
        }
      }
    }
  }
}

export default new Api()