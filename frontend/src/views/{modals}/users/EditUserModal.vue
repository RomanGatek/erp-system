<script setup>
import { computed } from 'vue'
import { Modal, BaseInput, BaseCheckbox } from '@/components'
import BaseButton from '@/components/common/BaseButton.vue'

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

const isRoleSelected = (roleName, userRoles) => {
  return userRoles?.some((role) => role.name === `ROLE_${roleName.toUpperCase()}`) || false
}

const handleUpdateUser = () => {
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
      <!-- Role selection -->
      <div class="space-y-2">
        <label class="block text-sm font-medium text-gray-700">Role</label>
        <div class="flex flex-wrap gap-2">
          <label v-for="role in availableRoles" :key="role.name"
            class="inline-flex items-center space-x-2 cursor-pointer">
            <input type="checkbox" :checked="isRoleSelected(role.name, user.roles)" @change="
              (e) => {
                if (e.target.checked) {
                  user.roles.push({ name: `ROLE_${role.name.toUpperCase()}` })
                } else {
                  user.roles = user.roles.filter(
                    (r) => r.name !== `ROLE_${role.name.toUpperCase()}`,
                  )
                }
              }
            "
              class="w-4 h-4 rounded border-gray-300 text-green-600 shadow-sm focus:ring-2 focus:ring-green-200 focus:ring-opacity-50" />
            <span class="px-2 py-1 rounded-full text-xs font-medium" :class="[role.bgColor, role.textColor]">
              {{ role.name }}
            </span>
          </label>
        </div>
        <span v-if="errors.roles" class="text-xs text-red-500">
          {{ errors.roles }}
        </span>
      </div>
      <BaseCheckbox v-model="user.active" label="Account active" variant="success" />
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