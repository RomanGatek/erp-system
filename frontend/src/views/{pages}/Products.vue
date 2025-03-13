<script setup>
import { ref, onMounted, watch } from 'vue'
import { $reactive, choosedColor } from '@/utils/index.js'
import { useNotifier, useProductsStore, useCategoriesStore } from '@/stores'

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
import { useErrorStore } from '@/stores/errors.store.js'

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
  { field: 'name', label: 'Name', sortable: true },
  { field: 'buyoutPrice', label: 'Buy / Sell', sortable: true },
  { field: 'description', label: 'Description', sortable: false },
  { field: 'productCategory', label: 'Category', sortable: false, class: 'text-right' },
  { field: 'actions', label: '', sortable: false, class: 'text-right' },
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
import BaseButton from '@/components/common/BaseButton.vue'
</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <StatusBar :error="errors.general" :loading="loading" class="mb-4" @clear-error="errors.clearServerErrors()" />

      <!-- Header overlay -->
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Products</h2>
        <div class="flex items-center space-x-4">
          <SearchBar v-model="searchInput" @update:modelValue="productsStore.setSearch($event)" />
          <BaseButton type="primary" class="text-sm! flex!" @click="isAddModalOpen = true">
            <span class="mr-2">Add</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd"
                d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                clip-rule="evenodd" />
            </svg>
          </BaseButton>
        </div>
      </div>

      <!-- Empty state -->
      <EmptyState v-if="!productsStore.paginateItems.length" message="No products to display" />

      <!-- Data table -->
      <template v-else>
        <div class="flex-1 overflow-hidden">
          <DataTable :headers="tableHeaders" :items="productsStore.paginateItems" :sort-by="productsStore.setSorting"
            :sorting="productsStore.sorting" :on-edit="openEditModal" :on-delete="deleteProduct">
            <template #row="{ item }">
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.name }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                <p
                  class="inline-flex items-center rounded-md bg-green-50 px-2 py-1 text-xs font-medium text-green-600 ring-1 ring-green-500/10 ring-inset">
                  {{ item.buyoutPrice }}
                </p>
                /
                <p
                  class="inline-flex items-center rounded-md bg-red-50 px-2 py-1 text-xs font-medium text-red-600 ring-1 ring-red-500/10 ring-inset">
                  {{ item.purchasePrice }}
                </p>
              </td>
              <td class="px-6 py-4 whitespace-normal text-gray-600 line-clamp-2">
                {{ item.description }}
              </td>

              <td class="px-6 py-4 whitespace-nowrap text text-gray-600">
                <span :class="choosedColor(item.productCategory)"
                  class="inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-gray-500/10 ring-inset">{{
                    item?.productCategory?.name ?? 'no category yet' }}</span>
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
    <Modal :show="isAddModalOpen" title="Add New Product" @close="cancelAdd">
      <div class="space-y-3">
        <BaseInput :error="errors.name" v-model="reactiveProduct.name" placeholder="Product name" label="Name" />
        <BaseInput v-model="reactiveProduct.buyoutPrice" :error="errors.buyoutPrice" type="number"
          placeholder="Buyout Price" label="Buyout Price" />
        <BaseInput v-model="reactiveProduct.purchasePrice" :error="errors.purchasePrice" type="number"
          placeholder="Purchase Price" label="Purchase Price" />
        <BaseInput v-model="reactiveProduct.description" :error="errors.description" placeholder="Product description"
          label="Product description" />
        <SearchSelect :items="caregoriesStore.items" v-model="reactiveProduct.productCategory"
          :error="errors.productCategory" by="name" label="Category" returnField="id"
          placeholder="Search category..." />

        <div class="flex justify-end space-x-3 pt-2">
          <BaseButton type="error" class="text-sm! font-bold flex!" @click="cancelAdd">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! font-bold flex!" @click="addProduct">
            Add Products
          </BaseButton>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal :show="isEditModalOpen" title="Edit Product" @close="cancelEdit">
      <div class="space-y-3">
        <BaseInput v-model="reactiveProduct.name" :error="errors.name" placeholder="Product name" label="Name"
          variant="success" />
        <BaseInput v-model="reactiveProduct.buyoutPrice" :error="errors.buyoutPrice" type="number"
          placeholder="Buyout Price" label="Buyout Price" variant="success" />
        <BaseInput v-model="reactiveProduct.purchasePrice" :error="errors.purchasePrice" type="number"
          placeholder="Purchase Price" label="Purchase Price" variant="success" />
        <BaseInput v-model="reactiveProduct.description" :error="errors.description" placeholder="Product description"
          label="Product description" variant="success" />
        <SearchSelect :items="caregoriesStore.items" v-model="reactiveProduct.productCategory"
          :error="errors.productCategory" by="name" label="Category" returnField="id"
          placeholder="Search category..." />
        <div class="flex justify-end space-x-3 pt-2">
          <BaseButton type="error" class="text-sm! font-bold flex!" @click="cancelEdit">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! font-bold flex!" @click="updateProduct">
            Edit Product
          </BaseButton>
        </div>
      </div>
    </Modal>
  </div>
</template>
