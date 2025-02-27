<template>
  <footer 
    class="bg-gradient-to-r from-blue-700 to-blue-500 text-white fixed bottom-0 w-full transition-all duration-300"
    :class="{ 'h-10': !isExpanded, 'h-48': isExpanded }"
    @mouseenter="isExpanded = true"
    @mouseleave="isExpanded = false"
  >
    <div class="container mx-auto px-4 h-full">
      <!-- Základní řádek, který je vždy vidět -->
      <div class="h-10 flex justify-between items-center">
        <div class="text-sm text-white/80">
          &copy; {{ new Date().getFullYear() }} ERP System
        </div>
      </div>

      <!-- Rozšířený obsah -->
      <div 
        class="transition-all duration-300 overflow-hidden"
        :class="{ 'h-0': !isExpanded, 'h-38': isExpanded }"
      >
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 py-4">
          <div class="text-center md:text-left">
            <h3 class="text-sm font-semibold mb-2">ERP System</h3>
            <p class="text-xs text-white/80">
              Powerful enterprise resource planning solution.
            </p>
          </div>
          
          <div class="text-center">
            <h3 class="text-sm font-semibold mb-2">Quick Links</h3>
            <div class="flex flex-col space-y-1">
              <router-link 
                to="/?section=team" 
                class="text-sm text-white/80 hover:text-white transition-colors duration-300"
              >
                Meet the Team
              </router-link>
              <router-link 
                to="/?section=about" 
                class="text-sm text-white/80 hover:text-white transition-colors duration-300"
              >
                About Course
              </router-link>
            </div>
          </div>

          <div class="text-center md:text-right">
            <h3 class="text-sm font-semibold mb-2">Connect With Us</h3>
            <div class="flex justify-center md:justify-end space-x-4">
              <a 
                href="mailto:info@erpsystem.com" 
                class="text-white/80 hover:text-white transition-colors duration-300 flex items-center gap-1"
              >
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M20,4H4C2.895,4,2,4.895,2,6v12c0,1.105,0.895,2,2,2h16c1.105,0,2-0.895,2-2V6C22,4.895,21.105,4,20,4z M20,8l-8,5L4,8V6l8,5l8-5V8z"/>
                </svg>
                <span class="text-xs">Contact</span>
              </a>
              <a 
                href="https://github.com/cooffeeRequired/HELL-TO-PASS" 
                target="_blank"
                class="text-white/80 hover:text-white transition-colors duration-300 flex items-center gap-1"
              >
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                </svg>
                <span class="text-xs">GitHub</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>

  <!-- Přidáme padding pro obsah nad footerem -->
  <div class="h-10"></div>
</template>

<script setup>
import { ref } from 'vue'
const isExpanded = ref(false)
</script> 