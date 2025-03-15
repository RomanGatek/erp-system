<template>
  <form @submit.prevent="handleLogin" class="space-y-4" autocomplete="off">
    <!-- Email Input -->
    <BaseInput autocomplete="email" :error="errors.email" v-model="formData.email" placeholder="admin@example.com"
      label="Email" />

    <!-- Password Input -->
    <div class="space-y-1.5">
      <label class="block text-sm text-gray-700">Password</label>
      <PasswordInput autocomplete="current-password" v-model="formData.password" class="bg-gray-50"
        :error="errors.password" required />
      <span v-if="errors.password" class="text-xs text-red-500">{{ errors.password }}</span>
    </div>

    <!-- General Error Message -->
    <div v-if="errors.general" class="text-sm text-red-600 text-center mt-4">
      {{ errors.general }}
    </div>

    <!-- Sign In Button -->
    <button type="submit"
      class="w-full flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
      <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
      </svg>
      Sign in
    </button>
  </form>
</template>

<script setup>
import { watch } from 'vue'
import api from '@/services/api.js'
import { useMeStore } from '@/stores/me.store.js'
import PasswordInput from '@/components/common/PasswordInput.vue'
import { useNotifier, useErrorStore } from '@/stores'
import { $reactive } from '@/utils'
import { BaseInput } from '@/components'

// Define emits
const emit = defineEmits(['success'])

const formData = $reactive({
  email: '',
  password: '',
})

const errors = useErrorStore()
const $notifier = useNotifier()
const meStore = useMeStore()

errors.validateField(Object.keys(formData.$cleaned()))

const handleLogin = async () => {
  try {
    errors.clearServerErrors()
    const [{ accessToken, refreshToken }, error] = await api.auth().login(formData.$cleaned())

    if (error) {
      errors.handle(error)
      return
    }

    if (!accessToken || !refreshToken) {
      errors.general = 'Invalid authentication token'
      return
    }

    localStorage.setItem('token', accessToken)
    localStorage.setItem('refresh-token', refreshToken)

    await meStore.fetchMe()

    // Emit success event to parent
    emit('success', 'Successfully logged in')

    // Reset form
    formData.$clear()

  } catch (err) {
    errors.handle(err)
    console.error(err)
    $notifier.error(err.message, 'Login failed', 5000)
  }
}

watch(
  () => [formData.email, formData.password],
  ([newEmail, newPassword], [oldEmail, oldPassword]) => {
    if (newEmail !== oldEmail) errors.clear('email')
    if (newPassword !== oldPassword) errors.clear('password')
  }
)
</script>