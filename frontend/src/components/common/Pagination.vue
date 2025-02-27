<script setup>
defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  totalItems: {
    type: Number,
    required: true
  },
  startItem: {
    type: Number,
    required: true
  },
  endItem: {
    type: Number,
    required: true
  }
})

defineEmits(['page-change'])
</script>

<template>
  <div class="flex items-center justify-between mt-4 text-sm text-gray-700">
    <div>
      Showing {{ startItem }} to {{ endItem }} of {{ totalItems }} entries
    </div>
    <div class="flex space-x-2">
      <button
        v-for="page in totalPages"
        :key="page"
        @click="$emit('page-change', page)"
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