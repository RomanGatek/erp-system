<template>
  <div class="min-h-screen bg-gray-50/50 backdrop-blur-sm py-12">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="max-w-3xl mx-auto">
        <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-8 mt-8">
          <!-- Success Animation -->
          <div class="mx-auto flex items-center justify-center h-24 w-24 relative mb-8">
            <!-- Outer ring animation -->
            <div class="absolute inset-0 rounded-full bg-green-100 animate-ping opacity-25"></div>
            <!-- Inner circle -->
            <div class="relative rounded-full bg-gradient-to-br from-green-100 to-green-50 p-6 shadow-inner ring-1 ring-green-100">
              <svg xmlns="http://www.w3.org/2000/svg" 
                class="h-12 w-12 text-green-500 animate-bounce" 
                fill="none" 
                viewBox="0 0 24 24" 
                stroke="currentColor"
              >
                <path 
                  stroke-linecap="round" 
                  stroke-linejoin="round" 
                  stroke-width="2" 
                  d="M5 13l4 4L19 7" 
                />
              </svg>
            </div>
          </div>
          
          <div class="text-center mb-8 animate-fade-in">
            <h1 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-green-400 bg-clip-text text-transparent mb-3">
              Payment Successful!
            </h1>
            <p class="text-gray-600">
              Your order has been placed successfully. You will receive a confirmation email shortly.
            </p>
          </div>
          
          <!-- Order Details -->
          <div class="bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl p-6 mb-8 animate-slide-up">
            <h2 class="text-lg font-semibold text-gray-800 mb-6 flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              Order Details
            </h2>
            
            <div class="space-y-4">
              <!-- Order Number -->
              <div class="flex items-center justify-between p-3 bg-white rounded-lg shadow-sm ring-1 ring-gray-100">
                <div class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14" />
                  </svg>
                  <span class="text-sm text-gray-600">Order Number</span>
                </div>
                <span class="font-medium text-gray-900">{{ orderNumber }}</span>
              </div>

              <!-- Order Date -->
              <div class="flex items-center justify-between p-3 bg-white rounded-lg shadow-sm ring-1 ring-gray-100">
                <div class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span class="text-sm text-gray-600">Order Date</span>
                </div>
                <span class="font-medium text-gray-900">{{ orderDate }}</span>
              </div>

              <!-- Total Amount -->
              <div class="flex items-center justify-between p-3 bg-white rounded-lg shadow-sm ring-1 ring-gray-100">
                <div class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span class="text-sm text-gray-600">Total Amount</span>
                </div>
                <span class="font-medium text-green-600">{{ orderTotal }}</span>
              </div>
            </div>
          </div>
          
          <!-- Action Buttons -->
          <div class="flex flex-col sm:flex-row gap-4 justify-center animate-fade-in">
            <button
              @click="$router.push('/catalog')"
              class="group px-6 py-3 bg-gradient-to-br from-gray-50 to-gray-100 text-gray-700 rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 hover:shadow-md relative overflow-hidden ring-1 ring-gray-200"
            >
              <span class="relative z-10 flex items-center justify-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 transform transition-transform group-hover:-translate-x-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
                Continue Shopping
              </span>
            </button>
            <button
              @click="$router.push('/user/orders')"
              class="group px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 hover:shadow-md relative overflow-hidden"
            >
              <span class="relative z-10 flex items-center justify-center gap-2">
                View Order Details
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 transform transition-transform group-hover:translate-x-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                </svg>
              </span>
              <div class="absolute inset-0 bg-gradient-to-r from-blue-700 to-blue-500 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
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
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
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

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.6s ease-out forwards;
}

.animate-slide-up {
  animation: slideUp 0.8s ease-out forwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes ping {
  75%, 100% {
    transform: scale(2);
    opacity: 0;
  }
}

.animate-ping {
  animation: ping 1s cubic-bezier(0, 0, 0.2, 1) infinite;
}

.animate-bounce {
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(-25%);
    animation-timing-function: cubic-bezier(0.8, 0, 1, 1);
  }
  50% {
    transform: translateY(0);
    animation-timing-function: cubic-bezier(0, 0, 0.2, 1);
  }
}
</style> 