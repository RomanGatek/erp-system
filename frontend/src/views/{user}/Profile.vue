<template>
  <div class="p-8">
    <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 max-w-5xl mx-auto">
      <!-- Header section -->
      <div class="relative bg-gradient-to-r from-blue-600 to-blue-400 p-6">
        <div class="flex items-center space-x-4">
          <div class="relative">
            <img
              :src="meStore.user?.avatar || 'https://ui-avatars.com/api/?name=' + meStore.user?.firstName + '+' + meStore.user?.lastName"
              class="w-16 h-16 rounded-full border-4 border-white/90 shadow-lg" :alt="meStore.user?.firstName" />
            <button @click="removeAvatar"
              class="absolute bottom-0 left-0 bg-red-500 rounded-full p-1.5 shadow-lg hover:bg-red-600 transition-all group">
              <svg class="w-3.5 h-3.5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
            <button @click="triggerFileInput"
              class="absolute bottom-0 right-0 bg-white/15 backdrop-blur-lg rounded-full p-1.5 shadow-lg hover:bg-white/25 transition-all group">
              <svg class="w-3.5 h-3.5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </button>
            <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="handleAvatarChange" />
          </div>
          <div class="text-white">
            <h1 class="text-xl font-bold">{{ meStore.user?.firstName }} {{ meStore.user?.lastName }}</h1>
            <p class="text-blue-100 text-sm">{{ meStore.user?.email }}</p>
            <div class="flex gap-2 mt-1.5">
              <span v-for="role in meStore.user?.roles" :key="role.name"
                class="px-2 py-0.5 rounded-full text-xs font-medium" :class="getRoleStyle(role.name)">
                {{ formatRole(role.name) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Token expiration info -->
      <div class="px-6 py-4 border-b bg-white/15 backdrop-blur-lg">
        <div class="flex items-center justify-between">
          <span class="text-gray-600 text-sm">Token expiration:</span>
          <span :class="[
            'font-mono px-3 py-1 rounded-full text-sm',
            remainingTime > 24 * 60 * 60 ? 'bg-green-100 text-green-800' :
              remainingTime > 60 * 60 ? 'bg-yellow-100 text-yellow-800' :
                'bg-red-100 text-red-800'
          ]">
            {{ formatRemainingTime }}
          </span>
        </div>
      </div>

      <!-- Profile settings -->
      <div class="p-6">
        <form @submit.prevent="updateProfile" class="space-y-6">
          <div class="grid grid-cols-3 gap-10">
            <!-- Personal information -->
            <div class="col-span-2 space-y-6">
              <div class="flex items-center justify-between">
                <h2 class="text-xl font-semibold text-gray-900">Personal Information</h2>
                <div class="h-0.5 flex-1 bg-gray-100 ml-4"></div>
              </div>

              <div class="grid grid-cols-2 gap-6">
                <BaseInput v-model="profileForm.firstName" label="First Name" placeholder="Your first name"
                  :error="serverErrors.firstName" />
                <BaseInput v-model="profileForm.lastName" label="Last Name" placeholder="Your last name"
                  :error="serverErrors.lastName" />
                <BaseInput disabled v-model="profileForm.email" type="email" label="Email Address" placeholder="your@email.com"
                  :error="serverErrors.email" />
                <BaseInput v-model="profileForm.username" label="Username" placeholder="Username"
                  :error="serverErrors.username" />
              </div>
            </div>

            <!-- Notifications -->
            <div class="space-y-6">
              <div class="flex items-center justify-between">
                <h2 class="text-xl font-semibold text-gray-900">Notifications</h2>
                <div class="h-0.5 flex-1 bg-gray-100 ml-4"></div>
              </div>

              <div class="space-y-4 bg-gray-50 p-5 rounded-xl">
                <BaseCheckbox v-model="profileForm.notifications.email" label="Email notifications" />
                <BaseCheckbox v-model="profileForm.notifications.push" label="Push notifications" />
              </div>
            </div>
          </div>

          <!-- Password change -->
          <div class="pt-6 border-t">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-semibold text-gray-900">Change Password</h2>
              <div class="h-0.5 flex-1 bg-gray-100 ml-4"></div>
            </div>

            <div class="flex items-start gap-6">
              <div class="flex-1 grid grid-cols-2 gap-6">
                <div class="min-h-[76px]">
                  <PasswordInput v-model="profileForm.newPassword" label="New Password" placeholder="••••••••"
                    :error="serverErrors.newPassword" />
                  <p v-if="serverErrors.newPassword" class="text-xs text-red-500 mt-1">
                    {{ serverErrors.newPassword }}
                  </p>
                </div>
                <div class="min-h-[76px]">
                  <PasswordInput v-model="profileForm.confirmPassword" label="Confirm Password" placeholder="••••••••"
                    :error="serverErrors.confirmPassword" />
                  <p v-if="serverErrors.confirmPassword" class="text-xs text-red-500 mt-1">
                    {{ serverErrors.confirmPassword }}
                  </p>
                </div>
              </div>
              <button type="button" @click="updatePassword"
                class="group px-3 py-2.5 mt-7 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-lg text-sm font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden whitespace-nowrap">
                <span class="relative z-10">Apply Change</span>
                <div
                  class="absolute inset-0 bg-gradient-to-r from-blue-500 to-blue-300 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                </div>
              </button>
            </div>
          </div>

          <!-- Add general error message display -->
          <div v-if="serverErrors.general" class="text-sm text-red-600 text-center mt-4">
            {{ serverErrors.general }}
          </div>

          <!-- Action buttons -->
          <div class="flex justify-end space-x-4 pt-6 border-t">
            <button type="button" @click="resetForm"
              class="group px-6 py-3 bg-white/15 backdrop-blur-lg text-gray-700 rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden">
              <span class="relative z-10">Cancel Changes</span>
              <div
                class="!bg-red-200 absolute inset-0 bg-gradient-to-r from-gray-500/20 to-white/30 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
              </div>
            </button>
            <button type="submit"
              class="group px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden">
              <span class="relative z-10">Save Changes</span>
              <div
                class="absolute inset-0 bg-gradient-to-r from-blue-500 to-blue-300 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
              </div>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useMeStore } from '@/stores/me.store.js'
import { notify } from '@kyvg/vue3-notification'
import {
  BaseInput,
  BaseCheckbox,
  PasswordInput
} from '@/components'
import { jwtDecode } from 'jwt-decode'

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
  newPassword: '',
  confirmPassword: '',
  avatar: '',
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

  // Validate file type and size
  if (!file.type.startsWith('image/')) {
    notify({
      type: 'error',
      text: 'Please select an image file'
    })
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    notify({
      type: 'error',
      text: 'File size should not exceed 5MB'
    })
    return
  }

  const formData = new FormData()
  formData.append('avatar', file)

  console.log(file);

  try {
    await meStore.updateAvatar(formData)
    notify({
      type: 'success',
      text: 'Avatar updated successfully'
    })
  } catch (error) {
    console.error('Upload error:', error)
    notify({
      type: 'error',
      text: error.response?.data?.message || 'Failed to update avatar'
    })
  }
}

// Add server errors state
const serverErrors = ref({
  firstName: '',
  lastName: '',
  avatar: '',
  email: '',
  username: '',
  newPassword: '',
  confirmPassword: '',
  general: ''
})

// Clear server errors function
const clearServerErrors = () => {
  serverErrors.value = {
    firstName: '',
    lastName: '',
    email: '',
    avatar: '',
    username: '',
    newPassword: '',
    confirmPassword: '',
    general: ''
  }
}

// Handle server validation errors
const handleServerValidationErrors = (error) => {
  clearServerErrors()

  if (!error.response?.data) {
    serverErrors.value.general = 'An unexpected error occurred'
    return
  }

  const data = error?.response?.data || null;

  console.error(data)

  if (data) {
    if (Array.isArray(data)) {
      data.forEach(e => {
        const { field, message } = e
        if (field === 'password') {
          serverErrors.value['newPassword'] = message
        } else if (field) {
          serverErrors.value[field] = message
        } else {
          serverErrors.value.general = message
        }
      });
    } else {
      const { field, message } = data
      if (field === 'password') {
        serverErrors.value['newPassword'] = message
      } else if (field) {
        serverErrors.value[field] = message
      } else {
        serverErrors.value.general = message
      }
    }
  }
}

const updateProfile = async () => {
  clearServerErrors()

  if (profileForm.newPassword && profileForm.newPassword !== profileForm.confirmPassword) {
    serverErrors.value.confirmPassword = 'Passwords do not match'
    return
  }

  try {
    await meStore.updateProfile({
      firstName: profileForm.firstName,
      lastName: profileForm.lastName,
      email: profileForm.email,
      username: profileForm.username,
      ...(profileForm.newPassword && {
        newPassword: profileForm.newPassword
      })
    })

    notify({
      type: 'success',
      text: 'Profile was successfully updated'
    })
  } catch (error) {
    handleServerValidationErrors(error)
    notify({
      type: 'error',
      text: serverErrors.value.general || 'Failed to update profile'
    })
  }
}

const updatePassword = async () => {
  clearServerErrors()

  if (!profileForm.newPassword || !profileForm.confirmPassword) {
    serverErrors.value.general = 'Please fill in all password fields'
    return
  }

  if (profileForm.newPassword !== profileForm.confirmPassword) {
    serverErrors.value.confirmPassword = 'Passwords do not match'
    return
  }

  try {
    await meStore.updatePassword(profileForm.newPassword)

    notify({
      type: 'success',
      text: 'Password successfully updated'
    })

    profileForm.newPassword = ''
    profileForm.confirmPassword = ''
  } catch (error) {
    handleServerValidationErrors(error)
    notify({
      type: 'error',
      text: serverErrors.value.general || 'Failed to update password'
    })
  }
}

const resetForm = () => {
  Object.assign(profileForm, {
    firstName: meStore.user?.firstName || '',
    lastName: meStore.user?.lastName || '',
    email: meStore.user?.email || '',
    username: meStore.user?.username || '',
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

// Clear server errors on input change
watch(() => profileForm, () => {
  clearServerErrors()
}, { deep: true })

const removeAvatar = async () => {
  try {
    await meStore.updateProfile({
      ...profileForm,
      avatar: null // Set avatar to null
    });
    notify({
      type: 'success',
      text: 'Avatar removed successfully'
    });
  } catch (error) {
    console.error('Remove error:', error);
    notify({
      type: 'error',
      text: error.response?.data?.message || 'Failed to remove avatar'
    });
  }
}
</script>

<style scoped>
.bg-white {
  overflow: hidden;
}
</style>
