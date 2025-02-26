import axios from 'axios'


class Api {
  constructor(ip = 'localhost', port = 8080, guest = false) {
    this.api = axios.create({
      baseURL: `http://${ip}:${port}/api`,
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    })
    if (guest) {
      this.api.defaults.headers.Authorization = null
    }
  }

  async get(url, params = {}) {
    const response = await this.api.get(url, { params })
    return response.data
  }

  async post(url, data = {}) {
    const response = await this.api.post(url, data)
    return response.data
  }

  async put(url, data = {}) {
    const response = await this.api.put(url, data)
    return response.data
  }

  async delete(url) {
    const response = await this.api.delete(url)
    return response.data
  }

  async patch(url, data = {}) {
    const response = await this.api.patch(url, data)
    return response.data
  }
}

export const guest = new Api('localhost', 8080, true)
export const user = new Api('localhost', 8080, false)