<template>
  <div class="min-h-[calc(100vh-theme(space.16))] flex items-center justify-center px-4">
    <div class="text-center">
      <!-- 401 Číslo -->
      <h1 class="text-9xl font-bold text-gray-900">401</h1>
      
      <!-- Ikona zámku -->
      <div class="my-8">
        <svg class="w-24 h-24 mx-auto text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
            d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
      </div>

      <!-- Text -->
      <h2 class="text-3xl font-semibold text-gray-700 mb-4">
        Nedostatečná oprávnění
      </h2>
      <p class="text-gray-500 mb-8 max-w-md mx-auto">
        Nemáte dostatečná oprávnění pro přístup k této stránce. Pokud si myslíte, že by jste měli mít přístup, kontaktujte prosím administrátora.
      </p>

      <!-- Tlačítka -->
      <div class="flex justify-center gap-4">
        <button 
          @click="$router.go(-1)"
          class="inline-flex items-center px-6 py-3 text-base font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors duration-200"
        >
          <svg class="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Zpět
        </button>

        <router-link 
          to="/" 
          class="inline-flex items-center px-6 py-3 text-base font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors duration-200"
        >
          <svg class="w-5 h-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
          Domů
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
defineOptions({
  name: 'UnauthorizedView'
})
</script>

<style scoped>
/* Jemná animace pro hover efekty */
button, a {
  transition: all 0.2s ease;
}

/* Stín pro tlačítka při hoveru */
button:hover, a:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
}
</style> 