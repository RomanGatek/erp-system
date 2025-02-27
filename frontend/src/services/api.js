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
    return await this.api.get(url, { ...params })
  }

  async post(url, data = {}) {
    return await this.api.post(url, data)
  }

  async put(url, data = {}) {
    return await this.api.put(url, { ...data })
  }

  async delete(url) {
    return await this.api.delete(url)
  }

  async patch(url, data = {}) {
    return await this.api.patch(url, { ...data })
  }
}

export const guest = new Api('localhost', 8080, true)
export const user = new Api('localhost', 8080, false)