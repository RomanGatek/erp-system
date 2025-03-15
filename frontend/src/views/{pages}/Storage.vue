<script setup>
import { ref, onMounted } from 'vue'
import { useInventoryStore } from '@/stores/storage.store.js'
import { $reactive } from '@/utils/index.js'
import { useNotifier } from '@/stores/notifier.store.js'
import { useErrorStore } from '@/stores/errors.store.js'
import BaseButton from '@/components/common/BaseButton.vue'

import {
  DataTable,
  SearchBar,
  Pagination,
  Modal,
  StatusBar,
  EmptyState,
  BaseInput,
  SearchSelect,
} from '@/components'

import { useProductsStore } from '@/stores/products.store.js'

defineOptions({
  name: 'StorageView',
})

const inventoryStore = useInventoryStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)
const errors = useErrorStore()
const productStore = useProductsStore()
const $notifier = useNotifier()

const reactiveItem = $reactive({
  id: null,
  product: {
    id: null,
    name: '',
    description: '',
    purchasePrice: 0.0,
    buyoutPrice: 0,
  },
  stockedAmount: null,
})

const tableHeaders = [
  { field: 'product.name', label: 'Product', sortable: true },
  { field: 'price', label: 'Pricing', sortable: true },
  { field: 'description', label: 'Description', sortable: false },
  { field: 'stockedAmount', label: 'Stock', sortable: true, class: 'text-center' },
  { field: 'actions', label: '', sortable: false, class: 'text-right w-[100px]' },
]

const loading = ref(false)

// Methods
const openAddModal = () => {
  reactiveItem.$clear()
  errors.clearServerErrors()
  isAddModalOpen.value = true
}

const addItem = async () => {
  try {
    await inventoryStore.addItem({ ...reactiveItem })
    if (!inventoryStore.error) {
      isAddModalOpen.value = false
      $notifier.success('Item was created successfully!')
      reactiveItem.$clear()
    } else {
      errors.handle(inventoryStore.error)
      if (errors.general) {
        isAddModalOpen.value = false
      }
    }
  } catch (err) {
    errors.handle(err)
    console.error(err)
    reactiveItem.$clear()
    isEditModalOpen.value = false
  }
}

const updateItem = async () => {
  try {
    const itemToUpdate = JSON.parse(JSON.stringify(reactiveItem))
    await inventoryStore.updateItem(itemToUpdate)

    if (!inventoryStore.error) {
      isEditModalOpen.value = false
      $notifier.success('Item was updated successfully!')
      reactiveItem.$clear()
    } else {
      errors.handle(inventoryStore.error)
      if (errors.general) {
        isEditModalOpen.value = false
      }
    }
  } catch (err) {
    errors.handle(err)
    console.error(err)
    reactiveItem.$clear()
    isEditModalOpen.value = false
  }
}

const deleteItem = async (itemId) => {
  if (confirm('Do you really want to delete this item?')) {
    try {
      await inventoryStore.deleteItem(itemId)
      $notifier.success('Item was deleted successfully!')
      reactiveItem.$clear()
    } catch (err) {
      console.error('Error deleting item:', err)
      errors.general = 'Failed to delete item. Please try again later.'
    }
  }
}

const openEditModal = (item) => {
  errors.clearServerErrors()
  reactiveItem.$clear()

  const itemCopy = JSON.parse(JSON.stringify(item))
  reactiveItem.id = itemCopy.id
  reactiveItem.stockedAmount = itemCopy.stockedAmount

  if (itemCopy.product) {
    reactiveItem.product = {
      id: itemCopy.product.id,
      name: itemCopy.product.name,
      description: itemCopy.product.description,
      purchasePrice: itemCopy.product.purchasePrice,
      buyoutPrice: itemCopy.product.buyoutPrice,
    }
  }

  isEditModalOpen.value = true
}

const cancelEdit = () => {
  errors.clearServerErrors()
  isEditModalOpen.value = false
  reactiveItem.$clear()
}

const cancelAdd = () => {
  errors.clearServerErrors()
  reactiveItem.$clear()
  isAddModalOpen.value = false
}

// Lifecycle
onMounted(async () => {
  errors.clearServerErrors()
  loading.value = true
  try {
    if (productStore.items.length === 0) {
      await productStore.fetchProducts()
    }
    await inventoryStore.fetchItems()

    if (inventoryStore.error) {
      errors.handle(inventoryStore.error)
    }
    if (productStore.error) {
      errors.handle(productStore.error)
    }
  } catch (err) {
    errors.handle(err)
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="w-full h-full">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 overflow-hidden">
        <!-- Header Section -->
        <div class="border-b border-gray-100">
          <div class="p-6">
            <StatusBar :error="errors.general" :loading="loading" class="mb-4"
              @clear-error="errors.clearServerErrors()" />

            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
              <div>
                <h2 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
                  Storage</h2>
                <p class="mt-1 text-sm text-gray-600">Manage your inventory</p>
              </div>

              <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
                <SearchBar v-model="searchInput" @update:modelValue="inventoryStore.setSearch($event)"
                  class="min-w-[250px]" />
                <BaseButton type="primary" class="text-sm! flex items-center justify-center gap-2 px-4 py-2!"
                  @click="openAddModal">
                  <span>Add Item</span>
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                </BaseButton>
              </div>
            </div>
          </div>
        </div>

        <!-- Content Section -->
        <div class="p-6">
          <!-- Empty state -->
          <EmptyState v-if="!inventoryStore.paginateItems.length" message="No items to display"
            class="bg-gray-50/50 rounded-xl py-12">
            <template #icon>
              <svg class="w-12 h-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
            </template>
            <template #message>
              <h3 class="mt-4 text-lg font-medium text-gray-900">No items in storage</h3>
              <p class="mt-1 text-gray-500">Start by adding your first inventory item</p>
            </template>
          </EmptyState>

          <!-- Data table -->
          <template v-else>
            <div class="relative rounded-xl overflow-hidden">
              <div
                class="max-h-[calc(100vh-20rem)] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300 hover:scrollbar-thumb-gray-400 scrollbar-track-transparent">
                <DataTable :headers="tableHeaders" :items="inventoryStore.paginateItems"
                  :sort-by="inventoryStore.setSorting" :sorting="inventoryStore.sorting" :on-edit="openEditModal"
                  :on-delete="deleteItem">
                  <template #row="{ item }">
                    <td class="px-4 py-3 whitespace-nowrap">
                      <div class="flex items-center">
                        <div
                          class="w-10 h-10 rounded-xl bg-gradient-to-br from-gray-100 to-gray-50 flex items-center justify-center ring-1 ring-gray-200">
                          <svg class="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                          </svg>
                        </div>
                        <div class="ml-3">
                          <div class="text-sm font-medium text-gray-900">{{ item.product.name }}</div>
                          <div class="text-xs text-gray-500 line-clamp-1">ID: #{{ item.id }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3 whitespace-nowrap">
                      <div class="space-y-1">
                        <div class="flex items-center gap-1.5">
                          <span class="text-xs text-gray-500">Sell:</span>
                          <span
                            class="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-medium bg-green-50 text-green-700 ring-1 ring-inset ring-green-600/20">
                            {{ item.product.buyoutPrice }} Kč
                          </span>
                        </div>
                        <div class="flex items-center gap-1.5">
                          <span class="text-xs text-gray-500">Buy:</span>
                          <span
                            class="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-medium bg-red-50 text-red-700 ring-1 ring-inset ring-red-600/20">
                            {{ item.product.purchasePrice }} Kč
                          </span>
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3">
                      <p class="text-sm text-gray-600 line-clamp-2">{{ item.product.description || 'No description' }}
                      </p>
                    </td>
                    <td class="px-4 py-3 whitespace-nowrap text-center">
                      <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium" :class="{
                        'bg-red-50 text-red-700 ring-1 ring-inset ring-red-600/20': item.stockedAmount === 0,
                        'bg-yellow-50 text-yellow-700 ring-1 ring-inset ring-yellow-600/20': item.stockedAmount > 0 && item.stockedAmount < 10,
                        'bg-green-50 text-green-700 ring-1 ring-inset ring-green-600/20': item.stockedAmount >= 10
                      }">
                        {{ item.stockedAmount || 0 }} units
                      </span>
                    </td>
                    <td class="px-4 py-3 whitespace-nowrap text-right">
                      <div class="flex justify-end items-center gap-2">
                        <button @click="openEditModal(item)"
                          class="p-1.5 text-blue-600 hover:text-blue-900 rounded-lg hover:bg-blue-50/80 transition-colors"
                          title="Edit Item">
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                          </svg>
                        </button>
                        <button @click="deleteItem(item.id)"
                          class="p-1.5 text-red-600 hover:text-red-900 rounded-lg hover:bg-red-50/80 transition-colors"
                          title="Delete Item">
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

            <div class="mt-6">
              <Pagination :store="useInventoryStore" />
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- Add Modal -->
    <Modal :show="isAddModalOpen" title="Add New Item" @close="cancelAdd">
      <div class="space-y-4">
        <SearchSelect :items="productStore.items" v-model="reactiveItem.product" by="name" label="Product"
          placeholder="Search product..." />

        <BaseInput v-model="reactiveItem.stockedAmount" type="number" label="Stocked Amount"
          placeholder="Enter amount..." min="0" />

        <div class="flex justify-end items-center gap-3 pt-4">
          <BaseButton type="error" class="text-sm!" @click="cancelAdd">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! bg-gradient-to-r from-blue-600 to-blue-400!" @click="addItem">
            Add Item
          </BaseButton>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal :show="isEditModalOpen" title="Edit Item" @close="cancelEdit">
      <div class="space-y-4">
        <SearchSelect :items="productStore.items" v-model="reactiveItem.product" by="name" label="Product"
          placeholder="Search product..." />

        <BaseInput v-model="reactiveItem.stockedAmount" type="number" label="Stocked Amount"
          placeholder="Enter amount..." min="0" />

        <div class="flex justify-end items-center gap-3 pt-4">
          <BaseButton type="error" class="text-sm!" @click="cancelEdit">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! bg-gradient-to-r from-blue-600 to-blue-400!" @click="updateItem">
            Save Changes
          </BaseButton>
        </div>
      </div>
    </Modal>
  </div>
</template>
