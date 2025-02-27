<template>
  <div class="p-8">
    <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100 max-w-5xl mx-auto">
      <!-- Menší header -->
      <div class="relative bg-gradient-to-r from-blue-500 to-blue-600 p-6">
        <div class="flex items-center space-x-4">
          <div class="relative">
            <img 
              :src="meStore.user?.avatar || 'https://ui-avatars.com/api/?name=' + meStore.user?.firstName + '+' + meStore.user?.lastName" 
              class="w-16 h-16 rounded-full border-4 border-white shadow-lg"
              :alt="meStore.user?.firstName"
            />
            <button 
              @click="triggerFileInput"
              class="absolute bottom-0 right-0 bg-white rounded-full p-1.5 shadow-lg hover:bg-gray-50 transition-colors"
            >
              <svg class="w-3.5 h-3.5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </button>
          </div>
          <div class="text-white">
            <h1 class="text-xl font-bold">{{ meStore.user?.firstName }} {{ meStore.user?.lastName }}</h1>
            <p class="text-blue-100 text-sm">{{ meStore.user?.email }}</p>
            <div class="flex gap-2 mt-1.5">
              <span 
                v-for="role in meStore.user?.roles" 
                :key="role.name"
                class="px-2 py-0.5 rounded-full text-xs font-medium"
                :class="getRoleStyle(role.name)"
              >
                {{ formatRole(role.name) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- JWT informace -->
      <div class="px-6 py-4 border-b bg-gray-50">
        <div class="flex items-center justify-between">
          <span class="text-gray-600 text-sm">Platnost tokenu vyprší za:</span>
          <span 
            :class="[
              'font-mono px-3 py-1 rounded-full text-sm',
              remainingTime > 24 * 60 * 60 ? 'bg-green-100 text-green-800' : 
              remainingTime > 60 * 60 ? 'bg-yellow-100 text-yellow-800' : 
              'bg-red-100 text-red-800'
            ]"
          >
            {{ formatRemainingTime }}
          </span>
        </div>
      </div>

      <!-- Nastavení profilu -->
      <div class="p-6">
        <form @submit.prevent="updateProfile" class="space-y-6">
          <!-- Osobní informace a notifikace vedle sebe -->
          <div class="grid grid-cols-3 gap-8">
            <!-- Osobní informace -->
            <div class="col-span-2 space-y-4">
              <h2 class="text-lg font-semibold">Osobní údaje</h2>
              <div class="grid grid-cols-2 gap-4">
                <BaseInput
                  v-model="profileForm.firstName"
                  label="Jméno"
                  placeholder="Vaše jméno"
                />
                <BaseInput
                  v-model="profileForm.lastName"
                  label="Příjmení"
                  placeholder="Vaše příjmení"
                />
              </div>
              <div class="grid grid-cols-2 gap-4">
                <BaseInput
                  v-model="profileForm.email"
                  type="email"
                  label="Email"
                  placeholder="vas@email.cz"
                />
                <BaseInput
                  v-model="profileForm.username"
                  label="Uživatelské jméno"
                  placeholder="Uživatelské jméno"
                />
              </div>
            </div>

            <!-- Notifikace -->
            <div class="space-y-4">
              <h2 class="text-lg font-semibold">Notifikace</h2>
              <div class="space-y-3 bg-gray-50 p-4 rounded-lg">
                <BaseCheckbox
                  v-model="profileForm.notifications.email"
                  label="Emailové notifikace"
                />
                <BaseCheckbox
                  v-model="profileForm.notifications.push"
                  label="Push notifikace"
                />
              </div>
            </div>
          </div>

          <!-- Změna hesla -->
          <div class="border-t pt-6">
            <h2 class="text-lg font-semibold mb-4">Změna hesla</h2>
            <div class="grid grid-cols-3 gap-4">
              <PasswordInput
                v-model="profileForm.currentPassword"
                label="Současné heslo"
                placeholder="••••••••"
              />
              <PasswordInput
                v-model="profileForm.newPassword"
                label="Nové heslo"
                placeholder="••••••••"
              />
              <PasswordInput
                v-model="profileForm.confirmPassword"
                label="Potvrzení hesla"
                placeholder="••••••••"
              />
            </div>
          </div>

          <!-- Tlačítka -->
          <div class="flex justify-end space-x-4 pt-6 border-t">
            <button
              type="button"
              @click="resetForm"
              class="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Zrušit změny
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Uložit změny
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useMeStore } from '@/stores/me'
import { notify } from '@kyvg/vue3-notification'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseCheckbox from '@/components/common/BaseCheckbox.vue'
import { jwtDecode } from 'jwt-decode'
import PasswordInput from '@/components/common/PasswordInput.vue'

defineOptions({
  name: 'ProfileView'
})

const meStore = useMeStore()
const fileInput = ref(null)

const profileForm = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
  notifications: {
    email: true,
    push: true
  }
})

// JWT token remaining time
const remainingTime = computed(() => {
  const token = localStorage.getItem('token')
  if (!token) return 0
  
  const decoded = jwtDecode(token)
  return Math.max(0, decoded.exp - Math.floor(Date.now() / 1000))
})

const formatRemainingTime = computed(() => {
  const hours = Math.floor(remainingTime.value / 3600)
  const minutes = Math.floor((remainingTime.value % 3600) / 60)
  
  if (hours > 24) {
    const days = Math.floor(hours / 24)
    return `${days} ${days === 1 ? 'den' : days < 5 ? 'dny' : 'dní'}`
  }
  if (hours > 0) {
    return `${hours}h ${minutes}m`
  }
  return `${minutes}m`
})

const formatRole = (role) => {
  return role.replace('ROLE_', '').toLowerCase()
}

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    // Zde by byla implementace nahrání avataru
    notify({
      type: 'success',
      text: 'Avatar byl úspěšně změněn'
    })
  } catch (error) {
    notify({
      type: 'error',
      text: 'Nepodařilo se změnit avatar'
    })
  }
}

const updateProfile = async () => {
  if (profileForm.newPassword && profileForm.newPassword !== profileForm.confirmPassword) {
    notify({
      type: 'error',
      text: 'Hesla se neshodují'
    })
    return
  }

  try {
    await meStore.updateProfile({
      firstName: profileForm.firstName,
      lastName: profileForm.lastName,
      email: profileForm.email,
      username: profileForm.username,
      ...(profileForm.newPassword && {
        currentPassword: profileForm.currentPassword,
        newPassword: profileForm.newPassword
      })
    })
    
    notify({
      type: 'success',
      text: 'Profil byl úspěšně aktualizován'
    })
  } catch (error) {
    notify({
      type: 'error',
      text: 'Nepodařilo se aktualizovat profil'
    })
  }
}

const resetForm = () => {
  Object.assign(profileForm, {
    firstName: meStore.user?.firstName || '',
    lastName: meStore.user?.lastName || '',
    email: meStore.user?.email || '',
    username: meStore.user?.username || '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

onMounted(() => {
  resetForm()
})

// Přidáme helper pro styly rolí
const getRoleStyle = (roleName) => {
  const roleStyles = {
    'ROLE_ADMIN': ['bg-red-100', 'text-red-800'],
    'ROLE_MANAGER': ['bg-blue-100', 'text-blue-800'],
    'ROLE_USER': ['bg-green-100', 'text-green-800']
  }
  return roleStyles[roleName] || ['bg-gray-100', 'text-gray-800']
}
</script>

<style scoped>
.bg-white {
  overflow: hidden;
}
</style> 