<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <StatusBar :error="errorStore.errors.general" :loading="loading" />

      <!-- Header overlay -->
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Products</h2>
        <div class="flex items-center space-x-4">
          <SearchBar v-model="searchInput" @update:modelValue="productsStore.setSearch($event)" />
          <button @click="isAddModalOpen = true"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-sm transition flex items-center">
            <span class="mr-2">Add Product</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd"
                d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                clip-rule="evenodd" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Empty state -->
      <EmptyState v-if="!productsStore.paginateItems.length" message="No products to display" />

      <!-- Data table -->
      <template v-else>
        <div class="max-h-[500px] overflow-y-auto">
          <DataTable
            :headers="tableHeaders"
            :items="productsStore.paginateItems"
            :sort-by="productsStore.setSorting"
            :sorting="productsStore.sorting"
            :on-edit="openEditModal"
            :on-delete="deleteProduct"
          >
            <template #row="{ item }">
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.name }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.price }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.description }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="openEditModal(item)"
                  class="text-blue-600 hover:text-blue-900 mr-4 p-1 rounded hover:bg-blue-50 cursor-pointer">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                  </svg>
                </button>
                <button @click="deleteProduct(item.id)"
                  class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 cursor-pointer">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </td>
            </template>
          </DataTable>
        </div>

        <Pagination :store="useProductsStore" />
      </template>
    </div>

    <!-- Add Modal -->
    <Modal :show="isAddModalOpen" title="Add New Product" @close="cancelAdd" @submit="addProduct">
      <div class="space-y-3">
        <BaseInput :error="errorStore.errors.name" v-model="reativeProduct.name" placeholder="Product name" label="Name" />
        <BaseInput v-model="reativeProduct.price" :error="serverErrors.price" type="number" placeholder="Price"
          label="Price" />
        <BaseInput v-model="reativeProduct.description" :error="serverErrors.description" placeholder="Product description"
          label="Product description" />
        <div class="flex justify-end space-x-3 pt-2">
          <button type="button" @click="cancelAdd"
            class="px-3 py-1.5 text-sm border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition">
            Cancel
          </button>
          <button type="submit"
            class="px-4 py-1.5 text-sm bg-blue-500 hover:bg-blue-600 text-white rounded-lg shadow-sm transition">
            Add Product
          </button>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal :show="isEditModalOpen" title="Edit Product" @close="cancelEdit" @submit="updateProduct">
      <div class="space-y-3">
        <BaseInput v-model="reativeProduct.name" :error="serverErrors.name" placeholder="Product name" label="Name"
          variant="success" />
        <BaseInput v-model="reativeProduct.price" :error="serverErrors.price" type="number" placeholder="Price"
          label="Price" variant="success" />
        <BaseInput v-model="reativeProduct.description" :error="serverErrors.description"
          placeholder="Product description" label="Product description" variant="success" />
        <div class="flex justify-between pt-2">
          <button type="submit"
            class="px-4 py-1.5 text-sm bg-green-500 hover:bg-green-600 text-white rounded-lg shadow-sm transition">
            Update
          </button>
          <button type="button" @click="cancelEdit"
            class="px-4 py-1.5 text-sm bg-gray-500 hover:bg-gray-600 text-white rounded-lg shadow-sm transition">
            Cancel
          </button>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useProductsStore } from '@/stores/products'
import { $reactive } from '@/utils/index.js'

import {
  DataTable,
  SearchBar,
  Pagination,
  Modal,
  StatusBar,
  EmptyState,
  BaseInput
} from '@/components/common'
import { useErrorStore } from '@/stores/errors.js'


defineOptions({
  name: 'ProductsView',
})

const productsStore = useProductsStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)
const errorStore = useErrorStore()

const reativeProduct = $reactive({
  id: undefined,
  name: '',
  price: null,
  description: ''
})

const tableHeaders = [
  { field: 'name', label: 'Name', sortable: true },
  { field: 'price', label: 'Price', sortable: true },
  { field: 'description', label: 'Description', sortable: false },
  { field: 'actions', label: '', sortable: false, class: 'text-right' },
]

const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    await productsStore.fetchProducts()
    if (productsStore.error) {
      errorStore.handle(productsStore.error)
    }
  } catch (err) {
    errorStore.handle(err)
    console.log(err)
  } finally {
    loading.value = false
  }
})

const addProduct = async () => {
  loading.value = true
  try {
    await productsStore.addProduct({ ...reativeProduct })
    if (productsStore.error) {
      errorStore.handle(productsStore.error)
      if (error.value) isAddModalOpen.value = false
    } else {
      reativeProduct.$clear();
      isAddModalOpen.value = false
    }
  } catch (e) {
    errorStore.handle(e)
  } finally {
    loading.value = false
  }
}



const deleteProduct = async (productId) => {
  if (confirm('Do you really want to delete this product?')) {
    loading.value = true
    await productsStore.deleteProduct(productId)
    if (productsStore.error) {
      errorStore.handle(productsStore.error)
    } else {
      isEditModalOpen.value = false
    }
    loading.value = false
  }
}
const updateProduct = async () => {
  loading.value = true
  await productsStore.updateProduct(reativeProduct.id, {
    name: reativeProduct.name,
    price: reativeProduct.price,
    description: reativeProduct.description
  })
  if (productsStore.error) {
    errorStore.handle(productsStore.error)
  } else {
    isEditModalOpen.value = false
  }
  loading.value = false
}


const openEditModal = (product) => {
  isEditModalOpen.value = true
  reativeProduct.$assign(product)
}
const cancelEdit = () => {
  isEditModalOpen.value = false
  reativeProduct.$clear();
}
const cancelAdd = () => {
  isAddModalOpen.value = false
  console.log(reativeProduct)
  reativeProduct.$clear();
  console.log(reativeProduct)
}
</script>
