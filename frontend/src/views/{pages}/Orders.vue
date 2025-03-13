<template>
  <div class="p-8">
    <div class="flex gap-6">
      <!-- Main Content -->
      <div class="flex-1 bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
        <StatusBar :error="errors.general" :loading="loading" class="mb-4" @clear-error="errors.clearServerErrors()" />

        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800">
            {{ isEditing ? 'Edit' : 'Create New' }} Order
          </h2>
          <p class="text-sm text-gray-500 mt-1 mb-2">Select order type</p>
          <div class="flex gap-2">
            <button @click="orderType = 'PURCHASE'"
              class="px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-300 border cursor-pointer transform hover:scale-105"
              :class="[
                orderType === 'PURCHASE'
                  ? 'bg-gradient-to-r from-red-500 to-red-400 text-white border-red-500 shadow-md'
                  : 'border-red-500 text-red-700 hover:bg-gradient-to-r hover:from-red-50 hover:to-red-100',
              ]">
              Purchase
            </button>
            <button @click="orderType = 'SELL'"
              class="px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-300 border cursor-pointer transform hover:scale-105"
              :class="[
                orderType === 'SELL'
                  ? 'bg-gradient-to-r from-green-500 to-green-400 text-white border-green-500 shadow-md'
                  : 'border-green-500 text-green-700 hover:bg-gradient-to-r hover:from-green-50 hover:to-green-100',
              ]">
              Sell
            </button>
          </div>
        </div>

        <!-- Order Form -->
        <form @submit.prevent="isEditing ? editOrder : createOrder()" class="space-y-6">
          <!-- Product Selection -->
          <MultiProductSelect :items="products" v-model="selectedProducts" placeholder="Search and add products..."
            class="product-select" :order-type="orderType" />

          <!-- Order Time -->
          <DateTimePicker v-model="orderTime" label="Order Time" :error="orderTimeError" />

          <!-- Comment with Toggle -->
          <div class="space-y-2">
            <div class="flex items-center justify-between">
              <label class="block text-sm font-medium text-gray-700">Comment</label>
              <button type="button" @click="showCommentField = !showCommentField"
                class="flex items-center gap-1 text-xs text-gray-500 hover:text-gray-700 transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                </svg>
                <span>{{ showCommentField ? 'Hide comment' : 'Add comment' }}</span>
              </button>
            </div>

            <transition enter-active-class="transition ease-out duration-200"
              enter-from-class="transform opacity-0 scale-95 -translate-y-4"
              enter-to-class="transform opacity-100 scale-100 translate-y-0"
              leave-active-class="transition ease-in duration-150"
              leave-from-class="transform opacity-100 scale-100 translate-y-0"
              leave-to-class="transform opacity-0 scale-95 -translate-y-4" @before-leave="isAnimating = true"
              @after-leave="isAnimating = false">
              <div v-if="showCommentField || comment" class="relative">
                <textarea v-model="comment" placeholder="Enter order comment..." rows="3" maxlength="500"
                  @blur="handleCommentBlur"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500 transition-all"></textarea>
                <div class="text-xs text-gray-400 text-right mt-1">{{ comment.length }}/500</div>
              </div>
            </transition>
          </div>

          <!-- Stock Information -->
          <div v-if="selectedProducts.length > 0"
            class="bg-gradient-to-br from-gray-50 to-gray-100 p-4 rounded-xl border border-gray-200/50 shadow-sm">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <div class="p-2 bg-white rounded-lg shadow-sm relative group cursor-help">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                  </svg>
                  <!-- Hover tooltip -->
                  <div
                    class="absolute left-0 bottom-full mb-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none">
                    <div class="bg-gray-800 text-white px-3 py-2 rounded-lg shadow-lg text-xs whitespace-nowrap">
                      {{ getTotalItemCount() }} items will be
                      {{ orderType === 'SELL' ? 'removed from' : 'added to' }} inventory
                      <div class="absolute left-3 top-full h-2 w-2 bg-gray-800 transform rotate-45"></div>
                    </div>
                  </div>
                </div>
                <p class="text-sm font-medium text-gray-700">Current Stock</p>
              </div>
              <div class="flex items-center gap-3">
                <div class="p-2 bg-white rounded-lg shadow-sm">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-700">Total Price</p>
                  <p class="text-xl font-bold text-gray-900">{{ formatPrice(totalPrice) }} Kč</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="mt-8 flex justify-center gap-5">
            <button type="button" @click="isEditing ? editOrder() : createOrder()"
              class="group px-5 py-2.5 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0 relative overflow-hidden shadow-md"
              :disabled="loading || !isValid">
              <span class="relative z-10">{{ isEditing ? 'Edit' : 'Create' }} Order</span>
              <div
                class="absolute inset-0 bg-gradient-to-r from-blue-500 to-blue-300 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
              </div>
            </button>
            <button v-if="isEditing" type="button" @click="cancelEdit"
              class="group px-5 py-2.5 bg-gradient-to-r from-gray-500 to-gray-300 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0 relative overflow-hidden shadow-md">
              <span class="relative z-10">Cancel</span>
              <div
                class="absolute inset-0 bg-gradient-to-r from-gray-500 to-gray-300 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
              </div>
            </button>
          </div>
        </form>
      </div>

      <!-- Recent Orders Sidebar -->
      <div class="w-96 bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
        <h3 class="text-lg font-medium text-gray-800 mb-4">
          Recent {{ orderType === 'PURCHASE' ? 'Purchase' : 'Sell' }} Orders
        </h3>
        <div class="space-y-4 h-[600px] overflow-y-auto pr-2 custom-scrollbar">
          <div v-for="order in filteredRecentOrders" :key="order.id"
            class="p-4 rounded-lg border border-gray-100 hover:border-gray-200 transition-all duration-200 relative overflow-hidden"
            :class="{
              'order-sell': order.orderType === 'SELL',
              'order-purchase': order.orderType === 'PURCHASE',
            }">
            <div class="flex justify-between items-start mb-2">
              <span class="text-sm font-medium text-gray-900">#{{ order.id }}</span>
              <div class="flex items-center gap-1">
                <span class="px-1.5 py-0.5 text-[10px] font-medium rounded-full" :class="{
                  'bg-yellow-100 text-yellow-800': order.status === 'PENDING',
                  'bg-green-100 text-green-800': order.status === 'CONFIRMED',
                  'bg-red-100 text-red-800': order.status === 'CANCELED',
                }">
                  {{ getStatusText(order.status) }}
                </span>
              </div>
            </div>
            <div class="space-y-1">
              <p class="text-sm text-gray-600">Items count: {{ order.orderItems.length }}</p>
              <div class="flex justify-between text-sm">
                <span class="font-medium">{{ formatPrice(order.cost) }} Kč</span>
              </div>
              <p class="text-xs text-gray-400">{{ formatDate(order.orderTime) }}</p>
              <p v-if="order.comment" class="text-xs text-gray-500 mt-2 line-clamp-2">
                {{ parseComment(order.comment) }}
              </p>
              <p v-if="order.approvedBy" class="text-xs text-gray-400">
                {{ order.status === 'CONFIRMED' ? 'Approved' : 'Rejected' }} by:
                {{ order.approvedBy.username }}
              </p>
              <p v-if="order.decisionTime" class="text-xs text-gray-400">
                Decision: {{ formatDate(order.decisionTime) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted, onBeforeUnmount } from 'vue'
import { StatusBar, DateTimePicker, MultiProductSelect } from '@/components'
import { useErrorStore } from '@/stores/errors.store.js'
import { useNotifier } from '@/stores/notifier.store.js'
import { useOrdersStore } from '@/stores/orders.store.js'
import api from '@/services/api'
import { formatDate } from '../../utils/index.js'
import { useRouter } from 'vue-router'

function parseComment(comment) {
  try {
    return comment ? JSON.parse(comment) : ''
    // eslint-disable-next-line no-unused-vars
  } catch (_) {
    return comment || ''
  }
}

// Stores
const ordersStore = useOrdersStore()
const errors = useErrorStore()
const $router = useRouter()
const $notifier = useNotifier()

// State
const loading = ref(false)
const products = ref([])
const selectedProducts = ref([])
const currentStock = ref(null)
const recentOrders = ref([])

// Create initial date 2 minutes in the future
const createFutureDate = () => {
  const futureDate = new Date()
  futureDate.setMinutes(futureDate.getMinutes() + 2)
  return futureDate.toISOString()
}

const orderTime = ref(createFutureDate())
const comment = ref('')
const orderType = ref('SELL')
const showCommentField = ref(false)
const prevCommentValue = ref('')
const isAnimating = ref(false)
const isEditing = ref(false)

// Computed
const totalPrice = computed(() => {
  if (selectedProducts.value.length === 0) return 0

  return selectedProducts.value.reduce((total, product) => {
    const priceToUse = orderType.value === 'SELL' ? product.buyoutPrice : product.purchasePrice
    return total + priceToUse * product.quantity
  }, 0)
})

const orderTimeError = computed(() => {
  if (!orderTime.value) return 'Order time is required'
  const selectedTime = new Date(orderTime.value)
  const now = new Date()
  if (selectedTime < now) return 'Order time cannot be in the past'
  return ''
})

const isValid = computed(() => {
  return selectedProducts.value.length > 0 && !orderTimeError.value
})

const filteredRecentOrders = computed(() => {
  return recentOrders.value.filter((order) => order.orderType === orderType.value)
})

// Methods
const formatPrice = (price) => {
  // Convert to string and split by decimal point
  const str = String(price)
  const parts = str.split('.')

  // If there's no decimal part, add .00
  if (parts.length === 1) {
    return `${parts[0]}.00`
  }

  // Get the decimal part and truncate to 2 places (no rounding)
  let decimal = parts[1]
  if (decimal.length > 2) {
    decimal = decimal.substring(0, 2)
  } else if (decimal.length === 1) {
    decimal = decimal + '0'
  }

  return `${parts[0]}.${decimal}`
}

const getStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return 'pending'
    case 'CONFIRMED':
      return 'confirmed'
    case 'CANCELED':
      return 'canceled'
    default:
      return status
  }
}

const getTotalItemCount = () => {
  return selectedProducts.value.reduce((total, product) => {
    return total + product.quantity
  }, 0)
}

const fetchProducts = async () => {
  const [response, err] = await api.products().getAll()
  products.value = response
  if (err) errors.handle(err)
}

const fetchRecentOrders = async () => {
  const [response, err] = await api.orders().getAll()
  recentOrders.value = response
  if (err) errors.handle(err)
}

const checkStock = async () => {
  if (selectedProducts.value.length === 0) {
    currentStock.value = null
    return
  }

  try {
    const [data, err] = await api.inventory().getAll()
    const inventoryItems = data.filter((item) =>
      selectedProducts.value.some((product) => product.id === item.product.id),
    )
    currentStock.value = inventoryItems.reduce((total, item) => total + item.quantity, 0)
    if (err) errors.handle(err)
  } catch (err) {
    errors.handle(err)
    currentStock.value = 0 // Set to 0 on error, not null
  }
}

const createOrder = async () => {
  if (!isValid.value) return

  loading.value = true

  const computedIds = selectedProducts.value.map((product) => {
    return {
      quantity: product.quantity,
      id: product.id,
    }
  })

  try {
    await api.orders().create({
      products: computedIds,
      orderType: orderType.value,
      comment: comment.value,
    })

    // Reset form
    selectedProducts.value = []
    comment.value = ''
    orderTime.value = createFutureDate()

    // Refresh orders list
    await fetchRecentOrders()
    $notifier.success('Orders created successfully')
  } catch (err) {
    errors.handle(err)
  } finally {
    loading.value = false
  }
}

async function editOrder() {
  if (!isValid.value) return

  loading.value = true

  const computedIds = selectedProducts.value.map((product) => {
    return {
      quantity: product.quantity,
      id: product.id,
    }
  })
  const id = ordersStore.order.id

  try {
    await api.orders().update(id, {
      products: computedIds,
      orderType: orderType.value,
      comment: comment.value,
    })

    // Reset form
    selectedProducts.value = []
    comment.value = ''
    orderTime.value = createFutureDate()

    $notifier.success(`Order with id ${id} was successfully edited`)

    await $router.push({ path: '/workflow' })
  } catch (err) {
    errors.handle(err)
  } finally {
    loading.value = false
  }
}

function cancelEdit() {
  selectedProducts.value = []
  currentStock.value = null
  orderTime.value = createFutureDate()
  orderType.value = 'SELL'
  ordersStore.order = null

  $router.push({ path: '/workflow' })
}

const handleCommentBlur = () => {
  // Only hide if not already animating
  if (!isAnimating.value) {
    // Hide the comment field
    showCommentField.value = false

    // Check if comment changed and show notification if needed
    if (
      (prevCommentValue.value.trim() !== '' || comment.value.trim() !== '') &&
      prevCommentValue.value !== comment.value
    ) {
      if (comment.value.trim() === '') {
        $notifier.info('Comment has been removed', 'Comment Updated')
      } else if (prevCommentValue.value.trim() === '') {
        $notifier.info('Comment has been added', 'Comment Updated')
      } else {
        $notifier.info('Comment has been updated', 'Comment Updated')
      }
      prevCommentValue.value = comment.value
    }
  }
}

// Watchers
watch(selectedProducts, async () => {
  await checkStock()
})

// Lifecycle
onMounted(async () => {
  await Promise.all([fetchProducts(), fetchRecentOrders()])

  if (ordersStore.order !== null) {
    isEditing.value = true

    if (Array.isArray(ordersStore.order.orderItems) && ordersStore.order.orderItems.length > 0) {
      selectedProducts.value = ordersStore.order.orderItems.map((selectedOrder) => {
        return {
          ...selectedOrder.inventoryItem.product,
          quantity: selectedOrder.quantity,
        }
      })
    }

    comment.value = ordersStore.order.comment
    orderType.value = ordersStore.order.orderType
  }
})

onBeforeUnmount(async () => {
  ordersStore.order = null
})
</script>

<style scoped>
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}

.order-sell {
  box-shadow: 0 2px 4px rgba(34, 197, 94, 0.05);
}

.order-purchase {
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.05);
}

.order-sell:hover {
  box-shadow: 0 4px 6px rgba(34, 197, 94, 0.1);
}

.order-purchase:hover {
  box-shadow: 0 4px 6px rgba(239, 68, 68, 0.1);
}

.order-sell::after,
.order-purchase::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 12px;
  height: 12px;
  clip-path: polygon(0 0, 0% 100%, 100% 100%);
}

.order-sell::after {
  background-color: rgb(34 197 94);
  /* green-500 */
}

.order-purchase::after {
  background-color: rgb(239 68 68);
  /* red-500 */
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.2);
  }

  70% {
    box-shadow: 0 0 0 6px rgba(34, 197, 94, 0);
  }

  100% {
    box-shadow: 0 0 0 0 rgba(34, 197, 94, 0);
  }
}

button {
  backface-visibility: hidden;
  -webkit-font-smoothing: subpixel-antialiased;
}

button:active {
  transform: scale(0.95);
}

[class*='bg-gradient-to-r'] {
  background-size: 200% auto;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

[class*='bg-gradient-to-r']:hover {
  background-position: right center;
}

.product-select :deep(.product-item) {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  background: white;
  margin-bottom: 0.5rem;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
}

.product-select :deep(.product-item:hover) {
  border-color: #d1d5db;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.product-select :deep(.product-info) {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr auto 120px;
  align-items: center;
  gap: 1rem;
}

.product-select :deep(.product-name) {
  font-weight: 500;
  color: #1f2937;
}

.product-select :deep(.product-description) {
  color: #6b7280;
  font-size: 0.875rem;
}

.product-select :deep(.product-price) {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  justify-content: flex-end;
  font-weight: 500;
}

.product-select :deep(.price-regular) {
  color: #10b981;
}

.product-select :deep(.price-special) {
  color: #ef4444;
  text-decoration: line-through;
  font-size: 0.875rem;
}

.product-select :deep(.quantity-input) {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
  text-align: center;
  font-size: 0.875rem;
  transition: all 0.2s ease;
}

.product-select :deep(.quantity-input:focus) {
  outline: none;
  border-color: #60a5fa;
  box-shadow: 0 0 0 2px rgba(96, 165, 250, 0.2);
}

.product-select :deep(.search-input) {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  transition: all 0.2s ease;
  background-color: #f9fafb;
  margin-bottom: 1rem;
}

.product-select :deep(.search-input:focus) {
  outline: none;
  border-color: #60a5fa;
  box-shadow: 0 0 0 2px rgba(96, 165, 250, 0.2);
  background-color: white;
}
</style>
