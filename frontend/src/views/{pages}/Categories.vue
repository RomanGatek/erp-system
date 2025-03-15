<script setup>
import { ref, onMounted, computed } from 'vue'
import { $reactive } from '@/utils/index.js'
import { useNotifier, useCategoriesStore } from '@/stores'
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
  ColorPicker,
} from '@/components'

defineOptions({
  name: 'CategoriesView',
})

const searchInput = ref('')
const errors = useErrorStore()
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

errors.validateField(reactiveCategory.$cleaned())

const tableHeaders = [
  { field: 'name', label: 'Category', sortable: true },
  { field: 'description', label: 'Description', sortable: false },
  { field: 'actions', label: '', sortable: false, class: 'text-right w-[100px]' },
]

const loading = ref(false)

const $actions = computed(() => ({
  fetchCategories: async () => {
    loading.value = true
    try {
      await categoriesStore.fetchCategories()
      if (categoriesStore.error) errors.handle(categoriesStore.error)
    } catch (error) {
      errors.handle(error)
      $notifier.error('An error occurred while fetching categories')
    } finally {
      loading.value = false
    }
  },
  openAddModal: () => {
    reactiveCategory.$clear()
    errors.clearServerErrors()
    isAddModalOpen.value = true
  },
  add: async () => {
    errors.clearServerErrors()
    try {
      await categoriesStore.addCategory(reactiveCategory.$cleaned())
      if (!categoriesStore.error) {
        $notifier.success('Category added successfully')
        isAddModalOpen.value = false
        reactiveCategory.$clear()
      } else {
        errors.handle(categoriesStore.error)
      }
    } catch (error) {
      errors.handle(error)
    }
  },
  openEditModal: (category) => {
    errors.clearServerErrors()
    reactiveCategory.$assign(category)
    isEditModalOpen.value = true
  },
  cancelAdd: () => {
    isAddModalOpen.value = false
    reactiveCategory.$clear()
    errors.clearServerErrors()
  },
  cancelEdit: () => {
    isEditModalOpen.value = false
    reactiveCategory.$clear()
    errors.clearServerErrors()
  },
  updateCategory: async () => {
    try {
      await categoriesStore.updateCategory(reactiveCategory.id, reactiveCategory.$cleaned())
      if (categoriesStore.error) {
        errors.handle(categoriesStore.error)
      } else {
        $notifier.success('Category updated successfully')
        isEditModalOpen.value = false
        reactiveCategory.$clear()
      }
    } catch (error) {
      errors.handle(error)
    }
  },
  deleteCategory: async (id) => {
    if (confirm('Are you sure you want to delete this category?\nThis action cannot be undone!\nYou can also delete the products associated with this category.')) {
      try {
        await categoriesStore.deleteCategory(id)
        if (categoriesStore.error) {
          errors.handle(categoriesStore.error)
        } else {
          $notifier.success('Category deleted successfully')
        }
      } catch (error) {
        console.error(error)
        $notifier.error('An error occurred while deleting the category')
      }
    }
  },
}))

onMounted(async () => {
  await $actions.value.fetchCategories()
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
                  Categories</h2>
                <p class="mt-1 text-sm text-gray-600">Manage product categories</p>
              </div>

              <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
                <SearchBar v-model="searchInput" @update:modelValue="categoriesStore.setSearch($event)"
                  class="min-w-[250px]" />
                <BaseButton type="primary" class="text-sm! flex items-center justify-center gap-2 px-4 py-2!"
                  @click="$actions.openAddModal">
                  <span>Add Category</span>
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
          <EmptyState v-if="!categoriesStore.paginateItems.length" message="No categories to display"
            class="bg-gray-50/50 rounded-xl py-12">
            <template #icon>
              <svg class="w-12 h-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A2 2 0 013 12V7a4 4 0 014-4z" />
              </svg>
            </template>
            <template #message>
              <h3 class="mt-4 text-lg font-medium text-gray-900">No categories found</h3>
              <p class="mt-1 text-gray-500">Start by adding your first category</p>
            </template>
          </EmptyState>

          <!-- Data table -->
          <template v-else>
            <div class="relative rounded-xl overflow-hidden">
              <div
                class="max-h-[calc(100vh-20rem)] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300 hover:scrollbar-thumb-gray-400 scrollbar-track-transparent">
                <DataTable :headers="tableHeaders" :items="categoriesStore.paginateItems"
                  :sort-by="categoriesStore.setSorting" :sorting="categoriesStore.sorting"
                  :on-edit="$actions.openEditModal" :on-delete="$actions.deleteCategory">
                  <template #row="{ item }">
                    <td class="px-4 py-3 whitespace-nowrap">
                      <div class="flex items-center">
                        <div class="w-10 h-10 rounded-xl bg-gradient-to-br" :style="{
                          backgroundColor: item.color || '#94A3B8',
                          opacity: 0.15
                        }"></div>
                        <div class="ml-3">
                          <div class="text-sm font-medium text-gray-900">{{ item.name }}</div>
                          <div class="text-xs text-gray-500 line-clamp-1">ID: #{{ item.id }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3">
                      <p class="text-sm text-gray-600 line-clamp-2">{{ item.description || 'No description' }}</p>
                    </td>
                    <td class="px-4 py-3 whitespace-nowrap text-right">
                      <div class="flex justify-end items-center gap-2">
                        <button @click="$actions.openEditModal(item)"
                          class="p-1.5 text-blue-600 hover:text-blue-900 rounded-lg hover:bg-blue-50/80 transition-colors"
                          title="Edit Category">
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                          </svg>
                        </button>
                        <button @click="$actions.deleteCategory(item.id)"
                          class="p-1.5 text-red-600 hover:text-red-900 rounded-lg hover:bg-red-50/80 transition-colors"
                          title="Delete Category">
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
              <Pagination :store="useCategoriesStore" />
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- Add Modal -->
    <Modal :show="isAddModalOpen" title="Add New Category" @close="$actions.cancelAdd">
      <div class="space-y-4">
        <BaseInput v-model="reactiveCategory.name" :error="errors.name" placeholder="Enter category name..."
          label="Name" />

        <BaseInput v-model="reactiveCategory.description" :error="errors.description" type="textarea"
          placeholder="Enter category description..." label="Description" :rows="3" />

        <ColorPicker v-model="reactiveCategory.color" :error="errors.color" label="Category Color" />

        <div class="flex justify-end items-center gap-3 pt-4">
          <BaseButton type="error" class="text-sm!" @click="$actions.cancelAdd">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! bg-gradient-to-r from-blue-600 to-blue-400!" @click="$actions.add">
            Add Category
          </BaseButton>
        </div>
      </div>
    </Modal>

    <!-- Edit Modal -->
    <Modal :show="isEditModalOpen" title="Edit Category" @close="$actions.cancelEdit">
      <div class="space-y-4">
        <BaseInput v-model="reactiveCategory.name" :error="errors.name" placeholder="Enter category name..."
          label="Name" />

        <BaseInput v-model="reactiveCategory.description" :error="errors.description" type="textarea"
          placeholder="Enter category description..." label="Description" :rows="3" />

        <ColorPicker v-model="reactiveCategory.color" :error="errors.color" label="Category Color" />

        <div class="flex justify-end items-center gap-3 pt-4">
          <BaseButton type="error" class="text-sm!" @click="$actions.cancelEdit">
            Cancel
          </BaseButton>
          <BaseButton type="primary" class="text-sm! bg-gradient-to-r from-blue-600 to-blue-400!"
            @click="$actions.updateCategory">
            Save Changes
          </BaseButton>
        </div>
      </div>
    </Modal>
  </div>
</template>
