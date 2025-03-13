<script setup>
import { ref } from 'vue'
import { Modal, BaseInput, BaseCheckbox } from '@/components'
import { $reactive } from '@/utils/index.js'
import BaseButton from '@/components/common/BaseButton.vue'

const props = defineProps({
  availableRoles: {
    type: Array,
    required: true
  },
  errors: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['add-user', 'cancel'])

const newUser = $reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: false,
  roles: [],
})

const handleAddUser = () => {
  emit('add-user', newUser.$cleaned())
}

const handleCancel = () => {
  newUser.$clear()
  emit('cancel')
}
</script>

<template>
  <Modal :show="true" title="Add New User" @close="handleCancel">
    <div class=" space-y-3">
      <div class="grid grid-cols-2 gap-3">
        <BaseInput v-model="newUser.firstName" placeholder="First Name" label="First name"
          :error="errors.firstName" :class="{ 'border-red-500': errors.firstName }" />
        <BaseInput v-model="newUser.lastName" placeholder="Last Name" label="Last name" :error="errors.lastName"
          :class="{ 'border-red-500': errors.lastName }" />
      </div>
      <BaseInput v-model="newUser.email" type="email" placeholder="Email" label="Email" :error="errors.email"
        :class="{ 'border-red-500': errors.email }" />
      <BaseInput v-model="newUser.username" placeholder="Username" label="Username" :error="errors.username"
        :class="{ 'border-red-500': errors.username }" />
      <BaseInput v-model="newUser.password" type="password" placeholder="Password" label="Password"
        :error="errors.password" :class="{ 'border-red-500': errors.password }" />
      <!-- Role selection -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Role</label>
        <div class="flex flex-wrap gap-2">
          <label v-for="role in availableRoles" :key="role.name"
            class="inline-flex items-center space-x-2 cursor-pointer">
            <input type="checkbox" v-model="newUser.roles" :value="{ name: `ROLE_${role.name.toUpperCase()}` }"
              class="w-4 h-4 rounded border-gray-300 text-blue-600 shadow-sm focus:ring-2 focus:ring-blue-200 focus:ring-opacity-50" />
            <span class="px-2 py-1 rounded-full text-xs font-medium" :class="[role.bgColor, role.textColor]">
              {{ role.name }}
            </span>
          </label>
        </div>
        <span v-if="errors.roles" class="text-xs text-red-500">
          {{ errors.roles }}
        </span>
      </div>
      <!-- General Error Message -->
      <div v-if="errors.general" class="text-sm text-red-600 text-center mt-4">
        {{ errors.general }}
      </div>
      <BaseCheckbox v-model="newUser.active" label="Account active" />
      <div class="flex justify-end space-x-3 pt-2">
        <BaseButton type="error" class="text-sm! font-bold flex!" @click="handleCancel">
          Cancel
        </BaseButton>
        <BaseButton type="primary" class="text-sm! font-bold flex!" @click="handleAddUser">
          Add User
        </BaseButton>
      </div>
    </div>
  </Modal>
</template> 