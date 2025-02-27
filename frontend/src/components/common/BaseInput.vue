<script setup>
defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  placeholder: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  },
  variant: {
    type: String,
    default: 'primary' // primary, success, warning, danger
  }
})

defineEmits(['update:modelValue'])
</script>

<template>
  <div class="space-y-1">
    <label v-if="label" class="block text-sm font-medium text-gray-700">
      {{ label }}
    </label>
    <input
      :type="type"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :placeholder="placeholder"
      class="w-full px-3 py-1.5 text-sm border rounded-lg shadow-sm transition-colors duration-200 ease-in-out"
      :class="[
        error 
          ? 'border-red-300 focus:ring-red-200 focus:border-red-500' 
          : variant === 'success'
          ? 'border-gray-300 focus:ring-green-200 focus:border-green-500'
          : 'border-gray-300 focus:ring-blue-200 focus:border-blue-500',
        'placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-opacity-50'
      ]"
    />
    <p v-if="error" class="text-xs text-red-600">{{ error }}</p>
  </div>
</template> 