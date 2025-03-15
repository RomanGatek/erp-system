<template>
  <div class="min-h-screen bg-gray-50/50 backdrop-blur-sm py-8">
    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="max-w-3xl mx-auto text-center mb-8">
        <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
          Checkout
        </h1>
        <p class="mt-2 text-sm text-gray-600">Complete your purchase securely</p>
      </div>

      <div class="max-w-3xl mx-auto space-y-6">
        <!-- Order Summary -->
        <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            Order Summary
          </h2>
          <div class="divide-y divide-gray-100">
            <div v-for="item in cart.items" :key="item.id" 
              class="py-4 flex items-center justify-between group hover:bg-gray-50/50 rounded-lg transition-colors">
              <div class="flex items-center gap-4">
                <!-- Item Image or Fallback -->
                <div class="relative w-16 h-16 rounded-lg overflow-hidden ring-1 ring-gray-100/50 bg-gradient-to-br from-gray-50 to-gray-100">
                  <template v-if="item.image">
                    <img 
                      :src="item.image" 
                      :alt="item.name"
                      class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                      @error="$event.target.style.display = 'none'"
                    />
                  </template>
                  <div v-if="!item.image || $event?.target?.style?.display === 'none'"
                    class="absolute inset-0 flex items-center justify-center"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" 
                      class="w-8 h-8 text-gray-400" 
                      fill="none" 
                      viewBox="0 0 24 24" 
                      stroke="currentColor"
                    >
                      <path stroke-linecap="round" 
                        stroke-linejoin="round" 
                        stroke-width="1.5" 
                        d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z" 
                      />
                    </svg>
                  </div>
                </div>

                <div>
                  <h3 class="text-sm font-medium text-gray-800">{{ item.name }}</h3>
                  <div class="mt-1 flex items-center gap-3">
                    <span class="text-xs text-gray-500">Quantity: {{ item.quantity }}</span>
                    <span class="text-xs font-medium text-blue-600">
                      {{ formatPrice(item.buyoutPrice) }} / ks
                    </span>
                  </div>
                </div>
              </div>
              <p class="text-sm font-medium text-gray-800">
                {{ formatPrice(item.buyoutPrice * item.quantity) }}
              </p>
            </div>
          </div>
          <div class="mt-6 pt-4 border-t border-gray-100">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-sm text-gray-500">Subtotal</p>
                <p class="text-lg font-semibold text-gray-900 mt-1">{{ formatPrice(totalAmount) }}</p>
              </div>
              <div class="text-right">
                <p class="text-sm text-gray-500">Items</p>
                <p class="text-lg font-semibold text-gray-900 mt-1">{{ cart.items.length }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Payment Methods -->
        <div class="bg-white rounded-2xl shadow-lg ring-1 ring-gray-100/50 p-6">
          <div @click="togglePaymentMethods" 
            class="flex justify-between items-center cursor-pointer group">
            <h2 class="text-lg font-semibold text-gray-800 flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                  d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
              Payment Method
            </h2>
            <svg xmlns="http://www.w3.org/2000/svg" 
              class="h-5 w-5 text-gray-400 transform transition-transform duration-300 group-hover:text-gray-600" 
              :class="{ 'rotate-180': showPaymentMethods }"
              viewBox="0 0 20 20" 
              fill="currentColor"
            >
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </div>
          
          <transition
            enter-active-class="transition-all duration-300 ease-out"
            leave-active-class="transition-all duration-200 ease-in"
            @enter="startTransition"
            @after-enter="endTransition"
            @before-leave="startTransition"
            @after-leave="endTransition"
          >
            <div v-if="showPaymentMethods" class="mt-6 overflow-hidden">
              <div class="payment-content animate-fade-in">
                <div class="grid grid-cols-3 gap-4 mb-6">
                  <!-- Credit Card -->
                  <button 
                    @click="selectPaymentMethod('card')"
                    :class="[
                      'p-4 rounded-xl border-2 flex flex-col items-center gap-2 transition-all duration-300 hover:shadow-md',
                      selectedMethod === 'card' 
                        ? 'border-blue-500 bg-blue-50/50 ring-2 ring-blue-500/10' 
                        : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50'
                    ]"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" :class="selectedMethod === 'card' ? 'text-blue-500' : 'text-gray-400'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="2" y="5" width="20" height="14" rx="2" />
                      <path d="M2 10h20" />
                    </svg>
                    <span class="text-sm font-medium" :class="selectedMethod === 'card' ? 'text-blue-700' : 'text-gray-700'">
                      Credit Card
                    </span>
                  </button>

                  <!-- Apple Pay -->
                  <button 
                    @click="selectPaymentMethod('apple')"
                    :class="[
                      'p-4 rounded-xl border-2 flex flex-col items-center gap-2 transition-all duration-300 hover:shadow-md',
                      selectedMethod === 'apple' 
                        ? 'border-blue-500 bg-blue-50/50 ring-2 ring-blue-500/10' 
                        : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50'
                    ]"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" :class="selectedMethod === 'apple' ? 'text-blue-500' : 'text-gray-400'" viewBox="0 0 170 170" fill="currentColor">
                      <path d="M150.37 130.25c-2.45 5.66-5.35 10.87-8.71 15.66-4.58 6.53-8.33 11.05-11.22 13.56-4.48 4.12-9.28 6.23-14.42 6.35-3.69 0-8.14-1.05-13.32-3.18-5.197-2.12-9.973-3.17-14.34-3.17-4.58 0-9.492 1.05-14.746 3.17-5.262 2.13-9.501 3.24-12.742 3.35-4.929 0.21-9.842-1.96-14.746-6.52-3.13-2.73-7.045-7.41-11.735-14.04-5.032-7.08-9.169-15.29-12.41-24.65-3.471-10.11-5.211-19.9-5.211-29.378 0-10.857 2.346-20.221 7.045-28.068 3.693-6.303 8.606-11.275 14.755-14.925s12.793-5.51 19.948-5.629c3.915 0 9.049 1.211 15.429 3.591 6.362 2.388 10.447 3.599 12.238 3.599 1.339 0 5.877-1.416 13.57-4.239 7.275-2.618 13.415-3.702 18.445-3.275 13.63 1.1 23.87 6.473 30.68 16.153-12.19 7.386-18.22 17.731-18.1 31.002 0.11 10.337 3.86 18.939 11.23 25.769 3.34 3.17 7.07 5.62 11.22 7.36-0.9 2.61-1.85 5.11-2.86 7.51zM119.11 7.24c0 8.102-2.96 15.667-8.86 22.669-7.12 8.324-15.732 13.134-25.071 12.375-0.119-0.972-0.188-1.995-0.188-3.07 0-7.778 3.386-16.102 9.399-22.908 3.002-3.446 6.82-6.311 11.45-8.597 4.62-2.252 8.99-3.497 13.1-3.71 0.12 1.083 0.17 2.166 0.17 3.24z"/>
                    </svg>
                    <span class="text-sm font-medium" :class="selectedMethod === 'apple' ? 'text-blue-700' : 'text-gray-700'">
                      Apple Pay
                    </span>
                  </button>

                  <!-- Google Pay -->
                  <button 
                    @click="selectPaymentMethod('google')"
                    :class="[
                      'p-4 rounded-xl border-2 flex flex-col items-center gap-2 transition-all duration-300 hover:shadow-md',
                      selectedMethod === 'google' 
                        ? 'border-blue-500 bg-blue-50/50 ring-2 ring-blue-500/10' 
                        : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50'
                    ]"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-16" :class="selectedMethod === 'google' ? 'text-blue-500' : 'text-gray-400'" viewBox="0 0 80 24" fill="currentColor">
                      <!-- Google Pay Logo SVG path -->
                    </svg>
                    <span class="text-sm font-medium" :class="selectedMethod === 'google' ? 'text-blue-700' : 'text-gray-700'">
                      Google Pay
                    </span>
                  </button>
                </div>

                <!-- Credit Card Form -->
                <div v-if="selectedMethod === 'card'" class="mt-6 animate-slide-down">
                  <CreditCard 
                    v-model:number="cardNumber"
                    v-model:name="cardName"
                    v-model:expiry="cardExpiry"
                    v-model:cvc="cardCvc"
                    v-model:paymentMethod="selectedCardType"
                  />
                </div>

                <!-- Apple Pay -->
                <div v-if="selectedMethod === 'apple'" class="mt-6 text-center animate-slide-down">
                  <button class="bg-black text-white py-3 px-6 rounded-xl inline-flex items-center gap-2 hover:bg-gray-900 transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 170 170" fill="white">
                      <!-- Apple Pay Logo SVG path -->
                    </svg>
                    <span class="text-sm font-medium">Pay with Apple Pay</span>
                  </button>
                </div>

                <!-- Google Pay -->
                <div v-if="selectedMethod === 'google'" class="mt-6 text-center animate-slide-down">
                  <button class="bg-white border-2 border-gray-200 py-3 px-6 rounded-xl inline-flex items-center gap-2 hover:bg-gray-50 transition-colors">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-10" viewBox="0 0 80 24">
                      <!-- Google Pay Logo SVG path -->
                    </svg>
                    <span class="text-sm font-medium text-gray-800">Pay with Google Pay</span>
                  </button>
                </div>
              </div>
            </div>
          </transition>
        </div>

        <!-- Action Buttons -->
        <div class="flex justify-between items-center">
          <button 
            @click="$router.push('/catalog')"
            class="text-blue-600 hover:text-blue-700 text-sm font-medium flex items-center gap-2 group"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 transform transition-transform group-hover:-translate-x-1" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M9.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L7.414 9H15a1 1 0 110 2H7.414l2.293 2.293a1 1 0 010 1.414z" clip-rule="evenodd" />
            </svg>
            Back to Cart
          </button>
          
          <button 
            @click="processPayment"
            :disabled="(selectedMethod === 'card' && !isPaymentValid) || isProcessingPayment"
            class="relative bg-gradient-to-r from-blue-600 to-blue-400 text-white px-8 py-3 rounded-xl text-sm font-medium transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0 overflow-hidden group"
          >
            <span class="relative z-10 flex items-center gap-2">
              <span v-if="isProcessingPayment">
                <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Processing...
              </span>
              <span v-else>
                Pay {{ formatPrice(totalAmount) }}
              </span>
            </span>
            <div class="absolute inset-0 bg-gradient-to-r from-blue-700 to-blue-500 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </button>
        </div>

        <!-- Payment Validation Message -->
        <div v-if="selectedMethod === 'card' && !isPaymentValid && showPaymentMethods" 
          class="text-center">
          <p class="text-sm text-red-500">
            Please complete all card details to proceed with payment
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useCartStore } from '@/stores/cart.store'
import { useRouter } from 'vue-router'
import { useNotifier } from '@/stores/notifier.store'
import CreditCard from '@/components/checkout/CreditCard.vue'
import { useMeStore } from '@/stores'

export default {
  name: 'Checkout',
  components: {
    CreditCard
  },
  setup() {
    const cart = useCartStore()
    const router = useRouter()
    const notifier = useNotifier()
    const me = useMeStore()

    const selectedMethod = ref('card')
    const cardNumber = ref('')
    const cardName = ref('')
    const cardExpiry = ref('')
    const cardCvc = ref('')
    const selectedCardType = ref('')
    const showPaymentMethods = ref(false) // Default to collapsed
    const isProcessingPayment = ref(false) // Loading state for payment processing

    // Set cardholder name from user profile immediately and log it
    if (me.user && me.user.firstName && me.user.lastName) {
      cardName.value = me.user.firstName + ' ' + me.user.lastName
      console.log('Setting card name to:', cardName.value)
    }

    // Animation methods for expanding/collapsing payment section
    const startTransition = (el) => {
      el.style.height = 'auto'
      const height = el.scrollHeight
      el.style.height = '0px'
      // Trigger reflow
      el.offsetHeight
      el.style.height = height + 'px'
    }

    const endTransition = (el) => {
      el.style.height = ''
    }

    const totalAmount = computed(() => {
      return cart.items.reduce((total, item) => total + (item.buyoutPrice * item.quantity), 0)
    })

    // Validace platební karty - kontroluje, zda jsou všechna povinná pole vyplněna
    const isPaymentValid = computed(() => {
      // Pro jiné platební metody než karta není potřeba validace
      if (selectedMethod.value !== 'card') {
        return true
      }
      
      // Testovací karty (vždy platné)
      const testCards = ['8888888888888888', '4444444444444444']
      const cardNumberWithoutSpaces = cardNumber.value ? cardNumber.value.replace(/\s/g, '') : ''
      
      // Kontrola, zda se jedná o testovací kartu
      if (testCards.includes(cardNumberWithoutSpaces)) {
        return cardExpiry.value && cardExpiry.value.includes('/') && cardCvc.value && cardCvc.value.length === 3
      }
      
      // Kontrola, zda je číslo karty kompletní (všechny 4 skupiny po 4 číslicích)
      const hasValidCardNumber = cardNumber.value && cardNumber.value.replace(/\s/g, '').length === 16
      
      // Kontrola data platnosti
      const hasValidExpiry = cardExpiry.value && cardExpiry.value.includes('/')
      
      // Kontrola bezpečnostního kódu CVC
      const hasValidCvc = cardCvc.value && cardCvc.value.length === 3
      
      return hasValidCardNumber && hasValidExpiry && hasValidCvc
    })

    const selectPaymentMethod = (method) => {
      selectedMethod.value = method
    }

    const togglePaymentMethods = () => {
      showPaymentMethods.value = !showPaymentMethods.value
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('cs-CZ', {
        style: 'currency',
        currency: 'CZK'
      }).format(price)
    }

    const processPayment = async () => {
      // Show payment methods if hidden when user tries to pay
      if (!showPaymentMethods.value) {
        showPaymentMethods.value = true
        return
      }
      
      // Zkontrolovat platnost platební karty, pokud je vybrána jako platební metoda
      if (selectedMethod.value === 'card' && !isPaymentValid.value) {
        notifier.error('Prosím vyplňte všechny údaje platební karty', 'Neúplné údaje', 3000)
        return
      }
      
      try {
        // Set loading state to true
        isProcessingPayment.value = true
        
        // Here you would typically make an API call to process the payment
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        notifier.success('Payment processed successfully!', 'Success', 3000)
        router.push('/order-confirmation')
      } catch (error) {
        notifier.error('Payment failed. Please try again.', 'Error', 5000)
      } finally {
        // Set loading state back to false regardless of outcome
        isProcessingPayment.value = false
      }
    }

    return {
      cart,
      selectedMethod,
      cardNumber,
      cardName,
      cardExpiry,
      cardCvc,
      selectedCardType,
      showPaymentMethods,
      totalAmount,
      isPaymentValid,
      isProcessingPayment,
      selectPaymentMethod,
      togglePaymentMethods,
      formatPrice,
      processPayment,
      startTransition,
      endTransition
    }
  }
}
</script>

<style scoped>
/* Transition animations */
.expand-enter-active,
.expand-leave-active {
  transition: height 0.35s ease, opacity 0.35s ease;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  height: 0;
  opacity: 0;
}

/* Content animations */
.animate-fade-in {
  animation: fade-in 0.4s ease forwards;
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

.animate-scale-in {
  animation: scale-in 0.4s ease forwards;
}

@keyframes scale-in {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.animate-slide-down {
  animation: slide-down 0.4s ease-out forwards;
}

@keyframes slide-down {
  from { transform: translateY(-20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style> 