<template>
  <div class="min-h-screen bg-gray-50/50 backdrop-blur-sm py-8">
    <div class="max-w-5xl mx-auto px-4 sm:px-6">
      <!-- Profile Header Card -->
      <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 overflow-hidden mb-6">
        <div class="relative h-32 bg-gradient-to-r from-blue-600 to-blue-400">
          <div class="absolute -bottom-10 left-6 flex items-end space-x-4">
            <div class="relative">
              <img
                :src="meStore.user?.avatar || 'https://ui-avatars.com/api/?name=' + meStore.user?.firstName + '+' + meStore.user?.lastName"
                class="w-24 h-24 rounded-2xl border-4 border-white shadow-lg object-cover"
                :alt="meStore.user?.firstName" />
              <div class="absolute -right-1 -bottom-1 flex space-x-1">
                <button @click="removeAvatar"
                  class="bg-red-500 rounded-lg p-1.5 shadow-md hover:bg-red-600 transition-all text-white">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
                <button @click="triggerFileInput"
                  class="bg-white rounded-lg p-1.5 shadow-md hover:bg-gray-50 transition-all text-gray-700">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                </button>
              </div>
              <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="handleAvatarChange" />
            </div>
            <div class="mb-3">
              <h1 class="text-xl font-bold text-white mb-1 flex items-center gap-2">
                {{ meStore.user?.firstName }} {{ meStore.user?.lastName }}
                <span class="text-sm font-normal opacity-90">@{{ meStore.user.username }}</span>
              </h1>
              <div class="flex items-center gap-3">
                <span class="text-sm text-white/90">{{ meStore.user?.email }}</span>
                <span class="px-2 py-0.5 rounded-full text-xs font-medium bg-white/10 backdrop-blur-sm" :class="getRoleStyle(highestRole)">
                  {{ formatRole(highestRole) }}
                </span>
              </div>
            </div>
          </div>
          <!-- Session Timer -->
          <div class="absolute top-3 right-4">
            <div class="bg-white/10 backdrop-blur-md rounded-lg p-2">
              <div class="text-xs text-white/80">Session expires in</div>
              <div :class="[
                'text-xs font-mono px-2 py-0.5 rounded-md text-white',
                remainingTime > 24 * 60 * 60 ? 'bg-green-400/20' :
                  remainingTime > 60 * 60 ? 'bg-yellow-400/20' : 'bg-red-400/20'
              ]">
                {{ formatRemainingTime }}
              </div>
            </div>
          </div>
        </div>
        <div class="h-16"></div>
      </div>

      <!-- Main Content Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Left Column -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Personal Information -->
          <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-6">
            <div class="flex items-center mb-6">
              <h2 class="text-lg font-semibold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">Personal Information</h2>
              <div class="ml-4 h-px flex-1 bg-gray-100"></div>
            </div>
            <form @submit.prevent="updateProfile" class="space-y-6">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <BaseInput v-model="reactiveProfile.firstName" label="First Name" placeholder="Your first name"
                  :error="errors.firstName" class="text-sm" />
                <BaseInput v-model="reactiveProfile.lastName" label="Last Name" placeholder="Your last name"
                  :error="errors.lastName" class="text-sm" />
                <BaseInput disabled v-model="reactiveProfile.email" type="email" label="Email Address"
                  placeholder="your@email.com" :error="errors.email" class="text-sm" />
                <BaseInput v-model="reactiveProfile.username" label="Username" placeholder="Username"
                  :error="errors.username" class="text-sm" />
              </div>

              <!-- Action Buttons -->
              <div class="flex justify-end space-x-3 pt-4">
                <BaseButton type="secondary" @click="resetForm" size="sm">
                  Reset
                </BaseButton>
                <BaseButton type="primary" @click="updateProfile" size="sm">
                  Save Changes
                </BaseButton>
              </div>
            </form>

            <!-- Password Change Collapsible -->
            <div class="mt-6 pt-4 border-t border-gray-100">
              <button @click="isPasswordChangeVisible = !isPasswordChangeVisible"
                class="w-full flex items-center justify-between text-sm text-gray-600 hover:text-gray-900 transition-colors">
                <span class="font-medium">Change Password</span>
                <svg class="w-4 h-4 transition-transform duration-200"
                  :class="{ 'rotate-180': isPasswordChangeVisible }" fill="none" viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              <Transition enter-active-class="transition-all duration-300 ease-in-out"
                leave-active-class="transition-all duration-200 ease-in-out"
                enter-from-class="transform opacity-0 -translate-y-2"
                enter-to-class="transform opacity-100 translate-y-0"
                leave-from-class="transform opacity-100 translate-y-0"
                leave-to-class="transform opacity-0 -translate-y-2">
                <div v-show="isPasswordChangeVisible" class="mt-4 space-y-4">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <PasswordInput v-model="reactiveProfile.newPassword" label="New Password" placeholder="••••••••"
                      :error="errors.newPassword" class="text-sm" />
                    <PasswordInput v-model="reactiveProfile.confirmPassword" label="Confirm Password"
                      placeholder="••••••••" :error="errors.confirmPassword" class="text-sm" />
                  </div>
                  <div class="flex justify-end">
                    <BaseButton type="primary" @click="updatePassword" size="sm">
                      Update Password
                    </BaseButton>
                  </div>
                </div>
              </Transition>
            </div>

            <!-- Privacy & Consents Collapsible -->
            <div class="mt-6 pt-4 border-t border-gray-100">
              <button @click="isPrivacyVisible = !isPrivacyVisible"
                class="w-full flex items-center justify-between text-sm text-gray-600 hover:text-gray-900 transition-colors">
                <span class="font-medium">Privacy & Consents</span>
                <svg class="w-4 h-4 transition-transform duration-200" :class="{ 'rotate-180': isPrivacyVisible }"
                  fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              <Transition enter-active-class="transition-all duration-300 ease-in-out"
                leave-active-class="transition-all duration-200 ease-in-out"
                enter-from-class="transform opacity-0 -translate-y-2"
                enter-to-class="transform opacity-100 translate-y-0"
                leave-from-class="transform opacity-100 translate-y-0"
                leave-to-class="transform opacity-0 -translate-y-2">
                <div v-show="isPrivacyVisible" class="mt-4 space-y-3">
                  <button @click="toggleConsent('marketing')" :class="[
                    'w-full flex items-center p-3 rounded-xl transition-colors cursor-pointer ring-1',
                    consents.marketing ? 'bg-emerald-50 ring-emerald-100 hover:bg-emerald-100/70' : 'bg-red-50 ring-red-100 hover:bg-red-100/70'
                  ]">
                    <div class="flex-1">
                      <h3 class="text-sm font-medium" :class="consents.marketing ? 'text-emerald-900' : 'text-red-900'">
                        Marketing Communications
                      </h3>
                      <p class="text-xs mt-0.5" :class="consents.marketing ? 'text-emerald-600' : 'text-red-600'">
                        Receive marketing updates and newsletters
                      </p>
                    </div>
                    <div class="flex items-center">
                      <span class="text-xs font-medium mr-2"
                        :class="consents.marketing ? 'text-emerald-600' : 'text-red-600'">
                        {{ consents.marketing ? 'Consented' : 'Not Consented' }}
                      </span>
                      <div :class="[
                        'w-2 h-2 rounded-full',
                        consents.marketing ? 'bg-emerald-500' : 'bg-red-500'
                      ]"></div>
                    </div>
                  </button>

                  <button @click="toggleConsent('analytics')" :class="[
                    'w-full flex items-center p-3 rounded-xl transition-colors cursor-pointer ring-1',
                    consents.analytics ? 'bg-emerald-50 ring-emerald-100 hover:bg-emerald-100/70' : 'bg-red-50 ring-red-100 hover:bg-red-100/70'
                  ]">
                    <div class="flex-1">
                      <h3 class="text-sm font-medium" :class="consents.analytics ? 'text-emerald-900' : 'text-red-900'">
                        Analytics Tracking
                      </h3>
                      <p class="text-xs mt-0.5" :class="consents.analytics ? 'text-emerald-600' : 'text-red-600'">
                        Allow usage analytics collection
                      </p>
                    </div>
                    <div class="flex items-center">
                      <span class="text-xs font-medium mr-2"
                        :class="consents.analytics ? 'text-emerald-600' : 'text-red-600'">
                        {{ consents.analytics ? 'Consented' : 'Not Consented' }}
                      </span>
                      <div :class="[
                        'w-2 h-2 rounded-full',
                        consents.analytics ? 'bg-emerald-500' : 'bg-red-500'
                      ]"></div>
                    </div>
                  </button>

                  <button @click="toggleConsent('thirdParty')" :class="[
                    'w-full flex items-center p-3 rounded-xl transition-colors cursor-pointer ring-1',
                    consents.thirdParty ? 'bg-emerald-50 ring-emerald-100 hover:bg-emerald-100/70' : 'bg-red-50 ring-red-100 hover:bg-red-100/70'
                  ]">
                    <div class="flex-1">
                      <h3 class="text-sm font-medium"
                        :class="consents.thirdParty ? 'text-emerald-900' : 'text-red-900'">
                        Third-Party Data Sharing
                      </h3>
                      <p class="text-xs mt-0.5" :class="consents.thirdParty ? 'text-emerald-600' : 'text-red-600'">
                        Allow sharing data with trusted partners
                      </p>
                    </div>
                    <div class="flex items-center">
                      <span class="text-xs font-medium mr-2"
                        :class="consents.thirdParty ? 'text-emerald-600' : 'text-red-600'">
                        {{ consents.thirdParty ? 'Consented' : 'Not Consented' }}
                      </span>
                      <div :class="[
                        'w-2 h-2 rounded-full',
                        consents.thirdParty ? 'bg-emerald-500' : 'bg-red-500'
                      ]"></div>
                    </div>
                  </button>

                  <div class="mt-4 pt-3 border-t border-gray-100">
                    <p class="text-xs text-gray-500">
                      Last updated: {{ formatConsentDate(consents.lastUpdated) }}
                    </p>
                    <a href="#" class="text-xs text-blue-600 hover:text-blue-800 mt-1 inline-block">
                      View Privacy Policy
                    </a>
                  </div>
                </div>
              </Transition>
            </div>
          </div>
        </div>

        <!-- Right Column -->
        <div class="space-y-6">
          <!-- Notification Settings -->
          <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-6">
            <div class="flex items-center mb-6">
              <h2 class="text-lg font-semibold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">Notifications</h2>
              <div class="ml-4 h-px flex-1 bg-gray-100"></div>
            </div>
            <div class="space-y-3">
              <button @click="reactiveProfile.notifications.email = !reactiveProfile.notifications.email" :class="[
                'w-full flex items-center p-3 rounded-xl transition-colors cursor-pointer ring-1',
                reactiveProfile.notifications.email ? 'bg-emerald-50 ring-emerald-100 hover:bg-emerald-100/70' : 'bg-red-50 ring-red-100 hover:bg-red-100/70'
              ]">
                <div class="flex-1">
                  <h3 class="text-sm font-medium"
                    :class="reactiveProfile.notifications.email ? 'text-emerald-900' : 'text-red-900'">
                    Email Notifications
                  </h3>
                  <p class="text-xs mt-0.5" :class="reactiveProfile.notifications.email ? 'text-emerald-600' : 'text-red-600'">
                    Receive updates via email
                  </p>
                </div>
                <div class="flex items-center">
                  <span class="text-xs font-medium mr-2"
                    :class="reactiveProfile.notifications.email ? 'text-emerald-600' : 'text-red-600'">
                    {{ reactiveProfile.notifications.email ? 'Enabled' : 'Disabled' }}
                  </span>
                  <div :class="[
                    'w-2 h-2 rounded-full',
                    reactiveProfile.notifications.email ? 'bg-emerald-500' : 'bg-red-500'
                  ]"></div>
                </div>
              </button>

              <button @click="reactiveProfile.notifications.push = !reactiveProfile.notifications.push" :class="[
                'w-full flex items-center p-3 rounded-xl transition-colors cursor-pointer ring-1',
                reactiveProfile.notifications.push ? 'bg-emerald-50 ring-emerald-100 hover:bg-emerald-100/70' : 'bg-red-50 ring-red-100 hover:bg-red-100/70'
              ]">
                <div class="flex-1">
                  <h3 class="text-sm font-medium"
                    :class="reactiveProfile.notifications.push ? 'text-emerald-900' : 'text-red-900'">
                    Push Notifications
                  </h3>
                  <p class="text-xs mt-0.5" :class="reactiveProfile.notifications.push ? 'text-emerald-600' : 'text-red-600'">
                    Receive instant updates
                  </p>
                </div>
                <div class="flex items-center">
                  <span class="text-xs font-medium mr-2"
                    :class="reactiveProfile.notifications.push ? 'text-emerald-600' : 'text-red-600'">
                    {{ reactiveProfile.notifications.push ? 'Enabled' : 'Disabled' }}
                  </span>
                  <div :class="[
                    'w-2 h-2 rounded-full',
                    reactiveProfile.notifications.push ? 'bg-emerald-500' : 'bg-red-500'
                  ]"></div>
                </div>
              </button>
            </div>
          </div>

          <!-- Recent Activity -->
          <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-6">
            <div class="flex items-center mb-6">
              <h2 class="text-lg font-semibold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">Recent Activity</h2>
              <div class="ml-4 h-px flex-1 bg-gray-100"></div>
            </div>
            <div class="space-y-3">
              <div v-if="!recentActivity?.length" class="text-center py-6 text-gray-500 text-sm">
                No recent activity
              </div>
              <div v-else v-for="activity in recentActivity" :key="activity.id"
                class="flex items-start space-x-3 p-3 rounded-xl bg-gray-50/80 ring-1 ring-gray-100">
                <div class="flex-shrink-0">
                  <div :class="[
                    'w-8 h-8 rounded-xl flex items-center justify-center',
                    getActivityTypeStyle(activity.type)
                  ]">
                    <i :class="getActivityIcon(activity.type)" class="text-sm"></i>
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-medium text-sm text-gray-900">{{ activity.title }}</p>
                  <p class="text-xs text-gray-600 mt-0.5">{{ activity.description }}</p>
                  <p class="text-xs text-gray-400 mt-1">{{ formatActivityDate(activity.timestamp) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useMeStore } from '@/stores/me.store.js'
import { notify } from '@kyvg/vue3-notification'
import { BaseInput, BaseCheckbox, PasswordInput } from '@/components'
import BaseButton from '@/components/common/BaseButton.vue'
import { jwtDecode } from 'jwt-decode'
import { format } from 'date-fns'
import { Transition } from 'vue'
import { useErrorStore } from '@/stores/errors.store.js'
import { $reactive } from '@/utils/index.js'

defineOptions({
  name: 'ProfileView'
})

const meStore = useMeStore()
const fileInput = ref(null)
const recentActivity = ref([])

const errors = useErrorStore()
const reactiveProfile = $reactive({
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

// Validate fields for error store
errors.validateField(reactiveProfile.$cleaned())

const isPasswordChangeVisible = ref(false)
const isPrivacyVisible = ref(false)

const consents = reactive({
  marketing: false,
  analytics: true,
  thirdParty: false,
  lastUpdated: new Date()
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
    return `${days} ${days === 1 ? 'day' : days < 5 ? 'days' : 'days'}`
  }
  if (hours > 0) {
    return `${hours}h ${minutes}m`
  }
  return `${minutes}m`
})

const formatRole = (role) => {
  return role.replace('ROLE_', '').toLowerCase()
}

const highestRole = computed(() => {
  const roles = meStore.user?.roles?.map(r => r.name) || []
  if (roles.includes('ROLE_ADMIN')) return 'ROLE_ADMIN'
  if (roles.includes('ROLE_MANAGER')) return 'ROLE_MANAGER'
  if (roles.includes('ROLE_USER')) return 'ROLE_USER'
  return 'ROLE_GUEST'
})

const getRoleStyle = (roleName) => {
  const roleStyles = {
    'ROLE_ADMIN': 'bg-red-400/20 text-red-700',
    'ROLE_MANAGER': 'bg-blue-400/20 text-blue-700',
    'ROLE_USER': 'bg-green-400/20 text-green-700',
    'ROLE_GUEST': 'bg-gray-400/20 text-gray-700'
  }
  return roleStyles[roleName] || 'bg-gray-400/20 text-gray-700'
}

const getActivityTypeStyle = (type) => {
  const styles = {
    login: 'bg-blue-100 text-blue-600',
    update: 'bg-green-100 text-green-600',
    security: 'bg-yellow-100 text-yellow-600',
    error: 'bg-red-100 text-red-600'
  }
  return styles[type] || styles.update
}

const getActivityIcon = (type) => {
  const icons = {
    login: 'fas fa-sign-in-alt',
    update: 'fas fa-sync',
    security: 'fas fa-shield-alt',
    error: 'fas fa-exclamation-triangle'
  }
  return icons[type] || icons.update
}

const formatActivityDate = (date) => {
  return format(new Date(date), 'MMM d, yyyy HH:mm')
}

// Existing functions
const triggerFileInput = () => {
  fileInput.value.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

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

  try {
    await meStore.updateAvatar(formData)
    notify({
      type: 'success',
      text: 'Avatar updated successfully'
    })
  } catch (error) {
    notify({
      type: 'error',
      text: error.response?.data?.message || 'Failed to update avatar'
    })
  }
}

const updateProfile = async () => {
  errors.clearServerErrors()

  try {
    const profileData = {
      firstName: reactiveProfile.firstName,
      lastName: reactiveProfile.lastName,
      email: meStore.user?.email,
      username: reactiveProfile.username
    }

    await meStore.updateProfile(profileData)
    if (!meStore.error) {
      notify({
        type: 'success',
        text: 'Profile updated successfully'
      })
    } else {
      errors.handle(meStore.error)
    }
  } catch (err) {
    errors.handle(err)
    console.error(err)
  }
}

const updatePassword = async () => {
  errors.clearServerErrors()

  if (!reactiveProfile.newPassword || !reactiveProfile.confirmPassword) {
    errors.newPassword = 'Please fill in all password fields'
    return
  }

  if (reactiveProfile.newPassword !== reactiveProfile.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    return
  }

  try {
    await meStore.updatePassword(reactiveProfile.newPassword)
    if (!meStore.error) {
      notify({
        type: 'success',
        text: 'Password updated successfully'
      })
      reactiveProfile.newPassword = ''
      reactiveProfile.confirmPassword = ''
    } else {
      errors.handle(meStore.error)
    }
  } catch (err) {
    errors.handle(err)
    console.error(err)
  }
}

const resetForm = () => {
  errors.clearServerErrors()
  reactiveProfile.$assign({
    firstName: meStore.user?.firstName || '',
    lastName: meStore.user?.lastName || '',
    email: meStore.user?.email || '',
    username: meStore.user?.username || '',
    newPassword: '',
    confirmPassword: ''
  })
}

const removeAvatar = async () => {
  try {
    await meStore.updateProfile({
      ...reactiveProfile,
      avatar: null
    })
    notify({
      type: 'success',
      text: 'Avatar removed successfully'
    })
  } catch (error) {
    notify({
      type: 'error',
      text: error.response?.data?.message || 'Failed to remove avatar'
    })
  }
}

// Mock function to fetch activity - replace with actual API call
const fetchRecentActivity = async () => {
  try {
    // TODO: Replace with actual API call
    const response = await fetch('/api/user/activity')
    recentActivity.value = await response.json()
  } catch (error) {
    console.error('Failed to fetch activity:', error)
  }
}

const toggleConsent = async (type) => {
  try {
    // TODO: Add API call to update consent
    consents[type] = !consents[type]
    consents.lastUpdated = new Date()

    notify({
      type: 'success',
      text: `${type.charAt(0).toUpperCase() + type.slice(1)} consent updated successfully`
    })
  } catch (error) {
    notify({
      type: 'error',
      text: 'Failed to update consent'
    })
  }
}

const formatConsentDate = (date) => {
  return format(new Date(date), 'MMM d, yyyy HH:mm')
}

onMounted(() => {
  resetForm()
  fetchRecentActivity()
})

watch(() => reactiveProfile, () => {
  const fields = ['firstName', 'lastName', 'email', 'username', 'newPassword', 'confirmPassword']
  fields.forEach(field => {
    if (reactiveProfile[field] !== undefined) {
      errors.clear(field)
    }
  })
}, { deep: true })
</script>

<style scoped>
/* Remove all styles since we're using Tailwind classes directly */

/* Transition for password change panel */
.password-change-enter-active,
.password-change-leave-active {
  transition: all 0.3s ease;
}

.password-change-enter-from,
.password-change-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
