<script setup>
import { onMounted, ref, watch } from 'vue'
import { useUserStore } from '@/stores/user.store.js'
import { useErrorStore } from '@/stores/errors.js'
import { useNotifier } from '@/stores/notifier.js'

import {
  DataTable,
  SearchBar,
  Pagination,
  Modal,
  StatusBar,
  EmptyState,
  BaseInput,
  BaseCheckbox
} from '@/components'
import { $reactive } from '@/utils/index.js'


defineOptions({
  name: 'UsersView',
})

const userStore = useUserStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)
const errorStore = useErrorStore()

const reactiveUser = $reactive({
  id: undefined,
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: false,
  roles: [],
});

const availableRoles = [
  { name: 'admin', bgColor: 'bg-red-100', textColor: 'text-red-800' },
  { name: 'manager', bgColor: 'bg-blue-100', textColor: 'text-blue-800' },
  { name: 'user', bgColor: 'bg-green-100', textColor: 'text-green-800' },
]

const tableHeaders = [
  { field: 'firstName', label: 'First name', sortable: true },
  { field: 'lastName', label: 'Last name', sortable: true },
  { field: 'email', label: 'Email', sortable: true},
  { field: 'username', label: 'Username', sortable: true },
  { field: 'roles', label: 'Role', sortable: true },
  { field: 'active', label: 'Active', sortable: true },
  { field: 'actions', label: '', sortable: false, class: 'text-right' },
]
const loading = ref(false)
const $notifier = useNotifier()

onMounted(async () => {
  errorStore.clearServerErrors()
  loading.value = true
  try {
    await userStore.fetchUsers()
    if (!userStore.error) {
      loading.value = false
    } else {
      errorStore.handle(userStore.error)
      loading.value = false
    }
  } catch (err) {
    errorStore.handle(err)
    loading.value = false
    console.error(err)
  }
})

const addUser = async () => {
  try {
    await userStore.addUser({ ...reactiveUser })
    if (!userStore.error) {
      isAddModalOpen.value = false
      $notifier.success("User was created successfully!")
      reactiveUser.$clear();
    } else {
      errorStore.handle(userStore.error)
      if (errorStore.errors.general) {
        isAddModalOpen.value = false
      }
    }
  } catch (err) {
    errorStore.handle(err)
    loading.value = false
    console.error(err)
  }
}

const updateUser = async () => {
  errorStore.clearServerErrors()
  try {
    await userStore.updateUser(reactiveUser)
    if (userStore.error) {
      errorStore.handle(userStore.error)
      if (errorStore.errors.general) isEditModalOpen.value = false
    } else {
      isEditModalOpen.value = false
      $notifier.success("User was edited successfully!")
      reactiveUser.$clear();
    }
  } catch (err) {
    errorStore.handle(err)
    loading.value = false
    console.error(err)
  }
}

const deleteUser = async (userId) => {
  if (confirm('Do you really want to delete this user?')) {
    try {
      await userStore.deleteUser(userId)
      $notifier.success("User deleted removed successfully!")
      reactiveUser.$clear();
    } catch (err) {
      console.error('Error deleting user:', err)
      errorStore.errors.general = 'Failed to delete user. Please try again later.'
    }
  }
}

const openEditModal = (index) => {
  reactiveUser.$clear();
  const user = userStore.items[index]
  reactiveUser.$assign(user);
  isEditModalOpen.value = true
}

const cancelAdd = () => {
  errorStore.clearServerErrors()
  reactiveUser.$clear()
  isAddModalOpen.value = false;
}

const cancelEdit = () => {
  errorStore.clearServerErrors()
  isEditModalOpen.value = false
  reactiveUser.$clear();
}

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
    () => reactiveUser.firstName,
    () => reactiveUser.lastName,
    () => reactiveUser.email,
    () => reactiveUser.username,
    () => reactiveUser.password,
  ],
  ([newFirstName, newLastName, newEmail, newUserName, newPassword],
   [oldFirstName, oldLastName, oldEmail, oldUsername, oldPassword]) => {
    if (newFirstName !== oldFirstName) {
      errorStore.clear('firstName')
    }
    if (newLastName !== oldLastName) {
      errorStore.clear('lastName')
    }
    if (newEmail !== oldEmail) {
      errorStore.clear('email')
    }
    if (newUserName !== oldUsername) {
      errorStore.clear('username')
    }
    if (oldPassword !== newPassword) {
      errorStore.clear('password')
    }
  }
)


</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <StatusBar
        :error="errorStore.errors.general"
        :loading="loading"
        class="mb-4"
        @clear-error="errorStore.clearServerErrors()"
      />

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

          <Pagination :store="useUserStore" />
        </div>
      </template>
    </div>

    <!-- Add Modal -->
    <Modal :show="isAddModalOpen" title="Add New User" @close="cancelAdd" @submit="addUser">
      <div class="space-y-3">
        <div class="grid grid-cols-2 gap-3">
          <BaseInput
            v-model="reactiveUser.firstName"
            placeholder="First Name"
            label="First name"
            :error="errorStore.errors.firstName"
            :class="{ 'border-red-500': errorStore.errors.firstName }"
          />
          <BaseInput
            v-model="reactiveUser.lastName"
            placeholder="Last Name"
            label="Last name"
            :error="errorStore.errors.lastName"
            :class="{ 'border-red-500': errorStore.errors.lastName }"
          />
        </div>
        <BaseInput
          v-model="reactiveUser.email"
          type="email"
          placeholder="Email"
          label="Email"
          :error="errorStore.errors.email"
          :class="{ 'border-red-500': errorStore.errors.email }"
        />
        <BaseInput
          v-model="reactiveUser.username"
          placeholder="Username"
          label="Username"
          :error="errorStore.errors.username"
          :class="{ 'border-red-500': errorStore.errors.username }"
        />
        <BaseInput
          v-model="reactiveUser.password"
          type="password"
          placeholder="Password"
          label="Password"
          :error="errorStore.errors.password"
          :class="{ 'border-red-500': errorStore.errors.password }"
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
                v-model="reactiveUser.roles"
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
          <span v-if="errorStore.errors.roles" class="text-xs text-red-500">
            {{ errorStore.errors.roles }}
          </span>
        </div>
        <!-- General Error Message -->
        <div v-if="errorStore.errors.general" class="text-sm text-red-600 text-center mt-4">
          {{ errorStore.errors.general }}
        </div>
        <BaseCheckbox v-model="reactiveUser.active" label="Is Active?" />
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
            v-model="reactiveUser.firstName"
            placeholder="First Name"
            label="First name"
            variant="success"
            :error="errorStore.errors.firstName"
            :class="{ 'border-red-500': errorStore.errors.firstName }"
          />
          <BaseInput
            v-model="reactiveUser.lastName"
            placeholder="Last Name"
            label="Last name"
            variant="success"
            :error="errorStore.errors.lastName"
            :class="{ 'border-red-500': errorStore.errors.lastName }"
          />
        </div>
        <BaseInput
          v-model="reactiveUser.email"
          type="email"
          placeholder="Email"
          label="Email"
          variant="success"
          :error="errorStore.errors.email"
          :class="{ 'border-red-500': errorStore.errors.email }"
        />
        <BaseInput
          v-model="reactiveUser.username"
          placeholder="Username"
          label="Username"
          variant="success"
          :error="errorStore.errors.username"
          :class="{ 'border-red-500': errorStore.errors.username }"
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
                :checked="isRoleSelected(role.name, reactiveUser.roles)"
                @change="
                  (e) => {
                    if (e.target.checked) {
                      reactiveUser.roles.push({ name: `ROLE_${role.name.toUpperCase()}` })
                    } else {
                      reactiveUser.roles = reactiveUser.roles.filter(
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
          <span v-if="errorStore.errors.roles" class="text-xs text-red-500">
            {{ errorStore.errors.roles }}
          </span>
        </div>
        <BaseCheckbox v-model="reactiveUser.active" label="Is Active?" variant="success" />
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
