<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <div class="text-center">
      <!-- 404 Číslo -->
      <h1 class="text-9xl font-bold text-gray-900 animate-pulse">404</h1>
      
      <!-- Animovaná ikona -->
      <div class="my-8">
        <svg class="w-24 h-24 mx-auto text-gray-400 animate-bounce" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
            d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>

      <!-- Text -->
      <h2 class="text-3xl font-semibold text-gray-700 mb-4">
        Stránka nenalezena
      </h2>
      <p class="text-gray-500 mb-8 max-w-md mx-auto">
        Omlouváme se, ale stránka, kterou hledáte, neexistuje nebo byla přesunuta.
      </p>

      <!-- Tlačítko pro návrat -->
      <router-link 
        to="/" 
        class="inline-flex items-center px-6 py-3 text-base font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors duration-200"
      >
        <svg class="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        Zpět na hlavní stránku
      </router-link>
    </div>
  </div>
</template>

<script setup>
defineOptions({
  name: 'NotFoundView'
})
</script> 