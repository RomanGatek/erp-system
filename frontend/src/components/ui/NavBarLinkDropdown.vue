<template>
  <div class="relative" @mouseenter="openDropdown" @mouseleave="closeDropdown">
    <button :class="{ 'bg-white/20 text-white shadow-lg': items.some(item => $route.path.startsWith(item.to)) }" class="text-white/90 hover:text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 flex items-center">
      <component :is="icon" class="w-4 h-4 mr-2" />
      {{ text }}
      <svg class="w-4 h-4 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
      </svg>
    </button>
    <transition name="fade">
      <div v-if="isOpen" class="absolute left-0 mt-2 w-48 rounded-xl shadow-lg bg-white/90 backdrop-blur-lg ring-1 ring-black/5 py-2 transition-all duration-300">
        <template v-for="item in items" :key="item.to">
          <router-link
            v-if="hasPermission(item.requiredRole)"
            :to="item.to"
            class="flex px-4 py-2 text-sm text-gray-700 hover:bg-gradient-to-r from-blue-50 to-blue-100 transition-colors duration-200"
            @click="closeDropdown"
          >
            <component :is="item.icon" class="w-4 h-4 mr-2" />
            {{ item.text }}
          </router-link>
          <span
            v-else
            class="flex px-4 py-2 text-sm text-gray-400 cursor-not-allowed"
            :title="`Insufficient permissions for ${item.text}`"
          >
            {{ item.text }}
          </span>
        </template>
      </div>
    </transition>
  </div>
</template>



<script setup>
import { ref } from 'vue'
import { hasPermission } from '@/utils/user-utils.js'

defineProps({
  text: String,
  icon: Object,
  items: Array,
})

const isOpen = ref(false)
let timeoutId = null

const openDropdown = () => {
  clearTimeout(timeoutId)
  isOpen.value = true
}

const closeDropdown = () => {
  timeoutId = setTimeout(() => {
    isOpen.value = false
  }, 200)
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active in <2.1.8 */ {
  opacity: 0;
}
</style>
