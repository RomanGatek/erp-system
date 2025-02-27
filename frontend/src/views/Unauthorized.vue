<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <div class="text-center">
      <!-- 401 Číslo -->
      <h1 class="text-9xl font-bold text-gray-900 animate-pulse">401</h1>
      
      <!-- Animovaná ikona -->
      <div class="my-8">
        <svg class="w-24 h-24 mx-auto text-gray-400 animate-bounce" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
            d="M12 15v2m0 0v2m0-2h2m-2 0H8m4-6V4m0 0v2m0-2h2m-2 0H8m12 8a4 4 0 01-4 4H8a4 4 0 01-4-4V8a4 4 0 014-4h8a4 4 0 014 4v8z" />
        </svg>
      </div>

      <!-- Text -->
      <h2 class="text-3xl font-semibold text-gray-700 mb-4">
        Přístup odepřen
      </h2>
      <p class="text-gray-500 mb-8 max-w-md mx-auto">
        Pro přístup k této stránce musíte být přihlášeni. Prosím, přihlaste se a zkuste to znovu.
      </p>

      <!-- Tlačítko pro přihlášení -->
      <router-link 
        to="/auth" 
        class="inline-flex items-center px-6 py-3 text-base font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors duration-200"
      >
        <svg class="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
        </svg>
        Přihlásit se
      </router-link>
    </div>
  </div>
</template>

<script setup>
defineOptions({
  name: 'UnauthorizedView'
})
</script> 