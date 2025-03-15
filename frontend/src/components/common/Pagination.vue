<script setup>
import { getPaginationInfo } from '@/utils'
import { computed } from 'vue'

// 1. We define that 'store' is a function (e.g. useProductsStore).
const props = defineProps({
  store: {
    required: true
  }
})

// 2. We call the store (just as you would call it in a parent component).
const piniaStore = props.store()

// 3. We create computed properties for the data.
const paginationInfo = computed(() =>
  getPaginationInfo(piniaStore.filtered, piniaStore.pagination)
)

const paginationStart = computed(() => paginationInfo.value.startItem)
const paginationEnd   = computed(() => paginationInfo.value.endItem)
const totalPages      = computed(() => paginationInfo.value.totalPages)
const totalItems      = computed(() => paginationInfo.value.totalItems)
const currentPage     = computed(() => piniaStore.pagination.currentPage)

// Add computed properties for navigation
const canGoPrevious = computed(() => currentPage.value > 1)
const canGoNext = computed(() => currentPage.value < totalPages.value)

// Navigation methods
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    piniaStore.setPage(page)
  }
}

const goToPrevious = () => {
  if (canGoPrevious.value) {
    goToPage(currentPage.value - 1)
  }
}

const goToNext = () => {
  if (canGoNext.value) {
    goToPage(currentPage.value + 1)
  }
}

defineEmits(['page-change'])
</script>

<template>
  <div class="flex flex-col sm:flex-row items-center justify-between gap-4 text-sm text-gray-700">
    <!-- Info text -->
    <div class="text-gray-600">
      Showing <span class="font-medium text-gray-900">{{ paginationStart }}</span> to <span
        class="font-medium text-gray-900">{{ paginationEnd }}</span> of <span
        class="font-medium text-gray-900">{{ totalItems }}</span> entries
    </div>

    <!-- Navigation buttons -->
    <div class="flex items-center gap-2">
      <!-- Previous button -->
      <button @click="goToPrevious" :disabled="!canGoPrevious"
        class="flex items-center gap-1 px-3 py-1.5 rounded-lg text-gray-700 transition-all duration-200"
        :class="[canGoPrevious ? 'hover:bg-gray-100 active:bg-gray-200' : 'opacity-50 cursor-not-allowed']">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        <span class="hidden sm:inline">Previous</span>
      </button>

      <!-- Page numbers -->
      <div class="flex items-center gap-1">
        <button v-for="page in totalPages" :key="page" @click="goToPage(page)"
          class="min-w-[2rem] h-8 flex items-center justify-center px-2 rounded-lg transition-all duration-200"
          :class="[
            currentPage === page
              ? 'bg-blue-600 text-white font-medium shadow-sm'
              : 'text-gray-700 hover:bg-gray-100 active:bg-gray-200'
          ]">
          {{ page }}
        </button>
      </div>

      <!-- Next button -->
      <button @click="goToNext" :disabled="!canGoNext"
        class="flex items-center gap-1 px-3 py-1.5 rounded-lg text-gray-700 transition-all duration-200"
        :class="[canGoNext ? 'hover:bg-gray-100 active:bg-gray-200' : 'opacity-50 cursor-not-allowed']">
        <span class="hidden sm:inline">Next</span>
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  </div>
</template>
