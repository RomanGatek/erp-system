<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: {
    type: String,
    default: 'No data to display'
  },
  description: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: 'inbox' // inbox, search, folder, document
  },
  action: {
    type: Object,
    default: null // { label: 'Add Item', onClick: () => {} }
  }
})

const iconPath = computed(() => {
  const paths = {
    inbox: 'M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4',
    search: 'M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z',
    folder: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z',
    document: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z'
  }
  return paths[props.icon] || paths.inbox
})
</script>

<template>
  <div class="text-center py-12 px-4">
    <div class="relative inline-block">
      <!-- Background Circle with Gradient -->
      <div class="absolute inset-0 bg-gradient-to-br from-blue-50 to-blue-100 rounded-full transform scale-150 opacity-75"></div>
      
      <!-- Icon -->
      <svg
        class="relative mx-auto h-16 w-16 text-blue-500 transform transition-transform duration-700 hover:scale-110"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="1.5"
          :d="iconPath"
          class="transition-all duration-700"
        />
      </svg>
    </div>

    <!-- Message -->
    <h3 class="mt-4 text-lg font-medium text-gray-900 transition-colors">
      {{ message }}
    </h3>

    <!-- Optional Description -->
    <p v-if="description" class="mt-2 text-sm text-gray-500 max-w-sm mx-auto">
      {{ description }}
    </p>

    <!-- Optional Action Button -->
    <div v-if="action" class="mt-6">
      <button
        @click="action.onClick"
        class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200 transform hover:-translate-y-0.5"
      >
        {{ action.label }}
      </button>
    </div>
  </div>
</template>

<style scoped>
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.relative svg {
  animation: float 6s ease-in-out infinite;
}
</style>
