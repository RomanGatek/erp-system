<!--suppress ALL -->
<template>
  <div class="min-h-screen bg-gray-50/50 backdrop-blur-sm py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 overflow-hidden">
        <!-- Header Section -->
        <div class="p-6 border-b border-gray-100">
          <StatusBar 
            :error="errors.general" 
            :loading="workflowStore.loading" 
            class="mb-6"
            @clear-error="errors.clearServerErrors()" 
          />

          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div>
              <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
                Approval Workflow
              </h1>
              <p class="mt-1 text-sm text-gray-600">Manage and approve orders</p>
            </div>
            
            <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 w-full sm:w-auto">
              <!-- Filter Pills -->
              <div class="flex gap-2 overflow-x-auto pb-1 sm:pb-0">
                <button 
                  v-for="filter in filters" 
                  :key="filter.value" 
                  @click="currentFilter = filter.value"
                  class="px-3 py-1.5 text-sm rounded-xl transition-all duration-200 whitespace-nowrap flex items-center gap-2 ring-1"
                  :class="[
                    currentFilter === filter.value
                      ? 'bg-gradient-to-r from-blue-600 to-blue-400 text-white ring-blue-400'
                      : 'bg-white text-gray-700 hover:bg-gray-50 ring-gray-200'
                  ]"
                >
                  <span class="w-2 h-2 rounded-full" :class="[
                    filter.value === 'pending' ? 'bg-yellow-400' : '',
                    filter.value === 'confirmed' ? 'bg-green-400' : '',
                    filter.value === 'canceled' ? 'bg-red-400' : '',
                    filter.value === 'all' ? 'bg-blue-400' : ''
                  ]"></span>
                  {{ filter.label }}
                </button>
              </div>
              
              <!-- Search Bar -->
              <SearchBar 
                v-model="searchQuery" 
                @update:modelValue="(value) => workflowStore.setSearch(value)"
                class="min-w-[250px]" 
              />
            </div>
          </div>
        </div>

        <!-- Content Section -->
        <div class="p-6">
          <!-- Empty state -->
          <EmptyState 
            v-if="!workflowStore.filtered.length" 
            class="bg-gray-50/50 rounded-xl p-8"
          >
            <template #icon>
              <svg class="w-12 h-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" 
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
              </svg>
            </template>
            <template #message>
              <h3 class="mt-4 text-lg font-medium text-gray-900">No orders to approve</h3>
              <p class="mt-1 text-gray-500">All orders have been processed</p>
            </template>
          </EmptyState>

          <!-- Data table -->
          <template v-else>
            <div class="relative rounded-xl overflow-hidden">
              <div class="max-h-[calc(100vh-20rem)] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300 hover:scrollbar-thumb-gray-400 scrollbar-track-transparent">
                <DataTable 
                  :headers="tableHeaders" 
                  :items="workflowStore.paginatedOrders" 
                  :sorting="workflowStore.sorting"
                  :sortBy="workflowStore.setSorting" 
                  :onEdit="openOrderDetail" 
                  :onDelete="confirmDelete"
                  :expandedRows="expandedRows" 
                  :onRowClick="toggleRow"
                >
                  <template #row="{ item }">
                    <td class="px-4 py-3 text-sm sticky left-0 z-[1] bg-inherit">
                      <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-xl bg-gradient-to-br from-blue-100 to-blue-50 flex items-center justify-center ring-1 ring-blue-100">
                          <span class="text-blue-700 font-medium text-sm">#{{ item.id }}</span>
                        </div>
                        <div class="flex flex-col">
                          <span class="font-medium text-gray-900">Order #{{ item.id }}</span>
                          <span class="text-xs text-gray-500">{{ formatDate(item.orderTime) }}</span>
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3 text-sm">
                      <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-50 text-blue-700 ring-1 ring-blue-600/20">
                        {{ item.orderItems?.length || 0 }} items
                      </span>
                    </td>
                    <td class="px-4 py-3 text-sm text-gray-600">
                      {{ formatDate(item.orderTime) }}
                    </td>
                    <td class="px-4 py-3 text-sm">
                      <span class="font-medium text-gray-900">{{ formatPrice(item.cost) }} Kč</span>
                    </td>
                    <td class="px-4 py-3 text-sm">
                      <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium" 
                        :class="{
                          'bg-green-50 text-green-700 ring-1 ring-green-600/20': item.orderType === 'SELL',
                          'bg-red-50 text-red-700 ring-1 ring-red-600/20': item.orderType === 'PURCHASE'
                        }"
                      >
                        {{ item.orderType }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-sm">
                      <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium" 
                        :class="{
                          'bg-yellow-50 text-yellow-700 ring-1 ring-yellow-600/20': item.status === 'PENDING',
                          'bg-green-50 text-green-700 ring-1 ring-green-600/20': item.status === 'CONFIRMED',
                          'bg-red-50 text-red-700 ring-1 ring-red-600/20': item.status === 'CANCELED'
                        }"
                      >
                        <span class="w-1.5 h-1.5 rounded-full" :class="{
                          'bg-yellow-500': item.status === 'PENDING',
                          'bg-green-500': item.status === 'CONFIRMED',
                          'bg-red-500': item.status === 'CANCELED'
                        }"></span>
                        {{ item.status }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-sm text-right">
                      <div class="flex items-center justify-end gap-2">
                        <button @click.stop="openOrderDetail(item)"
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
                        <button @click.stop="openOrderEdit(item)"
                          class="p-1.5 text-green-600 hover:text-green-900 rounded-lg hover:bg-green-50/80 transition-colors"
                          title="Edit Order"
                          :disabled="item.status !== 'PENDING'"
                          :class="{ 'opacity-50 cursor-not-allowed': item.status !== 'PENDING' }"
                        >
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                        <button @click.stop="confirmDelete(item)"
                          class="p-1.5 text-red-600 hover:text-red-900 rounded-lg hover:bg-red-50/80 transition-colors"
                          title="Delete Order"
                        >
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </div>
                    </td>
                  </template>

                  <template #expanded-content="{ item }">
                    <div class="bg-gray-50/50 p-6 space-y-6">
                      <div class="flex items-center justify-between">
                        <h4 class="text-sm font-medium text-gray-900">Order Items</h4>
                        <button @click.stop="toggleRow(item.id)"
                          class="p-1.5 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-white/50 transition-colors"
                        >
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                          </svg>
                        </button>
                      </div>

                      <div class="bg-white rounded-xl ring-1 ring-gray-100 overflow-hidden">
                        <table class="min-w-full divide-y divide-gray-200">
                          <thead class="bg-gray-50/80 backdrop-blur-sm">
                            <tr>
                              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500">Product</th>
                              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500">Quantity</th>
                              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500">Price</th>
                              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500">Total</th>
                            </tr>
                          </thead>
                          <tbody class="divide-y divide-gray-100">
                            <tr v-for="orderItem in item.orderItems" :key="orderItem.id" 
                              class="text-sm hover:bg-gray-50/50 transition-colors"
                            >
                              <td class="px-4 py-3 w-[40%]">
                                <div class="flex items-center gap-3">
                                  <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-gray-100 to-gray-50 flex items-center justify-center ring-1 ring-gray-200">
                                    <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                                    </svg>
                                  </div>
                                  <div>
                                    <div class="font-medium text-gray-900">{{ orderItem.name }}</div>
                                    <div class="text-xs text-gray-500">{{ orderItem.description }}</div>
                                  </div>
                                </div>
                              </td>
                              <td class="px-4 py-3 w-[20%]">
                                <div :class="{
                                  'text-red-600 font-medium': orderItem.needQuantity > orderItem.stockedQuantity,
                                  'text-gray-700': orderItem.needQuantity <= orderItem.stockedQuantity
                                }">
                                  {{ orderItem.needQuantity }}
                                </div>
                                <div class="text-xs text-gray-500">Stock: {{ orderItem.stockedQuantity }}</div>
                              </td>
                              <td class="px-4 py-3 w-[20%] text-gray-700">
                                {{ formatPrice(orderItem.buyoutPrice) }} Kč
                              </td>
                              <td class="px-4 py-3 w-[20%] font-medium text-gray-900">
                                {{ formatPrice(orderItem.buyoutPrice * orderItem.needQuantity) }} Kč
                              </td>
                            </tr>
                          </tbody>
                          <tfoot class="bg-gray-50/80">
                            <tr>
                              <td colspan="3" class="px-4 py-3 text-sm font-medium text-gray-700 text-right">
                                Total:
                              </td>
                              <td class="px-4 py-3 text-sm font-bold text-gray-900">
                                {{ formatPrice(item.cost) }} Kč
                              </td>
                            </tr>
                          </tfoot>
                        </table>
                      </div>
                    </div>
                  </template>
                </DataTable>
              </div>
            </div>

            <!-- Pagination -->
            <div class="mt-6">
              <Pagination :store="useWorkflowStore" />
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- Order Details Modal -->
    <Modal 
      :show="isDetailModalOpen" 
      title="Order Detail"
      @close="closeOrderDetail"
    >
      <div v-if="selectedOrder" class="space-y-6">
        <!-- Header with close button -->
        <div class="flex items-center justify-between pb-4">
          <div class="flex items-center gap-3">
            <h3 class="text-xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
              Order Details
            </h3>
            <div class="flex items-center gap-2">
              <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium" 
                :class="{
                  'bg-green-50 text-green-700 ring-1 ring-green-600/20': selectedOrder.orderType === 'SELL',
                  'bg-red-50 text-red-700 ring-1 ring-red-600/20': selectedOrder.orderType === 'PURCHASE'
                }"
              >
                {{ selectedOrder.orderType }}
              </span>
              <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium" 
                :class="{
                  'bg-yellow-50 text-yellow-700 ring-1 ring-yellow-600/20': selectedOrder.status === 'PENDING',
                  'bg-green-50 text-green-700 ring-1 ring-green-600/20': selectedOrder.status === 'CONFIRMED',
                  'bg-red-50 text-red-700 ring-1 ring-red-600/20': selectedOrder.status === 'CANCELED'
                }"
              >
                <span class="w-1.5 h-1.5 rounded-full" :class="{
                  'bg-yellow-500': selectedOrder.status === 'PENDING',
                  'bg-green-500': selectedOrder.status === 'CONFIRMED',
                  'bg-red-500': selectedOrder.status === 'CANCELED'
                }"></span>
                {{ selectedOrder.status }}
              </span>
            </div>
          </div>
          <button 
            @click="closeOrderDetail"
            class="p-2 text-gray-400 hover:text-gray-600 rounded-full hover:bg-gray-100 transition-colors"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Order Information -->
        <div class="grid grid-cols-2 gap-3">
          <div class="p-4 rounded-xl bg-gray-50/50 ring-1 ring-gray-100">
            <p class="text-sm font-medium text-gray-500">Order ID</p>
            <p class="text-base font-semibold text-gray-900">#{{ selectedOrder.id }}</p>
          </div>
          <div class="p-4 rounded-xl bg-gray-50/50 ring-1 ring-gray-100">
            <p class="text-sm font-medium text-gray-500">Date</p>
            <p class="text-base font-semibold text-gray-900">
              {{ formatDate(selectedOrder.orderTime) }}
            </p>
          </div>
          <div class="p-4 rounded-xl bg-gray-50/50 ring-1 ring-gray-100">
            <p class="text-sm font-medium text-gray-500">Items Count</p>
            <p class="text-base font-semibold text-gray-900">
              {{ selectedOrder.orderItems?.length || 0 }} items
            </p>
          </div>
          <div class="p-4 rounded-xl bg-gray-50/50 ring-1 ring-gray-100">
            <p class="text-sm font-medium text-gray-500">Total Amount</p>
            <p class="text-lg font-bold text-gray-900">
              {{ formatPrice(selectedOrder.cost) }} Kč
            </p>
          </div>
        </div>

        <!-- Approval/Rejection Section -->
        <div v-if="selectedOrder.status === 'PENDING'" class="pt-4 border-t border-gray-100">
          <div class="rounded-xl bg-gray-50/50 ring-1 ring-gray-100 overflow-hidden">
            <div class="p-4">
              <h3 class="text-base font-semibold text-gray-900 mb-4">Decision</h3>
              <BaseInput 
                v-model="modelComment" 
                type="textarea" 
                label="Comment"
                placeholder="Enter your decision comment..." 
                :rows="3" 
              />
            </div>

            <div class="flex justify-end gap-3 px-4 py-3 bg-gray-100/50">
              <BaseButton 
                :disabled="workflowStore.loading" 
                type="error" 
                class="px-4 py-2 bg-gradient-to-r from-red-600 to-red-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg disabled:opacity-50"
                @click="rejectOrder"
              >
                Reject Order
              </BaseButton>
              <BaseButton 
                :disabled="workflowStore.loading" 
                type="primary" 
                class="px-4 py-2 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg disabled:opacity-50"
                @click="approveOrder"
              >
                Confirm Order
              </BaseButton>
            </div>
          </div>
        </div>

        <!-- Approval History -->
        <div v-if="selectedOrder.status !== 'PENDING'" class="pt-4 border-t border-gray-100">
          <div class="rounded-xl bg-gray-50/50 ring-1 ring-gray-100 overflow-hidden">
            <div class="p-4">
              <h3 class="text-base font-semibold text-gray-900 mb-4">Approval History</h3>
              
              <div class="space-y-3">
                <div class="flex justify-between items-center">
                  <span class="text-sm font-medium text-gray-500">Status</span>
                  <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium" 
                    :class="{
                      'bg-green-50 text-green-700 ring-1 ring-green-600/20': selectedOrder.status === 'CONFIRMED',
                      'bg-red-50 text-red-700 ring-1 ring-red-600/20': selectedOrder.status === 'CANCELED'
                    }"
                  >
                    <span class="w-1.5 h-1.5 rounded-full" :class="{
                      'bg-green-500': selectedOrder.status === 'CONFIRMED',
                      'bg-red-500': selectedOrder.status === 'CANCELED'
                    }"></span>
                    {{ selectedOrder.status }}
                  </span>
                </div>

                <div v-if="selectedOrder.decisionTime" class="flex justify-between items-center">
                  <span class="text-sm font-medium text-gray-500">Decision Time</span>
                  <span class="text-sm text-gray-900">{{ formatDate(selectedOrder.decisionTime) }}</span>
                </div>

                <div v-if="selectedOrder.approvedBy" class="flex justify-between items-center">
                  <span class="text-sm font-medium text-gray-500">Processed By</span>
                  <span class="text-sm text-gray-900">
                    {{ selectedOrder.approvedBy.firstName }} {{ selectedOrder.approvedBy.lastName }}
                    <span class="text-gray-500">({{ selectedOrder.approvedBy.username }})</span>
                  </span>
                </div>

                <div v-if="selectedOrder.comment" class="pt-3 mt-3 border-t border-gray-200">
                  <span class="text-sm font-medium text-gray-500 block mb-2">Comment</span>
                  <div class="p-3 rounded-lg bg-white/50 ring-1 ring-gray-100">
                    <p class="text-sm text-gray-700 whitespace-pre-wrap">{{ parsedComment }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import {
  SearchBar,
  Modal,
  StatusBar,
  EmptyState,
  BaseInput,
  DataTable,
  Pagination,
} from '@/components'

import { useErrorStore, useNotifier, useWorkflowStore, useOrdersStore } from '@/stores'
import BaseButton from '@/components/common/BaseButton.vue'

import { useRouter } from 'vue-router'
import { formatDate, formatPrice } from '@/utils'

defineOptions({ name: 'WorkflowView' })

const $stores = {
  errors: useErrorStore(),
  notifier: useNotifier(),
  workflow: useWorkflowStore(),
  orders: useOrdersStore(),
}

// Stores
const errors = useErrorStore()
const ordersStore = useOrdersStore()
const $notifier = useNotifier()
const workflowStore = useWorkflowStore()
const $router = useRouter()

// Computed
const selectedOrder = computed(() => workflowStore.selectedOrder)
const parsedComment = computed(() => {
  try {
    return selectedOrder.value?.comment ? JSON.parse(selectedOrder.value.comment) : ''
  } catch {
    return selectedOrder.value?.comment || ''
  }
})

const modelComment = computed({
  get: () => {
    try {
      return approvalComment.value ? JSON.parse(approvalComment.value) : ''
      // eslint-disable-next-line no-unused-vars
    } catch (_) {
      return approvalComment.value || ''
    }
  },
  set: (value) => {
    approvalComment.value = JSON.stringify(value)
  },
})

// State
const searchQuery = ref('')
const currentFilter = ref('all')
const isDetailModalOpen = ref(false)
const approvalComment = ref('')
const expandedRows = ref(new Set())

// Watch search query
watch(searchQuery, (newValue) => {
  workflowStore.setSearch(newValue)
})
watch(currentFilter, (newValue) => {
  workflowStore.type = newValue
  workflowStore.pagination.currentPage = 1 // Reset to first page when filter changes
})

const tableHeaders = [
  { field: 'id', label: 'ID', sortable: true },
  { field: 'orderItems', label: 'Items Count', sortable: true },
  { field: 'orderTime', label: 'Date', sortable: true },
  { field: 'cost', label: 'Amount', sortable: true },
  { field: 'orderType', label: 'Type', sortable: true },
  { field: 'status', label: 'Status', sortable: true },
  { field: 'actions', label: '', sortable: false, class: 'text-right' },
]

const filters = [
  { label: 'All', value: 'all' },
  { label: 'Pending', value: 'pending' },
  { label: 'Approved', value: 'confirmed' },
  { label: 'Rejected', value: 'canceled' },
]

onMounted(async () => {
  try {
    await workflowStore.fetchOrders()
  } catch (err) {
    errors.handle(err)
  }
})

// Detail objednávky
const openOrderDetail = async (order) => {
  try {
    // Fetch fresh order data from API
    await workflowStore.getOrderById(order.id)
    approvalComment.value = ''
    isDetailModalOpen.value = true
  } catch (err) {
    errors.handle(err)
  }
}

const closeOrderDetail = () => {
  isDetailModalOpen.value = false
  approvalComment.value = ''
}

const openOrderEdit = async (order) => {
  if (order.status !== 'PENDING') {
    return $notifier.warning('You can edit only order with status PENDING.')
  }
  try {
    const orderFromApi = await workflowStore.getOrderById(order.id)
    ordersStore.loadOrderForEdit(orderFromApi)

    $router.push({ path: '/orders' })
  } catch (err) {
    errors.handle(err)
  }
}

const confirmDelete = async (order) => {
  if (confirm(`Are you sure you want to delete order #${order.id}?`)) {
    try {
      await workflowStore.deleteOrder(order.id)
      $notifier.success('Order was successfully deleted')
      await workflowStore.fetchOrders()
    } catch (err) {
      errors.handle(err)
    }
  }
}

// Update success messages
const approveOrder = async () => {
  if (!selectedOrder.value) return

  try {
    const success = await workflowStore.approveOrder(selectedOrder.value.id, approvalComment.value)
    if (success) {
      await workflowStore.fetchOrders()
      closeOrderDetail()
      $notifier.success('Order was successfully approved')
    }
  } catch (err) {
    errors.handle(err)
  }
}

const rejectOrder = async () => {
  if (!selectedOrder.value) return

  try {
    const success = await workflowStore.rejectOrder(selectedOrder.value.id, approvalComment.value)
    if (success) {
      await workflowStore.fetchOrders()
      closeOrderDetail()
      $notifier.success('Order was successfully rejected')
    }
  } catch (err) {
    errors.handle(err)
  }
}

const toggleRow = (id) => {
  if (expandedRows.value.has(id)) {
    expandedRows.value.delete(id)
  } else {
    expandedRows.value.add(id)
  }
}
</script>

<style scoped src="@/assets/WorkflowView.css" />
