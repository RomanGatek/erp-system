import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useErrorStore } from '@/stores/errors.store.js'
import { AxiosError } from 'axios'
import { notify } from '@kyvg/vue3-notification'

// Mock the notify function so we can spy on its calls
vi.mock('@kyvg/vue3-notification', () => ({
  notify: vi.fn()
}))

describe('useErrorStore', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useErrorStore()
    // Reset initial state
    store.errorDefault = {}
    // Override errors with a plain object including a $clear mock.
    // This is needed because the store calls this.errors.$clear().
    store.errors = { general: null, $clear: vi.fn() }
    store.externalStore = null
    vi.clearAllMocks()
  })

  it('setErrorDefault should merge the object with the existing errorDefault', () => {
    store.errorDefault = { a: 1 }
    store.setErrorDefault({ b: 2 })
    expect(store.errorDefault).toEqual({ b: 2, a: 1 })
  })

  it('clear should delete the specified field from errors', () => {
    store.errors = { general: 'error', field1: 'error1' }
    store.clear('field1')
    expect(store.errors.field1).toBeUndefined()
    expect(store.errors.general).toBe('error')
  })

  it('setExternalStore should set the externalStore reference', () => {
    const external = { error: 'some error' }
    store.setExternalStore(external)
    expect(store.externalStore).toStrictEqual(external)
  })

  describe('handle', () => {
    it('should set general error when AxiosError has no response data', () => {
      const axiosError = new AxiosError('Request failed')
      axiosError.response = { data: null }
      store.errors.$clear = vi.fn()

      store.handle(axiosError)

      expect(store.errors.$clear).toHaveBeenCalled()
      expect(store.errors.general).toBe('An unexpected error occurred')
    })

    it('should handle AxiosError with an array response data', () => {
      const errorArray = [
        { field: 'username', message: 'Invalid username' },
        { message: 'General error' }
      ]
      const axiosError = new AxiosError('Request failed')
      axiosError.response = { data: errorArray }
      store.errors.$clear = vi.fn()

      store.handle(axiosError)

      expect(store.errors.$clear).toHaveBeenCalled()
      expect(store.errors.username).toBe('Invalid username')
      expect(store.errors.general).toBe('General error')
    })

    it('should handle AxiosError with an object response data for special error fields', () => {
      const responseData = { field: 'database', message: 'Database error occurred' }
      const axiosError = new AxiosError('Request failed')
      axiosError.response = { data: responseData }
      store.errors.$clear = vi.fn()

      store.handle(axiosError)

      expect(store.errors.$clear).toHaveBeenCalled()
      expect(store.errors.general).toBe(
        'Database error occurred More information you can read on backend stacktrace'
      )
    })

    it('should handle AxiosError with an object response data for non-special error fields', () => {
      const responseData = { field: 'customField', message: 'Custom error' }
      const axiosError = new AxiosError('Request failed')
      axiosError.response = { data: responseData }
      store.errors.$clear = vi.fn()

      store.handle(axiosError)

      expect(store.errors.$clear).toHaveBeenCalled()
      expect(store.errors.customField).toBe('Custom error')
    })

    it('should handle non-AxiosError by calling notify and logging error', () => {
      const genericError = new Error('Something went wrong')
      store.errors.$clear = vi.fn()
      const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      store.handle(genericError)

      expect(store.errors.$clear).toHaveBeenCalled()
      expect(notify).toHaveBeenCalledWith({
        text: genericError.message,
        title: 'An unexpected error occurred',
        type: 'error',
        duration: 5000,
        speed: 500
      })
      expect(consoleErrorSpy).toHaveBeenCalledWith(genericError)
      consoleErrorSpy.mockRestore()
    })
  })
})
