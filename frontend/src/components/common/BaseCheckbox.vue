<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  label: {
    type: String,
    default: ''
  },
  variant: {
    type: String,
    default: 'primary' // primary, success
  }
})

defineEmits(['update:modelValue'])
</script>

<template>
  <label class="inline-flex items-center space-x-2 cursor-pointer">
    <input
      type="checkbox"
      :checked="modelValue"
      @change="$emit('update:modelValue', $event.target.checked)"
      class="w-4 h-4 rounded border-gray-300 shadow-sm transition-colors duration-200 ease-in-out"
      :class="[
        variant === 'success'
          ? 'text-green-600 focus:ring-green-200 focus:border-green-500'
          : 'text-blue-600 focus:ring-blue-200 focus:border-blue-500',
        'focus:outline-none focus:ring-2 focus:ring-opacity-50'
      ]"
    />
    <span class="text-sm text-gray-700">{{ label }}</span>
  </label>
</template> 