<script setup>
import { ref, onMounted } from 'vue'
import { useInventoryStore } from '@/stores/storage.js'
import { $reactive } from '@/utils/index.js'
import { useNotifier } from '@/stores/notifier.js'
import { useErrorStore } from '@/stores/errors.js'

import {
  DataTable,
  SearchBar,
  Pagination,
  Modal,
  StatusBar,
  EmptyState,
  BaseInput,
} from '@/components/common'
import SearchSelect from '@/components/common/SearchSelect.vue'
import { useProductsStore } from '@/stores/products.js'


defineOptions({
  name: 'StorageView',
})

const inventoryStore = useInventoryStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)
const errorStore = useErrorStore()
const productStore = useProductsStore()

const reactiveItem = $reactive({
  product: {
    name: '',
    description: '',
    price: 0.0
  },
  quantity: 0
})

const tableHeaders = [
  {
    field: 'product.name',
    label: 'Product Name',
    sortable: true
  },
  {
    field: 'price',
    label: 'Price',
    sortable: true
  },
  {
    field: 'description',
    label: 'Product Description',
    sortable: true
  },
  {
    field: 'quantity',
    label: 'Quantity',
    sortable: true
  },
  {
    field: 'actions',
    label: '',
    sortable: false,
    class: 'text-right'
  },
]

const loading = ref(false)
const $notifier = useNotifier()

onMounted(async() => {
  errorStore.clearServerErrors()
  loading.value = true
  try {
    if (productStore.items.length === 0) {
      await productStore.fetchProducts()
    }
    await inventoryStore.fetchItems()
    if (inventoryStore.error) {
      errorStore.handle(inventoryStore.error)
    }
    if (productStore.error) {
      errorStore.handle(productStore.error)
    }
  } catch (err) {
    errorStore.handle(err)
    loading.value = false
    console.error(err)
  } finally {
    loading.value = false
  }
})
const addItem = async() => {
  try {
    await inventoryStore.addItem({ ...reactiveItem })
    if (!inventoryStore.error) {
      isAddModalOpen.value = false
      $notifier.success("Item was created successfully!")
      reactiveItem.$clear()
    } else {
      errorStore.handle(inventoryStore.error)
      if (errorStore.errors.general) {
        isAddModalOpen.value = false
      }
    }
  } catch (err) {
    errorStore.handle(err)
    console.error(err)
    reactiveItem.$clear()
    isEditModalOpen.value = false
  }
}
const updateItem = async() => {
  try {
    await inventoryStore.updateItem(reactiveItem)
    if (!inventoryStore.error) {
      isAddModalOpen.value = false
      $notifier.success("Item was updated successfully!")
      reactiveItem.$clear()
    } else {
      errorStore.handle(inventoryStore.error)
      if (errorStore.errors.general) {
        isAddModalOpen.value = false
      }
    }
  } catch (err) {
    errorStore.handle(err)
    console.error(err)
    reactiveItem.$clear()
    isEditModalOpen.value = false
  }
  isEditModalOpen.value = false
}
const deleteItem = async(itemId) => {
  if (confirm('Do you really want to delete this item?')) {
    try {
      await inventoryStore.deleteItem(itemId)
      $notifier.success("Item deleted removed successfully!")
      reactiveItem.$clear();
    } catch (err) {
      console.error('Error deleting item:', err)
      errorStore.errors.general = 'Failed to delete item. Please try again later.'
    }
  }
}

const openEditModal = (item) => {
  errorStore.clearServerErrors();
  reactiveItem.$clear();
  isEditModalOpen.value = true;

  reactiveItem.$assign(item);
  reactiveItem.product = item.product; // Set the selected product for the SearchSelect component

  console.log(reactiveItem);
};

const cancelEdit = () => {
  errorStore.clearServerErrors()
  isEditModalOpen.value = false
  reactiveItem.$clear();
}

const cancelAdd = () => {
  errorStore.clearServerErrors()
  reactiveItem.$clear()
  isAddModalOpen.value = false;
}
</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <StatusBar
        :error="errorStore.errors.general"
        :loading="loading"
        class="mb-4"
        @clear-error="errorStore.clearServerErrors()"
      />

      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Storage</h2>
        <div class="flex items-center space-x-4">
          <SearchBar v-model="searchInput" @update:modelValue="inventoryStore.setSearch($event)"/>
          <button @click="reactiveItem.$clear();
                  errorStore.clearServerErrors();
                  isAddModalOpen = true"
                  class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-sm transition flex items-center">
            <span class="mr-2">Add Item</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd"
                    d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                    clip-rule="evenodd"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Empty state -->
      <EmptyState v-if="!inventoryStore.paginateItems.length" message="Žádné itemy k zobrazení"/>

      <!-- Data table -->
      <template v-else>
        <div class="max-h-[500px] overflow-y-auto">
          <DataTable
            :headers="tableHeaders"
            :items="inventoryStore.paginateItems"
            :sort-by="inventoryStore.setSorting"
            :sorting="inventoryStore.sorting"
            :on-edit="openEditModal"
            :on-delete="deleteItem"
          >
            <template #row=" { item }">
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.product.name }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.product.price }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.product.description }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.quantity }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="openEditModal(item)"
                        class="text-blue-600 hover:text-blue-900 mr-4 p-1 rounded hover:bg-blue-50 cursor-pointer">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/>
                  </svg>
                </button>
                <button @click="deleteItem(item.id)"
                        class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 cursor-pointer">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </td>
            </template>
          </DataTable>

          <Pagination :store="useInventoryStore"/>
        </div>
      </template>
    </div>

    <!-- Add Modal -->
    <Modal :show="isAddModalOpen" title="Add New Item" @close="cancelAdd" @submit="addItem">
      <div class="space-y-3">
        <SearchSelect :items="productStore.items" v-model="reactiveItem.product" by="name" label="Product"
                      placeholder="Search product.."/>
        <BaseInput v-model="reactiveItem.quantity" placeholder="10" label="Quantity"/>
        <div class="flex justify-end space-x-3 pt-2">
          <button type="button" @click="cancelAdd"
                  class="px-3 py-1.5 text-sm border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition">
            Cancel
          </button>
          <button type="submit"
                  class="px-4 py-1.5 text-sm bg-blue-500 hover:bg-blue-600 text-white rounded-lg shadow-sm transition">
            Add Item
          </button>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal :show="isEditModalOpen" title="Edit Item" @close="cancelEdit" @submit="updateItem">
      <div class="space-y-3">
        <SearchSelect :items="productStore.items" v-model="reactiveItem.product" by="name" label="Product"
                      placeholder="Search product.."/>
        <BaseInput v-model="reactiveItem.quantity" placeholder="10" label="Quantity"/>
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
