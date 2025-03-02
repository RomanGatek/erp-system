<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import DataTable from '@/components/common/DataTable.vue'
import SearchBar from '@/components/common/SearchBar.vue'
import Pagination from '@/components/common/Pagination.vue'
import Modal from '@/components/common/Modal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseCheckbox from '@/components/common/BaseCheckbox.vue'
import StatusBar from '@/components/common/StatusBar.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { getPaginationInfo, paginate } from '@/utils/pagination'

import {
  errorHandler,
  setErrorDefault,
  clearServerErrors
} from '@/utils/errorHandler.js'

defineOptions({
  name: 'UsersView',
})

const userStore = useUserStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)

const defaultUser = {
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: false,
  roles: [],
}


const defaultError = {
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  roles: '',
}

const newUser = reactive({ ...defaultUser })

const selectedUser = reactive({ ...defaultUser, roles: [] })

const availableRoles = [
  { name: 'admin', bgColor: 'bg-red-100', textColor: 'text-red-800' },
  { name: 'manager', bgColor: 'bg-blue-100', textColor: 'text-blue-800' },
  { name: 'user', bgColor: 'bg-green-100', textColor: 'text-green-800' },
]

const tableHeaders = [
  { field: 'firstName', label: 'First name', sortable: true },
  { field: 'lastName', label: 'Last name', sortable: true },
  { field: 'email', label: 'Email', sortable: true },
  { field: 'username', label: 'Username', sortable: true },
  { field: 'roles', label: 'Role', sortable: true },
  { field: 'active', label: 'Active', sortable: true },
  { field: 'actions', label: '', sortable: false, class: 'text-right' },
]
const loading = ref(false)
const error = ref('')
const serverErrors = ref({ ...defaultError })
setErrorDefault(defaultError)
const eHandler = errorHandler(serverErrors)

onMounted(async () => {
  clearServerErrors()
  loading.value = true
  try {
    await userStore.fetchUsers()
    if (!userStore.error) {
      loading.value = false
    } else {
      eHandler(userStore.error)
      loading.value = false
    }
  } catch (err) {
    console.error('Error fetching users:', err)
    loading.value = false
    error.value = 'Failed to load users. Please try again later.'
  }
})

const addUser = async () => {
  clearServerErrors()
  try {
    await userStore.addUser({ ...newUser })
    if (!userStore.error) {
      Object.assign(newUser, { ...newUser })
      isAddModalOpen.value = false
    } else {
      eHandler(userStore.error, error)
      console.log(userStore.error)
      if (error.value) isAddModalOpen.value = false
    }
  } catch (err) {
    console.error('Error adding user:', err)
    error.value = 'Failed to add user. Please try again later.'
  }
}

const openEditModal = (index) => {
  const user = userStore.users[index]
  Object.assign(selectedUser, {
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    username: user.username,
    active: user.active,
    roles: [...(user.roles || [])],
  })
  isEditModalOpen.value = true
}

const updateUser = async () => {
  clearServerErrors()
  try {
    await userStore.updateUser(selectedUser)
    if (userStore.error) {
      eHandler(userStore.error)
    } else {
      isEditModalOpen.value = false
    }
  } catch (err) {
    console.error('Error updating user:', err)
    error.value = 'Failed to update user. Please try again later.'
  }
}

const cancelEdit = () => {
  isEditModalOpen.value = false
  Object.assign(selectedUser, {
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    active: true,
    roles: [],
  })
}

const deleteUser = async (userId) => {
  if (confirm('Do you really want to delete this user?')) {
    try {
      await userStore.deleteUser(userId)
    } catch (err) {
      console.error('Error deleting user:', err)
      error.value = 'Failed to delete user. Please try again later.'
    }
  }
}

const cancelAdd = () => {
  isAddModalOpen.value = false
  Object.assign(newUser, {
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    password: '',
    active: true,
    roles: [],
  })
}

const paginationStart = computed(
  () =>
    getPaginationInfo(
      userStore.filteredUsers,
      userStore.pagination.currentPage,
      userStore.pagination.perPage,
    ).startItem,
)
const paginationEnd = computed(
  () =>
    getPaginationInfo(
      userStore.filteredUsers,
      userStore.pagination.currentPage,
      userStore.pagination.perPage,
    ).endItem,
)
const totalPages = computed(
  () =>
    getPaginationInfo(
      userStore.filteredUsers,
      userStore.pagination.currentPage,
      userStore.pagination.perPage,
    ).totalPages,
)
computed(() =>
  paginate(userStore.filteredUsers, userStore.pagination.currentPage, userStore.pagination.perPage),
)

const getRoleStyle = (roleName) => {
  const role = availableRoles.find((r) => roleName.toLowerCase().includes(r.name.toLowerCase()))
  return role ? [role.bgColor, role.textColor] : ['bg-gray-100', 'text-gray-800']
}

const formatRoleDisplay = (role) => {
  return role.name.replace('ROLE_', '').toLowerCase()
}

const isRoleSelected = (roleName, userRoles) => {
  return userRoles?.some((role) => role.name === `ROLE_${roleName.toUpperCase()}`) || false
}

watch(
  [
    () => newUser.firstName,
    () => newUser.lastName,
    () => newUser.email,
    () => newUser.username,
    () => newUser.password,
  ],
  () => {
    clearServerErrors()
  },
)
</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <StatusBar :error="error" :loading="loading" />

      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Users</h2>
        <div class="flex items-center space-x-4">
          <SearchBar v-model="searchInput" @update:modelValue="userStore.setSearch($event)" />
          <button
            @click="isAddModalOpen = true"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-sm transition flex items-center"
          >
            <span class="mr-2">Add User</span>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                clip-rule="evenodd"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- Empty state -->
      <EmptyState v-if="!userStore.paginatedUsers.length" message="No users to display" />

      <!-- Data table -->
      <template v-else>
        <div class="max-h-[500px] overflow-y-auto">
          <DataTable
            :headers="tableHeaders"
            :items="userStore.paginatedUsers"
            :sort-by="userStore.setSorting"
            :sorting="userStore.sorting"
            :on-edit="openEditModal"
            :on-delete="deleteUser"
          >
            <template #row="{ item, index }">
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.firstName }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.lastName }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.email }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.username }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex gap-1 flex-wrap">
                  <span
                    v-for="role in item.roles"
                    :key="role.id"
                    class="px-2 py-1 rounded-full text-xs font-medium"
                    :class="getRoleStyle(role.name)"
                  >
                    {{ formatRoleDisplay(role) }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                <span
                  :class="[
                    'px-2 py-1 rounded-full text-xs font-medium',
                    item.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                  ]"
                >
                  {{ item.active ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                  @click="openEditModal(index)"
                  class="text-blue-600 hover:text-blue-900 mr-4 p-1 rounded hover:bg-blue-50 cursor-pointer"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                    />
                  </svg>
                </button>
                <button
                  @click="deleteUser(item.id)"
                  class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 cursor-pointer"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </button>
              </td>
            </template>
          </DataTable>

          <Pagination
            :current-page="userStore.pagination.currentPage"
            :total-pages="totalPages"
            :total-items="userStore.filteredUsers.length"
            :start-item="paginationStart"
            :end-item="paginationEnd"
            @page-change="userStore.setPage"
          />
        </div>
      </template>
    </div>

    <!-- Add Modal -->
    <Modal :show="isAddModalOpen" title="Add New User" @close="cancelAdd" @submit="addUser">
      <div class="space-y-3">
        <div class="grid grid-cols-2 gap-3">
          <BaseInput
            v-model="newUser.firstName"
            placeholder="First Name"
            label="First name"
            :error="serverErrors.firstName"
            :class="{ 'border-red-500': serverErrors.firstName }"
          />
          <BaseInput
            v-model="newUser.lastName"
            placeholder="Last Name"
            label="Last name"
            :error="serverErrors.lastName"
            :class="{ 'border-red-500': serverErrors.lastName }"
          />
        </div>
        <BaseInput
          v-model="newUser.email"
          type="email"
          placeholder="Email"
          label="Email"
          :error="serverErrors.email"
          :class="{ 'border-red-500': serverErrors.email }"
        />
        <BaseInput
          v-model="newUser.username"
          placeholder="Username"
          label="Username"
          :error="serverErrors.username"
          :class="{ 'border-red-500': serverErrors.username }"
        />
        <BaseInput
          v-model="newUser.password"
          type="password"
          placeholder="Password"
          label="Password"
          :error="serverErrors.password"
          :class="{ 'border-red-500': serverErrors.password }"
        />
        <!-- Role selection -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Role</label>
          <div class="flex flex-wrap gap-2">
            <label
              v-for="role in availableRoles"
              :key="role.name"
              class="inline-flex items-center space-x-2 cursor-pointer"
            >
              <input
                type="checkbox"
                v-model="newUser.roles"
                :value="{ name: `ROLE_${role.name.toUpperCase()}` }"
                class="w-4 h-4 rounded border-gray-300 text-blue-600 shadow-sm focus:ring-2 focus:ring-blue-200 focus:ring-opacity-50"
              />
              <span
                class="px-2 py-1 rounded-full text-xs font-medium"
                :class="[role.bgColor, role.textColor]"
              >
                {{ role.name }}
              </span>
            </label>
          </div>
          <span v-if="serverErrors.roles" class="text-xs text-red-500">
            {{ serverErrors.roles }}
          </span>
        </div>
        <!-- General Error Message -->
        <div v-if="serverErrors.general" class="text-sm text-red-600 text-center mt-4">
          {{ serverErrors.general }}
        </div>
        <BaseCheckbox v-model="newUser.active" label="Is Active?" />
        <div class="flex justify-end space-x-3 pt-2">
          <button
            type="button"
            @click="cancelAdd"
            class="px-3 py-1.5 text-sm border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="px-4 py-1.5 text-sm bg-blue-500 hover:bg-blue-600 text-white rounded-lg shadow-sm transition"
          >
            Add User
          </button>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal :show="isEditModalOpen" title="Edit User" @close="cancelEdit" @submit="updateUser">
      <div class="space-y-3">
        <div class="grid grid-cols-2 gap-3">
          <BaseInput
            v-model="selectedUser.firstName"
            placeholder="First Name"
            label="First name"
            variant="success"
            :error="serverErrors.firstName"
            :class="{ 'border-red-500': serverErrors.firstName }"
          />
          <BaseInput
            v-model="selectedUser.lastName"
            placeholder="Last Name"
            label="Last name"
            variant="success"
            :error="serverErrors.lastName"
            :class="{ 'border-red-500': serverErrors.lastName }"
          />
        </div>
        <BaseInput
          v-model="selectedUser.email"
          type="email"
          placeholder="Email"
          label="Email"
          variant="success"
          :error="serverErrors.email"
          :class="{ 'border-red-500': serverErrors.email }"
        />
        <BaseInput
          v-model="selectedUser.username"
          placeholder="Username"
          label="Username"
          variant="success"
          :error="serverErrors.username"
          :class="{ 'border-red-500': serverErrors.username }"
        />
        <!-- Role selection -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700">Role</label>
          <div class="flex flex-wrap gap-2">
            <label
              v-for="role in availableRoles"
              :key="role.name"
              class="inline-flex items-center space-x-2 cursor-pointer"
            >
              <input
                type="checkbox"
                :checked="isRoleSelected(role.name, selectedUser.roles)"
                @change="
                  (e) => {
                    if (e.target.checked) {
                      selectedUser.roles.push({ name: `ROLE_${role.name.toUpperCase()}` })
                    } else {
                      selectedUser.roles = selectedUser.roles.filter(
                        (r) => r.name !== `ROLE_${role.name.toUpperCase()}`,
                      )
                    }
                  }
                "
                class="w-4 h-4 rounded border-gray-300 text-green-600 shadow-sm focus:ring-2 focus:ring-green-200 focus:ring-opacity-50"
              />
              <span
                class="px-2 py-1 rounded-full text-xs font-medium"
                :class="[role.bgColor, role.textColor]"
              >
                {{ role.name }}
              </span>
            </label>
          </div>
          <span v-if="serverErrors.roles" class="text-xs text-red-500">
            {{ serverErrors.roles }}
          </span>
        </div>
        <BaseCheckbox v-model="selectedUser.active" label="Is Active?" variant="success" />
        <div class="flex justify-between pt-2">
          <button
            type="submit"
            class="px-4 py-1.5 text-sm bg-green-500 hover:bg-green-600 text-white rounded-lg shadow-sm transition"
          >
            Update
          </button>
          <button
            type="button"
            @click="cancelEdit"
            class="px-4 py-1.5 text-sm bg-gray-500 hover:bg-gray-600 text-white rounded-lg shadow-sm transition"
          >
            Cancel
          </button>
        </div>
      </div>
    </Modal>
  </div>
</template>
