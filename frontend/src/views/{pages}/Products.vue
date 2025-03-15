<script setup>
import { ref, onMounted, watch } from 'vue'
import { $reactive, choosedColor } from '@/utils/index.js'
import { useNotifier, useProductsStore, useCategoriesStore } from '@/stores'
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

defineOptions({
  name: 'ProductsView',
})

const productsStore = useProductsStore()
const searchInput = ref('')
const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)
const errors = useErrorStore()
const $notifier = useNotifier()
const caregoriesStore = useCategoriesStore()

const reactiveProduct = $reactive({
  id: undefined,
  name: '',
  buyoutPrice: null,
  purchasePrice: null,
  description: '',
  productCategory: null,
})

errors.validateField(reactiveProduct.$cleaned())

const tableHeaders = [
  { field: 'name', label: 'Product', sortable: true },
  { field: 'buyoutPrice', label: 'Pricing', sortable: true },
  { field: 'description', label: 'Description', sortable: false },
  { field: 'productCategory', label: 'Category', sortable: false, class: 'text-right' },
  { field: 'actions', label: '', sortable: false, class: 'text-right w-[100px]' },
]

const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    await productsStore.fetchProducts()
    await caregoriesStore.fetchCategories()
    if (productsStore.error) errors.handle(productsStore.error)
  } catch (err) {
    errors.handle(err)
    console.log(err)
  } finally {
    loading.value = false
  }
})

const addProduct = async () => {
  loading.value = true
  try {
    await productsStore.addProduct(reactiveProduct.$cleaned())
    if (productsStore.error) {
      errors.handle(productsStore.error)
      if (errors.general) isAddModalOpen.value = false
    } else {
      reactiveProduct.$clear()
      isAddModalOpen.value = false
      $notifier.success('Product was created successfully!')
    }
  } catch (e) {
    errors.handle(e)
  } finally {
    loading.value = false
  }
}

const deleteProduct = async (productId) => {
  if (confirm('Do you really want to delete this product?')) {
    await productsStore.deleteProduct(productId)
    if (productsStore.error) {
      errors.handle(productsStore.error)
    } else {
      reactiveProduct.$clear()
      isEditModalOpen.value = false
      $notifier.success('Product was removed successfully!')
    }
  }
}

const updateProduct = async () => {
  await productsStore.updateProduct(reactiveProduct.id, {
    name: reactiveProduct.name,
    buyoutPrice: reactiveProduct.buyoutPrice,
    purchasePrice: reactiveProduct.purchasePrice,
    description: reactiveProduct.description,
    productCategory: reactiveProduct.productCategory.name,
  })
  if (productsStore.error) {
    errors.handle(productsStore.error)
    if (errors.general) {
      isEditModalOpen.value = false
    }
  } else {
    isEditModalOpen.value = false
    reactiveProduct.$clear()
    $notifier.success('Product was updated successfully!')
  }
}

const openEditModal = (product) => {
  isEditModalOpen.value = true
  reactiveProduct.$assign(product)
  errors.clearServerErrors()
}

const cancelEdit = () => {
  isEditModalOpen.value = false
  reactiveProduct.$clear()
  errors.clearServerErrors()
}

const cancelAdd = () => {
  isAddModalOpen.value = false
  reactiveProduct.$clear()
  errors.clearServerErrors()
}

watch(
  [
    () => reactiveProduct.name,
    () => reactiveProduct.purchasePrice,
    () => reactiveProduct.description,
    () => reactiveProduct.buyoutPrice,
  ],
  (
    [newName, newPurchasePrice, newDescription, newBuyoutPrice],
    [oldName, oldPurchasePrice, oldDescription, oldBuyoutPrice],
  ) => {
    if (newName !== oldName) {
      errors.clear('name')
    }
    if (newPurchasePrice !== oldPurchasePrice) {
      errors.clear('purchasePrice')
    }
    if (newDescription !== oldDescription) {
      errors.clear('description')
    }
    if (newBuyoutPrice !== oldBuyoutPrice) {
      errors.clear('buyoutPrice')
    }
  },
)
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
                  Products</h2>
                <p class="mt-1 text-sm text-gray-600">Manage your product catalog</p>
              </div>

              <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
                <SearchBar v-model="searchInput" @update:modelValue="productsStore.setSearch($event)"
                  class="min-w-[250px]" />
                <BaseButton type="primary" class="text-sm! flex items-center justify-center gap-2 px-4 py-2!"
                  @click="isAddModalOpen = true">
                  <span>Add Product</span>
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
          <EmptyState v-if="!productsStore.paginateItems.length" message="No products to display"
            class="bg-gray-50/50 rounded-xl py-12">
            <template #icon>
              <svg class="w-12 h-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
            </template>
            <template #message>
              <h3 class="mt-4 text-lg font-medium text-gray-900">No products found</h3>
              <p class="mt-1 text-gray-500">Start by adding your first product</p>
            </template>
          </EmptyState>

          <!-- Data table -->
          <template v-else>
            <div class="relative rounded-xl overflow-hidden">
              <div
                class="max-h-[calc(100vh-20rem)] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300 hover:scrollbar-thumb-gray-400 scrollbar-track-transparent">
                <DataTable :headers="tableHeaders" :items="productsStore.paginateItems"
                  :sort-by="productsStore.setSorting" :sorting="productsStore.sorting" :on-edit="openEditModal"
                  :on-delete="deleteProduct">
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
                          <div class="text-sm font-medium text-gray-900">{{ item.name }}</div>
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
                            {{ item.buyoutPrice }} Kč
                          </span>
                        </div>
                        <div class="flex items-center gap-1.5">
                          <span class="text-xs text-gray-500">Buy:</span>
                          <span
                            class="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-medium bg-red-50 text-red-700 ring-1 ring-inset ring-red-600/20">
                            {{ item.purchasePrice }} Kč
                          </span>
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3">
                      <p class="text-sm text-gray-600 line-clamp-2">{{ item.description || 'No description' }}</p>
                    </td>
                    <td class="px-4 py-3 whitespace-nowrap text-right">
                      <span :class="[
                        choosedColor(item.productCategory),
                        'inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset'
                      ]">
                        {{ item?.productCategory?.name ?? 'No category' }}
                      </span>
                    </td>
                    <td class="px-4 py-3 whitespace-nowrap text-right">
                      <div class="flex justify-end items-center gap-2">
                        <button @click="openEditModal(item)"
                          class="p-1.5 text-blue-600 hover:text-blue-900 rounded-lg hover:bg-blue-50/80 transition-colors"
                          title="Edit Product">
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                          </svg>
                        </button>
                        <button @click="deleteProduct(item.id)"
                          class="p-1.5 text-red-600 hover:text-red-900 rounded-lg hover:bg-red-50/80 transition-colors"
                          title="Delete Product">
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
              <Pagination :store="useProductsStore" />
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- Add Modal -->
    <Modal :show="isAddModalOpen" title="Add New Product" @close="cancelAdd">
      <div class="space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <BaseInput v-model="reactiveProduct.name" :error="errors.name" placeholder="Product name" label="Name" />
          <SearchSelect :items="caregoriesStore.items" v-model="reactiveProduct.productCategory"
            :error="errors.productCategory" by="name" label="Category" returnField="id"
            placeholder="Search category..." />
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <BaseInput v-model="reactiveProduct.buyoutPrice" :error="errors.buyoutPrice" type="number"
            placeholder="Buyout Price" label="Sell Price" />
          <BaseInput v-model="reactiveProduct.purchasePrice" :error="errors.purchasePrice" type="number"
            placeholder="Purchase Price" label="Buy Price" />
        </div>

        <BaseInput v-model="reactiveProduct.description" :error="errors.description" type="textarea"
          placeholder="Enter product description..." label="Description" :rows="3" />

        <div class="flex justify-end items-center gap-3 pt-4">
          <BaseButton type="error" class="text-sm!" @click="cancelAdd">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! bg-gradient-to-r from-blue-600 to-blue-400!" @click="addProduct">
            Add Product
          </BaseButton>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal :show="isEditModalOpen" title="Edit Product" @close="cancelEdit">
      <div class="space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <BaseInput v-model="reactiveProduct.name" :error="errors.name" placeholder="Product name" label="Name" />
          <SearchSelect :items="caregoriesStore.items" v-model="reactiveProduct.productCategory"
            :error="errors.productCategory" by="name" label="Category" returnField="id"
            placeholder="Search category..." />
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <BaseInput v-model="reactiveProduct.buyoutPrice" :error="errors.buyoutPrice" type="number"
            placeholder="Buyout Price" label="Sell Price" />
          <BaseInput v-model="reactiveProduct.purchasePrice" :error="errors.purchasePrice" type="number"
            placeholder="Purchase Price" label="Buy Price" />
        </div>

        <BaseInput v-model="reactiveProduct.description" :error="errors.description" type="textarea"
          placeholder="Enter product description..." label="Description" :rows="3" />

        <div class="flex justify-end items-center gap-3 pt-4">
          <BaseButton type="error" class="text-sm!" @click="cancelEdit">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! bg-gradient-to-r from-blue-600 to-blue-400!" @click="updateProduct">
            Save Changes
          </BaseButton>
        </div>
      </div>
    </Modal>
  </div>
</template>
