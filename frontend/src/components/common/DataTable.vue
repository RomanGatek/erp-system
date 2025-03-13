<script setup>
defineProps({
  headers: {
    type: Array,
    required: true,
  },
  items: {
    type: Array,
    required: true,
  },
  sortBy: {
    type: Function,
    required: true,
  },
  sorting: {
    type: Object,
    required: true,
  },
  onEdit: {
    type: Function,
    default: null,
  },
  onDelete: {
    type: Function,
    default: null,
  },
  expandedRows: {
    type: Set,
    default: () => new Set(),
  },
  onRowClick: {
    type: Function,
    default: null,
  },
})
</script>

<template>
  <div
    class="relative h-auto min-h-[100px] max-h-[calc(100vh-24rem)] overflow-y-auto scrollbar scrollbar-thin scrollbar-track-transparent scrollbar-thumb-gray-300 hover:scrollbar-thumb-gray-400 rounded-lg border border-gray-100"
  >
    <!-- Fixed Header -->
    <table class="w-full">
      <thead class="sticky top-0 z-10 bg-gray-50 shadow-sm">
        <tr class="border-b border-gray-100">
          <th
            v-for="header in headers"
            :key="header.field"
            :class="[
              'px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider bg-gray-50 min-w-[120px]',
              header.field === 'actions'
                ? 'text-right w-[100px] min-w-[100px]'
                : header.align || 'text-left',
              header.field === 'description' ? 'min-w-[200px] max-w-[400px]' : '',
              header.sortable ? 'cursor-pointer hover:bg-gray-100' : '',
              header.class || '',
              header.field === headers[0].field ? 'sticky left-0 z-[1] bg-inherit' : '',
            ]"
            :data-field="header.field"
            @click="header.sortable && sortBy(header.field)"
          >
            <div
              class="flex items-center"
              :class="
                header.field === 'actions'
                  ? 'justify-end'
                  : header.align === 'text-center'
                    ? 'justify-center'
                    : header.align === 'text-right'
                      ? 'justify-end'
                      : 'justify-start'
              "
            >
              <span>{{ header.label }}</span>
              <svg
                v-if="header.sortable && sorting.field === header.field"
                class="w-4 h-4 ml-1 text-blue-500"
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
        <template v-for="(item, index) in items" :key="item.id">
          <tr
            class="transition-colors duration-200 ease-in-out hover:bg-blue-50/5 group"
            :class="{
              'bg-blue-50/80': expandedRows?.has(item.id),
              'cursor-pointer': onRowClick != null,
              relative: expandedRows?.has(item.id),
            }"
            @click="onRowClick && onRowClick(item.id)"
          >
            <slot name="row" :item="item" :index="index">
              <td
                v-for="header in headers"
                :key="header.field"
                :data-field="header.field"
                :class="[
                  'px-6 py-4 whitespace-nowrap text-gray-600 min-w-[120px] transition-colors duration-200',
                  header.field === 'actions'
                    ? 'text-right w-[100px] min-w-[100px]'
                    : header.align || 'text-left',
                  header.field === 'description' ? 'min-w-[200px] max-w-[400px]' : '',
                  header.field === headers[0].field ? 'sticky left-0 z-[1] bg-inherit' : '',
                  expandedRows?.has(item.id)
                    ? 'after:absolute after:bottom-0 after:left-0 after:right-0 after:h-0.5 after:bg-blue-500 after:transform after:scale-x-0 group-hover:after:scale-x-100 after:transition-transform after:duration-300'
                    : '',
                ]"
              >
                <template v-if="header.field === 'actions' && (onEdit || onDelete)">
                  <div class="flex justify-end space-x-2">
                    <button
                      v-if="onEdit"
                      @click.stop="onEdit(item)"
                      class="text-blue-600 hover:text-blue-900 p-1 rounded hover:bg-blue-50 transition-colors"
                    >
                      <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                        />
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                        />
                      </svg>
                    </button>
                    <button
                      v-if="onDelete"
                      @click.stop="onDelete(item.id)"
                      class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 transition-colors"
                    >
                      <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                    </button>
                  </div>
                </template>
                <template v-else-if="header.type === 'image'">
                  <img
                    v-if="item[header.field]"
                    :src="item[header.field]"
                    :alt="item[header.field]"
                    :class="[
                      'w-8 h-8 object-cover rounded',
                      header.align === 'text-center'
                        ? 'mx-auto'
                        : header.align === 'text-right'
                          ? 'ml-auto'
                          : '',
                    ]"
                  />
                  <div
                    v-else
                    :class="[
                      'w-8 h-8 bg-gray-100 rounded flex items-center justify-center',
                      header.align === 'text-center'
                        ? 'mx-auto'
                        : header.align === 'text-right'
                          ? 'ml-auto'
                          : '',
                    ]"
                  >
                    <svg
                      class="w-4 h-4 text-gray-400"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                      />
                    </svg>
                  </div>
                </template>
                <template v-else>
                  {{ item[header.field] }}
                </template>
              </td>
            </slot>
          </tr>
          <!-- Expanded content -->
          <tr v-if="expandedRows.has(item.id)">
            <td :colspan="headers.length" class="px-6 py-4 bg-gray-50/80 backdrop-blur-sm">
              <div
                class="animate-[expandContent_0.3s_cubic-bezier(0.4,0,0.2,1)] origin-top will-change-transform will-change-opacity"
              >
                <slot name="expanded-content" :item="item" />
              </div>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
</template>
