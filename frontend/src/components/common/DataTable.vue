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
  <div class="relative overflow-hidden rounded-xl ring-1 ring-gray-100">
    <div class="h-auto min-h-[100px] max-h-[calc(100vh-24rem)] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300/50 hover:scrollbar-thumb-gray-400/50 scrollbar-track-transparent">
      <table class="w-full border-collapse">
        <!-- Fixed Header -->
        <thead class="sticky top-0 z-10">
          <tr class="border-b border-gray-100">
            <th v-for="header in headers" :key="header.field"
              :class="[
                'px-4 py-3 text-xs font-medium bg-gray-50/80 backdrop-blur-sm transition-colors min-w-[120px]',
                header.field === 'actions' ? 'text-right w-[100px] min-w-[100px]' : '',
                header.field === 'description' ? 'min-w-[200px] max-w-[400px]' : '',
                header.sortable ? 'cursor-pointer hover:bg-gray-100/80' : '',
                header.class || '',
                header.field === headers[0].field ? 'sticky left-0 z-[1] bg-inherit' : '',
                header.sortable && sorting.field === header.field ? 'text-blue-600' : 'text-gray-500',
                header.align === 'right' ? 'text-right' : 
                header.align === 'center' ? 'text-center' : 
                'text-left'
              ]"
              :data-field="header.field"
              @click="header.sortable && sortBy(header.field)"
            >
              <div class="flex items-center gap-1.5"
                :class="header.field === 'actions' ? 'justify-end' : header.align === 'center' ? 'justify-center' : 'justify-start'"
              >
                <span>{{ header.label }}</span>
                <svg v-if="header.sortable" class="w-4 h-4" :class="{ 'text-blue-500': sorting.field === header.field }"
                  fill="none" viewBox="0 0 24 24" stroke="currentColor"
                >
                  <path v-if="sorting.field !== header.field" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
                  <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    :d="sorting.direction === 'asc' ? 'M5 15l7-7 7 7' : 'M19 9l-7 7-7-7'" />
                </svg>
              </div>
            </th>
          </tr>
        </thead>

        <!-- Table Body -->
        <tbody class="divide-y divide-gray-100">
          <template v-for="(item, index) in items" :key="item.id">
            <tr class="group transition-colors duration-200"
              :class="[
                expandedRows?.has(item.id) ? 'bg-blue-50/80' : 'hover:bg-gray-50/80',
                onRowClick && 'cursor-pointer'
              ]"
              @click="onRowClick && onRowClick(item.id)"
            >
              <slot name="row" :item="item" :index="index">
                <td v-for="header in headers" :key="header.field" :data-field="header.field"
                  :class="[
                    'px-4 py-3 text-sm transition-colors',
                    header.field === 'actions' ? 'text-right w-[100px] min-w-[100px]' : '',
                    header.field === 'description' ? 'min-w-[200px] max-w-[400px]' : '',
                    header.field === headers[0].field ? 'sticky left-0 z-[1] bg-inherit' : '',
                    expandedRows?.has(item.id) ? 'text-blue-900' : 'text-gray-600',
                    header.align === 'right' ? 'text-right' : 
                    header.align === 'center' ? 'text-center' : 
                    'text-left'
                  ]"
                >
                  <template v-if="header.field === 'actions' && (onEdit || onDelete)">
                    <div class="flex justify-end items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button v-if="onEdit" @click.stop="onEdit(item)"
                        class="p-1.5 text-blue-600 hover:text-blue-900 rounded-lg hover:bg-blue-50/80 transition-colors"
                        title="View Details"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      </button>
                      <button v-if="onDelete" @click.stop="onDelete(item.id)"
                        class="p-1.5 text-red-600 hover:text-red-900 rounded-lg hover:bg-red-50/80 transition-colors"
                        title="Delete"
                      >
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </template>
                  <template v-else-if="header.type === 'image'">
                    <div class="flex" :class="[
                      header.align === 'center' ? 'justify-center' : header.align === 'right' ? 'justify-end' : 'justify-start'
                    ]">
                      <img v-if="item[header.field]" :src="item[header.field]" :alt="item[header.field]"
                        class="w-8 h-8 object-cover rounded-lg ring-1 ring-gray-100"
                      />
                      <div v-else
                        class="w-8 h-8 rounded-lg bg-gray-50 ring-1 ring-gray-100 flex items-center justify-center"
                      >
                        <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                      </div>
                    </div>
                  </template>
                  <template v-else>
                    <div :class="[
                      'w-full',
                      header.align === 'right' ? 'text-right' : 
                      header.align === 'center' ? 'text-center' : 
                      'text-left'
                    ]">
                      {{ item[header.field] }}
                    </div>
                  </template>
                </td>
              </slot>
            </tr>

            <!-- Expanded Content -->
            <tr v-if="expandedRows.has(item.id)" class="border-t border-blue-100">
              <td :colspan="headers.length" class="p-0 bg-gray-50/50">
                <div class="animate-[expandContent_0.3s_cubic-bezier(0.4,0,0.2,1)] origin-top">
                  <slot name="expanded-content" :item="item" />
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style>
@keyframes expandContent {
  from {
    opacity: 0;
    transform: scaleY(0);
  }
  to {
    opacity: 1;
    transform: scaleY(1);
  }
}
</style>
