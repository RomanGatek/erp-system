<script setup>
import { ref, watch, computed, onMounted, onBeforeUnmount } from 'vue'
import { useInventoryStore } from '@/stores/storage.store.js'
import { useNotifier } from '@/stores/notifier.js'

const props = defineProps({
  items: {
    type: Array,
    required: true
  },
  modelValue: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: 'Search products...'
  },
  orderType: {
    type: String,
    default: 'SELL'
  }
})

const emit = defineEmits(['update:modelValue'])
const inventoryStore = useInventoryStore()
const $notifier = useNotifier()

const searchQuery = ref('')
const showDropdown = ref(false)
const searchContainerRef = ref(null)

// Load inventory data
onMounted(async () => {
  if (inventoryStore.items.length === 0) {
    await inventoryStore.fetchItems()
  }
})

// Check if product exists in inventory
const isProductInInventory = (productId) => {
  return inventoryStore.items.some(item => item.product.id === productId)
}

// Watch for order type changes
watch(() => props.orderType, async (newOrderType) => {
  if (newOrderType === 'SELL') {
    // Make sure inventory is loaded
    if (inventoryStore.items.length === 0) {
      await inventoryStore.fetchItems()
    }

    // Remove products that don't exist in inventory when switching to SELL
    const productsToKeep = selectedProducts.value.filter(product =>
      isProductInInventory(product.id)
    )

    const removedCount = selectedProducts.value.length - productsToKeep.length
    if (removedCount > 0) {
      selectedProducts.value = productsToKeep
      emit('update:modelValue', productsToKeep)

      // Show notification
      $notifier.info(
        `${removedCount} product${removedCount > 1 ? 's' : ''} removed because ${removedCount > 1 ? 'they are' : 'it is'} out of stock.`,
        'Mode Changed'
      )
    }
  }
}, { immediate: true })

const filteredItems = computed(() => {
  // If dropdown is shown but search is empty, show all items (limited to 10 for performance)
  if (showDropdown.value && !searchQuery.value) {
    let initialItems = [...props.items];

    // Filter out inventory items when in SELL mode
    if (props.orderType === 'SELL') {
      initialItems = initialItems.filter(item => isProductInInventory(item.id));
    }

    // Return only first 10 items to avoid performance issues
    return initialItems.slice(0, 10);
  }

  const query = searchQuery.value.toLowerCase().trim();

  // Special filter for showing only added products
  if (query === '@add' || query === '@added') {
    return props.items.filter(item =>
      isProductSelected(item.id)
    );
  }

  // Filter out products not in inventory when in SELL mode
  let filteredResults = props.items.filter(item =>
    item.name.toLowerCase().includes(query)
  );

  if (props.orderType === 'SELL') {
    filteredResults = filteredResults.filter(item =>
      isProductInInventory(item.id)
    );
  }

  return filteredResults;
})

const isProductSelected = (productId) => {
  return props.modelValue.some(selected => selected.id === productId)
}

// Close dropdown when clicking outside
const handleClickOutside = (event) => {
  if (searchContainerRef.value && !searchContainerRef.value.contains(event.target)) {
    showDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})

const selectedProducts = ref(props.modelValue)

watch(selectedProducts, (newValue) => {
  emit('update:modelValue', newValue)
}, { deep: true })

watch(searchQuery, (newValue) => {
  if (newValue) {
    showDropdown.value = true
  }
})

const addProduct = (product) => {
  // For SELL mode, only allow adding products that exist in inventory
  if (props.orderType === 'SELL' && !isProductInInventory(product.id)) {
    return
  }

  selectedProducts.value.push({
    id: product.id,
    name: product.name,
    quantity: 1,
    buyoutPrice: product.buyoutPrice,
    purchasePrice: product.purchasePrice,
    description: product.description,
    price: props.orderType === 'SELL' ? product.buyoutPrice : product.purchasePrice
  })
  searchQuery.value = ''
  showDropdown.value = false
}

const removeProduct = (index) => {
  selectedProducts.value.splice(index, 1)
}

const updateQuantity = (index, value) => {
  const quantity = parseInt(value)
  if (quantity > 0) {
    selectedProducts.value[index].quantity = quantity
  }
}

const getTotalPrice = (product) => {
  if (props.orderType === 'SELL') {
    return product.buyoutPrice * product.quantity
  } else {
    return product.purchasePrice * product.quantity
  }
}

const clearAllProducts = () => {
  const count = selectedProducts.value.length
  if (count > 0) {
    selectedProducts.value = []

    // Show notification
    $notifier.info(
      `${count} product${count > 1 ? 's' : ''} removed from selection.`,
      'All Products Removed'
    )
  }
}
</script>

<template>
  <div class="space-y-4">
    <label class="block text-sm font-medium text-gray-700">Products</label>

    <!-- Search Input -->
    <div class="relative" ref="searchContainerRef">
      <div
        class="flex items-center pl-10 pr-4 py-2 bg-gray-50 rounded-lg focus-within:bg-white focus-within:ring-2 focus-within:ring-blue-500"
      >
        <svg
          class="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          />
        </svg>

        <input
          v-model="searchQuery"
          @focus="showDropdown = true"
          @click="showDropdown = true"
          type="text"
          :placeholder="placeholder + ' (or type @added to see added items)'"
          class="outline-none w-full bg-transparent border-none p-0 focus:ring-0 text-sm placeholder-gray-400"
        />

        <button
          v-if="selectedProducts.length > 0"
          @click="clearAllProducts"
          type="button"
          class="flex items-center whitespace-nowrap text-xs font-medium bg-red-50 text-red-600 px-3 py-1.5 rounded-md border border-transparent hover:border-red-200 transition-all duration-200"
        >
          <svg class="w-3.5 h-3.5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
          Delete All
        </button>
      </div>

      <!-- Dropdown -->
      <div v-if="showDropdown && filteredItems.length > 0"
        class="absolute z-10 mt-1 w-full bg-white rounded-lg shadow-lg border border-gray-200 max-h-60 overflow-auto">
        <div v-if="!searchQuery" class="px-4 py-2 text-xs text-gray-500 border-b border-gray-100">
          Showing top 10 products. Type to search more.
        </div>
        <div
          v-for="item in filteredItems"
          :key="item.id"
          @click="isProductSelected(item.id) ? null : addProduct(item)"
          :class="[
            'px-4 py-2 cursor-pointer',
            isProductSelected(item.id)
              ? (props.orderType === 'SELL' ? 'bg-green-50 hover:bg-green-100' : 'bg-red-50 hover:bg-red-100')
              : (props.orderType === 'SELL' && !isProductInInventory(item.id))
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'hover:bg-gray-50'
          ]"
        >
          <div class="flex justify-between items-center">
            <div>
              <div class="flex items-center">
                <p class="text-sm font-medium"
                   :class="{'text-gray-900': !(props.orderType === 'SELL' && !isProductInInventory(item.id)),
                           'text-gray-400': props.orderType === 'SELL' && !isProductInInventory(item.id)}">
                  {{ item.name }}
                </p>
                <span v-if="isProductSelected(item.id)"
                      :class="[
                        'ml-2 text-xs font-medium px-1.5 py-0.5 rounded-full',
                        props.orderType === 'SELL'
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      ]">
                  Added
                </span>
                <span v-else-if="props.orderType === 'SELL' && !isProductInInventory(item.id)"
                      class="ml-2 text-xs font-medium px-1.5 py-0.5 rounded-full bg-gray-100 text-gray-600">
                  Out of stock
                </span>
              </div>
              <p class="text-xs" :class="props.orderType === 'SELL' && !isProductInInventory(item.id) ? 'text-gray-400' : 'text-gray-500'">
                {{ item.description }}
              </p>
            </div>
            <div class="flex items-center">
              <p v-if="props.orderType === 'SELL'"
                 class="inline-flex items-center rounded-md bg-green-50 px-2 py-1 text-xs font-medium text-green-600 ring-1 ring-green-500/10 ring-inset">
                {{ formatPrice(item.buyoutPrice) }}
              </p>
              <p v-if="props.orderType === 'PURCHASE'"
                 class="inline-flex items-center rounded-md bg-red-50 px-2 py-1 text-xs font-medium text-red-600 ring-1 ring-red-500/10 ring-inset">
                {{ formatPrice(item.purchasePrice) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Selected Products List with Scrollable Container -->
    <div class="mt-2 border border-gray-200 rounded-lg shadow-sm">
      <div class="max-h-[140px] overflow-y-auto pr-1 custom-scrollbar">
        <div class="space-y-2 p-2">
          <div v-if="selectedProducts.length === 0" class="p-4 text-center text-gray-500 text-sm">
            No products selected yet. Search and add products above.
          </div>
          <div v-for="(product, index) in selectedProducts" :key="product.id"
            class="flex items-start gap-2 py-2 px-3 bg-gray-50 rounded-lg group hover:bg-gray-100 transition-colors duration-200 border border-transparent hover:border-gray-200">
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900 truncate">{{ product.name }}</p>
              <p class="text-xs text-gray-500 line-clamp-1">{{ product.description }}</p>
            </div>

            <div class="flex items-center space-x-2 flex-shrink-0">
              <div class="flex flex-col items-end">
                <div class="flex items-center gap-1">
                  <p v-if="props.orderType === 'SELL'"
                     class="inline-flex items-center rounded-md bg-green-50 px-1.5 py-0.5 text-xs font-medium text-green-600 ring-1 ring-green-500/10 ring-inset">
                    {{ formatPrice(product.buyoutPrice) }}
                  </p>
                  <p v-if="props.orderType === 'PURCHASE'"
                     class="inline-flex items-center rounded-md bg-red-50 px-1.5 py-0.5 text-xs font-medium text-red-600 ring-1 ring-red-500/10 ring-inset">
                    {{ formatPrice(product.purchasePrice) }}
                  </p>
                </div>
                <p class="text-xs font-medium mt-0.5"
                   :class="props.orderType === 'SELL' ? 'text-green-600' : 'text-red-600'">
                  {{ formatPrice(getTotalPrice(product)) }}
                </p>
              </div>

              <input
                type="number"
                v-model="product.quantity"
                @input="updateQuantity(index, $event.target.value)"
                min="1"
                class="w-10 px-1 py-0.5 text-sm border rounded-md focus:ring-1 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm transition-all duration-200 hover:border-gray-300 outline-none appearance-none text-center"
              />

              <button
                @click="removeProduct(index)"
                class="p-0.5 text-gray-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-all duration-200 rounded-full hover:bg-red-50"
                type="button"
              >
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
function formatPrice(price) {
  // Convert to string and split by decimal point
  const str = String(price);
  const parts = str.split('.');

  // If there's no decimal part, add .00
  if (parts.length === 1) {
    return `${parts[0]}.00`;
  }

  // Get the decimal part and truncate to 2 places (no rounding)
  let decimal = parts[1];
  if (decimal.length > 2) {
    decimal = decimal.substring(0, 2);
  } else if (decimal.length === 1) {
    decimal = decimal + '0';
  }

  return `${parts[0]}.${decimal}`;
}
</script>

<style scoped>
/* Custom scrollbar styling */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: rgba(203, 213, 225, 0.8) transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: rgba(203, 213, 225, 0.8);
  border-radius: 20px;
}

.custom-scrollbar:hover::-webkit-scrollbar-thumb {
  background-color: rgba(148, 163, 184, 0.8);
}

/* Remove arrows/spinners from number inputs */
input[type=number]::-webkit-inner-spin-button,
input[type=number]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
input[type=number] {
  -moz-appearance: textfield;
}
</style>
