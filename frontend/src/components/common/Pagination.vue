<script setup>
import { getPaginationInfo } from '@/utils/pagination.js'
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

defineEmits(['page-change'])
</script>

<template>
  <div class="flex items-center justify-between mt-4 text-sm text-gray-700">
    <div>
      Showing {{ paginationStart }} to {{ paginationEnd }} of {{ totalItems }} entries
    </div>
    <div class="flex space-x-2">
      <button
        v-for="page in totalPages"
        :key="page"
        @click="piniaStore.setPage(page)"
        :class="[
          'px-3 py-1 rounded',
          currentPage === page
            ? 'bg-blue-500 text-white'
            : 'bg-gray-100 hover:bg-gray-200',
        ]"
      >
        {{ page }}
      </button>
    </div>
  </div>
</template>
