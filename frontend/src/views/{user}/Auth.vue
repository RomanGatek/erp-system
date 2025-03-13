<template>
  <div class="min-h-[calc(95dvh-theme(space.20))] flex items-center justify-center">
    <div class="w-[400px] bg-white rounded-3xl p-8">
      <!-- Lock Icon -->
      <div class="flex justify-center mb-6">
        <svg class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24">
          <path stroke="currentColor" stroke-width="2" d="M17 11H7V7a5 5 0 0110 0v4zm-5-7a3 3 0 00-3 3v4h6V7a3 3 0 00-3-3zM5 11h14v10H5V11z"/>
        </svg>
      </div>

      <!-- Header -->
      <div class="text-center mb-6">
        <h1 class="text-2xl font-semibold text-gray-900 mb-2">
          {{ isLogin ? 'Welcome back' : 'Create account' }}
        </h1>
        <p class="text-gray-600 text-sm">
          {{ isLogin ? 'Sign in to your account' : 'Sign up for a new account' }}
        </p>
      </div>

      <!-- Login Form -->
      <form v-if="isLogin" @submit.prevent="handleLogin" class="space-y-4" autocomplete="off">
        <!-- Email Input -->
        <div class="space-y-1.5">
          <label class="block text-sm text-gray-700">Email address</label>
          <input
            type="email"
            v-model="email"
            placeholder="admin@example.com"
            class="w-full px-3 py-2 bg-gray-50 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
            :class="{ 'border-red-500': serverErrors.email, 'border-gray-200': !serverErrors.email }"
            required
          />
          <span v-if="serverErrors.email" class="text-xs text-red-500">{{ serverErrors.email }}</span>
        </div>

        <!-- Password Input -->
        <div class="space-y-1.5">
          <label class="block text-sm text-gray-700">Password</label>
          <PasswordInput
            v-model="password"
            class="bg-gray-50"
            :error="serverErrors.password"
            required
          />
          <span v-if="serverErrors.password" class="text-xs text-red-500">{{ serverErrors.password }}</span>
        </div>

        <!-- General Error Message -->
        <div v-if="serverErrors.general" class="text-sm text-red-600 text-center mt-4">
          {{ serverErrors.general }}
        </div>

        <!-- Sign In Button -->
        <button
          type="submit"
          class="w-full flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
          </svg>
          Sign in
        </button>
      </form>

      <!-- Register Form -->
      <form v-else @submit.prevent="handleRegister" class="space-y-4" autocomplete="off">
        <!-- Email Input -->
        <div class="space-y-1.5">
          <label class="block text-sm text-gray-700">Email address</label>
          <input
            type="email"
            v-model="email"
            placeholder="your@email.com"
            class="w-full px-3 py-2 bg-gray-50 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
            :class="{ 'border-red-500': serverErrors.email, 'border-gray-200': !serverErrors.email }"
            required
          />
          <span v-if="serverErrors.email" class="text-xs text-red-500">{{ serverErrors.email }}</span>
        </div>

        <!-- Password Input -->
        <div class="space-y-1.5">
          <label class="block text-sm text-gray-700">Password</label>
          <PasswordInput
            v-model="password"
            class="bg-gray-50"
            :error="serverErrors.password"
            required
          />
          <span v-if="serverErrors.password" class="text-xs text-red-500">{{ serverErrors.password }}</span>
        </div>

        <!-- Confirm Password Input -->
        <div class="space-y-1.5">
          <label class="block text-sm text-gray-700">Confirm Password</label>
          <PasswordInput
            v-model="confirmPassword"
            class="bg-gray-50"
            :error="serverErrors.confirmPassword"
            required
          />
          <span v-if="serverErrors.confirmPassword" class="text-xs text-red-500">{{ serverErrors.confirmPassword }}</span>
        </div>

        <!-- General Error Message -->
        <div v-if="serverErrors.general" class="text-sm text-red-600 text-center mt-4">
          {{ serverErrors.general }}
        </div>

        <!-- Sign Up Button  -->
        <button
          type="submit"
          class="w-full flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/>
          </svg>
          Sign up
        </button>
      </form>

      <!-- Toggle Form -->
      <div class="mt-4 text-center">
        <button
          type="button"
          @click="toggleForm"
          class="text-sm text-blue-600 hover:text-blue-700 transition-colors"
        >
          {{ isLogin ? "Don't have an account? Sign up" : 'Already have an account? Sign in' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useMeStore } from '@/stores/me.store.js'
import { notify } from '@kyvg/vue3-notification'
import PasswordInput from '@/components/common/PasswordInput.vue'

const router = useRouter()
const isLogin = ref(true)
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

// Server errors state
const serverErrors = ref({
  email: '',
  password: '',
  confirmPassword: '',
  general: ''
})

// Clear server errors
const clearServerErrors = () => {
  serverErrors.value = {
    email: '',
    password: '',
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

  const { field: errorField, message: errorMessage } = error.response.data

  console.log(errorField, errorMessage)

  if (errorField) {
    serverErrors.value[errorField] = errorMessage
  } else {
    serverErrors.value.general = errorMessage
  }
}

const toggleForm = () => {
  isLogin.value = !isLogin.value
  clearServerErrors()
  email.value = ''
  password.value = ''
  confirmPassword.value = ''
}

const handleLogin = async () => {
  try {
    clearServerErrors()
    const token = await api.auth().login({email: email.value, password: password.value})
    if (!token) {
      serverErrors.value.general = 'Invalid authentication token'
      return
    }
    localStorage.setItem('token', token)
    const meStore = useMeStore()
    await meStore.fetchMe(token)
    await router.push('/')
    notify({
      type: 'success',
      text: 'Successfully logged in',
      duration: 2000
    })
  } catch (err) {
    handleServerValidationErrors(err)
    notify({
      type: 'error',
      text: serverErrors.value.general || 'Login failed',
      duration: 5000
    })
  }
}

const handleRegister = async () => {
  try {
    clearServerErrors()
    await api.auth().register({
      email: email.value,
      password: password.value,
      confirmPassword: confirmPassword.value
    })
    notify({
      type: 'success',
      text: 'Successfully registered! Please sign in.',
      duration: 3000
    })
    isLogin.value = true
    email.value = ''
    password.value = ''
    confirmPassword.value = ''
  } catch (err) {
    handleServerValidationErrors(err)
    notify({
      type: 'error',
      text: serverErrors.value.general || 'Registration failed',
      duration: 5000
    })
  }
}

// Clear server errors on input change
watch([email, password, confirmPassword], () => {
  clearServerErrors()
})
</script>

<style scoped>
/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
