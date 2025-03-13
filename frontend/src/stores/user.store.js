// stores/user.js
import { defineStore } from 'pinia'
import api from '@/services/api'
import { useMeStore } from '@/stores/me.store.js'
import { setupSort, filter } from '@/utils'
import { paginateViaState } from '@/utils'
import { AxiosError } from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    /** @type {import('@/services/api').User[]} */
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
    paginatedUsers: (state) => paginateViaState(state)
  },
  actions: {
    async fetchUsers() {
      this.loading = true
      try {
        /**@type {[User[], AxiosError]}   */
        const [data, error] = await api.users().getAll()
        if (error) throw error
        const me = useMeStore().user
        this.items = data.filter(user => user?.email !== me?.email)
        this.error = null;
      } catch (error) {
        this.error = error
        this.loading = false
      }
    },
    async addUser(user) {
      const payload = { ...user, roles: user.roles.map(role => role.name.replace('ROLE_', '')) }
      const [_, error] = await api.users().add(payload)
      this.error = error
      await this.fetchUsers()
    },
    async updateUser(user) {
      const payload = {
        lastName: user.lastName,
        firstName: user.firstName,
        email: user.email,
        username: user.username,
        roles: user.roles.map(role => role.name.replace('ROLE_', '')),
        active: user.active,
        password: user.password
      }
      var _;
      [_, this.error] = await api.users().update(user.id, payload)
      if (!this.error) {
        await this.fetchUsers()
        this.editedUserIndex = null
      }
    },
    async deleteUser(userId) {
      var _;
      [_, this.error] = await api.users().delete(userId)
      await this.fetchUsers()
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
