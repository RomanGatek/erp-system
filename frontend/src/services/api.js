import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    Authorization: `Bearer ${localStorage.getItem('token')}`,
    'Access-Control-Allow-Origin': '*',
    'Content-Type': 'application/json',
  },
})

export default api
