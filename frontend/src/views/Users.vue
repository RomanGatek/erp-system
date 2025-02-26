<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'

// Přidáme jméno komponenty pro linter
defineOptions({
  name: 'UsersView',
})

const userStore = useUserStore()
const newUser = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: true,
  roles: []
})
const isEditing = ref(false)
const isModalOpen = ref(false)
const isAddUserModalOpen = ref(false)
const selectedUser = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  active: true,
  roles: []
})
const editedUserIndex = ref(null)

const searchInput = ref('')

if (userStore.error) {
  console.log(userStore.error)
}

const tableHeaders = [
  { field: 'firstName', label: 'Jméno', sortable: true },
  { field: 'lastName', label: 'Příjmení', sortable: true },
  { field: 'email', label: 'Email', sortable: true },
  { field: 'username', label: 'Uživatelské jméno', sortable: true },
  { field: 'active', label: 'Aktivní', sortable: true },
  { field: 'actions', label: 'Akce', sortable: false },
]

const sortBy = (field) => {
  const header = tableHeaders.find((h) => h.field === field)
  if (header?.sortable) {
    userStore.setSorting(field)
  }
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

onMounted(async () => {
  await userStore.fetchUsers()
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
  isAddUserModalOpen.value = false
}

const openUserModal = (index) => {
  isEditing.value = true
  editedUserIndex.value = index
  Object.assign(selectedUser, userStore.users[index])
  isModalOpen.value = true
}

const updateUser = async () => {
  await userStore.updateUser(selectedUser)
  isModalOpen.value = false
  isEditing.value = false
  editedUserIndex.value = null
}

const cancelEdit = () => {
  isModalOpen.value = false
  isEditing.value = false
  editedUserIndex.value = null
}

const deleteUser = async (userId) => {
  if (confirm('Are you sure you want to delete this user?')) {
    await userStore.deleteUser(userId)
  }
}

const cancelAddUser = () => {
  isAddUserModalOpen.value = false
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

const handleSearch = () => {
  userStore.setSearch(searchInput.value)
}
</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Users</h2>
        <div class="flex items-center space-x-4">
          <!-- Search -->
          <div class="relative flex-grow">
            <div
              class="flex items-center pl-10 pr-4 py-2 bg-gray-50 rounded-lg focus-within:bg-white focus-within:ring-2 focus-within:ring-blue-500"
            >
              <!-- Search icon -->
              <svg
                class="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>

              <!-- Search input -->
              <input
                v-model="searchInput"
                @input="handleSearch"
                type="text"
                placeholder="Hledat..."
                class="outline-none w-full bg-transparent border-none p-0 focus:ring-0 text-sm placeholder-gray-400"
              />
            </div>
          </div>

          <!-- Add User Button -->
          <button
            @click="isAddUserModalOpen = true"
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
      <!-- Table -->
      <div class="overflow-hidden rounded-lg border border-gray-100">
        <table class="w-full">
          <thead>
            <tr class="bg-gray-50 border-b border-gray-100">
              <th
                v-for="header in tableHeaders"
                :key="header.field"
                :class="[
                  'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider',
                  header.sortable ? 'cursor-pointer hover:bg-gray-100' : '',
                ]"
                @click="header.sortable && sortBy(header.field)"
              >
                <div class="flex items-center space-x-1">
                  <span>{{ header.label }}</span>
                  <svg
                    v-if="header.sortable && userStore.sorting.field === header.field"
                    class="w-4 h-4"
                  >
                    <path
                      :d="
                        userStore.sorting.direction === 'asc' ? 'M5 15l7-7 7 7' : 'M19 9l-7 7-7-7'
                      "
                      stroke="currentColor"
                      stroke-width="2"
                      fill="none"
                    />
                  </svg>
                </div>
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="(user, index) in userStore.paginatedUsers"
              :key="user.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ user.firstName }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">{{ user.lastName }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">{{ user.email }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">{{ user.username }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                <span
                  :class="[
                    'px-2 py-1 rounded-full text-xs font-medium',
                    user.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
                  ]"
                >
                  {{ user.active ? 'Aktivní' : 'Neaktivní' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                  @click="openUserModal(index)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  Edit
                </button>
                <button @click="deleteUser(user.id)" class="text-red-600 hover:text-red-900">
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex items-center justify-between mt-4 text-sm text-gray-700">
        <div>
          Showing {{ paginationStart }} to {{ paginationEnd }} of
          {{ userStore.filteredUsers.length }} entries
        </div>
        <div class="flex space-x-2">
          <button
            v-for="page in totalPages"
            :key="page"
            @click="userStore.setPage(page)"
            :class="[
              'px-3 py-1 rounded',
              userStore.pagination.currentPage === page
                ? 'bg-blue-500 text-white'
                : 'bg-gray-100 hover:bg-gray-200',
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal pro přidání uživatele -->
    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="isAddUserModalOpen" class="fixed inset-0 z-50" @click="cancelAddUser">
        <div class="absolute inset-0 bg-gray-900/75 backdrop-blur-sm"></div>

        <div class="fixed inset-0 flex items-center justify-center">
          <Transition
            enter-active-class="transition ease-out duration-300"
            enter-from-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            enter-to-class="opacity-100 translate-y-0 sm:scale-100"
            leave-active-class="transition ease-in duration-200"
            leave-from-class="opacity-100 translate-y-0 sm:scale-100"
            leave-to-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
          >
            <div
              v-if="isAddUserModalOpen"
              class="bg-white p-8 rounded-2xl shadow-xl w-[32rem] ring-1 ring-gray-200 relative"
              @click.stop
            >
              <div class="flex justify-between items-center mb-6">
                <h2 class="text-2xl font-bold text-gray-800">Add New User</h2>
                <button @click="cancelAddUser" class="text-gray-500 hover:text-gray-700 transition">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </button>
              </div>
              <form @submit.prevent="addUser" class="space-y-4">
                <div class="grid grid-cols-2 gap-4">
                  <input
                    v-model="newUser.firstName"
                    type="text"
                    placeholder="Jméno"
                    class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                  />
                  <input
                    v-model="newUser.lastName"
                    type="text"
                    placeholder="Příjmení"
                    class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <input
                  v-model="newUser.email"
                  type="email"
                  placeholder="Email"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                />
                <input
                  v-model="newUser.username"
                  type="text"
                  placeholder="Uživatelské jméno"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                />
                <input
                  v-model="newUser.password"
                  type="password"
                  placeholder="Heslo"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                />
                <div class="flex items-center space-x-3">
                  <input
                    type="checkbox"
                    id="active"
                    v-model="newUser.active"
                    class="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
                  />
                  <label for="active" class="text-gray-700">Aktivní</label>
                </div>
                <div class="flex justify-end space-x-3">
                  <button
                    type="button"
                    @click="cancelAddUser"
                    class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    class="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded-lg shadow-md transition"
                  >
                    Add User
                  </button>
                </div>
              </form>
            </div>
          </Transition>
        </div>
      </div>
    </Transition>

    <!-- Modal pro editaci uživatele -->
    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="isModalOpen" class="fixed inset-0 z-50" @click="cancelEdit">
        <div class="absolute inset-0 bg-gray-900/75 backdrop-blur-sm"></div>

        <div class="fixed inset-0 flex items-center justify-center">
          <Transition
            enter-active-class="transition ease-out duration-300"
            enter-from-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            enter-to-class="opacity-100 translate-y-0 sm:scale-100"
            leave-active-class="transition ease-in duration-200"
            leave-from-class="opacity-100 translate-y-0 sm:scale-100"
            leave-to-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
          >
            <div
              v-if="isModalOpen"
              class="bg-white p-8 rounded-2xl shadow-xl w-[32rem] ring-1 ring-gray-200 relative"
              @click.stop
            >
              <h2 class="text-2xl font-bold text-gray-800 mb-4">Edit User</h2>
              <div class="grid grid-cols-2 gap-4 mb-4">
                <input
                  v-model="selectedUser.firstName"
                  type="text"
                  placeholder="Jméno"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500"
                />
                <input
                  v-model="selectedUser.lastName"
                  type="text"
                  placeholder="Příjmení"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500"
                />
              </div>
              <input
                v-model="selectedUser.email"
                type="email"
                placeholder="Email"
                class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500 mb-4"
              />
              <input
                v-model="selectedUser.username"
                type="text"
                placeholder="Uživatelské jméno"
                class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500 mb-4"
              />
              <div class="flex items-center space-x-3 mb-6">
                <input
                  type="checkbox"
                  id="edit-active"
                  v-model="selectedUser.active"
                  class="w-4 h-4 text-green-600 rounded border-gray-300 focus:ring-green-500"
                />
                <label for="edit-active" class="text-gray-700">Aktivní</label>
              </div>
              <div class="flex justify-between">
                <button
                  class="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded-lg shadow-md transition"
                  @click="updateUser"
                >
                  Update
                </button>
                <button
                  class="bg-gray-500 hover:bg-gray-600 text-white px-6 py-2 rounded-lg shadow-md transition"
                  @click="cancelEdit"
                >
                  Cancel
                </button>
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </Transition>
  </div>
</template>
