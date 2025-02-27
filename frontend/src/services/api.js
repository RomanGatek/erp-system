import axios from 'axios'

const baseURL = 'http://localhost:8080/api'

// Create user-specific instance
const user = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add request interceptor to add token
user.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // Pro FormData automaticky nastaví správný Content-Type
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Create public instance for non-authenticated requests
const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export { api, user }