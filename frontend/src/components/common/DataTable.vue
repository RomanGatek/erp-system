<script setup>

defineProps({
  headers: {
    type: Array,
    required: true
  },
  items: {
    type: Array,
    required: true
  },
  sortBy: {
    type: Function,
    required: true
  },
  sorting: {
    type: Object,
    required: true
  },
  onEdit: {
    type: Function,
    required: true
  },
  onDelete: {
    type: Function,
    required: true
  }
})
</script>

<template>
  <div class="relative rounded-lg border border-gray-100">
    <!-- Fixed Header -->
    <table class="w-full">
      <thead class="sticky top-0 z-10 bg-gray-50">
        <tr class="border-b border-gray-100">
          <th
            v-for="header in headers"
            :key="header.field"
            :class="[
              'px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider bg-gray-50',
              header.field === 'actions' ? 'text-right' : 'text-left',
              header.sortable ? 'cursor-pointer hover:bg-gray-100' : '',
              header.class || ''
            ]"
            @click="header.sortable && sortBy(header.field)"
          >
            <div class="flex items-center" :class="header.field === 'actions' ? 'justify-end' : 'space-x-1'">
              <span>{{ header.label }}</span>
              <svg
                v-if="header.sortable && sorting.field === header.field"
                class="w-4 h-4 ml-1"
                :class="sorting.direction === 'asc' ? 'text-green-500' : 'text-red-500'"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  :d="sorting.direction === 'asc' ? 'M5 15l7-7 7 7' : 'M19 9l-7 7-7-7'"
                />
              </svg>
            </div>
          </th>
        </tr>
      </thead>

      <!-- Scrollable Body -->
      <tbody class="divide-y divide-gray-100 bg-white">
        <tr
          v-for="(item, index) in items"
          :key="item.id"
          class="hover:bg-gray-50 transition-colors"
        >
          <slot name="row" :item="item" :index="index">
            <td
              v-for="header in headers"
              :key="header.field"
              class="px-6 py-4 whitespace-nowrap"
              :class="header.field === 'actions' ? 'text-right' : 'text-gray-600'"
            >
              <template v-if="header.field === 'actions'">
                <button
                  @click="onEdit(index)"
                  class="text-blue-600 hover:text-blue-900 mr-4 p-1 rounded hover:bg-blue-50 cursor-pointer"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                  </svg>
                </button>
                <button
                  @click="onDelete(item.id)"
                  class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 cursor-pointer"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </template>
              <template v-else-if="header.type === 'image'">
                <img
                  v-if="item[header.field]"
                  :src="item[header.field]"
                  :alt="item[header.field]"
                  class="w-8 h-8 object-cover rounded"
                />
                <div v-else class="w-8 h-8 bg-gray-100 rounded flex items-center justify-center">
                  <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
              </template>
              <template v-else>
                {{ item[header.field] }}
              </template>
            </td>
          </slot>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.relative {
  height: auto;
  min-height: 100px;
  max-height: calc(100vh - 350px);
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #CBD5E1 transparent;
}

/* Show scrollbar only when content overflows */
.relative {
  scrollbar-gutter: stable;
}

.relative::-webkit-scrollbar {
  width: 6px;
  height: 6px; /* For horizontal scrollbar */
}

.relative::-webkit-scrollbar-track {
  background: transparent;
}

.relative::-webkit-scrollbar-thumb {
  background-color: #CBD5E1;
  border-radius: 20px;
  transition: background-color 0.2s;
}

.relative::-webkit-scrollbar-thumb:hover {
  background-color: #94A3B8;
}

thead {
  background-color: #F9FAFB;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* Column sizing */
th, td {
  min-width: 120px;
}

/* Make description column wider */
th[data-field="description"],
td[data-field="description"] {
  min-width: 200px;
  max-width: 400px;
}

/* Make action column narrower */
th[data-field="actions"],
td[data-field="actions"] {
  min-width: 100px;
  width: 100px;
}

/* First column sticky behavior */
th:first-child,
td:first-child {
  position: sticky;
  left: 0;
  background: inherit;
  z-index: 1;
}
</style>
