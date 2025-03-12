import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useMeStore } from '@/stores/me.store.js'
import { api } from '@/services/api'

// Mock the API service
vi.mock('@/services/api', () => ({
  api: {
    get: vi.fn(),
    put: vi.fn(),
    post: vi.fn()
  }
}))

describe('useMeStore', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useMeStore()
    // Reset store state
    store.user = null
    store.error = null
    // Clear localStorage
    localStorage.clear()
    // Reset all mock function calls
    vi.clearAllMocks()
  })

  it('fetchMe: should set user on success', async () => {
    const mockUser = { id: 1, name: 'Test User' }
    api.get.mockResolvedValue({ data: mockUser })

    await store.fetchMe()
    expect(api.get).toHaveBeenCalledWith('/me')
    expect(store.user).toEqual(mockUser)
    expect(store.error).toBeNull()
  })

  it('fetchMe: should set error on failure', async () => {
    const mockError = new Error('Failed to fetch')
    api.get.mockRejectedValue(mockError)

    await store.fetchMe()
    expect(api.get).toHaveBeenCalledWith('/me')
    expect(store.error).toBe(mockError)
  })

  it('clearUser: should reset user and error', () => {
    store.user = { id: 1, name: 'Test User' }
    store.error = new Error('Some error')
    store.clearUser()
    expect(store.user).toBeNull()
    expect(store.error).toBeNull()
  })

  it('logout: should remove token and reset user and error', async () => {
    localStorage.setItem('token', 'dummy')
    store.user = { id: 1, name: 'Test User' }
    store.error = new Error('Some error')

    await store.logout()
    expect(localStorage.getItem('token')).toBeNull()
    expect(store.user).toBeNull()
    expect(store.error).toBeNull()
  })

  it('updateProfile: should update user and return updated data', async () => {
    const profileData = { name: 'Updated Name' }
    const mockResponseData = { id: 1, name: 'Updated Name' }
    api.put.mockResolvedValue({ data: mockResponseData })

    const result = await store.updateProfile(profileData)
    expect(api.put).toHaveBeenCalledWith('/me', profileData)
    expect(store.user).toEqual(mockResponseData)
    expect(result).toEqual(mockResponseData)
  })

  it('updatePassword: should update user with new password while retaining other properties', async () => {
    // Set initial user data
    store.user = { id: 1, name: 'Test User', email: 'test@example.com' }
    const newPassword = 'newpassword'
    const mockResponseData = 'hashed_new_password'
    api.post.mockResolvedValue({ data: mockResponseData })

    await store.updatePassword(newPassword)
    expect(api.post).toHaveBeenCalledWith('/me/change-password', { password: newPassword })
    expect(store.user).toEqual({
      id: 1,
      name: 'Test User',
      email: 'test@example.com',
      password: mockResponseData
    })
  })

  it('updateAvatar: should update user with new avatar data and return it', async () => {
    const formData = new FormData()
    formData.append('avatar', 'dummy_avatar')
    const mockResponseData = { id: 1, name: 'Test User', avatar: 'new_avatar_url' }
    api.post.mockResolvedValue({ data: mockResponseData })

    const result = await store.updateAvatar(formData)
    expect(api.post).toHaveBeenCalledWith('/me/avatar', formData)
    expect(store.user).toEqual(mockResponseData)
    expect(result).toEqual(mockResponseData)
  })
})
