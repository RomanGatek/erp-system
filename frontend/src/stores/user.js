// stores/user.js
import { defineStore } from 'pinia'
import {user as api} from '@/services/api' // Import your axios instance

export const useUserStore = defineStore('user', {
  state: () => ({
    users: [],
    loading: false,
    error: null,
    isEditing: false,
    editedUserIndex: null,
    pagination: {
      currentPage: 1,
      totalPages: 1,
      perPage: 10,
      total: 0,
    },
    sorting: {
      field: 'username',
      direction: 'asc',
    },
    searchQuery: '',
  }),
  getters: {
    editingUser: (state) => {
      if (state?.editedUserIndex !== null) {
        return { ...state?.users[state?.editedUserIndex] }
      }
      return null
    },
    filteredUsers: (state) => {
      let filtered = [...state.users]

      if (state.searchQuery) {
        const searchValue = state.searchQuery.toLowerCase()
        filtered = filtered.filter(
          (user) =>
            String(user.first_name || '')
              .toLowerCase()
              .includes(searchValue) ||
            String(user.last_name || '')
              .toLowerCase()
              .includes(searchValue) ||
            String(user.email || '')
              .toLowerCase()
              .includes(searchValue) ||
            String(user.username || '')
              .toLowerCase()
              .includes(searchValue),
        )
      }

      // Sort
      if (state.sorting.field !== 'actions') {
        filtered.sort((a, b) => {
          const aVal = String(a[state.sorting.field] || '').toLowerCase()
          const bVal = String(b[state.sorting.field] || '').toLowerCase()
          const direction = state.sorting.direction === 'asc' ? 1 : -1
          return aVal > bVal ? direction : -direction
        })
      }

      return filtered
    },
    paginatedUsers: (state) => {
      const start = (state.pagination.currentPage - 1) * state.pagination.perPage
      const end = start + state.pagination.perPage
      return state.filteredUsers.slice(start, end)
    },
  },
  actions: {
    async fetchUsers() {
      this.loading = true
      try {
        const response = await api.get('/users')
        this.users = response
      } catch (error) {
        this.error = error.response.message || error.message
      } finally {
        this.loading = false
      }
    },
    async addUser(user) {
      try {
        const payload = {...user, roles: ["admin"]};
        await api.post('/users', payload)
        await this.fetchUsers()
      } catch (error) {
        this.error = error.response.message || error.message
      }
    },
    async updateUser(user) {
      try {
        await api.put(`/users/${user.id}`, user)
        await this.fetchUsers()
        this.isEditing = false
        this.editedUserIndex = null
      } catch (error) {
        this.error = error.response.message || error.message
      }
    },
    async deleteUser(userId) {
      try {
        await api.delete(`/users/${userId}`)
        await this.fetchUsers()
      } catch (error) {
        this.error = error.response.message || error.message
      }
    },
    editUser(index) {
      this.isEditing = true
      this.editedUserIndex = index
    },
    cancelEdit() {
      this.isEditing = false
      this.editedUserIndex = null
    },
    setSorting(field) {
      if (this.sorting.field === field) {
        this.sorting.direction = this.sorting.direction === 'asc' ? 'desc' : 'asc'
      } else {
        this.sorting.field = field
        this.sorting.direction = 'asc'
      }
    },
    setPage(page) {
      this.pagination.currentPage = page
    },
    setSearch(query) {
      this.searchQuery = query.trim()
      this.pagination.currentPage = 1
    },
  },
})
