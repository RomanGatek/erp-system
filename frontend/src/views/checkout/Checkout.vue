<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="max-w-3xl mx-auto text-center mb-8">
        <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
          Checkout
        </h1>
        <p class="mt-2 text-sm text-gray-600">Complete your purchase securely</p>
      </div>

      <div class="max-w-3xl mx-auto">
        <!-- Order Summary -->
        <div class="bg-white rounded-xl shadow-sm p-4 mb-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-3">Order Summary</h2>
          <div class="divide-y divide-gray-200">
            <div v-for="item in cart.items" :key="item.id" class="py-3 flex items-center justify-between">
              <div class="flex items-center">
                <template v-if="item.image">
                  <img :src="item.image" :alt="item.name" class="w-12 h-12 object-cover rounded-lg">
                </template>
                <div v-else class="w-12 h-12 flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 rounded-lg">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    class="w-6 h-6 text-gray-300"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="1.5"
                      d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"
                    />
                  </svg>
                </div>
                <div class="ml-3">
                  <h3 class="text-xs font-medium text-gray-800">{{ item.name }}</h3>
                  <p class="text-xs text-gray-500">Quantity: {{ item.quantity }}</p>
                </div>
              </div>
              <p class="text-xs font-medium text-gray-800">
                {{ formatPrice(item.buyoutPrice * item.quantity) }}
              </p>
            </div>
          </div>
          <div class="mt-4 pt-3 border-t border-gray-200">
            <div class="flex justify-between text-sm font-medium text-gray-900">
              <p>Total</p>
              <p>{{ formatPrice(totalAmount) }}</p>
            </div>
          </div>
        </div>

        <!-- Payment Methods -->
        <div class="bg-white rounded-xl shadow-sm p-4 mb-6">
          <div 
            @click="togglePaymentMethods" 
            class="flex justify-between items-center cursor-pointer"
          >
            <h2 class="text-lg font-semibold text-gray-800">Payment Method</h2>
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              class="h-5 w-5 text-gray-500 transform transition-transform duration-300" 
              :class="{ 'rotate-180': showPaymentMethods }"
              viewBox="0 0 20 20" 
              fill="currentColor"
            >
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </div>
          
          <transition
            name="expand"
            @enter="startTransition"
            @after-enter="endTransition"
            @before-leave="startTransition"
            @after-leave="endTransition"
          >
            <div v-if="showPaymentMethods" class="mt-4 overflow-hidden">
              <div class="payment-content animate-fade-in">
                <div class="grid grid-cols-3 gap-3 mb-4 animate-scale-in">
                  <!-- Credit Card -->
                  <button 
                    @click="selectPaymentMethod('card')"
                    :class="[
                      'p-3 rounded-lg border-2 flex flex-col items-center justify-center gap-1 transition-all',
                      selectedMethod === 'card' 
                        ? 'border-blue-500 bg-blue-50' 
                        : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50'
                    ]"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" :class="selectedMethod === 'card' ? 'text-blue-500' : 'text-gray-400'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="2" y="5" width="20" height="14" rx="2" />
                      <path d="M2 10h20" />
                    </svg>
                    <span class="text-xs font-medium" :class="selectedMethod === 'card' ? 'text-blue-700' : 'text-gray-700'">
                      Credit Card
                    </span>
                  </button>

                  <!-- Apple Pay -->
                  <button 
                    @click="selectPaymentMethod('apple')"
                    :class="[
                      'p-3 rounded-lg border-2 flex flex-col items-center justify-center gap-1 transition-all',
                      selectedMethod === 'apple' 
                        ? 'border-blue-500 bg-blue-50' 
                        : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50'
                    ]"
                  >
                    <!-- Apple Logo -->
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" :class="selectedMethod === 'apple' ? 'text-blue-500' : 'text-gray-400'" viewBox="0 0 170 170" fill="currentColor">
                      <path d="M150.37 130.25c-2.45 5.66-5.35 10.87-8.71 15.66-4.58 6.53-8.33 11.05-11.22 13.56-4.48 4.12-9.28 6.23-14.42 6.35-3.69 0-8.14-1.05-13.32-3.18-5.197-2.12-9.973-3.17-14.34-3.17-4.58 0-9.492 1.05-14.746 3.17-5.262 2.13-9.501 3.24-12.742 3.35-4.929 0.21-9.842-1.96-14.746-6.52-3.13-2.73-7.045-7.41-11.735-14.04-5.032-7.08-9.169-15.29-12.41-24.65-3.471-10.11-5.211-19.9-5.211-29.378 0-10.857 2.346-20.221 7.045-28.068 3.693-6.303 8.606-11.275 14.755-14.925s12.793-5.51 19.948-5.629c3.915 0 9.049 1.211 15.429 3.591 6.362 2.388 10.447 3.599 12.238 3.599 1.339 0 5.877-1.416 13.57-4.239 7.275-2.618 13.415-3.702 18.445-3.275 13.63 1.1 23.87 6.473 30.68 16.153-12.19 7.386-18.22 17.731-18.1 31.002 0.11 10.337 3.86 18.939 11.23 25.769 3.34 3.17 7.07 5.62 11.22 7.36-0.9 2.61-1.85 5.11-2.86 7.51zM119.11 7.24c0 8.102-2.96 15.667-8.86 22.669-7.12 8.324-15.732 13.134-25.071 12.375-0.119-0.972-0.188-1.995-0.188-3.07 0-7.778 3.386-16.102 9.399-22.908 3.002-3.446 6.82-6.311 11.45-8.597 4.62-2.252 8.99-3.497 13.1-3.71 0.12 1.083 0.17 2.166 0.17 3.24z"/>
                    </svg>
                    <span class="text-xs font-medium" :class="selectedMethod === 'apple' ? 'text-blue-700' : 'text-gray-700'">
                      Apple Pay
                    </span>
                  </button>

                  <!-- Google Pay -->
                  <button 
                    @click="selectPaymentMethod('google')"
                    :class="[
                      'p-3 rounded-lg border-2 flex flex-col items-center justify-center gap-1 transition-all',
                      selectedMethod === 'google' 
                        ? 'border-blue-500 bg-blue-50' 
                        : 'border-gray-200 hover:border-blue-200 hover:bg-gray-50'
                    ]"
                  >
                    <!-- Google Pay Logo -->
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-12" :class="selectedMethod === 'google' ? 'text-blue-500' : 'text-gray-400'" viewBox="0 0 80 24" fill="currentColor">
                      <path d="M35.29 11.26H29.8V12.96H33.32C33.11 15.27 31.32 16.53 29.8 16.53C28.68 16.53 27.61 16.09 26.89 15.36C26.18 14.63 25.78 13.65 25.78 12.59C25.78 11.53 26.18 10.56 26.9 9.83C27.62 9.1 28.68 8.66 29.81 8.66C30.96 8.66 31.84 9.17 32.39 9.64L33.6 8.43C32.75 7.64 31.48 6.95 29.8 6.95C28.15 6.95 26.57 7.57 25.46 8.68C24.38 9.75 23.8 11.12 23.8 12.59C23.8 14.06 24.38 15.43 25.46 16.51C26.62 17.66 28.14 18.23 29.8 18.23C31.34 18.23 32.83 17.71 33.91 16.58C34.95 15.48 35.42 14.04 35.42 12.4C35.42 11.97 35.37 11.6 35.29 11.26Z" fill="currentColor"/>
                      <path d="M40.84 8.54C39.85 8.54 38.94 8.9 38.28 9.55C37.63 10.21 37.26 11.12 37.26 12.09C37.26 13.06 37.63 13.97 38.28 14.63C38.94 15.28 39.85 15.64 40.84 15.64C41.82 15.64 42.74 15.28 43.39 14.63C44.04 13.97 44.41 13.06 44.41 12.09C44.41 11.12 44.04 10.21 43.39 9.55C42.74 8.9 41.82 8.54 40.84 8.54ZM40.84 13.93C40.26 13.93 39.72 13.7 39.32 13.3C38.92 12.9 38.7 12.37 38.7 11.81C38.7 11.25 38.92 10.72 39.32 10.32C39.72 9.92 40.26 9.69 40.84 9.69C41.41 9.69 41.95 9.91 42.35 10.32C42.75 10.72 42.98 11.25 42.98 11.81C42.98 12.37 42.75 12.9 42.35 13.3C41.95 13.7 41.41 13.93 40.84 13.93Z" fill="currentColor"/>
                      <path d="M49.2 8.54C48.21 8.54 47.3 8.9 46.64 9.55C45.99 10.21 45.62 11.12 45.62 12.09C45.62 13.06 45.99 13.97 46.64 14.63C47.3 15.28 48.21 15.64 49.2 15.64C50.18 15.64 51.1 15.28 51.75 14.63C52.4 13.97 52.77 13.06 52.77 12.09C52.77 11.12 52.4 10.21 51.75 9.55C51.1 8.9 50.18 8.54 49.2 8.54ZM49.2 13.93C48.62 13.93 48.08 13.7 47.68 13.3C47.28 12.9 47.06 12.37 47.06 11.81C47.06 11.25 47.28 10.72 47.68 10.32C48.08 9.92 48.62 9.69 49.2 9.69C49.77 9.69 50.31 9.91 50.71 10.32C51.11 10.72 51.34 11.25 51.34 11.81C51.34 12.37 51.11 12.9 50.71 13.3C50.31 13.7 49.77 13.93 49.2 13.93Z" fill="currentColor"/>
                      <path d="M57.23 8.54C56.31 8.54 55.43 8.86 54.8 9.49C54.17 10.12 53.85 10.9 53.85 11.81C53.85 13.65 55.4 15.17 57.14 15.17C57.98 15.17 58.57 14.84 58.9 14.52H58.92V14.88C58.92 15.58 58.4 16.11 57.51 16.11C56.86 16.11 56.38 15.76 56.19 15.29L55 15.29L55 15.83C55.39 16.77 56.37 17.27 57.55 17.27C59.09 17.27 60.3 16.15 60.3 14.43V8.73H58.92V9.38H58.9C58.55 8.88 57.91 8.54 57.23 8.54ZM57.44 10.06C58.31 10.06 58.96 10.81 58.96 11.82C58.96 12.83 58.31 13.58 57.44 13.58C56.58 13.58 55.92 12.82 55.92 11.81C55.92 10.8 56.58 10.06 57.44 10.06Z" fill="currentColor"/>
                      <path d="M66.6 8.54C65.61 8.54 64.7 8.9 64.04 9.55C63.39 10.21 63.02 11.12 63.02 12.09C63.02 13.06 63.39 13.97 64.04 14.63C64.7 15.28 65.61 15.64 66.6 15.64C67.58 15.64 68.5 15.28 69.15 14.63C69.8 13.97 70.17 13.06 70.17 12.09C70.17 11.12 69.8 10.21 69.15 9.55C68.5 8.9 67.58 8.54 66.6 8.54ZM66.6 13.93C66.02 13.93 65.48 13.7 65.08 13.3C64.68 12.9 64.46 12.37 64.46 11.81C64.46 11.25 64.68 10.72 65.08 10.32C65.48 9.92 66.02 9.69 66.6 9.69C67.17 9.69 67.71 9.91 68.11 10.32C68.51 10.72 68.74 11.25 68.74 11.81C68.74 12.37 68.51 12.9 68.11 13.3C67.71 13.7 67.17 13.93 66.6 13.93Z" fill="currentColor"/>
                      <path d="M74.24 15.45V7.24H72.81V15.45H74.24Z" fill="currentColor"/>
                      <path d="M79.15 15.45V9.86H80.57V8.73H79.15V7.66C79.15 7.19 79.4 6.94 79.88 6.94H80.58V5.81H79.73C78.61 5.81 77.72 6.69 77.72 7.81V8.73H76.72V9.86H77.72V15.45H79.15Z" fill="currentColor"/>
                    </svg>
                    <span class="text-xs font-medium" :class="selectedMethod === 'google' ? 'text-blue-700' : 'text-gray-700'">
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
                  <div class="bg-black text-white py-3 px-6 rounded-lg inline-flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 170 170" fill="white">
                      <path d="M150.37 130.25c-2.45 5.66-5.35 10.87-8.71 15.66-4.58 6.53-8.33 11.05-11.22 13.56-4.48 4.12-9.28 6.23-14.42 6.35-3.69 0-8.14-1.05-13.32-3.18-5.197-2.12-9.973-3.17-14.34-3.17-4.58 0-9.492 1.05-14.746 3.17-5.262 2.13-9.501 3.24-12.742 3.35-4.929 0.21-9.842-1.96-14.746-6.52-3.13-2.73-7.045-7.41-11.735-14.04-5.032-7.08-9.169-15.29-12.41-24.65-3.471-10.11-5.211-19.9-5.211-29.378 0-10.857 2.346-20.221 7.045-28.068 3.693-6.303 8.606-11.275 14.755-14.925s12.793-5.51 19.948-5.629c3.915 0 9.049 1.211 15.429 3.591 6.362 2.388 10.447 3.599 12.238 3.599 1.339 0 5.877-1.416 13.57-4.239 7.275-2.618 13.415-3.702 18.445-3.275 13.63 1.1 23.87 6.473 30.68 16.153-12.19 7.386-18.22 17.731-18.1 31.002 0.11 10.337 3.86 18.939 11.23 25.769 3.34 3.17 7.07 5.62 11.22 7.36-0.9 2.61-1.85 5.11-2.86 7.51zM119.11 7.24c0 8.102-2.96 15.667-8.86 22.669-7.12 8.324-15.732 13.134-25.071 12.375-0.119-0.972-0.188-1.995-0.188-3.07 0-7.778 3.386-16.102 9.399-22.908 3.002-3.446 6.82-6.311 11.45-8.597 4.62-2.252 8.99-3.497 13.1-3.71 0.12 1.083 0.17 2.166 0.17 3.24z"/>
                    </svg>
                    <span class="text-sm font-medium">Pay with Apple Pay</span>
                  </div>
                </div>

                <!-- Google Pay -->
                <div v-if="selectedMethod === 'google'" class="mt-6 text-center animate-slide-down">
                  <div class="bg-white border-2 border-gray-200 py-3 px-6 rounded-lg inline-flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-10" viewBox="0 0 80 24">
                      <path d="M7.36 11.14L7.36 13.49L10.6 13.49C10.46 14.42 9.77 15.08 7.36 15.08C5.24 15.08 3.67 13.43 3.67 11.32C3.67 9.2 5.25 7.56 7.36 7.56C8.61 7.56 9.46 8.09 10.01 8.61L11.74 6.89C10.71 5.92 9.25 5.25 7.36 5.25C3.97 5.25 1.2 8.03 1.2 11.32C1.2 14.6 3.97 17.38 7.36 17.38C10.87 17.38 13.1 15.09 13.1 11.45C13.1 11.03 13.06 11.59 13.02 11.16H7.36V11.14Z" fill="#4285F4"/>
                      <path d="M21.35 10.49C20.99 9.27 19.8 8.54 18.45 8.54C17.08 8.54 15.88 9.29 15.54 10.51L15.54 10.51C15.37 11.13 15.38 11.81 15.55 12.47C15.85 13.65 16.88 14.48 18.15 14.61L18.15 14.61C18.25 14.62 18.35 14.63 18.45 14.63C18.56 14.63 18.66 14.62 18.76 14.61L18.76 14.61C19.92 14.49 20.9 13.68 21.25 12.48C21.42 11.85 21.49 11.13 21.35 10.49ZM18.45 13.42C17.84 13.42 17.19 13.03 16.96 12.44L16.96 12.44C16.83 12.08 16.84 11.7 16.96 11.35L16.96 11.35C17.19 10.75 17.84 10.36 18.45 10.36C19.05 10.36 19.7 10.75 19.93 11.33C20.06 11.7 20.06 12.09 19.93 12.44C19.7 13.03 19.05 13.42 18.45 13.42Z" fill="#EA4335"/>
                      <path d="M35.29 11.26H29.8V12.96H33.32C33.11 15.27 31.32 16.53 29.8 16.53C28.68 16.53 27.61 16.09 26.89 15.36C26.18 14.63 25.78 13.65 25.78 12.59C25.78 11.53 26.18 10.56 26.9 9.83C27.62 9.1 28.68 8.66 29.81 8.66C30.96 8.66 31.84 9.17 32.39 9.64L33.6 8.43C32.75 7.64 31.48 6.95 29.8 6.95C28.15 6.95 26.57 7.57 25.46 8.68C24.38 9.75 23.8 11.12 23.8 12.59C23.8 14.06 24.38 15.43 25.46 16.51C26.62 17.66 28.14 18.23 29.8 18.23C31.34 18.23 32.83 17.71 33.91 16.58C34.95 15.48 35.42 14.04 35.42 12.4C35.42 11.97 35.37 11.6 35.29 11.26Z" fill="#4285F4"/>
                      <path d="M40.84 8.54C39.85 8.54 38.94 8.9 38.28 9.55C37.63 10.21 37.26 11.12 37.26 12.09C37.26 13.06 37.63 13.97 38.28 14.63C38.94 15.28 39.85 15.64 40.84 15.64C41.82 15.64 42.74 15.28 43.39 14.63C44.04 13.97 44.41 13.06 44.41 12.09C44.41 11.12 44.04 10.21 43.39 9.55C42.74 8.9 41.82 8.54 40.84 8.54ZM40.84 13.93C40.26 13.93 39.72 13.7 39.32 13.3C38.92 12.9 38.7 12.37 38.7 11.81C38.7 11.25 38.92 10.72 39.32 10.32C39.72 9.92 40.26 9.69 40.84 9.69C41.41 9.69 41.95 9.91 42.35 10.32C42.75 10.72 42.98 11.25 42.98 11.81C42.98 12.37 42.75 12.9 42.35 13.3C41.95 13.7 41.41 13.93 40.84 13.93Z" fill="#4285F4"/>
                      <path d="M49.2 8.54C48.21 8.54 47.3 8.9 46.64 9.55C45.99 10.21 45.62 11.12 45.62 12.09C45.62 13.06 45.99 13.97 46.64 14.63C47.3 15.28 48.21 15.64 49.2 15.64C50.18 15.64 51.1 15.28 51.75 14.63C52.4 13.97 52.77 13.06 52.77 12.09C52.77 11.12 52.4 10.21 51.75 9.55C51.1 8.9 50.18 8.54 49.2 8.54ZM49.2 13.93C48.62 13.93 48.08 13.7 47.68 13.3C47.28 12.9 47.06 12.37 47.06 11.81C47.06 11.25 47.28 10.72 47.68 10.32C48.08 9.92 48.62 9.69 49.2 9.69C49.77 9.69 50.31 9.91 50.71 10.32C51.11 10.72 51.34 11.25 51.34 11.81C51.34 12.37 51.11 12.9 50.71 13.3C50.31 13.7 49.77 13.93 49.2 13.93Z" fill="#4285F4"/>
                      <path d="M57.23 8.54C56.31 8.54 55.43 8.86 54.8 9.49C54.17 10.12 53.85 10.9 53.85 11.81C53.85 13.65 55.4 15.17 57.14 15.17C57.98 15.17 58.57 14.84 58.9 14.52H58.92V14.88C58.92 15.58 58.4 16.11 57.51 16.11C56.86 16.11 56.38 15.76 56.19 15.29L55 15.29L55 15.83C55.39 16.77 56.37 17.27 57.55 17.27C59.09 17.27 60.3 16.15 60.3 14.43V8.73H58.92V9.38H58.9C58.55 8.88 57.91 8.54 57.23 8.54ZM57.44 10.06C58.31 10.06 58.96 10.81 58.96 11.82C58.96 12.83 58.31 13.58 57.44 13.58C56.58 13.58 55.92 12.82 55.92 11.81C55.92 10.8 56.58 10.06 57.44 10.06Z" fill="#4285F4"/>
                      <path d="M66.6 8.54C65.61 8.54 64.7 8.9 64.04 9.55C63.39 10.21 63.02 11.12 63.02 12.09C63.02 13.06 63.39 13.97 64.04 14.63C64.7 15.28 65.61 15.64 66.6 15.64C67.58 15.64 68.5 15.28 69.15 14.63C69.8 13.97 70.17 13.06 70.17 12.09C70.17 11.12 69.8 10.21 69.15 9.55C68.5 8.9 67.58 8.54 66.6 8.54ZM66.6 13.93C66.02 13.93 65.48 13.7 65.08 13.3C64.68 12.9 64.46 12.37 64.46 11.81C64.46 11.25 64.68 10.72 65.08 10.32C65.48 9.92 66.02 9.69 66.6 9.69C67.17 9.69 67.71 9.91 68.11 10.32C68.51 10.72 68.74 11.25 68.74 11.81C68.74 12.37 68.51 12.9 68.11 13.3C67.71 13.7 67.17 13.93 66.6 13.93Z" fill="#4285F4"/>
                      <path d="M74.24 15.45V7.24H72.81V15.45H74.24Z" fill="currentColor"/>
                      <path d="M79.15 15.45V9.86H80.57V8.73H79.15V7.66C79.15 7.19 79.4 6.94 79.88 6.94H80.58V5.81H79.73C78.61 5.81 77.72 6.69 77.72 7.81V8.73H76.72V9.86H77.72V15.45H79.15Z" fill="currentColor"/>
                    </svg>
                    <span class="text-sm font-medium text-gray-800">Pay with Google Pay</span>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </div>

        <!-- Action Buttons -->
        <div class="flex justify-between items-center">
          <button 
            @click="$router.push('/catalog')"
            class="text-blue-600 hover:text-blue-700 text-sm font-medium flex items-center gap-1"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M9.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L7.414 9H15a1 1 0 110 2H7.414l2.293 2.293a1 1 0 010 1.414z" clip-rule="evenodd" />
            </svg>
            Back to Cart
          </button>
          
          <button 
            @click="processPayment"
            :disabled="(selectedMethod === 'card' && !isPaymentValid) || isProcessingPayment"
            class="bg-gradient-to-r from-blue-600 to-blue-400 text-white px-6 py-2 rounded-lg text-sm font-medium hover:from-blue-700 hover:to-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transform transition-all hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100 disabled:hover:from-blue-600 disabled:hover:to-blue-400"
          >
            <span v-if="isProcessingPayment" class="flex items-center">
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Zpracování...
            </span>
            <span v-else>
              Pay {{ formatPrice(totalAmount) }}
            </span>
          </button>
          
          <!-- Informační zpráva o neúplných údajích karty -->
          <div v-if="selectedMethod === 'card' && !isPaymentValid && showPaymentMethods" class="mt-2 text-xs text-red-500 text-right">
            Pro dokončení platby je nutné správně vyplnit všechny údaje karty
          </div>
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
/* Animation for expand/collapse transition */
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

/* Animation for content fade-in */
.animate-fade-in {
  animation: fade-in 0.4s ease forwards;
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Animation for content scale-in */
.animate-scale-in {
  animation: scale-in 0.4s ease forwards;
}

@keyframes scale-in {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

/* Animation for slide down */
.animate-slide-down {
  animation: slide-down 0.4s ease-out forwards;
}

@keyframes slide-down {
  from { transform: translateY(-20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style> 