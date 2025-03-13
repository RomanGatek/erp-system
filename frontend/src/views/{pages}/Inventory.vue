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
          <h2 class="text-2xl font-bold text-gray-800">Create Stock Order</h2>
        </div>

        <!-- Stock Order Form -->
        <form @submit.prevent="createStockOrder" class="space-y-6">
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
            label="Quantity to Order"
            placeholder="Enter quantity"
            min="1"
            :error="quantityError"
          />

          <!-- Expected Delivery Date -->
          <DateTimePicker
            v-model="expectedDeliveryDate"
            label="Expected Delivery Date"
            :error="deliveryDateError"
            :min-date="new Date()"
          />

          <!-- Supplier Information -->
          <BaseInput
            v-model="supplier"
            type="text"
            label="Supplier"
            placeholder="Enter supplier name"
            :error="supplierError"
          />

          <!-- Purchase Price -->
          <BaseInput
            v-model="purchasePrice"
            type="number"
            label="Purchase Price per Unit"
            placeholder="Enter price per unit"
            min="0.01"
            step="0.01"
            :error="priceError"
          />

          <!-- Notes -->
          <BaseInput
            v-model="notes"
            type="textarea"
            label="Notes"
            placeholder="Enter additional notes..."
            :maxlength="500"
            :rows="3"
            :counter="true"
          />

          <!-- Order Summary -->
          <div v-if="selectedProduct && isValid"
            class="bg-gray-50 p-4 rounded-lg space-y-2">
            <p class="text-sm text-gray-600">
              Current stock: <span class="font-medium">{{ currentStock }}</span>
            </p>
            <p class="text-sm text-gray-600">
              Total order value: <span class="font-medium">{{ formatPrice(totalOrderValue) }} Kč</span>
            </p>
          </div>

          <!-- Submit Button -->
          <div class="flex justify-end">
            <button
              type="submit"
              class="px-6 py-2.5 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="loading || !isValid"
            >
              Create Stock Order
            </button>
          </div>
        </form>
      </div>

      <!-- Recent Stock Orders Sidebar -->
      <div class="w-96 bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
        <h3 class="text-lg font-medium text-gray-800 mb-4">Recent Stock Orders</h3>
        <div class="space-y-4 h-[600px] overflow-y-auto pr-2 custom-scrollbar">
          <div v-for="order in recentStockOrders.slice(0, 5)" :key="order.id"
            class="p-4 rounded-lg border border-gray-100 hover:border-gray-200 transition-all duration-200">
            <div class="flex justify-between items-start mb-2">
              <span class="text-sm font-medium text-gray-900">#{{ order.id }}</span>
              <span
                class="px-2 py-1 text-xs font-medium rounded-full"
                :class="{
                  'bg-yellow-100 text-yellow-800': order.status === 'PENDING',
                  'bg-green-100 text-green-800': order.status === 'DELIVERED',
                  'bg-blue-100 text-blue-800': order.status === 'IN_TRANSIT',
                  'bg-red-100 text-red-800': order.status === 'CANCELED'
                }"
              >
                {{ getStatusText(order.status) }}
              </span>
            </div>
            <div class="space-y-1">
              <p class="text-sm text-gray-600">{{ order.product.name }}</p>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">{{ order.quantity }} units</span>
                <span class="font-medium">{{ formatPrice(order.totalValue) }} Kč</span>
              </div>
              <p class="text-xs text-gray-400">
                Expected: {{ formatDate(order.expectedDeliveryDate) }}
              </p>
              <p class="text-xs text-gray-500">
                Supplier: {{ order.supplier }}
              </p>
              <p v-if="order.notes" class="text-xs text-gray-500 mt-2 line-clamp-2">
                {{ order.notes }}
              </p>
              <p v-if="order.deliveryDate" class="text-xs text-gray-400">
                Delivered: {{ formatDate(order.deliveryDate) }}
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
import { StatusBar, SearchSelect, BaseInput, DateTimePicker } from '@/components'
import { useErrorStore } from '@/stores/errors.store.js'
import { useNotifier } from '@/stores/notifier.store.js'
import { api } from '@/services/api'
import { useInventoryStore } from '@/stores/inventory.store'

// Stores
const errorStore = useErrorStore()
const inventoryStore = useInventoryStore()
const $notifier = useNotifier()

// State
const loading = ref(false)
const products = ref([])
const selectedProduct = ref(null)
const quantity = ref(1)
const currentStock = ref(null)
const recentStockOrders = computed(() => inventoryStore.stockOrders.slice(0, 5))
const expectedDeliveryDate = ref(new Date().toISOString().slice(0, 16))
const supplier = ref('')
const purchasePrice = ref('')
const notes = ref('')

// Computed
const totalOrderValue = computed(() => {
  if (!purchasePrice.value || !quantity.value) return 0
  return purchasePrice.value * quantity.value
})

const quantityError = computed(() => {
  if (!quantity.value) return 'Quantity is required'
  if (quantity.value < 1) return 'Quantity must be at least 1'
  return ''
})

const deliveryDateError = computed(() => {
  if (!expectedDeliveryDate.value) return 'Delivery date is required'
  const selectedDate = new Date(expectedDeliveryDate.value)
  const now = new Date()
  if (selectedDate < now) return 'Delivery date cannot be in the past'
  return ''
})

const supplierError = computed(() => {
  if (!supplier.value.trim()) return 'Supplier is required'
  return ''
})

const priceError = computed(() => {
  if (!purchasePrice.value) return 'Purchase price is required'
  if (purchasePrice.value <= 0) return 'Price must be greater than 0'
  return ''
})

const isValid = computed(() => {
  return selectedProduct.value &&
         !quantityError.value &&
         !deliveryDateError.value &&
         !supplierError.value &&
         !priceError.value
})

// Methods
const formatPrice = (price) => {
  return Number(price).toFixed(2)
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
    case 'PENDING': return 'Pending'
    case 'IN_TRANSIT': return 'In Transit'
    case 'DELIVERED': return 'Delivered'
    case 'CANCELED': return 'Canceled'
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

const createStockOrder = async () => {
  if (!isValid.value) return

  const result = await inventoryStore.createStockOrder({
    productId: selectedProduct.value.id,
    quantity: quantity.value,
    expectedDeliveryDate: expectedDeliveryDate.value,
    supplier: supplier.value,
    purchasePrice: purchasePrice.value,
    notes: notes.value
  })

  if (result) {
    // Reset form
    selectedProduct.value = null
    quantity.value = 1
    currentStock.value = null
    supplier.value = ''
    purchasePrice.value = ''
    notes.value = ''
    expectedDeliveryDate.value = new Date().toISOString().slice(0, 16)
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
    inventoryStore.fetchStockOrders()
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
