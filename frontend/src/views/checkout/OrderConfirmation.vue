<template>
  <div class="min-h-screen bg-gray-50 py-12">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="max-w-3xl mx-auto text-center">
        <div class="bg-white rounded-2xl shadow-sm p-8 mt-8">
          <!-- Success Icon -->
          <div class="mx-auto flex items-center justify-center h-20 w-20 rounded-full bg-green-100 mb-6">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          
          <h1 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-green-400 bg-clip-text text-transparent mb-4">
            Payment Successful!
          </h1>
          
          <p class="text-gray-600 mb-8">
            Your order has been placed successfully. You will receive a confirmation email shortly.
          </p>
          
          <div class="bg-gray-50 rounded-xl p-6 mb-8">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Order Summary</h2>
            <div class="flex justify-between border-b pb-4 mb-4">
              <span class="text-gray-600">Order Number:</span>
              <span class="font-medium">{{ orderNumber }}</span>
            </div>
            <div class="flex justify-between border-b pb-4 mb-4">
              <span class="text-gray-600">Date:</span>
              <span class="font-medium">{{ orderDate }}</span>
            </div>
            <div class="flex justify-between font-medium">
              <span class="text-gray-800">Total:</span>
              <span class="text-green-600">{{ orderTotal }}</span>
            </div>
          </div>
          
          <div class="flex flex-col sm:flex-row gap-4 justify-center">
            <button
              @click="$router.push('/catalog')"
              class="bg-gray-100 text-gray-800 px-6 py-3 rounded-xl font-medium hover:bg-gray-200 transition-colors"
            >
              Continue Shopping
            </button>
            <button
              @click="$router.push('/user/orders')"
              class="bg-gradient-to-r from-blue-600 to-blue-400 text-white px-6 py-3 rounded-xl font-medium hover:from-blue-700 hover:to-blue-500 transition-colors"
            >
              View Order Details
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useCartStore } from '@/stores/cart.store'

export default {
  name: 'OrderConfirmation',
  setup() {
    const cart = useCartStore()
    
    const orderNumber = ref(generateOrderNumber())
    const orderDate = ref(formatDate(new Date()))
    const orderTotal = ref(formatPrice(calculateTotal(cart.items)))
    
    // Clear cart after successful payment
    onMounted(() => {
      setTimeout(() => {
        cart.clearItems()
      }, 500)
    })
    
    function generateOrderNumber() {
      return 'ORD-' + Math.floor(100000 + Math.random() * 900000)
    }
    
    function formatDate(date) {
      return new Intl.DateTimeFormat('cs-CZ', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      }).format(date)
    }
    
    function calculateTotal(items) {
      return items.reduce((total, item) => {
        return total + (item.buyoutPrice || item.price) * item.quantity
      }, 0)
    }
    
    function formatPrice(price) {
      return new Intl.NumberFormat('cs-CZ', {
        style: 'currency',
        currency: 'CZK'
      }).format(price)
    }
    
    return {
      orderNumber,
      orderDate,
      orderTotal
    }
  }
}
</script> 