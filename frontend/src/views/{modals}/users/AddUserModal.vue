<script setup>
import { ref } from 'vue'
import { Modal, BaseInput } from '@/components'
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
  active: false,
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
      <!-- Role selection using XSelect -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700 mb-1">Role</label>
        <XSelect
          v-model="selectedRole"
          :options="roleOptions"
          label="Role"
          @update:modelValue="updateSelectedRole"
        />
        <span v-if="errors.roles" class="text-xs text-red-500">
          {{ errors.roles }}
        </span>
      </div>
      <!-- General Error Message -->
      <div v-if="errors.general" class="text-sm text-red-600 text-center mt-4">
        {{ errors.general }}
      </div>

      <!-- Account active slider toggle -->
      <div class="flex items-center justify-between pt-2">
        <label class="text-sm font-medium text-gray-700">Account active</label>
        <button
          type="button"
          @click="newUser.active = !newUser.active"
          class="relative inline-block w-12 h-6 rounded-full cursor-pointer"
          :class="newUser.active ? 'bg-green-500' : 'bg-gray-300'"
        >
          <span
            class="absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform duration-200 ease-in-out transform"
            :class="{ 'translate-x-6': newUser.active }"
          ></span>
        </button>
      </div>

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
