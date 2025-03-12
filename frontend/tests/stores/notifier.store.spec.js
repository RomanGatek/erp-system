import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach } from 'vitest'
import { useNotifier } from '@/stores/notifier.store.js'

// Zjednodušené testy pro notifier store
describe('Notifier Store', () => {
  let store

  beforeEach(() => {
    // Create a fresh pinia instance for each test
    setActivePinia(createPinia())
    store = useNotifier()
  })

  describe('actions', () => {
    it('should have success method', () => {
      expect(typeof store.success).toBe('function')
    })

    it('should have error method', () => {
      expect(typeof store.error).toBe('function')
    })

    it('should have info method', () => {
      expect(typeof store.info).toBe('function')
    })

    it('should have warning method', () => {
      expect(typeof store.warning).toBe('function')
    })
  })
}) 