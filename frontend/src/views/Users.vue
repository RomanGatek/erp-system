<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { notify } from '@kyvg/vue3-notification'
import DataTable from '@/components/common/DataTable.vue'
import SearchBar from '@/components/common/SearchBar.vue'
import Pagination from '@/components/common/Pagination.vue'
import Modal from '@/components/common/Modal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseCheckbox from '@/components/common/BaseCheckbox.vue'
import StatusBar from '@/components/common/StatusBar.vue'
import EmptyState from '@/components/common/EmptyState.vue'

// Přidáme jméno komponenty pro linter
defineOptions({
  name: 'UsersView',
})

const userStore = useUserStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)

const newUser = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: true,
  roles: []
})

const selectedUser = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: true,
  roles: []
})

// Upravíme definici rolí
const availableRoles = [
  { name: 'admin', bgColor: 'bg-red-100', textColor: 'text-red-800' },
  { name: 'manager', bgColor: 'bg-blue-100', textColor: 'text-blue-800' },
  { name: 'user', bgColor: 'bg-green-100', textColor: 'text-green-800' }
]

// Upravíme tableHeaders - přidáme roles
const tableHeaders = [
  { field: 'firstName', label: 'Jméno', sortable: true },
  { field: 'lastName', label: 'Příjmení', sortable: true },
  { field: 'email', label: 'Email', sortable: true },
  { field: 'username', label: 'Uživatelské jméno', sortable: true },
  { field: 'roles', label: 'Role', sortable: true },
  { field: 'active', label: 'Aktivní', sortable: true },
  { field: 'actions', label: 'Akce', sortable: false, class: 'text-right' },
]

const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    await userStore.fetchUsers()
    if (useUserStore.error) {
      error.value = useUserStore.error
    }
  } catch (err) {
    error.value = 'Nepodařilo se načíst data uživatelů: ' + err.message
  } finally {
    loading.value = false
  }
})

const addUser = async () => {
  await userStore.addUser({ ...newUser })
  Object.assign(newUser, {
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    password: '',
    active: true,
    roles: []
  })
  isAddModalOpen.value = false
}

const openEditModal = (index) => {
  const user = userStore.users[index]
  // Vytvoříme hlubokou kopii uživatele
  Object.assign(selectedUser, {
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    username: user.username,
    active: user.active,
    roles: [...(user.roles || [])] // Zajistíme, že roles je vždy pole
  })
  isEditModalOpen.value = true
}

const updateUser = async () => {
  try {
    await userStore.updateUser(selectedUser)
    isEditModalOpen.value = false
  } catch (error) {
    notify({
      type: 'error',
      text: error.message,
      duration: 5000
    })
  }
}

const cancelEdit = () => {
  isEditModalOpen.value = false
  // Vyčistíme data
  Object.assign(selectedUser, {
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    active: true,
    roles: []
  })
}

const deleteUser = async (userId) => {
  if (confirm('Opravdu chcete smazat tohoto uživatele?')) {
    await userStore.deleteUser(userId)
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
    roles: []
  })
}

const paginationStart = computed(
  () => (userStore.pagination.currentPage - 1) * userStore.pagination.perPage + 1,
)

const paginationEnd = computed(() =>
  Math.min(
    paginationStart.value + userStore.pagination.perPage - 1,
    userStore.filteredUsers.length,
  ),
)

const totalPages = computed(() =>
  Math.ceil(userStore.filteredUsers.length / userStore.pagination.perPage),
)

// Pomocná funkce pro získání stylu role
const getRoleStyle = (roleName) => {
  const role = availableRoles.find(r => 
    roleName.toLowerCase().includes(r.name.toLowerCase())
  )
  return role ? [role.bgColor, role.textColor] : ['bg-gray-100', 'text-gray-800']
}

// Pomocná funkce pro formátování zobrazení role
const formatRoleDisplay = (role) => {
  return role.name.replace('ROLE_', '').toLowerCase()
}

// Upravíme funkci pro kontrolu vybrané role
const isRoleSelected = (roleName, userRoles) => {
  return userRoles?.some(role => 
    role.name === `ROLE_${roleName.toUpperCase()}`
  ) || false
}
</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <StatusBar
        :error="error"
        :loading="loading"
      />

      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Users</h2>
        <div class="flex items-center space-x-4">
          <SearchBar
            v-model="searchInput"
            @update:modelValue="userStore.setSearch($event)"
          />
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

      <!-- Loading overlay -->
      <div
        v-if="loading"
        class="absolute inset-0 bg-white/50 flex items-center justify-center rounded-2xl"
      >
        <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500"></div>
      </div>

      <!-- Empty state -->
      <EmptyState
        v-else-if="!userStore.paginatedUsers.length"
        message="Žádní uživatelé k zobrazení"
      />

      <!-- Data table -->
      <template v-else>
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
                {{ item.active ? 'Aktivní' : 'Neaktivní' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="openEditModal(index)"
                class="text-blue-600 hover:text-blue-900 mr-4 p-1 rounded hover:bg-blue-50 cursor-pointer"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                </svg>
              </button>
              <button
                @click="deleteUser(item.id)"
                class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 cursor-pointer"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
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
      </template>
    </div>

    <!-- Add Modal -->
    <Modal
      :show="isAddModalOpen"
      title="Add New User"
      @close="cancelAdd"
      @submit="addUser"
    >
      <div class="space-y-3">
        <div class="grid grid-cols-2 gap-3">
          <BaseInput
            v-model="newUser.firstName"
            placeholder="Jméno"
            label="Jméno"
          />
          <BaseInput
            v-model="newUser.lastName"
            placeholder="Příjmení"
            label="Příjmení"
          />
        </div>
        <BaseInput
          v-model="newUser.email"
          type="email"
          placeholder="Email"
          label="Email"
        />
        <BaseInput
          v-model="newUser.username"
          placeholder="Uživatelské jméno"
          label="Uživatelské jméno"
        />
        <BaseInput
          v-model="newUser.password"
          type="password"
          placeholder="Heslo"
          label="Heslo"
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
        </div>
        <BaseCheckbox
          v-model="newUser.active"
          label="Aktivní"
        />
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
    <Modal
      :show="isEditModalOpen"
      title="Edit User"
      @close="cancelEdit"
      @submit="updateUser"
    >
      <div class="space-y-3">
        <div class="grid grid-cols-2 gap-3">
          <BaseInput
            v-model="selectedUser.firstName"
            placeholder="Jméno"
            label="Jméno"
            variant="success"
          />
          <BaseInput
            v-model="selectedUser.lastName"
            placeholder="Příjmení"
            label="Příjmení"
            variant="success"
          />
        </div>
        <BaseInput
          v-model="selectedUser.email"
          type="email"
          placeholder="Email"
          label="Email"
          variant="success"
        />
        <BaseInput
          v-model="selectedUser.username"
          placeholder="Uživatelské jméno"
          label="Uživatelské jméno"
          variant="success"
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
                @change="(e) => {
                  if (e.target.checked) {
                    selectedUser.roles.push({ name: `ROLE_${role.name.toUpperCase()}` })
                  } else {
                    selectedUser.roles = selectedUser.roles.filter(
                      r => r.name !== `ROLE_${role.name.toUpperCase()}`
                    )
                  }
                }"
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
        </div>
        <BaseCheckbox
          v-model="selectedUser.active"
          label="Aktivní"
          variant="success"
        />
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
