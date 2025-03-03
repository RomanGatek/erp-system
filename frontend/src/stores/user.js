// stores/user.js
import { defineStore } from 'pinia'
import { user as api } from '@/services/api'
import { useMeStore } from '@/stores/me'
import { setupSort, sort } from '@/utils/sorting.js'

/**
 * @typedef {{id: number, username: string, firstName: string, lastName: string, email: string, active: boolean, avatar?: string, roles: string[]}} User
 */


export const useUserStore = defineStore('user', {
  state: () => ({
    /** @type User[] */
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
    sorting: setupSort('name'),
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
      // eslint-disable-next-line no-unsafe-optional-chaining
      let filtered = [...state?.users]

      if (state.searchQuery) {
        const searchValue = state.searchQuery.toLowerCase()
        filtered = filtered.filter(
          (user) =>
            String(user.first_name || '')
              .toLowerCase()
              .includes(searchValue) ||
            String(user.lastName || '')
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
      return sort(state, filtered)
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
        /**@type {{id: number, username: string, email: string, roles: string[], active: boolean}[]}   */
        const response = (await api.get('/users')).data
        const me = useMeStore().user
        this.users = response.filter(user => user?.email !== me?.email)
      } catch (error) {
        this.error = error;
        this.loading = false
      }
    },
    async addUser(user) {
      try {
        const payload = {...user, roles: user.roles.map(role => role.name.replace('ROLE_', ''))};
        await api.post('/users', payload)
        await this.fetchUsers()
      } catch (error) {
        this.error = error;
      }
    },
    async updateUser(user) {
      try {
        await api.put(`/users/${user.id}`, {
          lastName: user.lastName,
          firstName: user.firstName,
          email: user.email,
          username: user.username,
          roles: user.roles.map(role => role.name.replace('ROLE_', '')),
          active: user.active,
          password: user.password,
        })
        await this.fetchUsers()
        this.editedUserIndex = null
      } catch (error) {
        this.error = error;
      }
    },
    async deleteUser(userId) {
      try {
        await api.delete(`/users/${userId}`)
        await this.fetchUsers()
      } catch (error) {
        this.error = error;
      }
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
