// stores/user.js
import { defineStore } from 'pinia'
import { api } from '@/services/api'
import { useMeStore } from '@/stores/me'
import { setupSort, filter } from '@/utils/table-utils.js'
import { __paginate } from '@/utils/pagination.js'

/**
 * @typedef {{id: number, username: string, firstName: string, lastName: string, email: string, active: boolean, avatar?: string, roles: string[]}} User
 */


export const useUserStore = defineStore('user', {
  state: () => ({
    /** @type User[] */
    items: [],
    loading: false,
    error: null,
    searchQuery: '',
    sorting: setupSort('username'),
    pagination: { currentPage: 1, perPage: 10 },
    isEditing: false,
    editedUserIndex: null
  }),
  getters: {
    editingUser: (state) => {
      if (state?.editedUserIndex !== null) {
        return { ...state?.users[state?.editedUserIndex] }
      }
      return null
    },
    filtered: (state) => filter(state, user => {
      const searchValue = state.searchQuery.toLowerCase()

      return String(user.first_name || '')
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
          .includes(searchValue)
    }),
    paginatedUsers: (state) => __paginate(state)
  },
  actions: {
    async fetchUsers() {
      this.loading = true
      try {
        /**@type {User[]}   */
        const response = (await api.get('/users')).data
        const me = useMeStore().user
        this.items = response.filter(user => user?.email !== me?.email)
        this.error = null;
      } catch (error) {
        this.error = error
        this.loading = false
      }
    },
    async addUser(user) {
      try {
        const payload = { ...user, roles: user.roles.map(role => role.name.replace('ROLE_', '')) }
        await api.post('/users', payload)
        await this.fetchUsers()
        this.error = null;
      } catch (error) {
        this.error = error
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
          password: user.password
        })
        await this.fetchUsers()
        this.editedUserIndex = null
        this.error = null;
      } catch (error) {
        this.error = error
      }
    },
    async deleteUser(userId) {
      try {
        await api.delete(`/users/${userId}`)
        await this.fetchUsers()
        this.error = null;
      } catch (error) {
        this.error = error
      }
    },
    setSearch(query) {
      this.searchQuery = query
      this.pagination.currentPage = 1 // Reset to the first page when searching
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
    }
  }
})
