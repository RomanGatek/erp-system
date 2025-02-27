<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useInventoryStore } from '@/stores/inventory'
import DataTable from '@/components/common/DataTable.vue'
import SearchBar from '@/components/common/SearchBar.vue'
import Pagination from '@/components/common/Pagination.vue'
import Modal from '@/components/common/Modal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import StatusBar from '@/components/common/StatusBar.vue'
import EmptyState from '@/components/common/EmptyState.vue'

defineOptions({
  name: 'InventoryView',
})

const inventoryStore = useInventoryStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)

const newItem = reactive({
  productName: '',
  quantity: 0,
  location: ''
})

const selectedItem = reactive({
  productName: '',
  quantity: 0,
  location: ''
})

const tableHeaders = [
  { field: 'productName', label: 'Název produktu', sortable: true },
  { field: 'quantity', label: 'Množství', sortable: true },
  { field: 'location', label: 'Lokace', sortable: true },
  { field: 'actions', label: 'Akce', sortable: false, class: 'text-right' },
]

const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    await inventoryStore.fetchItems()
    if (inventoryStore.error) {
      error.value = inventoryStore.error
    }
  } catch (err) {
    error.value = 'Nepodařilo se načíst data inventáře: ' + err.message
  } finally {
    loading.value = false
  }
})

const addItem = async () => {
  await inventoryStore.addItem({ ...newItem })
  Object.assign(newItem, {
    productName: '',
    quantity: 0,
    location: ''
  })
  isAddModalOpen.value = false
}

const openEditModal = (index) => {
  isEditModalOpen.value = true
  Object.assign(selectedItem, inventoryStore.items[index])
}

const updateItem = async () => {
  await inventoryStore.updateItem(selectedItem)
  isEditModalOpen.value = false
}

const cancelEdit = () => {
  isEditModalOpen.value = false
}

const deleteItem = async (itemId) => {
  if (confirm('Opravdu chcete smazat tuto položku?')) {
    await inventoryStore.deleteItem(itemId)
  }
}

const cancelAdd = () => {
  isAddModalOpen.value = false
  Object.assign(newItem, {
    productName: '',
    quantity: 0,
    location: ''
  })
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

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Přesuneme StatusBar nad hlavní obsah, aby byl vždy viditelný -->
      <StatusBar
        :error="error"
        :loading="loading"
        class="mb-4"
      />

      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Inventory</h2>
        <div class="flex items-center space-x-4">
          <SearchBar
            v-model="searchInput"
            @update:modelValue="inventoryStore.setSearch($event)"
          />
          <button
            @click="isAddModalOpen = true"
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

      <!-- Loading overlay -->
      <div
        v-if="loading"
        class="absolute inset-0 bg-white/50 flex items-center justify-center rounded-2xl"
      >
        <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500"></div>
      </div>

      <!-- Empty state -->
      <EmptyState
        v-else-if="!inventoryStore.paginatedItems.length"
        message="Žádné itemy k zobrazení"
      />

      <!-- Data table -->
      <template v-else>
        <DataTable
          :headers="tableHeaders"
          :items="inventoryStore.paginatedItems"
          :sort-by="inventoryStore.setSorting"
          :sorting="inventoryStore.sorting"
          :on-edit="openEditModal"
          :on-delete="deleteItem"
        >
          <template #row="{ item }">
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
                @click="openEditModal(index)"
                class="text-blue-600 hover:text-blue-900 mr-4 p-1 rounded hover:bg-blue-50 cursor-pointer"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                </svg>
              </button>
              <button
                @click="deleteItem(item.id)"
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
          :current-page="inventoryStore.pagination.currentPage"
          :total-pages="totalPages"
          :total-items="inventoryStore.filteredItems.length"
          :start-item="paginationStart"
          :end-item="paginationEnd"
          @page-change="inventoryStore.setPage"
        />
      </template>
    </div>

    <!-- Add Modal -->
    <Modal
      :show="isAddModalOpen"
      title="Add New Item"
      @close="cancelAdd"
      @submit="addItem"
    >
      <div class="space-y-3">
        <BaseInput
          v-model="newItem.productName"
          placeholder="Název produktu"
          label="Název produktu"
        />
        <BaseInput
          v-model="newItem.quantity"
          type="number"
          placeholder="Množství"
          label="Množství"
        />
        <BaseInput
          v-model="newItem.location"
          placeholder="Lokace"
          label="Lokace"
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
            Add Item
          </button>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal
      :show="isEditModalOpen"
      title="Edit Item"
      @close="cancelEdit"
      @submit="updateItem"
    >
      <div class="space-y-3">
        <BaseInput
          v-model="selectedItem.productName"
          placeholder="Název produktu"
          label="Název produktu"
          variant="success"
        />
        <BaseInput
          v-model="selectedItem.quantity"
          type="number"
          placeholder="Množství"
          label="Množství"
          variant="success"
        />
        <BaseInput
          v-model="selectedItem.location"
          placeholder="Lokace"
          label="Lokace"
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