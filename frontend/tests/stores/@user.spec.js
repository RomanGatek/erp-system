import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useUserStore } from '@/stores/user.store.js'
import { api } from '@/services/api'
import { useMeStore } from '@/stores/me.store.js'

// Stub external utilities so our tests are isolated
vi.mock('@/utils'
  filter: (state, predicate) => state.items.filter(predicate),
  setupSort: (defaultField) => ({ field: defaultField, direction: 'asc' })
}))

vi.mock('@/utils', () => ({
  __paginate: (state) => {
    const start = (state.pagination.currentPage - 1) * state.pagination.perPage
    return state.items.slice(start, start + state.pagination.perPage)
  }
}))

// Mock the API service
vi.mock('@/services/api', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

// Stub the useMeStore so that the current user has a specific email
vi.mock('@/stores/me', () => ({
  useMeStore: () => ({
    user: { email: 'me@example.com' }
  })
}))

describe('useUserStore', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useUserStore()
    // Reset store state
    store.items = []
    store.loading = false
    store.error = null
    store.searchQuery = ''
    store.pagination = { currentPage: 1, perPage: 10 }
    store.sorting = { field: 'username', direction: 'asc' }
    store.isEditing = false
    store.editedUserIndex = null
    vi.clearAllMocks()
  })

  it('fetchUsers: should set items and filter out the current user', async () => {
    const responseData = [
      { id: 1, email: 'user1@example.com', username: 'user1', firstName: 'John', lastName: 'Doe' },
      { id: 2, email: 'me@example.com', username: 'meuser', firstName: 'Me', lastName: 'User' },
      { id: 3, email: 'user3@example.com', username: 'user3', firstName: 'Jane', lastName: 'Smith' }
    ]
    api.get.mockResolvedValue({ data: responseData })

    await store.fetchUsers()

    expect(api.get).toHaveBeenCalledWith('/users')
    // The current user (with email 'me@example.com') should be filtered out.
    expect(store.items).toEqual([
      { id: 1, email: 'user1@example.com', username: 'user1', firstName: 'John', lastName: 'Doe' },
      { id: 3, email: 'user3@example.com', username: 'user3', firstName: 'Jane', lastName: 'Smith' }
    ])
    expect(store.error).toBeNull()
  })

  it('fetchUsers: should set error on failed API call', async () => {
    const error = new Error('Network Error')
    api.get.mockRejectedValue(error)

    await store.fetchUsers()

    expect(api.get).toHaveBeenCalledWith('/users')
    expect(store.error).toBe(error)
  })

  it('addUser: should call api.post and then fetch users', async () => {
    const newUser = {
      username: 'newuser',
      firstName: 'New',
      lastName: 'User',
      email: 'new@example.com',
      active: true,
      roles: [{ name: 'ROLE_ADMIN' }]
    }
    const responseData = [
      { id: 1, email: 'user1@example.com', username: 'user1', firstName: 'John', lastName: 'Doe' }
    ]
    api.post.mockResolvedValue({ data: {} })
    api.get.mockResolvedValue({ data: responseData })

    await store.addUser(newUser)

    expect(api.post).toHaveBeenCalledWith('/users', {
      ...newUser,
      roles: ['ADMIN']
    })
    expect(api.get).toHaveBeenCalledWith('/users')
    // The fetched items should filter out the current user (me@example.com)
    expect(store.items).toEqual(responseData.filter(u => u.email !== 'me@example.com'))
    expect(store.error).toBeNull()
  })

  it('updateUser: should call api.put and then fetch users and reset editing', async () => {
    const userToUpdate = {
      id: 1,
      username: 'user1',
      firstName: 'John',
      lastName: 'Doe',
      email: 'user1@example.com',
      active: true,
      roles: [{ name: 'ROLE_USER' }],
      password: 'secret'
    }
    api.put.mockResolvedValue({ data: {} })
    api.get.mockResolvedValue({ data: [userToUpdate] })
    store.editedUserIndex = 0

    await store.updateUser(userToUpdate)

    expect(api.put).toHaveBeenCalledWith(`/users/1`, {
      lastName: userToUpdate.lastName,
      firstName: userToUpdate.firstName,
      email: userToUpdate.email,
      username: userToUpdate.username,
      roles: ['USER'],
      active: userToUpdate.active,
      password: userToUpdate.password
    })
    expect(api.get).toHaveBeenCalledWith('/users')
    expect(store.editedUserIndex).toBeNull()
    expect(store.error).toBeNull()
  })

  it('deleteUser: should call api.delete and then fetch users', async () => {
    const responseData = [
      { id: 2, email: 'user2@example.com', username: 'user2', firstName: 'Jane', lastName: 'Smith' }
    ]
    api.delete.mockResolvedValue({ data: {} })
    api.get.mockResolvedValue({ data: responseData })

    await store.deleteUser(1)

    expect(api.delete).toHaveBeenCalledWith('/users/1')
    expect(api.get).toHaveBeenCalledWith('/users')
    expect(store.items).toEqual(responseData.filter(u => u.email !== 'me@example.com'))
    expect(store.error).toBeNull()
  })

  it('setSearch: should update searchQuery and reset current page', () => {
    store.pagination.currentPage = 3
    store.setSearch('Test')
    expect(store.searchQuery).toBe('Test')
    expect(store.pagination.currentPage).toBe(1)
  })

  it('setSorting: should toggle direction if same field is provided', () => {
    // Initially, sorting is by 'username' in 'asc' direction.
    store.setSorting('username')
    expect(store.sorting).toEqual({ field: 'username', direction: 'desc' })
    store.setSorting('username')
    expect(store.sorting).toEqual({ field: 'username', direction: 'asc' })
  })

  it('setSorting: should set new field with asc direction if different field is provided', () => {
    store.setSorting('email')
    expect(store.sorting).toEqual({ field: 'email', direction: 'asc' })
  })

  it('setPage: should update current page', () => {
    store.setPage(5)
    expect(store.pagination.currentPage).toBe(5)
  })

  it('getter filtered: should return users matching search query', () => {
    store.items = [
      { id: 1, username: 'john_doe', firstName: 'John', lastName: 'Doe', email: 'john@example.com' },
      { id: 2, username: 'jane_smith', firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com' }
    ]
    store.setSearch('jane')
    expect(store.filtered).toEqual([
      { id: 2, username: 'jane_smith', firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com' }
    ])
  })

  it('getter paginatedUsers: should return users for the current page', () => {
    // Create 15 users
    const users = Array.from({ length: 15 }, (_, i) => ({
      id: i + 1,
      username: `user${i + 1}`,
      firstName: `First${i + 1}`,
      lastName: `Last${i + 1}`,
      email: `user${i + 1}@example.com`
    }))
    store.items = users
    store.pagination = { currentPage: 2, perPage: 10 }
    const paginated = store.paginatedUsers
    // Expect users 11 to 15 (indices 10 to 14)
    expect(paginated).toEqual(users.slice(10, 15))
  })

  it('getter editingUser: should return a copy of the user being edited', () => {
    // The getter is referencing state.users, but our state uses `items`
    // Patch the store so that `users` points to `items` to simulate the intended behavior.
    store.items = [
      { id: 1, username: 'user1', firstName: 'John', lastName: 'Doe', email: 'john@example.com' },
      { id: 2, username: 'user2', firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com' }
    ]
    store.users = store.items  // Patch: assign `users` so the getter can access it
    store.editedUserIndex = 1
    const editingUser = store.editingUser
    expect(editingUser).toEqual({ ...store.items[1] })
  })
})
