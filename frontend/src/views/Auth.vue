<template>
  <div class="h-[calc(100vh-theme(space.16))] w-full flex items-center justify-center">
    <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[400px] bg-white rounded-3xl p-8">
      <!-- Lock Icon -->
      <div class="flex justify-center mb-6">
        <svg class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24">
          <path stroke="currentColor" stroke-width="2" d="M17 11H7V7a5 5 0 0110 0v4zm-5-7a3 3 0 00-3 3v4h6V7a3 3 0 00-3-3zM5 11h14v10H5V11z"/>
        </svg>
      </div>

      <!-- Header -->
      <div class="text-center mb-6">
        <h1 class="text-2xl font-semibold text-gray-900 mb-2">
          <transition name="fade" mode="out-in">
            <span v-if="isLogin" key="login">Welcome back</span>
            <span v-else key="register">Create account</span>
          </transition>
        </h1>
        <p class="text-gray-600 text-sm">
          <transition name="fade" mode="out-in">
            <span v-if="isLogin" key="login">Sign in to your account</span>
            <span v-else key="register">Sign up for a new account</span>
          </transition>
        </p>
      </div>

      <transition name="fade" mode="out-in">
        <form v-if="isLogin" @submit.prevent="handleLogin" class="space-y-4" autocomplete="off" key="login">
          <!-- Email Input -->
          <div class="space-y-1.5">
            <label class="block text-sm text-gray-700">Email address</label>
            <input
              type="email"
              v-model="email"
              @blur="handleBlur('email')"
              placeholder="admin@example.com"
              class="w-full px-3 py-2 bg-gray-50 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
              :class="{ 'border-red-500': emailError, 'border-gray-200': !emailError }"
              required
            />
            <span v-if="emailError" class="text-xs text-red-500">{{ emailError }}</span>
          </div>

          <!-- Password Input -->
          <div class="space-y-1.5">
            <label class="block text-sm text-gray-700">Password</label>
            <PasswordInput
              v-model="password"
              @blur="handleBlur('password')"
              class="bg-gray-50"
              :error="passwordError"
              required
            />
            <span v-if="passwordError" class="text-xs text-red-500">{{ passwordError }}</span>
          </div>

          <!-- Error Message -->
          <div v-if="error" class="text-sm text-red-600 text-center">
            {{ error }}
          </div>

          <!-- Sign In Button -->
          <button
            type="submit"
            :disabled="!isFormValid"
            class="w-full flex items-center justify-center gap-2 px-4 py-2 text-white rounded-lg transition-colors"
            :class="[
              isFormValid 
                ? 'bg-blue-600 hover:bg-blue-700' 
                : 'bg-gray-400 cursor-not-allowed'
            ]"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
            </svg>
            Sign in
          </button>
        </form>

        <form v-else @submit.prevent="handleRegister" class="space-y-4" autocomplete="off" key="register">
          <!-- Email Input -->
          <div class="space-y-1.5">
            <label class="block text-sm text-gray-700">Email address</label>
            <input
              type="email"
              v-model="email"
              @blur="handleBlur('email')"
              placeholder="your@email.com"
              class="w-full px-3 py-2 bg-gray-50 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
              :class="{ 'border-red-500': emailError, 'border-gray-200': !emailError }"
              required
            />
            <span v-if="emailError" class="text-xs text-red-500">{{ emailError }}</span>
          </div>

          <!-- Password Input -->
          <div class="space-y-1.5">
            <label class="block text-sm text-gray-700">Password</label>
            <PasswordInput
              v-model="password"
              @blur="handleBlur('password')"
              class="bg-gray-50"
              :error="passwordError"
              required
            />
            <span v-if="passwordError" class="text-xs text-red-500">{{ passwordError }}</span>
          </div>

          <!-- Confirm Password Input -->
          <div class="space-y-1.5">
            <label class="block text-sm text-gray-700">Confirm Password</label>
            <PasswordInput
              v-model="confirmPassword"
              @blur="handleBlur('confirmPassword')"
              class="bg-gray-50"
              :error="confirmPasswordError"
              required
            />
            <span v-if="confirmPasswordError" class="text-xs text-red-500">{{ confirmPasswordError }}</span>
          </div>

          <!-- Error Message -->
          <div v-if="error" class="text-sm text-red-600 text-center">
            {{ error }}
          </div>

          <!-- Sign Up Button -->
          <button
            type="submit"
            :disabled="!isFormValid"
            class="w-full flex items-center justify-center gap-2 px-4 py-2 text-white rounded-lg transition-colors"
            :class="[
              isFormValid 
                ? 'bg-blue-600 hover:bg-blue-700' 
                : 'bg-gray-400 cursor-not-allowed'
            ]"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/>
            </svg>
            Sign up
          </button>
        </form>
      </transition>

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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '../services/auth'
import { useMeStore } from '../stores/me'
import { notify } from '@kyvg/vue3-notification'
import PasswordInput from '../components/common/PasswordInput.vue'

const router = useRouter()
const isLogin = ref(true)
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const touched = ref({
  email: false,
  password: false,
  confirmPassword: false
})

// Validation rules
const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{10,35}$/

// Validation computed properties
const emailError = computed(() => {
  if (!touched.value.email || !email.value) return ''
  if (!emailRegex.test(email.value)) return 'Please enter a valid email address'
  return ''
})

const passwordError = computed(() => {
  if (!touched.value.password || !password.value) return ''
  if (password.value.length < 10) return 'Password must be at least 10 characters long'
  if (password.value.length > 35) return 'Password must not exceed 35 characters'
  if (!passwordRegex.test(password.value)) {
    return 'Password must contain at least one lowercase letter, one uppercase letter, and one number'
  }
  return ''
})

const confirmPasswordError = computed(() => {
  if (!touched.value.confirmPassword || !confirmPassword.value) return ''
  if (password.value !== confirmPassword.value) return 'Passwords do not match'
  return ''
})

const isFormValid = computed(() => {
  if (isLogin.value) {
    return !emailError.value && !passwordError.value && email.value && password.value
  }
  return !emailError.value && !passwordError.value && !confirmPasswordError.value && 
         email.value && password.value && confirmPassword.value
})

// Handle input blur events
const handleBlur = (field) => {
  touched.value[field] = true
}

const toggleForm = () => {
  isLogin.value = !isLogin.value
  error.value = ''
  email.value = ''
  password.value = ''
  confirmPassword.value = ''
}

const handleLogin = async () => {
  try {
    const token = await login(email.value, password.value)
    if (!token) {
      error.value = 'Invalid authentication token'
      return
    }
    localStorage.setItem('token', token)
    const meStore = useMeStore()
    await meStore.fetchMe(token)
    router.push('/')
    notify({
      type: 'success',
      text: 'Successfully logged in',
      duration: 2000
    })
  } catch (err) {
    error.value = err.response?.message || err.message
    notify({
      type: 'error',
      text: `Login failed: ${error.value}`,
      duration: 5000
    })
  }
}

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }
  
  try {
    await register(email.value, password.value)
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
    error.value = err.response?.message || err.message
    notify({
      type: 'error',
      text: `Registration failed: ${error.value}`,
      duration: 5000
    })
  }
}
</script>

<style scoped>
/* Prevent autofill background */
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
    transition: background-color 5000s ease-in-out 0s;
    -webkit-text-fill-color: inherit !important;
}

/* Fade transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Slide transition */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease-out;
}

.slide-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>
