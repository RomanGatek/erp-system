<script setup>
import { defineProps } from 'vue'

const props = defineProps({
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
  <div class="overflow-hidden rounded-lg border border-gray-100">
    <table class="w-full">
      <thead>
        <tr class="bg-gray-50 border-b border-gray-100">
          <th
            v-for="header in headers"
            :key="header.field"
            :class="[
              'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider',
              header.sortable ? 'cursor-pointer hover:bg-gray-100' : '',
            ]"
            @click="header.sortable && sortBy(header.field)"
          >
            <div class="flex items-center space-x-1">
              <span>{{ header.label }}</span>
              <svg
                v-if="header.sortable && sorting.field === header.field"
                class="w-4 h-4"
              >
                <path
                  :d="
                    sorting.direction === 'asc'
                      ? 'M5 15l7-7 7 7'
                      : 'M19 9l-7 7-7-7'
                  "
                  stroke="currentColor"
                  stroke-width="2"
                  fill="none"
                />
              </svg>
            </div>
          </th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-100">
        <tr
          v-for="(item, index) in items"
          :key="item.id"
          class="hover:bg-gray-50 transition-colors"
        >
          <slot name="row" :item="item" :index="index">
            <!-- Default row rendering -->
            <td
              v-for="header in headers"
              :key="header.field"
              class="px-6 py-4 whitespace-nowrap"
              :class="header.field === 'actions' ? 'text-right' : 'text-gray-600'"
            >
              <template v-if="header.field === 'actions'">
                <button
                  @click="onEdit(index)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  Edit
                </button>
                <button
                  @click="onDelete(item.id)"
                  class="text-red-600 hover:text-red-900"
                >
                  Delete
                </button>
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