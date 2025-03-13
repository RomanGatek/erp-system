<script setup>
import { ref, onMounted, watch, computed } from 'vue'
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
  ColorPicker,
} from '@/components'
import { useErrorStore } from '@/stores/errors.store.js'

defineOptions({
  name: 'ProductsView',
})

const searchInput = ref('')
const errorStore = useErrorStore()
const $notifier = useNotifier()
const categoriesStore = useCategoriesStore()

const isAddModalOpen = ref(false)
const isEditModalOpen = ref(false)

const reactiveCategory = $reactive({
  id: undefined,
  name: '',
  description: '',
  color: '',
})

const tableHeaders = [
  { field: 'id', label: 'ID', sortable: true },
  { field: 'name', label: 'Name', sortable: true },
  { field: 'description', label: 'Description', sortable: false, class: 'text-right' },
  { field: 'actions', label: '', sortable: false, class: 'text-right' },
]

const loading = ref(false)

const $actions = computed(() => {
  return {
    fetchCategories: async () => {
      loading.value = true
      try {
        await categoriesStore.fetchCategories()
      } catch (error) {
        $notifier.error('An error occurred while fetching categories')
      } finally {
        loading.value = false
      }
    },
    add: async () => {
      loading.value = true
      try {
        await categoriesStore.addCategory(reactiveCategory.$cleaned())
        $notifier.success('Category added successfully')
        isAddModalOpen.value = false
        reactiveCategory.$clear()
      } catch (error) {
        errorStore.handle(error)
      } finally {
        loading.value = false
      }
    },
    openEditModal: (category) => {
      reactiveCategory.$assign(category)
      isEditModalOpen.value = true
    },
    cancelAdd: () => {
      isAddModalOpen.value = false
      reactiveCategory.$clear()
      errorStore.clearServerErrors()
    },
    cancelEdit: () => {
      isEditModalOpen.value = false
      reactiveCategory.$clear()
      errorStore.clearServerErrors()
    },
    updateCategory: async () => {
      loading.value = true
      try {
        await categoriesStore.updateCategory(reactiveCategory.id, reactiveCategory.$cleaned())
        $notifier.success('Category updated successfully')
        isEditModalOpen.value = false
        reactiveCategory.$clear()
      } catch (error) {
        errorStore.handle(error)
      } finally {
        loading.value = false
      }
    },
    deleteCategory: async (id) => {
      if (confirm('Are you sure you want to delete this category?')) {
        loading.value = true
        try {
          await categoriesStore.deleteCategory(id)
          if (categoriesStore.error) {
            errorStore.handle(categoriesStore.error)
          } else {
            $notifier.success('Category deleted successfully')
          }
        } catch (error) {
          console.log(error)
          $notifier.error('An error occurred while deleting the category')
        } finally {
          loading.value = false
        }
      }
    },
  }
})

onMounted(async () => {
  $actions.value.fetchCategories()
})
</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <StatusBar
        :error="errorStore.errors.general"
        :loading="loading"
        class="mb-4"
        @clear-error="errorStore.clearServerErrors()"
      />

      <!-- Header overlay -->
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Categories</h2>
        <div class="flex items-center space-x-4">
          <SearchBar v-model="searchInput" @update:modelValue="categoriesStore.setSearch($event)" />
          <button
            @click="isAddModalOpen = true"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-sm transition flex items-center"
          >
            <span class="mr-2">Add</span>
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

      <!-- Empty state -->
      <EmptyState v-if="!categoriesStore.paginateItems.length" message="No products to display" />

      <!-- Data table -->
      <template v-else>
        <div class="flex-1 overflow-hidden">
          <DataTable
            :headers="tableHeaders"
            :items="categoriesStore.paginateItems"
            :sort-by="categoriesStore.setSorting"
            :sorting="categoriesStore.sorting"
            :on-edit="$actions.openEditModal"
            :on-delete="$actions.deleteCategory"
          >
            <template #row="{ item }">
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                <p
                  :class="choosedColor(item)"
                  class="inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-gray-500/10 ring-inset"
                >
                  {{ item.name }}
                </p>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.description }}
              </td>

              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                  @click="$actions.openEditModal(item)"
                  class="text-blue-600 hover:text-blue-900 mr-4 p-1 rounded hover:bg-blue-50 cursor-pointer"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                    />
                  </svg>
                </button>
                <button
                  @click="$actions.deleteCategory(item.id)"
                  class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 cursor-pointer"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </button>
              </td>
            </template>
          </DataTable>
        </div>

        <Pagination :store="useCategoriesStore" />
      </template>
    </div>

    <!-- Add Modal -->
    <Modal
      :show="isAddModalOpen"
      title="Add New Category"
      @close="$actions.cancelAdd"
      @submit="$actions.add"
    >
      <div class="space-y-3">
        <BaseInput
          :error="errorStore.errors.name"
          v-model="reactiveCategory.name"
          placeholder="Category name"
          label="Name"
        />
        <BaseInput
          :error="errorStore.errors.description"
          v-model="reactiveCategory.description"
          placeholder="Category description"
          label="Description"
        />
        <ColorPicker
          :error="errorStore.errors.color"
          v-model="reactiveCategory.color"
          label="Color"
        />

        <div class="flex justify-end space-x-3 pt-2">
          <button
            type="button"
            @click="$actions.cancelAdd"
            class="px-3 py-1.5 text-sm border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="px-4 py-1.5 text-sm bg-blue-500 hover:bg-blue-600 text-white rounded-lg shadow-sm transition"
          >
            Add
          </button>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal
      :show="isEditModalOpen"
      title="Edit Category"
      @close="$actions.cancelEdit"
      @submit="$actions.updateCategory"
    >
      <div class="space-y-3">
        <BaseInput
          :error="errorStore.errors.name"
          v-model="reactiveCategory.name"
          placeholder="Category name"
          label="Name"
        />
        <BaseInput
          :error="errorStore.errors.description"
          v-model="reactiveCategory.description"
          placeholder="Category description"
          label="Description"
        />
        <ColorPicker
          :error="errorStore.errors.color"
          v-model="reactiveCategory.color"
          label="Color"
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
            @click="$actions.cancelEdit"
            class="px-4 py-1.5 text-sm bg-gray-500 hover:bg-gray-600 text-white rounded-lg shadow-sm transition"
          >
            Cancel
          </button>
        </div>
      </div>
    </Modal>
  </div>
</template>
