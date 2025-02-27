// stores/user.js
import { defineStore } from 'pinia'
import { user as api } from '@/services/api'
import { useMeStore } from '@/stores/me'
import { notify } from '@kyvg/vue3-notification'


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
        /**@type {{id: number, username: string, email: string, roles: string[], active: boolean}[]}   */
        const response = (await api.get('/users')).data
        const me = useMeStore().user
        this.users = response.filter(user => user.email !== me.email)
      } catch (error) {
        this.error = error.response.data || error.data
        console.log(error)
        notify({
          type: 'error',
          text: 'Nastala chyba při načítání dat. Chyba: ' + error.response.data || error.message,
          duration: 5000,
          speed: 500
        })
      } finally {
        this.loading = false
      }
    },
    async addUser(user) {
      try {
        console.log(user)
        const payload = {...user, roles: user.roles.map(role => role.name.replace('ROLE_', ''))};
        await api.post('/users', payload)
        await this.fetchUsers()
        notify({
          type: 'success',
          text: 'Údaje byly úspěšně přidány.',
          duration: 5000,
          speed: 500
        })
      } catch (error) {
        notify({
          type: 'error',
          text: 'Nastala chyba při přidávání dat. Chyba: ' + error.response.data || error.message,
          duration: 5000,
          speed: 500
        })
        this.error = error.response.message || error.message
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
        this.isEditing = false
        this.editedUserIndex = null
        notify({
          type: 'success',
          text: 'Údaje byly úspěšně aktualizovány.',
          duration: 5000,
          speed: 500
        })
      } catch (error) {
        notify({
          type: 'error',
          text: 'Nastala chyba při aktualizování dat.',
          duration: 5000,
          speed: 500
        })
        console.log(error.response.data)
        this.error = error.response.data || error.message
      }
    },
    async deleteUser(userId) {
      try {
        await api.delete(`/users/${userId}`)
        await this.fetchUsers()
        notify({
          type: 'success',
          text: 'Údaje byly úspěšně smazány.',
          duration: 5000,
          speed: 500
        })
      } catch (error) {
        notify({
          type: 'error',
          text: 'Nastala chyba při smazání dat. Chyba: ' + error.response.data || error.message,
          duration: 5000,
          speed: 500
        })
        this.error = error.response.data || error.message
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
