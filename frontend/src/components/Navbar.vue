<template>
  <div class="bg-gradient-to-r from-blue-700 to-blue-500 sticky top-0 z-50 shadow-lg backdrop-blur-sm bg-opacity-90">
    <nav class="container mx-auto px-4 py-4">
      <div class="flex justify-between items-center">
        <div class="flex items-center space-x-8">
          <router-link to="/" class="text-2xl font-bold text-white hover:text-indigo-200 transition-colors duration-300 flex items-center">
            <svg class="w-8 h-8 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
            ERP System
          </router-link>
          <div class="hidden md:flex space-x-1">
            <template v-for="link in navLinks" :key="link.to">
              <router-link 
                v-if="hasPermission(link.requiredRole)"
                :to="link.to" 
                class="text-white/90 hover:text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 flex items-center hover:bg-white/10"
                :class="{ 'bg-white/20 text-white shadow-lg': $route.path === link.to }"
              >
                <component :is="link.icon" class="w-4 h-4 mr-2"/>
                {{ link.text }}
              </router-link>
              <span
                v-else
                class="text-white/50 cursor-not-allowed px-4 py-2 rounded-full text-sm font-medium flex items-center"
                :title="`Nemáte oprávnění pro přístup k ${link.text}`"
              >
                <component :is="link.icon" class="w-4 h-4 mr-2"/>
                {{ link.text }}
              </span>
            </template>
          </div>
        </div>
        <div v-if="isLoggedIn" class="flex items-center space-x-6">
          <span class="hidden md:inline text-white/90"></span>
          <div class="relative">
            <button 
              @click="toggleProfileMenu"
              class="group px-4 py-2 bg-white/10 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden flex items-center"
            >
              <span class="relative z-10 flex items-center">
                <div class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center mr-2">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </span>
              <div class="absolute inset-0 bg-gradient-to-r from-white/20 to-white/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            </button>

            <div v-if="showProfileMenu" 
                 class="absolute right-0 mt-2 w-48 rounded-xl shadow-lg bg-white/90 backdrop-blur-lg ring-1 ring-black/5 py-1 transform transition-all duration-300"
                 @click.away="showProfileMenu = false"
            >
              <router-link to="/profile" 
                class="flex px-4 py-2 text-sm text-gray-700 hover:bg-gradient-to-r from-blue-50 to-blue-100 transition-colors duration-200"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                Profile
              </router-link>
              <button 
                @click="logout" 
                class="group flex w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gradient-to-r from-red-50 to-red-100 transition-all duration-300 relative overflow-hidden"
              >
                <span class="relative z-10 flex items-center">
                  <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  Logout
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import { useMeStore } from '@/stores/me'

const router = useRouter()
const meStore = useMeStore()
const showProfileMenu = ref(false)

const isLoggedIn = computed(() => {
  return !!meStore.user
})

const hasPermission = (requiredRole) => {
  if (!meStore.user) return false
  const userRoles = Object.values(meStore.user?.roles ?? []).map(role => role.name)
  
  switch(requiredRole) {
    case 'ADMIN':
      return userRoles.includes('ROLE_ADMIN')
    case 'MANAGER':
      return userRoles.includes('ROLE_MANAGER')
    case 'USER':
      return true // Každý přihlášený uživatel má základní práva
    default:
      return true
  }
}

const navLinks = [
  { 
    to: '/', 
    text: 'Home',
    requiredRole: 'USER', // Dostupné pro všechny přihlášené
    icon: {
      template: `
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
        </svg>
      `
    }
  },
  { 
    to: '/users', 
    text: 'Users',
    requiredRole: 'ADMIN',
    icon: {
      template: `
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/>
        </svg>
      `
    }
  },
  { 
    to: '/products', 
    text: 'Products',
    requiredRole: 'MANAGER',
    icon: {
      template: `
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
        </svg>
      `
    }
  },
  { 
    to: '/inventory', 
    text: 'Inventory',
    requiredRole: 'MANAGER',
    icon: {
      template: `
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
        </svg>
      `
    }
  }
]

const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value
}

const logout = async () => {
  try {
    showProfileMenu.value = false
    
    await Swal.fire({
      title: 'Odhlašování...',
      icon: 'info',
      showConfirmButton: false,
      timer: 1000,
      timerProgressBar: true,
      didOpen: async () => {
        await meStore.logout()
      },
      customClass: {
        popup: 'swal-modern',
        title: 'swal-title',
        timerProgressBar: 'swal-timer'
      }
    })

    router.push('/auth')
  } catch (error) {
    console.error('Logout failed:', error)
  }
}
</script>

<style scoped>
nav a {
  transition: all 0.3s ease;
}

[title] {
  position: relative;
}

[title]:hover::after {
  content: attr(title);
  position: absolute;
  bottom: -30px;
  left: 50%;
  transform: translateX(-50%);
  padding: 4px 8px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  font-size: 12px;
  border-radius: 4px;
  white-space: nowrap;
  z-index: 10;
}

.logout-spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

:deep(.swal-modern) {
  border-radius: 16px !important;
  padding: 1.5rem !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1) !important;
}

:deep(.swal-title) {
  font-size: 1.5rem !important;
  color: #374151 !important;
}

:deep(.swal-timer) {
  background: #3b82f6 !important;
  height: 0.25rem !important;
  border-radius: 999px !important;
}
</style>
