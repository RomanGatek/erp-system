// stores/user.js
import { defineStore } from 'pinia';
import api from '@/services/api'; // Import your axios instance

export const useUserStore = defineStore('user', {
  state: () => ({
    users: [],
    loading: false,
    error: null,
    isEditing: false,
    editedUserIndex: null,
  }),
  getters: {
    editingUser: (state) => {
      if (state?.editedUserIndex !== null) {
        return { ...state?.users[state?.editedUserIndex] };
      }
      return null;
    },
  },
  actions: {
    async fetchUsers() {
      this.loading = true;
      try {
        const response = await api.get('/users');
        this.users = response.data;
      } catch (error) {
        this.error = error.response.data.message || error.message;
      } finally {
        this.loading = false;
      }
    },
    async addUser(user) {
      try {
        await api.post('/users', user);
        await this.fetchUsers();
      } catch (error) {
        this.error = error.response.data.message || error.message;
      }
    },
    async updateUser(user) {
      try {
        await api.put(`/users/${user.id}`, user);
        await this.fetchUsers();
        this.isEditing = false;
        this.editedUserIndex = null;
      } catch (error) {
        this.error = error.response.data.message || error.message;
      }
    },
    async deleteUser(userId) {
      try {
        await api.delete(`/users/${userId}`);
        await this.fetchUsers();
      } catch (error) {
        this.error = error.response.data.message || error.message;
      }
    },
    editUser(index) {
      this.isEditing = true;
      this.editedUserIndex = index;
    },
    cancelEdit() {
      this.isEditing = false;
      this.editedUserIndex = null;
    },
  },
});
