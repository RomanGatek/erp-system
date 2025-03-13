<template>
  <router-link
    v-if="hasPermission(requiredRole) && !dropdown"
    :to="to"
    class="text-white/90 hover:text-white px-4 py-2 rounded-full text-sm font-medium transition-all duration-300 flex items-center hover:bg-white/10"
    :class="{
      'bg-white/20 text-white shadow-lg': $route.path === to
      }"
  >
    <component :is="icon" class="w-4 h-4 mr-2" />
    {{ text }}
  </router-link>
  <span
    v-else-if="!hasPermission(requiredRole) && !dropdown"
    class="text-white/50 cursor-not-allowed px-4 py-2 rounded-full text-sm font-medium flex items-center"
    :title="`Insufficient permissions fro ${text}`"
  >
    <component :is="icon" class="w-4 h-4 mr-2" />
    {{ text }}
  </span>
</template>

<script setup>
import { hasPermission } from '@/utils'

defineProps({
  to: String,
  text: String,
  icon: Object,
  requiredRole: String,
  dropdown: Boolean,
})
</script>
