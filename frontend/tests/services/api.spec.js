import { api } from '@/services/api.js'
import { auth } from '@/services/auth.js'
import {
  afterEach,
  beforeEach,
  describe,
  it,
  expect,
  vi
} from 'vitest'
import MockAdapter from 'axios-mock-adapter'


describe('API service', async () => {
  afterEach(() => vi.clearAllMocks())

  it('should create a api axios instance', () => {
    expect(api.defaults.baseURL).toBe('http://localhost:8080/api')
    expect(api.defaults.headers['Content-Type']).toBe('application/json')
  })

  it('should add token to request headers', async () => {
      const token = 'test-token'
      localStorage.setItem('token', token)

      const config = { headers: {} }
      await api.interceptors.request?.handlers[0].fulfilled(config)

      expect(config.headers.Authorization).toBe(`Bearer ${token}`)
    })

    it('should remove Content-Type for FormData', async () => {
      const config = {
        data: new FormData(),
        headers: { 'Content-Type': 'application/json' }
      }
      await api.interceptors.request.handlers[0].fulfilled(config)

      expect(config.headers['Content-Type']).toBeUndefined()
    })
})
