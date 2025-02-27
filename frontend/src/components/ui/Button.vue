<template>
  <button 
    :class="[
      'group px-6 py-3 rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden',
      variant === 'primary' ? 'bg-gradient-to-r from-blue-600 to-blue-400 text-white' : 'bg-white/15 backdrop-blur-lg text-gray-700',
      className
    ]"
    v-bind="$attrs"
  >
    <span class="relative z-10 flex items-center justify-center">
      <slot />
    </span>
    <div 
      class="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      :class="variant === 'primary' ? 'bg-gradient-to-r from-blue-500 to-blue-300' : 'bg-gradient-to-r from-gray-500/20 to-white/30'"
    ></div>
  </button>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary'].includes(value)
  },
  className: {
    type: String,
    default: ''
  }
})
</script> 