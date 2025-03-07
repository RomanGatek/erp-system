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
              :class="currentFilter === filter.value 
                ? 'bg-blue-500 text-white' 
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            >
              {{ filter.label }}
            </button>
          </div>
          <SearchBar v-model="searchQuery" @update:modelValue="filterOrders" />
        </div>
      </div>

      <!-- Empty state -->
      <EmptyState v-if="!filteredOrders.length" message="No orders to display" />

      <!-- Data table -->
      <template v-else>
        <div>
          <DataTable
            :headers="tableHeaders"
            :items="filteredOrders"
            :sort-by="setSorting"
            :sorting="sorting"
            :on-edit="openOrderDetail"
            :on-delete="() => {}"
          >
            <template #row="{ item }">
              <td class="px-6 py-4 whitespace-nowrap text-gray-700 font-medium">
                {{ item.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-gray-600">
                {{ item.product ? item.product.name : 'N/A' }}
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
                    'bg-yellow-100 text-yellow-800': item.status === 'PENDING',
                    'bg-green-100 text-green-800': item.status === 'CONFIRMED',
                    'bg-red-100 text-red-800': item.status === 'CANCELED'
                  }"
                >
                  {{ getStatusText(item.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div v-if="item.type === 'STOCK'" 
                  class="flex items-center justify-center w-8 h-8 rounded-full bg-green-100"
                  title="Stock Order"
                >
                  <svg
                    class="w-5 h-5 text-green-600 transform -translate-x-px"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2.5"
                      d="M20 12H4m0 0l6-6m-6 6l6 6"
                    />
                  </svg>
                </div>
                <div v-else 
                  class="flex items-center justify-center w-8 h-8 rounded-full bg-red-100"
                  title="Customer Order"
                >
                  <svg
                    class="w-5 h-5 text-red-600 transform translate-x-px"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2.5"
                      d="M4 12h16m0 0l-6-6m6 6l-6 6"
                    />
                  </svg>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                <button
                  @click="openOrderDetail(item)"
                  class="text-blue-600 hover:text-blue-900 p-1 rounded hover:bg-blue-50 cursor-pointer"
                  title="View Details"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                </button>
                <button
                  @click="confirmDelete(item)"
                  class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50 cursor-pointer"
                  title="Delete Order"
                >
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </td>
            </template>
          </DataTable>
        </div>
      </template>
    </div>

    <!-- Order Details Modal -->
    <Modal :show="isDetailModalOpen" title="Order Details" @close="closeOrderDetail">
      <div v-if="selectedOrder" class="space-y-6">
        <!-- Basic Information -->
        <div class="space-y-3 pb-4 border-b border-gray-200">
          <div class="flex justify-between">
            <h3 class="text-lg font-medium text-gray-800">Basic Information</h3>
            <span
              class="px-2 py-1 text-xs font-medium rounded-full"
              :class="{
                'bg-yellow-100 text-yellow-800': selectedOrder.status === 'PENDING',
                'bg-green-100 text-green-800': selectedOrder.status === 'CONFIRMED',
                'bg-red-100 text-red-800': selectedOrder.status === 'CANCELED'
              }"
            >
              {{ getStatusText(selectedOrder.status) }}
            </span>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-gray-500">Order ID</p>
              <p class="font-medium">{{ selectedOrder.id }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Date</p>
              <p class="font-medium">{{ formatDate(selectedOrder.orderTime) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Product</p>
              <p class="font-medium">{{ selectedOrder.product ? selectedOrder.product.name : 'N/A' }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Quantity</p>
              <p class="font-medium">{{ selectedOrder.amount }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Total Amount</p>
              <p class="font-medium">{{ formatPrice(selectedOrder.cost) }} Kč</p>
            </div>
          </div>
        </div>

        <!-- Approval/Rejection Section -->
        <div v-if="selectedOrder.status === 'PENDING'" class="space-y-3 pt-4 border-t border-gray-200">
          <h3 class="text-lg font-medium text-gray-800">Decision</h3>
          <div>
            <label for="comment" class="block text-sm font-medium text-gray-700 mb-1">
              Comment
            </label>
            <textarea
              id="comment"
              v-model="approvalComment"
              rows="3"
              class="w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              placeholder="Enter your decision comment..."
            ></textarea>
          </div>
          <div class="flex justify-end space-x-3 pt-2">
            <button
              @click="rejectOrder"
              class="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition"
              :disabled="workflowStore.loading"
            >
              Reject
            </button>
            <button
              @click="approveOrder"
              class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition"
              :disabled="workflowStore.loading"
            >
              Approve
            </button>
          </div>
        </div>

        <!-- Approval History -->
        <div v-if="selectedOrder.status !== 'PENDING'" class="space-y-3 pt-4 border-t border-gray-200">
          <h3 class="text-lg font-medium text-gray-800">Approval History</h3>
          <div class="bg-gray-50 p-4 rounded-lg">
            <p class="text-sm text-gray-700">
              <span class="font-medium">Status:</span> {{ getStatusText(selectedOrder.status) }}
            </p>
            <p class="text-sm text-gray-700" v-if="selectedOrder.decisionTime">
              <span class="font-medium">Approved/Rejected:</span> {{ formatDate(selectedOrder.decisionTime) }}
            </p>
            <p class="text-sm text-gray-700" v-if="selectedOrder.approvedBy">
              <span class="font-medium">User:</span> {{ selectedOrder.approvedBy }}
            </p>
            <p class="text-sm text-gray-700 mt-2" v-if="selectedOrder.comment">
              <span class="font-medium">Comment:</span>
            </p>
            <p class="text-sm text-gray-700 mt-1 p-2 bg-white rounded border border-gray-200" v-if="selectedOrder.comment">
              {{ selectedOrder.comment }}
            </p>
          </div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  DataTable, 
  SearchBar, 
  Modal, 
  StatusBar, 
  EmptyState 
} from '@/components/common'
import { useErrorStore } from '@/stores/errors.js'
import { useNotifier } from '@/stores/notifier.js'
import { useWorkflowStore } from '@/stores/workflow.store.js'

defineOptions({
  name: 'WorkflowView',
})

// Stores
const errorStore = useErrorStore()
const $notifier = useNotifier()
const workflowStore = useWorkflowStore()

// State
const searchQuery = ref('')
const currentFilter = ref('all')
const isDetailModalOpen = ref(false)
const approvalComment = ref('')

// Table headers in English
const tableHeaders = [
  { field: 'id', label: 'ID', sortable: true },
  { field: 'product.name', label: 'Product', sortable: true },
  { field: 'orderTime', label: 'Date', sortable: true },
  { field: 'cost', label: 'Amount', sortable: true },
  { field: 'status', label: 'Status', sortable: true },
  { field: 'type', label: 'Type', sortable: false },
  { field: 'actions', label: '', sortable: false, class: 'text-right' },
]

// Filters in English
const filters = [
  { label: 'All', value: 'all' },
  { label: 'Pending', value: 'pending' },
  { label: 'Approved', value: 'confirmed' },
  { label: 'Rejected', value: 'canceled' },
]

// Sorting state
const sorting = ref({
  field: 'orderTime',
  direction: 'desc'
})

// Fetch data
onMounted(async () => {
  try {
    await workflowStore.fetchOrders()
  } catch (err) {
    errorStore.handle(err)
  }
})

// Computed selected order from store
const selectedOrder = computed(() => workflowStore.selectedOrder)

// Filtrované objednávky podle vyhledávání a filtru
const filteredOrders = computed(() => {
  // Ensure we have an array, not HTML content
  if (!Array.isArray(workflowStore.items)) {
    console.error('Orders is not an array:', workflowStore.items);
    return [];
  }
  
  return workflowStore.filteredOrders(
    currentFilter.value === 'all' ? 'all' : currentFilter.value, 
    searchQuery.value
  );
})

// Metody pro práci s tabulkou
const setSorting = (field) => {
  if (sorting.value.field === field) {
    sorting.value.direction = sorting.value.direction === 'asc' ? 'desc' : 'asc'
  } else {
    sorting.value.field = field
    sorting.value.direction = 'asc'
  }
}

const filterOrders = () => {
  // Filtrování se provádí automaticky díky computed property
}

// Formátování data
const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('cs-CZ', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Update the status text function to English
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING': return 'Pending'
    case 'CONFIRMED': return 'Approved'
    case 'CANCELED': return 'Rejected'
    default: return status
  }
}

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

// Update price formatting to 2 decimal places without rounding
const formatPrice = (price) => {
  return (Math.floor(price * 100) / 100).toFixed(2)
}

const confirmDelete = async (order) => {
  if (confirm(`Are you sure you want to delete order #${order.id}?`)) {
    try {
      await workflowStore.deleteOrder(order.id);
      $notifier.success('Order was successfully deleted');
      await workflowStore.fetchOrders();
    } catch (err) {
      errorStore.handle(err);
    }
  }
}

// Update success messages
const approveOrder = async () => {
  if (!selectedOrder.value) return
  
  try {
    const success = await workflowStore.approveOrder(selectedOrder.value.id, approvalComment.value);
    if (success) {
      await workflowStore.fetchOrders();
      closeOrderDetail();
      $notifier.success('Order was successfully approved');
    }
  } catch (err) {
    errorStore.handle(err);
  }
}

const rejectOrder = async () => {
  if (!selectedOrder.value) return
  
  try {
    const success = await workflowStore.rejectOrder(selectedOrder.value.id, approvalComment.value);
    if (success) {
      await workflowStore.fetchOrders();
      closeOrderDetail();
      $notifier.success('Order was successfully rejected');
    }
  } catch (err) {
    errorStore.handle(err);
  }
}
</script>

<style scoped>
/* Přidáme hezké přechody */
.transition {
  transition: all 0.2s ease;
}

/* Oživení tlačítek při najetí */
button:hover {
  transform: translateY(-1px);
}

/* Vylepšený styl pro tabulku */
.orders-table {
  border-radius: 8px;
  overflow: hidden;
}

/* Drobné zvýraznění pro status tagy */
span[class*="bg-"] {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* Vylepšení modálního okna */
:deep(.modal-content) {
  max-width: 700px;
}
</style> 
