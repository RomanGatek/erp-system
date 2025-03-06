<template>
  <div class="p-8">
    <div class="flex gap-6">
      <!-- Main Content -->
      <div class="flex-1 bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
        <StatusBar
          :error="errorStore.errors.general"
          :loading="loading"
          class="mb-4"
          @clear-error="errorStore.clearServerErrors()"
        />

        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800">Create New Order</h2>
        </div>

        <!-- Order Form -->
        <form @submit.prevent="createOrder" class="space-y-6">
          <!-- Product Selection -->
          <SearchSelect
            :items="products"
            v-model="selectedProduct"
            by="name"
            returnField="id"
            label="Product"
            placeholder="Search product..."
          />

          <!-- Quantity Input -->
          <BaseInput
            v-model="quantity"
            type="number"
            label="Quantity"
            placeholder="Enter quantity"
            min="1"
            :error="quantityError"
          />

          <!-- Order Time -->
          <DateTimePicker
            v-model="orderTime"
            label="Order Time"
            :error="orderTimeError"
            :min-date="new Date()"
          />

          <!-- Comment -->
          <BaseInput
            v-model="comment"
            type="textarea"
            label="Comment"
            placeholder="Enter order comment..."
            :maxlength="500"
            :rows="3"
            :counter="true"
          />

          <!-- Stock Information -->
          <div v-if="selectedProduct && currentStock !== null" 
            class="bg-gray-50 p-4 rounded-lg space-y-2">
            <p class="text-sm text-gray-600">
              Current stock: <span class="font-medium">{{ currentStock }}</span>
            </p>
            <p class="text-sm text-gray-600">
              Price per unit: <span class="font-medium">{{ formatPrice(selectedProduct.price) }} Kč</span>
            </p>
            <p class="text-sm text-gray-600">
              Total price: <span class="font-medium">{{ formatPrice(totalPrice) }} Kč</span>
            </p>
          </div>

          <!-- Submit Button -->
          <div class="flex justify-end">
            <button
              type="submit"
              class="px-6 py-2.5 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="loading || !isValid"
            >
              Create Order
            </button>
          </div>
        </form>
      </div>

      <!-- Recent Orders Sidebar -->
      <div class="w-96 bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
        <h3 class="text-lg font-medium text-gray-800 mb-4">Recent Orders</h3>
        <div class="space-y-4 h-[600px] overflow-y-auto pr-2 custom-scrollbar">
          <div v-for="order in recentOrders.slice(0, 5)" :key="order.id" 
            class="p-4 rounded-lg border border-gray-100 hover:border-gray-200 transition-all duration-200">
            <div class="flex justify-between items-start mb-2">
              <span class="text-sm font-medium text-gray-900">#{{ order.id }}</span>
              <span
                class="px-2 py-1 text-xs font-medium rounded-full"
                :class="{
                  'bg-yellow-100 text-yellow-800': order.status === 'PENDING',
                  'bg-green-100 text-green-800': order.status === 'CONFIRMED',
                  'bg-red-100 text-red-800': order.status === 'CANCELED'
                }"
              >
                {{ getStatusText(order.status) }}
              </span>
            </div>
            <div class="space-y-1">
              <p class="text-sm text-gray-600">{{ order.product.name }}</p>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">{{ order.amount }} ks</span>
                <span class="font-medium">{{ formatPrice(order.cost) }} Kč</span>
              </div>
              <p class="text-xs text-gray-400">{{ formatDate(order.orderTime) }}</p>
              <p v-if="order.comment" class="text-xs text-gray-500 mt-2 line-clamp-2">
                {{ order.comment }}
              </p>
              <p v-if="order.approvedBy" class="text-xs text-gray-400">
                {{ order.status === 'CONFIRMED' ? 'Approved' : 'Rejected' }} by: {{ order.approvedBy }}
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
import { ref, computed, onMounted, watch } from 'vue'
import { StatusBar, SearchSelect, BaseInput, DateTimePicker } from '@/components/common'
import { useErrorStore } from '@/stores/errors.js'
import { useNotifier } from '@/stores/notifier.js'
import { api } from '@/services/api'

// Stores
const errorStore = useErrorStore()
const $notifier = useNotifier()

// State
const loading = ref(false)
const products = ref([])
const selectedProduct = ref(null)
const quantity = ref(1)
const currentStock = ref(null)
const recentOrders = ref([])
const orderTime = ref(new Date().toISOString().slice(0, 16))
const comment = ref('')

// Computed
const totalPrice = computed(() => {
  if (!selectedProduct.value) return 0
  return selectedProduct.value.price * quantity.value
})

const quantityError = computed(() => {
  if (!selectedProduct.value) return ''
  if (quantity.value < 1) return 'Quantity must be at least 1'
  if (currentStock.value !== null && quantity.value > currentStock.value) {
    return 'Not enough stock available'
  }
  return ''
})

const orderTimeError = computed(() => {
  if (!orderTime.value) return 'Order time is required'
  const selectedTime = new Date(orderTime.value)
  const now = new Date()
  if (selectedTime < now) return 'Order time cannot be in the past'
  return ''
})

const isValid = computed(() => {
  return selectedProduct.value && 
         quantity.value > 0 && 
         currentStock.value >= quantity.value &&
         !orderTimeError.value
})

// Methods
const formatPrice = (price) => {
  return Number(price).toFixed(4)
}

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

const getStatusText = (status) => {
  switch (status) {
    case 'PENDING': return 'Čeká na schválení'
    case 'CONFIRMED': return 'Schváleno'
    case 'CANCELED': return 'Zamítnuto'
    default: return status
  }
}

const fetchProducts = async () => {
  try {
    const response = await api.get('/products')
    products.value = response.data
  } catch (err) {
    errorStore.handle(err)
  }
}

const fetchRecentOrders = async () => {
  try {
    const response = await api.get('/orders')
    recentOrders.value = response.data
  } catch (err) {
    errorStore.handle(err)
  }
}

const checkStock = async () => {
  if (!selectedProduct.value) {
    currentStock.value = null
    return
  }

  try {
    const response = await api.get('/inventory')
    const inventoryItem = response.data.find(
      item => item.product.id === selectedProduct.value.id
    )
    currentStock.value = inventoryItem ? inventoryItem.quantity : 0
  } catch (err) {
    errorStore.handle(err)
    currentStock.value = 0
  }
}

const createOrder = async () => {
  if (!isValid.value) return

  loading.value = true
  try {
    // Create order
    const response = await api.post('/orders', null, {
      params: {
        itemId: selectedProduct.value.id,
        quantity: quantity.value
      }
    })

    if (response.data) {
      // If stock is insufficient, automatically cancel the order
      if (currentStock.value < quantity.value) {
        await api.put(`/orders/${response.data.id}/cancel`, 'Insufficient stock')
        $notifier.error('Order automatically canceled - insufficient stock')
      } else {
        // Add comment if provided
        if (comment.value.trim()) {
          await api.put(`/orders/${response.data.id}/workflow-comment`, comment.value)
        }
        $notifier.success('Order created successfully')
      }

      // Reset form
      selectedProduct.value = null
      quantity.value = 1
      currentStock.value = null
      comment.value = ''
      orderTime.value = new Date().toISOString().slice(0, 16)

      // Refresh orders list
      await fetchRecentOrders()
    }
  } catch (err) {
    errorStore.handle(err)
  } finally {
    loading.value = false
  }
}

// Watchers
watch(selectedProduct, async () => {
  await checkStock()
})

// Lifecycle
onMounted(async () => {
  await Promise.all([
    fetchProducts(),
    fetchRecentOrders()
  ])
})
</script>

<style scoped>
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #CBD5E1 transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #CBD5E1;
  border-radius: 20px;
}
</style> 