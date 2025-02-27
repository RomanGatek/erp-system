<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Inventory</h2>
        <div class="flex items-center space-x-4">
          <!-- Search -->
          <div class="relative flex-grow">
            <div
              class="flex items-center pl-10 pr-4 py-2 bg-gray-50 rounded-lg focus-within:bg-white focus-within:ring-2 focus-within:ring-blue-500"
            >
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

              <input
                v-model="searchInput"
                @input="handleSearch"
                type="text"
                placeholder="Hledat..."
                class="outline-none w-full bg-transparent border-none p-0 focus:ring-0 text-sm placeholder-gray-400"
              />
            </div>
          </div>

          <!-- Add Item Button -->
          <button
            @click="isAddItemModalOpen = true"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-sm transition flex items-center"
          >
            <span class="mr-2">Add Item</span>
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
                    v-if="header.sortable && inventoryStore.sorting.field === header.field"
                    class="w-4 h-4"
                  >
                    <path
                      :d="
                        inventoryStore.sorting.direction === 'asc'
                          ? 'M5 15l7-7 7 7'
                          : 'M19 9l-7 7-7-7'
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
              v-for="(item, index) in inventoryStore.paginatedItems"
              :key="item.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.productName }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.quantity }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.location }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                  @click="openItemModal(index)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="deleteItem(item.id)"
                  class="text-red-600 hover:text-red-900"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Přidat před uzavírací tag první div.bg-white -->
      <div class="flex items-center justify-between mt-4 text-sm text-gray-700">
        <div>
          Showing {{ paginationStart }} to {{ paginationEnd }} of
          {{ inventoryStore.filteredItems.length }} entries
        </div>
        <div class="flex space-x-2">
          <button
            v-for="page in totalPages"
            :key="page"
            @click="inventoryStore.setPage(page)"
            :class="[
              'px-3 py-1 rounded',
              inventoryStore.pagination.currentPage === page
                ? 'bg-blue-500 text-white'
                : 'bg-gray-100 hover:bg-gray-200',
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- Add Item Modal -->
    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="isAddItemModalOpen" class="fixed inset-0 z-50" @click="cancelAddItem">
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
              v-if="isAddItemModalOpen"
              class="bg-white p-8 rounded-2xl shadow-xl w-[32rem] ring-1 ring-gray-200 relative"
              @click.stop
            >
              <div class="flex justify-between items-center mb-6">
                <h2 class="text-2xl font-bold text-gray-800">Add New Item</h2>
                <button
                  @click="cancelAddItem"
                  class="text-gray-500 hover:text-gray-700 transition"
                >
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
              <form @submit.prevent="addItem" class="space-y-4">
                <input
                  v-model="newItem.productName"
                  type="text"
                  placeholder="Název produktu"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                />
                <input
                  v-model="newItem.quantity"
                  type="number"
                  placeholder="Množství"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                />
                <input
                  v-model="newItem.location"
                  type="text"
                  placeholder="Lokace"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500"
                />
                <div class="flex justify-end space-x-3">
                  <button
                    type="button"
                    @click="cancelAddItem"
                    class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    class="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded-lg shadow-md transition"
                  >
                    Add Item
                  </button>
                </div>
              </form>
            </div>
          </Transition>
        </div>
      </div>
    </Transition>

    <!-- Edit Item Modal -->
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
              <h2 class="text-2xl font-bold text-gray-800 mb-4">Edit Item</h2>
              <form @submit.prevent="updateItem" class="space-y-4">
                <input
                  v-model="selectedItem.productName"
                  type="text"
                  placeholder="Název produktu"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500"
                />
                <input
                  v-model="selectedItem.quantity"
                  type="number"
                  placeholder="Množství"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500"
                />
                <input
                  v-model="selectedItem.location"
                  type="text"
                  placeholder="Lokace"
                  class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500"
                />
                <div class="flex justify-between">
                  <button
                    type="submit"
                    class="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded-lg shadow-md transition"
                  >
                    Update
                  </button>
                  <button
                    type="button"
                    @click="cancelEdit"
                    class="bg-gray-500 hover:bg-gray-600 text-white px-6 py-2 rounded-lg shadow-md transition"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </Transition>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useInventoryStore } from '@/stores/inventory'
import { notify } from '@kyvg/vue3-notification'

defineOptions({
  name: 'InventoryView',
})

const inventoryStore = useInventoryStore()
const newItem = reactive({
  productName: '',
  quantity: 0,
  location: ''
})
const isEditing = ref(false)
const isModalOpen = ref(false)
const isAddItemModalOpen = ref(false)
const selectedItem = reactive({
  productName: '',
  quantity: 0,
  location: ''
})
const editedItemIndex = ref(null)

const searchInput = ref('')

if (inventoryStore.error) {
  notify({
    type: 'error',
    text: inventoryStore.error,
    duration: 5000,
    speed: 500
  })
}

const tableHeaders = [
  { field: 'productName', label: 'Název produktu', sortable: true },
  { field: 'quantity', label: 'Množství', sortable: true },
  { field: 'location', label: 'Lokace', sortable: true },
  { field: 'actions', label: 'Akce', sortable: false },
]

const sortBy = (field) => {
  const header = tableHeaders.find((h) => h.field === field)
  if (header?.sortable) {
    inventoryStore.setSorting(field)
  }
}

onMounted(async () => {
  await inventoryStore.fetchItems()
})

const addItem = async () => {
  await inventoryStore.addItem({ ...newItem })
  Object.assign(newItem, {
    productName: '',
    quantity: 0,
    location: ''
  })
  isAddItemModalOpen.value = false
}

const openItemModal = (index) => {
  isEditing.value = true
  editedItemIndex.value = index
  Object.assign(selectedItem, inventoryStore.items[index])
  isModalOpen.value = true
}

const updateItem = async () => {
  await inventoryStore.updateItem(selectedItem)
  isModalOpen.value = false
  isEditing.value = false
  editedItemIndex.value = null
}

const cancelEdit = () => {
  isModalOpen.value = false
  isEditing.value = false
  editedItemIndex.value = null
}

const deleteItem = async (itemId) => {
  if (confirm('Opravdu chcete smazat tuto položku?')) {
    await inventoryStore.deleteItem(itemId)
  }
}

const cancelAddItem = () => {
  isAddItemModalOpen.value = false
  Object.assign(newItem, {
    productName: '',
    quantity: 0,
    location: ''
  })
}

const handleSearch = () => {
  inventoryStore.setSearch(searchInput.value)
}

const paginationStart = computed(
  () => (inventoryStore.pagination.currentPage - 1) * inventoryStore.pagination.perPage + 1,
)

const paginationEnd = computed(() =>
  Math.min(
    paginationStart.value + inventoryStore.pagination.perPage - 1,
    inventoryStore.filteredItems.length,
  ),
)

const totalPages = computed(() =>
  Math.ceil(inventoryStore.filteredItems.length / inventoryStore.pagination.perPage),
)
</script>
