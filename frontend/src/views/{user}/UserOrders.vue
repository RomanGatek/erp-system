<template>
  <div class="min-h-screen bg-gray-50/50 backdrop-blur-sm py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
          My Orders
        </h1>
        <p class="mt-2 text-sm text-gray-600">View and manage your order history</p>
      </div>

      <!-- Orders Table -->
      <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-6">
        <DataTable :headers="tableHeaders" :items="orders" :sort-by="sortBy" :sorting="sorting"
          :expanded-rows="expandedRows" :on-row-click="toggleOrderDetail">
          <!-- Custom Row Template -->
          <template #row="{ item }">
            <td class="px-4 py-3 text-sm sticky left-0 z-[1] bg-inherit"
              :class="expandedRows.has(item.id) ? 'text-blue-900' : 'text-gray-600'">
              {{ item.orderNumber }}
            </td>
            <td class="px-4 py-3 text-sm" :class="expandedRows.has(item.id) ? 'text-blue-900' : 'text-gray-600'">
              {{ formatDate(item.date) }}
            </td>
            <td class="px-4 py-3 text-sm" :class="expandedRows.has(item.id) ? 'text-blue-900' : 'text-gray-600'">
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full" :class="getStatusColor(item.status)"></span>
                {{ item.status }}
              </div>
            </td>
            <td class="px-4 py-3 text-sm text-right"
              :class="expandedRows.has(item.id) ? 'text-blue-900' : 'text-gray-600'">
              {{ formatPrice(item.total) }}
            </td>
          </template>

          <!-- Expanded Content Template -->
          <template #expanded-content="{ item }">
            <div class="p-6 bg-white">
              <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <!-- Order Details -->
                <div>
                  <h3 class="text-lg font-semibold text-gray-800 mb-4">Order Details</h3>
                  <div class="space-y-4">
                    <div class="flex justify-between py-2 border-b border-gray-100">
                      <span class="text-gray-600">Order Number</span>
                      <span class="font-medium text-gray-900">{{ item.orderNumber }}</span>
                    </div>
                    <div class="flex justify-between py-2 border-b border-gray-100">
                      <span class="text-gray-600">Date</span>
                      <span class="font-medium text-gray-900">{{ formatDate(item.date) }}</span>
                    </div>
                    <div class="flex justify-between py-2 border-b border-gray-100">
                      <span class="text-gray-600">Status</span>
                      <span class="font-medium" :class="getStatusTextColor(item.status)">{{ item.status }}</span>
                    </div>
                    <div class="flex justify-between py-2 border-b border-gray-100">
                      <span class="text-gray-600">Total Amount</span>
                      <span class="font-medium text-gray-900">{{ formatPrice(item.total) }}</span>
                    </div>
                  </div>

                  <!-- Order Items -->
                  <div class="mt-6">
                    <h4 class="font-medium text-gray-800 mb-3">Items</h4>
                    <div class="space-y-3">
                      <div v-for="product in item.products" :key="product.id"
                        class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                        <div class="flex items-center gap-3">
                          <div
                            class="w-12 h-12 bg-white rounded-lg ring-1 ring-gray-100 flex items-center justify-center overflow-hidden">
                            <img v-if="product.image" :src="product.image" :alt="product.name"
                              class="w-full h-full object-cover" />
                            <svg v-else class="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24"
                              stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                          </div>
                          <div>
                            <p class="font-medium text-gray-800">{{ product.name }}</p>
                            <p class="text-sm text-gray-500">Quantity: {{ product.quantity }}</p>
                          </div>
                        </div>
                        <span class="font-medium text-gray-800">{{ formatPrice(product.price * product.quantity)
                        }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Order Timeline -->
                <div>
                  <h3 class="text-lg font-semibold text-gray-800 mb-4">Order Timeline</h3>
                  <div class="relative">
                    <div class="absolute top-0 left-4 h-full w-0.5 bg-gray-200"></div>
                    <div class="space-y-6">
                      <div v-for="(event, index) in item.timeline" :key="index" class="relative flex gap-4">
                        <div class="absolute top-0 left-4 h-full w-0.5"
                          :class="index === item.timeline.length - 1 ? 'bg-transparent' : 'bg-gray-200'"></div>
                        <div class="relative z-10 flex items-center justify-center w-8 h-8 rounded-full"
                          :class="getTimelineStatusColor(event.type)">
                          <svg class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path v-if="event.type === 'created'" stroke-linecap="round" stroke-linejoin="round"
                              stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                            <path v-else-if="event.type === 'processing'" stroke-linecap="round" stroke-linejoin="round"
                              stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                            <path v-else-if="event.type === 'shipped'" stroke-linecap="round" stroke-linejoin="round"
                              stroke-width="2" d="M5 13l4 4L19 7" />
                            <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M9 12l2 2 4-4" />
                          </svg>
                        </div>
                        <div class="flex-1 pt-1.5">
                          <p class="font-medium text-gray-900">{{ event.title }}</p>
                          <p class="text-sm text-gray-500">{{ formatDate(event.date) }}</p>
                          <p v-if="event.description" class="mt-1 text-sm text-gray-600">{{ event.description }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </DataTable>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import DataTable from '@/components/common/DataTable.vue'
import api from '@/services/api'
import { useInventoryStore, useOrdersStore, useWorkflowStore } from '@/stores'

// Table configuration
const tableHeaders = [
  { field: 'orderNumber', label: 'Order Number', sortable: true },
  { field: 'date', label: 'Date', sortable: true },
  { field: 'status', label: 'Status', sortable: true },
  { field: 'total', label: 'Total', sortable: true, align: 'right' }
]

const sorting = ref({ field: 'date', direction: 'desc' })
const expandedRows = ref(new Set())

console.log('A')

const orderStore = useWorkflowStore()

onMounted(async () => {
  await orderStore.fetchOrders()
})

// Mock data - replace with actual API call
const orders = ref([
  {
    id: 1,
    orderNumber: 'ORD-123456',
    date: new Date('2024-03-15T14:30:00'),
    status: 'Delivered',
    total: 2499.99,
    products: [
      { id: 1, name: 'Product 1', quantity: 2, price: 999.99, image: null },
      { id: 2, name: 'Product 2', quantity: 1, price: 500.01, image: null }
    ],
    timeline: [
      {
        type: 'created',
        title: 'Order Placed',
        date: new Date('2024-03-15T14:30:00'),
        description: 'Order was successfully placed'
      },
      {
        type: 'processing',
        title: 'Processing',
        date: new Date('2024-03-15T15:00:00'),
        description: 'Order is being processed'
      },
      {
        type: 'shipped',
        title: 'Shipped',
        date: new Date('2024-03-16T09:00:00'),
        description: 'Order has been shipped via DHL'
      },
      {
        type: 'delivered',
        title: 'Delivered',
        date: new Date('2024-03-17T14:20:00'),
        description: 'Package was delivered successfully'
      }
    ]
  },
  // Add more mock orders as needed
])

// Sorting function
const sortBy = (field) => {
  if (sorting.value.field === field) {
    sorting.value.direction = sorting.value.direction === 'asc' ? 'desc' : 'asc'
  } else {
    sorting.value = { field, direction: 'asc' }
  }

  orders.value.sort((a, b) => {
    const modifier = sorting.value.direction === 'asc' ? 1 : -1
    if (a[field] < b[field]) return -1 * modifier
    if (a[field] > b[field]) return 1 * modifier
    return 0
  })
}

// Toggle order detail
const toggleOrderDetail = (orderId) => {
  if (expandedRows.value.has(orderId)) {
    expandedRows.value.delete(orderId)
  } else {
    expandedRows.value.add(orderId)
  }
}

// Helper functions
const formatDate = (date) => {
  return new Intl.DateTimeFormat('cs-CZ', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(date))
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('cs-CZ', {
    style: 'currency',
    currency: 'CZK'
  }).format(price)
}

const getStatusColor = (status) => {
  const colors = {
    'Pending': 'bg-yellow-400',
    'Processing': 'bg-blue-400',
    'Shipped': 'bg-purple-400',
    'Delivered': 'bg-green-400',
    'Cancelled': 'bg-red-400'
  }
  return colors[status] || 'bg-gray-400'
}

const getStatusTextColor = (status) => {
  const colors = {
    'Pending': 'text-yellow-600',
    'Processing': 'text-blue-600',
    'Shipped': 'text-purple-600',
    'Delivered': 'text-green-600',
    'Cancelled': 'text-red-600'
  }
  return colors[status] || 'text-gray-600'
}

const getTimelineStatusColor = (type) => {
  const colors = {
    'created': 'bg-blue-500',
    'processing': 'bg-yellow-500',
    'shipped': 'bg-purple-500',
    'delivered': 'bg-green-500'
  }
  return colors[type] || 'bg-gray-500'
}
</script>