<template>
  <div class="bg-gradient-to-r from-gray-800 to-gray-900 sticky top-0 z-50 shadow-lg">
    <nav class="container mx-auto px-4 py-3">
      <div class="flex justify-between items-center">
        <div class="flex items-center space-x-6">
          <router-link to="/" class="text-xl font-semibold text-white hover:text-gray-300 transition-colors duration-300">
            ERP System
          </router-link>
          <div class="hidden md:flex space-x-4">
            <router-link 
              v-for="link in navLinks" 
              :key="link.to" 
              :to="link.to" 
              class="text-gray-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-all duration-300 flex items-center"
              :class="{ 'bg-gray-700 text-white': $route.path === link.to }"
            >
              <component :is="link.icon" class="w-4 h-4 mr-2"/>
              {{ link.text }}
            </router-link>
          </div>
        </div>
        <div v-if="isLoggedIn" class="flex items-center space-x-4">
          <span class="hidden md:inline text-gray-300">Welcome back!</span>
          <div class="relative">
            <button 
              @click="toggleProfileMenu"
              class="flex items-center text-gray-300 hover:text-white focus:outline-none"
            >
              <div class="w-8 h-8 rounded-full bg-gray-600 flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
              <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <div v-if="showProfileMenu" 
                 class="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 py-1"
                 @click.away="showProfileMenu = false"
            >
              <router-link to="/profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center">
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                Profile
              </router-link>
              <button @click="logout" class="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 flex items-center">
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </div>
</template>

<script>
import Swal from 'sweetalert2';

// SVG komponenty pro navigační ikony
const HomeIcon = {
  template: `
    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
    </svg>
  `
};

const UsersIcon = {
  template: `
    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/>
    </svg>
  `
};

const ProductsIcon = {
  template: `
    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
    </svg>
  `
};

const InventoryIcon = {
  template: `
    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
    </svg>
  `
};

export default {
  name: 'NavbarComponent',
  components: {
    HomeIcon,
    UsersIcon,
    ProductsIcon,
    InventoryIcon
  },
  data() {
    return {
      isLoggedIn: !!localStorage.getItem('token'),
      showProfileMenu: false,
      navLinks: [
        { to: '/', text: 'Home', icon: 'HomeIcon' },
        { to: '/users', text: 'Users', icon: 'UsersIcon' },
        { to: '/products', text: 'Products', icon: 'ProductsIcon' },
        { to: '/inventory', text: 'Inventory', icon: 'InventoryIcon' }
      ]
    }
  },
  methods: {
    toggleProfileMenu() {
      this.showProfileMenu = !this.showProfileMenu;
    },
    logout() {
      this.showProfileMenu = false;
      Swal.fire({
        title: 'Logging out...',
        icon: 'info',
        showConfirmButton: false,
        timer: 1000,
        timerProgressBar: true,
        didOpen: () => {
          localStorage.removeItem('token');
          this.isLoggedIn = false;
        }
      }).then(() => {
        this.$router.push('/auth');
      });
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
  transition: all 0.3s ease;
}
</style>
