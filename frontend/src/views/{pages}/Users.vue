<script setup>
import { onMounted, ref, watch } from 'vue'
import { useUserStore } from '@/stores/user.store.js'
import { useErrorStore } from '@/stores/errors.store.js'
import { useNotifier } from '@/stores/notifier.store.js'

import {
  DataTable,
  SearchBar,
  Pagination,
  StatusBar,
  EmptyState,
} from '@/components'
import { $reactive } from '@/utils/index.js'
import BaseButton from '@/components/common/BaseButton.vue'
import AddUserModal from '@/views/{modals}/users/AddUserModal.vue'
import EditUserModal from '@/views/{modals}/users/EditUserModal.vue'

defineOptions({
  name: 'UsersView',
})

const userStore = useUserStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)
const errors = useErrorStore()
const $notifier = useNotifier()

const reactiveUser = $reactive({
  id: undefined,
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: false,
  roles: [],
})

const availableRoles = [
  { name: 'admin', bgColor: 'bg-red-100', textColor: 'text-red-800' },
  { name: 'manager', bgColor: 'bg-blue-100', textColor: 'text-blue-800' },
  { name: 'user', bgColor: 'bg-green-100', textColor: 'text-green-800' },
]

const tableHeaders = [
  { field: 'firstName', label: 'Name', sortable: true, align: 'left' },
  { field: 'lastName', label: 'Last Name', sortable: true },
  { field: 'email', label: 'Email', sortable: true },
  { field: 'username', label: 'Username', sortable: true },
  { field: 'roles', label: 'Roles', sortable: true },
  { field: 'active', label: 'Status', sortable: true },
  { field: 'actions', label: '', sortable: false, align: 'right' },
]

const loading = ref(false)

// Validate fields
errors.validateField(reactiveUser.$cleaned())

// Fetch users on mount
onMounted(async () => {
  errors.clearServerErrors()
  loading.value = true
  try {
    await userStore.fetchUsers()
    loading.value = false
  } catch (err) {
    errors.handle(err)
    loading.value = false
    console.error('Error fetching users:', err)
  }
})

// Add new user
const addUser = async (userData) => {
  try {
    await userStore.addUser(userData)
    if (!userStore.error) {
      isAddModalOpen.value = false
      $notifier.success('User was created successfully!')
      reactiveUser.$clear()
    } else {
      errors.handle(userStore.error)
      if (errors.general) {
        isAddModalOpen.value = false
      }
    }
  } catch (err) {
    errors.handle(err)
    console.error('Error adding user:', err)
  }
}

// Update existing user
const updateUser = async (userData) => {
  errors.clearServerErrors()
  try {
    await userStore.updateUser(userData)
    if (!userStore.error) {
      isEditModalOpen.value = false
      $notifier.success('User was updated successfully!')
      reactiveUser.$clear()
    } else {
      errors.handle(userStore.error)
      if (errors.general) isEditModalOpen.value = false
    }
  } catch (err) {
    errors.handle(err)
    console.error('Error updating user:', err)
  }
}

// Delete user
const deleteUser = async (userId) => {
  if (confirm('Are you sure you want to delete this user?')) {
    try {
      await userStore.deleteUser(userId)
      if (!userStore.error) {
        $notifier.success('User was deleted successfully!')
        reactiveUser.$clear()
      } else {
        errors.handle(userStore.error)
      }
    } catch (err) {
      errors.handle(err)
      console.error('Error deleting user:', err)
    }
  }
}

// Open edit modal
const openEditModal = (index) => {
  reactiveUser.$clear()
  const user = userStore.paginatedUsers[index]
  const fullUser = userStore.items.find(item => item.id === user.id)
  reactiveUser.$assign(fullUser || user)
  isEditModalOpen.value = true
}

// Modal cancel handlers
const cancelAdd = () => {
  errors.clearServerErrors()
  reactiveUser.$clear()
  isAddModalOpen.value = false
}

const cancelEdit = () => {
  errors.clearServerErrors()
  isEditModalOpen.value = false
  reactiveUser.$clear()
}

// Helper functions
const getRoleStyle = (roleName) => {
  const role = availableRoles.find((r) => roleName.toLowerCase().includes(r.name.toLowerCase()))
  return role ? [role.bgColor, role.textColor] : ['bg-gray-100', 'text-gray-800']
}

const formatRoleDisplay = (role) => {
  return role.name.replace('ROLE_', '').toLowerCase()
}

// Watch for changes to clear errors
watch(
  [
    () => reactiveUser.firstName,
    () => reactiveUser.lastName,
    () => reactiveUser.email,
    () => reactiveUser.username,
    () => reactiveUser.password,
  ],
  (newValues, oldValues) => {
    const fields = ['firstName', 'lastName', 'email', 'username', 'password']
    fields.forEach((field, index) => {
      if (newValues[index] !== oldValues[index]) {
        errors.clear(field)
      }
    })
  },
)
</script>

<template>
  <div class="w-full h-full">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-6">
        <!-- Status bar -->
        <StatusBar :error="errors.general" :loading="loading" class="mb-6" @clear-error="errors.clearServerErrors()" />

        <!-- Header Section -->
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
          <div>
            <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
              Users Management
            </h1>
            <p class="mt-1 text-sm text-gray-600">Manage user accounts and permissions</p>
          </div>

          <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 w-full sm:w-auto">
            <SearchBar v-model="searchInput" @update:modelValue="userStore.setSearch($event)" class="w-full sm:w-64" />
            <BaseButton type="primary"
              class="flex items-center justify-center gap-2 px-4 py-2.5 bg-gradient-to-r from-blue-600 to-blue-400 hover:from-blue-700 hover:to-blue-500 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg"
              @click="isAddModalOpen = true">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd"
                  d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                  clip-rule="evenodd" />
              </svg>
              Add User
            </BaseButton>
          </div>
        </div>

        <!-- Empty state with improved styling -->
        <EmptyState v-if="!userStore.paginatedUsers.length" class="bg-gray-50/50 rounded-xl p-8">
          <template #icon>
            <svg class="w-12 h-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </template>
          <template #message>
            <h3 class="mt-4 text-lg font-medium text-gray-900">No users found</h3>
            <p class="mt-1 text-gray-500">Get started by creating a new user</p>
          </template>
        </EmptyState>

        <!-- Data table with improved styling -->
        <template v-else>
          <div class="relative rounded-xl overflow-hidden">
            <div
              class="max-h-[calc(100vh-20rem)] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300 hover:scrollbar-thumb-gray-400 scrollbar-track-transparent">
              <DataTable :headers="tableHeaders" :items="userStore.paginatedUsers" :sort-by="userStore.setSorting"
                :sorting="userStore.sorting" :on-edit="openEditModal" :on-delete="deleteUser">
                <template #row="{ item, index }">
                  <td class="px-4 py-3 text-sm sticky left-0 z-[1] bg-inherit">
                    <div class="flex items-center gap-3">
                      <div
                        class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-100 to-blue-50 flex items-center justify-center ring-1 ring-blue-100">
                        <span class="text-blue-700 font-medium text-sm">
                          {{ item.firstName[0] }}{{ item.lastName[0] }}
                        </span>
                      </div>
                      <div>
                        <div class="font-medium text-gray-900">{{ item.firstName }}</div>
                        <div class="text-xs text-gray-500">ID: {{ item.id }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">
                    {{ item.lastName }}
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">
                    <div class="flex items-center gap-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" viewBox="0 0 20 20"
                        fill="currentColor">
                        <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
                        <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
                      </svg>
                      {{ item.email }}
                    </div>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">
                    <div class="flex items-center gap-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400" viewBox="0 0 20 20"
                        fill="currentColor">
                        <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
                          clip-rule="evenodd" />
                      </svg>
                      {{ item.username }}
                    </div>
                  </td>
                  <td class="px-4 py-3 text-sm">
                    <div class="flex gap-1.5 flex-wrap">
                      <span v-for="role in item.roles" :key="role.id"
                        class="px-2.5 py-1 rounded-full text-xs font-medium"
                        :class="getRoleStyle(formatRoleDisplay(role))">
                        {{ formatRoleDisplay(role) }}
                      </span>
                    </div>
                  </td>
                  <td class="px-4 py-3 text-sm">
                    <span :class="[
                      'inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium',
                      item.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                    ]">
                      <span class="w-1.5 h-1.5 rounded-full"
                        :class="item.active ? 'bg-green-500' : 'bg-red-500'"></span>
                      {{ item.active ? 'Active' : 'Inactive' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-right">
                    <div class="flex items-center justify-end gap-2">
                      <button @click="openEditModal(index)"
                        class="p-1.5 text-blue-600 hover:text-blue-900 rounded-lg hover:bg-blue-50/80 transition-colors"
                        title="Edit User">
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                        </svg>
                      </button>
                      <button @click="deleteUser(item.id)"
                        class="p-1.5 text-red-600 hover:text-red-900 rounded-lg hover:bg-red-50/80 transition-colors"
                        title="Delete User">
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </template>
              </DataTable>
            </div>
          </div>

          <!-- Pagination with improved styling -->
          <div class="mt-6">
            <Pagination :store="useUserStore" />
          </div>
        </template>
      </div>
    </div>

    <!-- Modals -->
    <AddUserModal v-show="isAddModalOpen" :available-roles="availableRoles" :errors="errors" @add-user="addUser"
      @cancel="cancelAdd" />

    <EditUserModal v-show="isEditModalOpen" :available-roles="availableRoles" :user="reactiveUser" :errors="errors"
      @update-user="updateUser" @cancel="cancelEdit" />
  </div>
</template>
