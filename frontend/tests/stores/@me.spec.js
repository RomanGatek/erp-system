import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useMeStore } from '@/stores/me.store.js'

// Mock API functions
const mockCurrent = vi.fn()
const mockUpdateProfile = vi.fn()
const mockUpdatePassword = vi.fn()
const mockUpdateAvatar = vi.fn()

// Mock the API service
vi.mock('@/services/api', () => {
  return {
    default: {
      me: () => ({
        current: mockCurrent,
        updateProfile: mockUpdateProfile,
        updatePassword: mockUpdatePassword,
        updateAvatar: mockUpdateAvatar
      })
    }
  }
})

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
    mockCurrent.mockResolvedValue([mockUser, null])

    await store.fetchMe()
    expect(mockCurrent).toHaveBeenCalled()
    expect(store.user).toEqual(mockUser)
    expect(store.error).toBeNull()
  })

  it('fetchMe: should set error on failure', async () => {
    const mockError = new Error('Failed to fetch')
    mockCurrent.mockResolvedValue([null, mockError])

    await store.fetchMe()
    expect(mockCurrent).toHaveBeenCalled()
    expect(store.user).toBeNull()
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
    mockUpdateProfile.mockResolvedValue([mockResponseData, null])

    const result = await store.updateProfile(profileData)
    expect(mockUpdateProfile).toHaveBeenCalledWith(profileData)
    expect(store.user).toEqual(mockResponseData)
    expect(result).toEqual(mockResponseData)
  })

  it('updatePassword: should update user password and return updated data', async () => {
    // Set initial user data
    store.user = { id: 1, name: 'Test User', email: 'test@example.com' }
    const newPassword = 'newpassword'
    const mockResponseData = { id: 1, name: 'Test User', email: 'test@example.com' }
    mockUpdatePassword.mockResolvedValue([mockResponseData, null])

    const result = await store.updatePassword(newPassword)
    expect(mockUpdatePassword).toHaveBeenCalledWith(newPassword)
    expect(store.user).toEqual(mockResponseData)
    expect(result).toEqual(mockResponseData)
  })

  it('updateAvatar: should update user with new avatar data and return it', async () => {
    const formData = new FormData()
    formData.append('avatar', 'dummy_avatar')
    const mockResponseData = { id: 1, name: 'Test User', avatar: 'new_avatar_url' }
    mockUpdateAvatar.mockResolvedValue([mockResponseData, null])

    const result = await store.updateAvatar(formData)
    expect(mockUpdateAvatar).toHaveBeenCalledWith(formData)
    expect(store.user).toEqual(mockResponseData)
    expect(result).toEqual(mockResponseData)
  })
})
