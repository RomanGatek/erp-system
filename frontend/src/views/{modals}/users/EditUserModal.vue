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
    <div class="space-y-3">
      <div class="grid grid-cols-2 gap-3">
        <BaseInput v-model="user.firstName" placeholder="First Name" label="First name" variant="success"
          :error="errors.firstName" :class="{ 'border-red-500': errors.firstName }" />
        <BaseInput v-model="user.lastName" placeholder="Last Name" label="Last name" variant="success"
          :error="errors.lastName" :class="{ 'border-red-500': errors.lastName }" />
      </div>
      <BaseInput v-model="user.email" type="email" placeholder="Email" label="Email" variant="success"
        :error="errors.email" :class="{ 'border-red-500': errors.email }" />
      <BaseInput v-model="user.username" placeholder="Username" label="Username" variant="success"
        :error="errors.username" :class="{ 'border-red-500': errors.username }" />

      <!-- Role selection using XSelect -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700 mb-1">Role</label>
        <XSelect v-model="selectedRole" :options="roleOptions" label="Role" @update:modelValue="updateSelectedRole" />
        <span v-if="errors.roles" class="text-xs text-red-500">
          {{ errors.roles }}
        </span>
      </div>

      <!-- Account active slider toggle -->
      <div class="flex items-center justify-between pt-2">
        <label class="text-sm font-medium text-gray-700">Account active</label>
        <button
          type="button"
          @click="user.active = !user.active"
          class="relative inline-block w-12 h-6 rounded-full cursor-pointer"
          :class="user.active ? 'bg-green-500' : 'bg-gray-300'"
        >
          <span
            class="absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform duration-200 ease-in-out transform"
            :class="{ 'translate-x-6': user.active }"
          ></span>
        </button>
      </div>

      <div class="flex justify-between pt-2">
        <BaseButton type="error" class="text-sm! font-bold flex!" @click="handleCancel">
          Cancel
        </BaseButton>
        <BaseButton type="primary" class="text-sm! font-bold flex!" @click="handleUpdateUser">
          Update User
        </BaseButton>
      </div>
    </div>
  </Modal>
</template>
