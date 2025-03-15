<template>
  <form @submit.prevent="handleRegister" class="space-y-4" autocomplete="off">
    <!-- Email Input -->
    <BaseInput autocomplete="email" :error="errors.email" v-model="formData.email" placeholder="your@email.com"
      label="Email" />

    <!-- Username Input -->
    <BaseInput autocomplete="username" :error="errors.username" v-model="formData.username" placeholder="username"
      label="Username" />

    <!-- Password Input -->
    <div class="space-y-1.5">
      <label class="block text-sm text-gray-700">Password</label>
      <PasswordInput autocomplete="new-password" v-model="formData.password" class="bg-gray-50" :error="errors.password"
        required />
      <span v-if="errors.password" class="text-xs text-red-500">{{ errors.password }}</span>
    </div>

    <!-- Confirm Password Input -->
    <div class="space-y-1.5">
      <label class="block text-sm text-gray-700">Confirm Password</label>
      <PasswordInput autocomplete="new-password" v-model="formData.confirmPassword" class="bg-gray-50"
        :error="errors.confirmPassword" required />
      <span v-if="errors.confirmPassword" class="text-xs text-red-500">{{ errors.confirmPassword }}</span>
    </div>

    <!-- General Error Message -->
    <div v-if="errors.general" class="text-sm text-red-600 text-center mt-4">
      {{ errors.general }}
    </div>

    <!-- Sign Up Button -->
    <button type="submit"
      class="w-full flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
      <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
      </svg>
      Sign up
    </button>
  </form>
</template>

<script setup>
import { watch } from 'vue'
import api from '@/services/api.js'
import PasswordInput from '@/components/common/PasswordInput.vue'
import { useNotifier, useErrorStore } from '@/stores'
import { $reactive } from '@/utils'
import { BaseInput } from '@/components'

// Define emits
const emit = defineEmits(['success'])

const formData = $reactive({
  email: '',
  username: '',
  password: '',
  confirmPassword: ''
})

const errors = useErrorStore()
const $notifier = useNotifier()

errors.validateField(Object.keys(formData.$cleaned()))

const handleRegister = async () => {
  try {
    errors.clearServerErrors()

    // Validate passwords match
    if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match'
      return
    }

    const [response, error] = await api.auth().register(formData.$cleaned())

    if (error) {
      errors.handle(error)
      return
    }

    // Emit success event to parent
    emit('success', 'Successfully registered! Please sign in.')

    // Reset form
    formData.$clear()

  } catch (err) {
    errors.handle(err)
  }
}

watch(
  () => [formData.email, formData.username, formData.password, formData.confirmPassword],
  ([newEmail, newUsername, newPassword, newConfirmPassword], [oldEmail, oldUsername, oldPassword, oldConfirmPassword]) => {
    if (newEmail !== oldEmail) errors.clear('email')
    if (newUsername !== oldUsername) errors.clear('username')
    if (newPassword !== oldPassword) errors.clear('password')
    if (newConfirmPassword !== oldConfirmPassword) errors.clear('confirmPassword')
  }
)
</script>