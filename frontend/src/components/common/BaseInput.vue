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
      class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 transition-colors"
      :class="[
        error 
          ? 'border-red-500 focus:ring-red-200' 
          : 'border-gray-300 focus:ring-blue-200'
      ]"
      v-bind="$attrs"
    />
    <span v-if="error" class="text-xs text-red-500">{{ error }}</span>
  </div>
</template> 