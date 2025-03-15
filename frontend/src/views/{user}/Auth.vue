<template>
  <div class="min-h-[calc(95dvh-theme(space.20))] flex items-center justify-center">
    <div class="w-[400px] bg-white rounded-3xl p-8">
      <!-- Lock Icon -->
      <div class="flex justify-center mb-6">
        <svg class="w-8 h-8 text-blue-600" fill="none" viewBox="0 0 24 24">
          <path stroke="currentColor" stroke-width="2"
            d="M17 11H7V7a5 5 0 0110 0v4zm-5-7a3 3 0 00-3 3v4h6V7a3 3 0 00-3-3zM5 11h14v10H5V11z" />
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

      <!-- Toggle between Login and Register components with animation -->
      <Transition name="fade" mode="out-in">
        <component :is="isLogin ? Login : Register" @success="handleSuccess" />
      </Transition>

      <!-- Toggle Form -->
      <div class="mt-4 text-center">
        <button type="button" @click="toggleForm" class="text-sm text-blue-600 hover:text-blue-700 transition-colors">
          {{ isLogin ? "Don't have an account? Sign up" : 'Already have an account? Sign in' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useNotifier, useErrorStore } from '@/stores'
import Login from '@/components/auth/Login.vue'
import Register from '@/components/auth/Register.vue'

const router = useRouter()
const $notifier = useNotifier()
const errors = useErrorStore()

const isLogin = ref(true)

const toggleForm = () => {
  isLogin.value = !isLogin.value
  errors.clearServerErrors()
}

const handleSuccess = async (message) => {
  // If registration is successful, switch to login
  if (!isLogin.value) {
    isLogin.value = true
    $notifier.success('Please sign in.', message || 'Successfully registered!', 3000)
  } else {
    // For login success, redirect to home
    await router.push('/')
    $notifier.success('', message || 'Successfully logged in', 2000)
  }
}
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
