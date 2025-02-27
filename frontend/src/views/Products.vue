<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <StatusBar
        :error="error"
        :loading="loading"
      />

      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Products</h2>
        <div class="flex items-center space-x-4">
          <SearchBar
            v-model="searchInput"
            @update:modelValue="productsStore.setSearch($event)"
          />
          <button
            @click="isAddModalOpen = true"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-sm transition flex items-center"
          >
            <span class="mr-2">Add Product</span>
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
        v-else-if="!productsStore.paginatedProducts.length"
        message="Žádné produkty k zobrazení"
      />

      <!-- Data table -->
      <template v-else>
        <DataTable
          :headers="tableHeaders"
          :items="productsStore.paginatedProducts"
          :sort-by="productsStore.setSorting"
          :sorting="productsStore.sorting"
          :on-edit="openEditModal"
          :on-delete="deleteProduct"
        />

        <Pagination
          :current-page="productsStore.pagination.currentPage"
          :total-pages="totalPages"
          :total-items="productsStore.filteredProducts.length"
          :start-item="paginationStart"
          :end-item="paginationEnd"
          @page-change="productsStore.setPage"
        />
      </template>
    </div>

    <!-- Add Modal -->
    <Modal
      :show="isAddModalOpen"
      title="Add New Product"
      @close="cancelAdd"
      @submit="addProduct"
    >
      <div class="space-y-3">
        <BaseInput
          v-model="newProduct.name"
          placeholder="Název produktu"
          label="Název"
        />
        <BaseInput
          v-model="newProduct.price"
          type="number"
          placeholder="Cena"
          label="Cena"
        />
        <BaseInput
          v-model="newProduct.description"
          placeholder="Popis produktu"
          label="Popis"
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
            Add Product
          </button>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal
      :show="isEditModalOpen"
      title="Edit Product"
      @close="cancelEdit"
      @submit="updateProduct"
    >
      <div class="space-y-3">
        <BaseInput
          v-model="selectedProduct.name"
          placeholder="Název produktu"
          label="Název"
          variant="success"
        />
        <BaseInput
          v-model="selectedProduct.price"
          type="number"
          placeholder="Cena"
          label="Cena"
          variant="success"
        />
        <BaseInput
          v-model="selectedProduct.description"
          placeholder="Popis produktu"
          label="Popis"
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

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useProductsStore } from '@/stores/products'
import { notify } from '@kyvg/vue3-notification'
import DataTable from '@/components/common/DataTable.vue'
import SearchBar from '@/components/common/SearchBar.vue'
import Pagination from '@/components/common/Pagination.vue'
import Modal from '@/components/common/Modal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import StatusBar from '@/components/common/StatusBar.vue'
import EmptyState from '@/components/common/EmptyState.vue'

defineOptions({
  name: 'ProductsView',
})

const productsStore = useProductsStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)

const newProduct = reactive({
  name: '',
  price: null,
  description: ''
})

const selectedProduct = reactive({
  name: '',
  price: null,
  description: ''
})

const tableHeaders = [
  { field: 'name', label: 'Název', sortable: true },
  { field: 'price', label: 'Cena', sortable: true },
  { field: 'description', label: 'Popis', sortable: false },
  { field: 'actions', label: 'Akce', sortable: false },
]

const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    await productsStore.fetchProducts()
    error.value = ''
  } catch (err) {
    error.value = 'Nepodařilo se načíst data produktů: ' + err.message
  } finally {
    loading.value = false
  }
})

if (productsStore.error) {
  notify({
    type: 'error',
    text: productsStore.error,
    duration: 5000,
    speed: 500
  })
}

const sortBy = (field) => {
  const header = tableHeaders.find((h) => h.field === field)
  if (header?.sortable) {
    productsStore.setSorting(field)
  }
}

const addProduct = async () => {
  await productsStore.addProduct({ ...newProduct })
  Object.assign(newProduct, {
    name: '',
    price: null,
    description: ''
  })
  isAddModalOpen.value = false
}

const openEditModal = (index) => {
  isEditModalOpen.value = true
  selectedProduct.name = productsStore.products[index].name
  selectedProduct.price = productsStore.products[index].price
  selectedProduct.description = productsStore.products[index].description
}

const updateProduct = async () => {
  await productsStore.updateProduct(selectedProduct)
  isEditModalOpen.value = false
}

const cancelEdit = () => {
  isEditModalOpen.value = false
}

const deleteProduct = async (productId) => {
  if (confirm('Opravdu chcete smazat tento produkt?')) {
    await productsStore.deleteProduct(productId)
  }
}

const cancelAdd = () => {
  isAddModalOpen.value = false
  Object.assign(newProduct, {
    name: '',
    price: null,
    description: ''
  })
}

const paginationStart = computed(
  () => (productsStore.pagination.currentPage - 1) * productsStore.pagination.perPage + 1,
)

const paginationEnd = computed(() =>
  Math.min(
    paginationStart.value + productsStore.pagination.perPage - 1,
    productsStore.filteredProducts.length,
  ),
)

const totalPages = computed(() =>
  Math.ceil(productsStore.filteredProducts.length / productsStore.pagination.perPage),
)
</script>
