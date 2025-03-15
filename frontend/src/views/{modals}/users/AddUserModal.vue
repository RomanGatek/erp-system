<script setup>
import { ref } from 'vue'
import { Modal, BaseInput, PasswordInput } from '@/components'
import { $reactive } from '@/utils/index.js'
import BaseButton from '@/components/common/BaseButton.vue'
import XSelect from '@/components/common/XSelect.vue'

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
  active: true,
  roles: [],
})

// Convert roles for XSelect format - using just the name property without prefix for the value
const roleOptions = props.availableRoles.map(role => ({
  value: role.name,  // Use just the name without prefix as the value
  label: role.name
}))

// Currently selected role - initialize with the first role option
const selectedRole = ref(roleOptions[0]?.value || '')

// Initialize with the default role
if (selectedRole.value) {
  newUser.roles = [{ name: `ROLE_${selectedRole.value.toUpperCase()}` }]
}

// Update the roles array when the selected role changes
const updateSelectedRole = (roleName) => {
  // Clear previous roles and add the new one with proper format
  newUser.roles = [{ name: `ROLE_${roleName.toUpperCase()}` }]
}

const handleAddUser = () => {
  // Make sure a role is set before submitting
  if (newUser.roles.length === 0 && selectedRole.value) {
    updateSelectedRole(selectedRole.value)
  }
  emit('add-user', newUser.$cleaned())
}

const handleCancel = () => {
  newUser.$clear()
  emit('cancel')
}
</script>

<template>
  <Modal :show="true" title="Add New User" @close="handleCancel">
    <div class="space-y-4">
      <!-- Personal Information Section -->
      <div class="bg-gray-50/50 rounded-lg p-4 space-y-3">
        <h3 class="text-sm font-medium text-gray-700">Personal Information</h3>
        <div class="grid grid-cols-2 gap-3">
          <BaseInput v-model="newUser.firstName" placeholder="First Name" label="First name" :error="errors.firstName"
            class="text-sm" />
          <BaseInput v-model="newUser.lastName" placeholder="Last Name" label="Last name" :error="errors.lastName"
            class="text-sm" />
        </div>
        <BaseInput v-model="newUser.email" type="email" placeholder="Email" label="Email" :error="errors.email"
          class="text-sm" />
      </div>

      <!-- Account Details Section -->
      <div class="bg-gray-50/50 rounded-lg p-4 space-y-3">
        <h3 class="text-sm font-medium text-gray-700">Account Details</h3>
        <BaseInput v-model="newUser.username" placeholder="Username" label="Username" :error="errors.username"
          class="text-sm" />
        <PasswordInput v-model="newUser.password" placeholder="Password" label="Password" :error="errors.password"
          class="text-sm" />

        <!-- Role Selection -->
        <div class="space-y-2 flex items-center gap-3">
          <XSelect v-model="selectedRole" :options="roleOptions" label="Role" class="text-sm"
            @update:modelValue="updateSelectedRole" />
          <span v-if="errors.roles" class="text-xs text-red-500 pl-1">
            {{ errors.roles }}
          </span>
        </div>

        <!-- Account Status -->
        <div class="pt-2">
          <label
            class="flex items-center justify-between p-3 bg-gray-50/80 rounded-lg cursor-pointer group hover:bg-gray-100/50 transition-colors">
            <div>
              <span class="text-sm font-medium text-gray-700">Account Status</span>
              <p class="text-xs text-gray-500">Enable or disable user account</p>
            </div>
            <div class="flex items-center gap-3">
              <span class="text-sm" :class="newUser.active ? 'text-green-600' : 'text-gray-400'">
                {{ newUser.active ? 'Active' : 'Inactive' }}
              </span>
              <div class="relative">
                <input type="checkbox" class="sr-only peer" v-model="newUser.active" />
                <div
                  class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500">
                </div>
              </div>
            </div>
          </label>
        </div>
      </div>

      <!-- Error Message -->
      <div v-if="errors.general" class="p-3 bg-red-50 border border-red-100 rounded-lg">
        <p class="text-sm text-red-600 text-center">{{ errors.general }}</p>
      </div>

      <!-- Action Buttons -->
      <div class="flex justify-end gap-3 pt-4">
        <BaseButton type="secondary" class="px-4!" @click="handleCancel">
          Cancel
        </BaseButton>
        <BaseButton type="primary" class="px-4!" @click="handleAddUser">
          Add User
        </BaseButton>
      </div>
    </div>
  </Modal>
</template>
