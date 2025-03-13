<template>
  <div class="p-6 sm:p-8 bg-gray-50/50">
    <div class="bg-white rounded-3xl shadow-xl max-w-5xl mx-auto overflow-hidden border border-gray-100/80">
      <!-- Header section -->
      <div class="relative bg-gradient-to-r from-blue-600 to-blue-400 p-8">
        <div class="flex flex-col sm:flex-row sm:items-center gap-6">
          <div class="relative">
            <img
              :src="meStore.user?.avatar || 'https://ui-avatars.com/api/?name=' + meStore.user?.firstName + '+' + meStore.user?.lastName"
              class="w-20 h-20 rounded-full border-4 border-white/90 shadow-lg object-cover"
              :alt="meStore.user?.firstName" />

            <div class="absolute -bottom-1 -right-1 flex gap-2">
              <button @click="removeAvatar"
                class="bg-red-500 rounded-full p-2 shadow-lg hover:bg-red-600 transition-all group">
                <svg class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
              <button @click="triggerFileInput"
                class="bg-white/20 backdrop-blur-lg rounded-full p-2 shadow-lg hover:bg-white/30 transition-all">
                <svg class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
              </button>
            </div>
            <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="handleAvatarChange" />
          </div>

          <div class="text-white">
            <h1 class="text-2xl font-bold">{{ meStore.user?.firstName }} {{ meStore.user?.lastName }}</h1>
            <p class="text-blue-100 text-sm">{{ meStore.user?.email }}</p>
            <div class="flex gap-2 mt-3">
              <span v-if="meStore.user?.roles.find(r => r.name.includes('ADMIN'))"
                class="px-3 py-0.5 rounded-full text-xs font-medium" :class="getRoleStyle('ROLE_ADMIN')">
                {{ formatRole('ROLE_ADMIN') }}
              </span>
              <span v-else-if="meStore.user?.roles.find(r => r.name.includes('MANAGER'))"
                class="px-3 py-0.5 rounded-full text-xs font-medium" :class="getRoleStyle('ROLE_MANAGER')">
                {{ formatRole('ROLE_MANAGER') }}
              </span>
              <span v-else class="px-3 py-0.5 rounded-full text-xs font-medium" :class="getRoleStyle('ROLE_USER')">
                {{ formatRole('ROLE_USER') }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Token expiration info -->
      <div class="px-6 py-3 border-b bg-white">
        <div class="flex items-center justify-between">
          <span class="text-gray-600 text-sm font-medium">Token expiration</span>
          <span :class="[
            'font-mono px-2.5 py-0.5 rounded-full text-xs font-medium',
            remainingTime > 10 * 60 ? 'bg-green-100 text-green-800' :
              remainingTime > 5 * 60 ? 'bg-amber-100 text-amber-800' :
                'bg-red-100 text-red-800'
          ]">
            {{ formatRemainingTime }}
          </span>
        </div>
      </div>

      <!-- Profile settings -->
      <div class="p-6">
        <form @submit.prevent="updateProfile" class="space-y-6">
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Personal information -->
            <div class="lg:col-span-2 space-y-5">
              <div class="flex items-center gap-3">
                <h2 class="text-lg font-semibold text-gray-900">Personal Information</h2>
                <div class="h-0.5 flex-1 bg-gray-100"></div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <BaseInput v-model="profileForm.firstName" label="First Name" placeholder="Your first name"
                  :error="errors.firstName" class="text-sm" />
                <BaseInput v-model="profileForm.lastName" label="Last Name" placeholder="Your last name"
                  :error="errors.lastName" class="text-sm" />
                <BaseInput disabled v-model="profileForm.email" type="email" label="Email Address"
                  placeholder="your@email.com" :error="errors.email" class="text-sm" />
                <BaseInput v-model="profileForm.username" label="Username" placeholder="Username"
                  :error="errors.username" class="text-sm" />
              </div>
            </div>

            <!-- Notifications -->
            <div class="space-y-5">
              <div class="flex items-center gap-3">
                <h2 class="text-lg font-semibold text-gray-900">Notifications</h2>
                <div class="h-0.5 flex-1 bg-gray-100"></div>
              </div>

              <div class="space-y-3 bg-gray-50 p-4 rounded-xl border border-gray-100">
                <BaseCheckbox v-model="profileForm.notifications.email" label="Email notifications" class="text-sm" />
                <BaseCheckbox v-model="profileForm.notifications.push" label="Push notifications" class="text-sm" />
              </div>
            </div>
          </div>

          <!-- Password change -->
          <div class="pt-6 border-t">
            <div class="flex items-center gap-3 mb-4">
              <h2 class="text-lg font-semibold text-gray-900">Change Password</h2>
              <div class="h-0.5 flex-1 bg-gray-100"></div>
            </div>

            <div class="flex flex-col sm:flex-row items-start gap-4">
              <div class="w-full sm:flex-1 grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div class="min-h-[70px]">
                  <PasswordInput v-model="profileForm.password" label="New Password" placeholder="••••••••"
                    :error="errors.password" class="text-sm" />
                  <p v-if="errors.password" class="text-xs text-red-500 mt-1">
                    {{ errors.password }}
                  </p>
                </div>
                <div class="min-h-[70px]">
                  <PasswordInput v-model="profileForm.confirmPassword" label="Confirm Password" placeholder="••••••••"
                    :error="errors.confirmPassword" class="text-sm" />
                  <p v-if="errors.confirmPassword" class="text-xs text-red-500 mt-1">
                    {{ errors.confirmPassword }}
                  </p>
                </div>
              </div>
              <BaseButton type="primary" @click="updatePassword" class="mt-6 w-full sm:w-auto whitespace-nowrap text-sm">
                Apply Change
              </BaseButton>
            </div>
          </div>

          <!-- Add general error message display -->
          <div v-if="errors.general" class="text-xs text-red-600 text-center mt-3 p-2 bg-red-50 rounded-lg">
            {{ errors.general }}
          </div>

          <!-- Action buttons -->
          <div class="flex flex-col sm:flex-row justify-end gap-3 pt-5 border-t">
            <BaseButton type="secondary" @click="profileForm.$clear()" class="w-full sm:w-auto text-sm">
              Cancel Changes
            </BaseButton>
            <BaseButton type="primary" @click="updateProfile" class="w-full sm:w-auto text-sm">
              Save Changes
            </BaseButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, onUnmounted } from 'vue'
import { useMeStore, useErrorStore, useNotifier } from '@/stores'
import { notify } from '@kyvg/vue3-notification'
import {
  BaseCheckbox,
  PasswordInput,
} from '@/components'
import { jwtDecode } from 'jwt-decode'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import { $reactive } from '@/utils'

defineOptions({
  name: 'ProfileView'
})

const meStore = useMeStore()
const errors = useErrorStore()
const $notifier = useNotifier()

const fileInput = ref(null)
const profileForm = $reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  avatar: '',
  notifications: {
    email: true,
    push: true
  }
})

errors.validateField(profileForm.$cleaned())

// JWT token remaining time
const remainingTime = ref(0)

const updateRemainingTime = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    remainingTime.value = 0
    return
  }

  const decoded = jwtDecode(token)
  remainingTime.value = Math.max(0, decoded.exp - Math.floor(Date.now() / 1000))
}

const formatRemainingTime = computed(() => {
  const minutes = Math.floor(remainingTime.value / 60)
  const seconds = remainingTime.value % 60

  if (remainingTime.value > 10 * 60) {
    return `${Math.floor(remainingTime.value / 60)} min`
  } else if (remainingTime.value > 5 * 60) {
    return `${minutes} min`
  } else if (remainingTime.value > 60) {
    return `${minutes} min`
  } else {
    return `${seconds} sec`
  }
})

let intervalId = null

onMounted(() => {
  profileForm.$assign(meStore.user)
  updateRemainingTime()
  intervalId = setInterval(updateRemainingTime, 1000)
})

onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId)
  }
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
    $notifier.error('Please select an image file')
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    $notifier.error('File size should not exceed 5MB')
    return
  }

  const formData = new FormData()
  formData.append('avatar', file)

  try {
    await meStore.updateAvatar(formData)

    $notifier.success('Avatar updated successfully')
  } catch (error) {
    console.error('Upload error:', error)
    $notifier.error(error.response?.data?.message || 'Failed to update avatar')
  }
}


const updateProfile = async () => {
  errors.clearServerErrors()

  if (profileForm.password && profileForm.password !== profileForm.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    return
  }

  try {
    await meStore.updateProfile(profileForm.$cleaned())
    if (meStore.error) {
      errors.handle(meStore.error)
      return
    }
    $notifier.success('Profile updated successfully')
  } catch (error) {
    errors.handle(error)
  }
}

const updatePassword = async () => {
  errors.clearServerErrors()

  if (!profileForm.password || !profileForm.confirmPassword) {
    if (!profileForm.password) errors.password = 'Please fill in all password fields'
    if (!profileForm.confirmPassword) errors.confirmPassword = 'Please fill in all password fields'
    return
  }

  if (profileForm.password !== profileForm.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    return
  }

  try {
    await meStore.updatePassword(profileForm.password)
    if (meStore.error) {
      errors.handle(meStore.error)
      console.log(errors.fields())
      return
    }
    $notifier.success('Password updated successfully')
    profileForm.password = ''
    profileForm.confirmPassword = ''
  } catch (error) {
    errors.handle(error)
  }
}



onMounted(() => profileForm.$assign(meStore.user))

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
  errors.clearServerErrors()
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
