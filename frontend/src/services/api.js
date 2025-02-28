import axios from 'axios'
import { notify } from '@kyvg/vue3-notification'; // Import notification

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

const fetchItems = async () => {
    try {
        const response = await api.get('/inventory');
        items.value = response.data;
        error.value = null;
    } catch (err) {
        console.log(err.message);
        error.value = err.message;
        notify({ // Notify user on error
            type: 'error',
            text: 'Failed to fetch items: ' + err.message,
            duration: 5000,
        });
    }
}

export { api, user }