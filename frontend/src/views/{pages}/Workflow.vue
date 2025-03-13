<!--suppress ALL -->
<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <StatusBar
        :error="errorStore.errors.general"
        :loading="workflowStore.loading"
        class="mb-4"
        @clear-error="errorStore.clearServerErrors()"
      />

      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-800">Approval Workflow</h2>
        <div class="flex items-center space-x-4">
          <div class="flex space-x-2">
            <button
              v-for="filter in filters"
              :key="filter.value"
              @click="currentFilter = filter.value"
              class="px-3 py-1.5 text-sm rounded-lg transition"
              :class="
                currentFilter === filter.value
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              "
            >
              {{ filter.label }}
            </button>
          </div>
          <SearchBar
            v-model="searchQuery"
            @update:modelValue="(value) => workflowStore.setSearch(value)"
          />
        </div>
      </div>

      <!-- Empty state -->
      <EmptyState v-if="!workflowStore.filtered.length" message="No orders to display" />

      <!-- Data table -->
      <template v-else>
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
            <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
              {{ item.id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-gray-600">
              {{ item.orderItems?.length || 0 }} items
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-gray-600">
              {{ formatDate(item.orderTime) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-gray-600">
              {{ formatPrice(item.cost) }} Kč
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                class="px-2 py-1 text-xs font-medium rounded-full"
                :class="{
                  'bg-green-100 text-green-800': item.orderType === 'SELL',
                  'bg-red-100 text-red-800': item.orderType === 'PURCHASE',
                }"
              >
                {{ item.orderType }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                class="px-2 py-1 text-xs font-medium rounded-full"
                :class="{
                  'bg-yellow-100 text-yellow-800': item.status === 'PENDING',
                  'bg-green-100 text-green-800': item.status === 'CONFIRMED',
                  'bg-red-100 text-red-800': item.status === 'CANCELED',
                }"
              >
                {{ item.status }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
              <button
                @click.stop="openOrderDetail(item)"
                class="text-blue-600 hover:text-blue-900 p-1 rounded hover:bg-blue-50"
                title="View Details"
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
                @click.stop="openOrderEdit(item)"
                class="text-green-600 hover:text-green-900 p-1 rounded hover:bg-blue-50"
                title="Edit Order"
              >
                <svg
                  fill="none"
                  stroke="currentColor"
                  x="0px"
                  y="0px"
                  class="w-5 h-5"
                  viewBox="0 0 24 24"
                >
                  <path
                    d="M 18 2 L 15.585938 4.4140625 L 19.585938 8.4140625 L 22 6 L 18 2 z M 14.076172 5.9238281 L 3 17 L 3 21 L 7 21 L 18.076172 9.9238281 L 14.076172 5.9238281 z"
                  ></path>
                </svg>
              </button>
              <button
                @click.stop="confirmDelete(item)"
                class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50"
                title="Delete Order"
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
            </td>
          </template>

          <template #expanded-content="{ item }">
            <div class="bg-white rounded-xl shadow-sm ring-1 ring-gray-200">
              <div class="p-4">
                <div class="flex items-center justify-between mb-3">
                  <h4 class="text-sm font-medium text-gray-700">Order Items</h4>
                  <button
                    @click.stop="toggleRow(item.id)"
                    class="p-1 text-gray-400 hover:text-gray-500 rounded-full hover:bg-gray-100"
                  >
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M6 18L18 6M6 6l12 12"
                      />
                    </svg>
                  </button>
                </div>
                <div class="overflow-x-auto">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50/80 backdrop-blur-sm">
                      <tr>
                        <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">
                          Product
                        </th>
                        <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">
                          Quantity
                        </th>
                        <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Price</th>
                        <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Total</th>
                      </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                      <tr
                        v-for="orderItem in item.orderItems"
                        :key="orderItem.id"
                        class="text-sm hover:bg-gray-50/80"
                      >
                        <td class="px-4 py-2 w-[40%]">
                          <div class="text-gray-900 font-medium">{{ orderItem.name }}</div>
                          <div class="text-xs text-gray-500">{{ orderItem.description }}</div>
                        </td>
                        <td class="px-4 py-2 w-[20%]">
                          <div
                            :class="{
                              'text-red-600 font-medium':
                                orderItem.needQuantity > orderItem.stockedQuantity,
                              'text-gray-600': orderItem.needQuantity <= orderItem.stockedQuantity,
                            }"
                          >
                            {{ orderItem.needQuantity }}
                          </div>
                          <div class="text-xs text-gray-500">
                            Stock: {{ orderItem.stockedQuantity }}
                          </div>
                        </td>
                        <td class="px-4 py-2 w-[20%] text-gray-600">
                          {{ formatPrice(orderItem.buyoutPrice) }} Kč
                        </td>
                        <td class="px-4 py-2 w-[20%] font-medium text-gray-900">
                          {{ formatPrice(orderItem.buyoutPrice * orderItem.needQuantity) }} Kč
                        </td>
                      </tr>
                    </tbody>
                    <tfoot class="bg-gray-50/80 backdrop-blur-sm">
                      <tr>
                        <td
                          colspan="3"
                          class="px-4 py-2 text-sm font-medium text-gray-700 text-right"
                        >
                          Total:
                        </td>
                        <td class="px-4 py-2 text-sm font-bold text-gray-900">
                          {{ formatPrice(item.cost) }} Kč
                        </td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            </div>
          </template>
        </DataTable>

        <Pagination :store="useWorkflowStore" />
      </template>
    </div>

    <!-- Order Details Modal -->
    <Modal :show="isDetailModalOpen" @close="closeOrderDetail" title="Order Details">
      <div v-if="selectedOrder" class="space-y-4">
        <!-- Header with close button -->
        <div class="flex items-center justify-between pb-2 border-b border-gray-200">
          <h6 class="text-xl font-bold text-gray-900"></h6>
          <button
            @click="closeOrderDetail"
            class="p-1.5 text-gray-400 hover:text-gray-500 rounded-full hover:bg-gray-100 transition-colors"
            title="Close"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <!-- Basic Information -->
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Basic Information</h3>
            <div class="flex items-center gap-1.5">
              <span
                class="px-2 py-0.5 text-xs font-medium rounded-full"
                :class="{
                  'bg-green-100 text-green-800': selectedOrder.orderType === 'SELL',
                  'bg-red-100 text-red-800': selectedOrder.orderType === 'PURCHASE',
                }"
              >
                {{ selectedOrder.orderType }}
              </span>
              <span
                class="px-2 py-0.5 text-xs font-medium rounded-full"
                :class="{
                  'bg-yellow-100 text-yellow-800': selectedOrder.status === 'PENDING',
                  'bg-green-100 text-green-800': selectedOrder.status === 'CONFIRMED',
                  'bg-red-100 text-red-800': selectedOrder.status === 'CANCELED',
                }"
              >
                {{ selectedOrder.status }}
              </span>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4 p-4 bg-gray-50 rounded-xl">
            <div class="space-y-1">
              <p class="text-sm font-medium text-gray-500">Order ID</p>
              <p class="text-base font-semibold text-gray-900">#{{ selectedOrder.id }}</p>
            </div>
            <div class="space-y-1">
              <p class="text-sm font-medium text-gray-500">Date</p>
              <p class="text-base font-semibold text-gray-900">
                {{ formatDate(selectedOrder.orderTime) }}
              </p>
            </div>
            <div class="space-y-1">
              <p class="text-sm font-medium text-gray-500">Items Count</p>
              <p class="text-base font-semibold text-gray-900">
                {{ selectedOrder.orderItems?.length || 0 }} items
              </p>
            </div>
            <div class="space-y-1">
              <p class="text-sm font-medium text-gray-500">Total Amount</p>
              <p class="text-lg font-bold text-gray-900">
                {{ formatPrice(selectedOrder.cost) }} Kč
              </p>
            </div>
          </div>
        </div>

        <!-- Approval/Rejection Section -->
        <div
          v-if="selectedOrder.status === 'PENDING'"
          class="space-y-4 pt-4 border-t border-gray-200"
        >
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Decision</h3>
          </div>

          <div class="bg-white rounded-xl border border-gray-200">
            <div class="p-4">
              <BaseInput
                v-model="modelComment"
                type="textarea"
                label="Comment"
                placeholder="Enter your decision comment..."
                :rows="3"
              />
            </div>

            <div class="flex justify-end gap-2 px-4 py-3 bg-gray-50 rounded-b-xl">
              <button
                @click="rejectOrder"
                class="px-3 py-1.5 text-sm font-medium text-red-700 bg-red-50 rounded-lg hover:bg-red-100 focus:outline-none focus:ring-2 focus:ring-red-500/20 transition-all disabled:opacity-50"
                :disabled="workflowStore.loading"
              >
                Reject Order
              </button>
              <button
                @click="approveOrder"
                class="px-3 py-1.5 text-sm font-medium text-white bg-blue-500 rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all disabled:opacity-50"
                :disabled="workflowStore.loading"
              >
                Approve Order
              </button>
            </div>
          </div>
        </div>

        <!-- Approval History -->
        <div
          v-if="selectedOrder.status !== 'PENDING'"
          class="space-y-4 pt-4 border-t border-gray-200"
        >
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">Approval History</h3>
          </div>

          <div class="bg-gray-50 rounded-xl divide-y divide-gray-200">
            <div class="p-4 space-y-3">
              <div class="flex justify-between items-center">
                <span class="text-sm font-medium text-gray-500">Status</span>
                <span
                  class="px-2 py-0.5 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-50 text-green-700 ring-1 ring-green-600/20':
                      selectedOrder.status === 'CONFIRMED',
                    'bg-red-50 text-red-700 ring-1 ring-red-600/20':
                      selectedOrder.status === 'CANCELED',
                  }"
                >
                  {{ selectedOrder.status }}
                </span>
              </div>

              <div v-if="selectedOrder.decisionTime" class="flex justify-between items-center">
                <span class="text-sm font-medium text-gray-500">Decision Time</span>
                <span class="text-sm text-gray-900">{{
                  formatDate(selectedOrder.decisionTime)
                }}</span>
              </div>

              <div v-if="selectedOrder.approvedBy" class="flex justify-between items-center">
                <span class="text-sm font-medium text-gray-500">Processed By</span>
                <span class="text-sm text-gray-900">
                  {{ selectedOrder.approvedBy.firstName }} {{ selectedOrder.approvedBy.lastName }}
                  <span class="text-gray-500">({{ selectedOrder.approvedBy.username }})</span>
                </span>
              </div>
            </div>

            <div v-if="selectedOrder.comment" class="p-4 space-y-2">
              <span class="text-sm font-medium text-gray-500">Comment</span>
              <p
                class="mt-1 text-sm text-gray-700 bg-white p-3 rounded-lg border border-gray-200 whitespace-pre-wrap"
              >
                {{ parsedComment }}
              </p>
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
const errorStore = useErrorStore()
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
    errorStore.handle(err)
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
    errorStore.handle(err)
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
    errorStore.handle(err)
  }
}

const confirmDelete = async (order) => {
  if (confirm(`Are you sure you want to delete order #${order.id}?`)) {
    try {
      await workflowStore.deleteOrder(order.id)
      $notifier.success('Order was successfully deleted')
      await workflowStore.fetchOrders()
    } catch (err) {
      errorStore.handle(err)
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
    errorStore.handle(err)
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
    errorStore.handle(err)
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
