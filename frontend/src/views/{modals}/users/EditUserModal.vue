<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { Modal, BaseInput } from '@/components'
import BaseButton from '@/components/common/BaseButton.vue'
import XSelect from '@/components/common/XSelect.vue'

const props = defineProps({
  user: {
    type: Object,
    required: true
  },
  availableRoles: {
    type: Array,
    required: true
  },
  errors: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update-user', 'cancel'])

// Convert roles for XSelect format
const roleOptions = computed(() => {
  return props.availableRoles.map(role => ({
    value: role.name,
    label: role.name
  }))
})

// Currently selected role
const selectedRole = ref('')

// Function to extract role name without the prefix
const getRoleNameWithoutPrefix = (roleName) => {
  return roleName?.replace('ROLE_', '').toLowerCase() || ''
}

// Set the selectedRole based on user's current roles
const updateSelectedRoleFromUser = () => {
  if (props.user.roles?.length > 0) {
    // Extract the role name without prefix for display
    const roleName = getRoleNameWithoutPrefix(props.user.roles[0].name)
    const matchingRole = props.availableRoles.find(r => r.name.toLowerCase() === roleName)
    if (matchingRole) {
      selectedRole.value = matchingRole.name
    } else if (roleOptions.value.length > 0) {
      selectedRole.value = roleOptions.value[0].value
    }
  } else if (roleOptions.value.length > 0) {
    selectedRole.value = roleOptions.value[0].value
    // If user has no roles, initialize with the first available role
    props.user.roles = [{ name: `ROLE_${roleOptions.value[0].value.toUpperCase()}` }]
  }
}

// Update the user's roles when selected role changes
const updateSelectedRole = (roleName) => {
  props.user.roles = [{ name: `ROLE_${roleName.toUpperCase()}` }]
}

// Initialize on mount
onMounted(() => {
  updateSelectedRoleFromUser()
})

// Watch for changes in the user prop to update the selected role
watch(() => props.user, () => {
  updateSelectedRoleFromUser()
}, { deep: true, immediate: true })

const handleUpdateUser = () => {
  // Ensure the role is set before submitting
  if (props.user.roles?.length === 0 && selectedRole.value) {
    updateSelectedRole(selectedRole.value)
  }
  emit('update-user', props.user)
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<template>
  <Modal :show="true" title="Edit User" @close="handleCancel">
    <div class="space-y-4">
      <!-- Personal Information Section -->
      <div class="bg-gray-50/50 rounded-lg p-4 space-y-3">
        <h3 class="text-sm font-medium text-gray-700">Personal Information</h3>
        <div class="grid grid-cols-2 gap-3">
          <BaseInput 
            v-model="user.firstName" 
            placeholder="First Name" 
            label="First name" 
            :error="errors.firstName"
            class="text-sm"
          />
          <BaseInput 
            v-model="user.lastName" 
            placeholder="Last Name" 
            label="Last name" 
            :error="errors.lastName"
            class="text-sm"
          />
        </div>
        <BaseInput 
          v-model="user.email" 
          type="email" 
          placeholder="Email" 
          label="Email" 
          :error="errors.email"
          class="text-sm"
        />
      </div>

      <!-- Account Details Section -->
      <div class="bg-gray-50/50 rounded-lg p-4 space-y-3">
        <h3 class="text-sm font-medium text-gray-700">Account Details</h3>
        <BaseInput 
          v-model="user.username" 
          placeholder="Username" 
          label="Username" 
          :error="errors.username"
          class="text-sm"
        />

        <!-- Role Selection -->
        <div class="space-y-2">
          <XSelect 
            v-model="selectedRole" 
            :options="roleOptions" 
            label="Role"
            class="text-sm"
            @update:modelValue="updateSelectedRole" 
          />
          <span v-if="errors.roles" class="text-xs text-red-500 pl-1">
            {{ errors.roles }}
          </span>
        </div>

        <!-- Account Status -->
        <div class="pt-2">
          <label class="flex items-center justify-between p-3 bg-gray-50/80 rounded-lg cursor-pointer group hover:bg-gray-100/50 transition-colors">
            <div>
              <span class="text-sm font-medium text-gray-700">Account Status</span>
              <p class="text-xs text-gray-500">Enable or disable user account</p>
            </div>
            <div class="flex items-center gap-3">
              <span class="text-sm" :class="user.active ? 'text-green-600' : 'text-gray-400'">
                {{ user.active ? 'Active' : 'Inactive' }}
              </span>
              <div class="relative">
                <input type="checkbox" class="sr-only peer" v-model="user.active" />
                <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"></div>
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
        <BaseButton 
          type="secondary" 
          class="px-4!" 
          @click="handleCancel"
        >
          Cancel
        </BaseButton>
        <BaseButton 
          type="primary" 
          class="px-4!" 
          @click="handleUpdateUser"
        >
          Update User
        </BaseButton>
      </div>
    </div>
  </Modal>
</template>
