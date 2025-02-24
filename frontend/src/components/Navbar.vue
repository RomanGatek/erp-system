<template>
  <div class="bg-gray-800 p-4">
    <nav class="flex justify-between items-center">
      <div class="flex space-x-4">
        <router-link to="/" class="text-white hover:text-gray-400">Home</router-link>
        <router-link to="/users" class="text-white hover:text-gray-400">Users</router-link>
        <router-link to="/products" class="text-white hover:text-gray-400">Products</router-link>
        <router-link to="/inventory" class="text-white hover:text-gray-400">Inventory</router-link>
      </div>
      <div v-if="isLoggedIn">
        <button @click="logout" class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-600">
          Logout
        </button>
      </div>
    </nav>
  </div>
</template>

<script>
import Swal from 'sweetalert2';

export default {
  name: 'NavbarComponent',
  data() {
    return {
      isLoggedIn: !!localStorage.getItem('token')
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('token');
      this.isLoggedIn = false;
      Swal.fire({
        icon: 'success',
        title: 'Logout Successful',
        text: 'You have been successfully logged out!',
      });
      this.$router.push('/auth');
    }
  },
  watch: {
    '$route'() {
      this.isLoggedIn = !!localStorage.getItem('token');
    }
  }
}
</script>

<style scoped>
nav a {
  transition: color 0.3s;
}
</style>
