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
  },
  disabled: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: Number,
    default: null
  },
  rows: {
    type: Number,
    default: 3
  },
  counter: {
    type: Boolean,
    default: false
  }
})

defineEmits(['update:modelValue'])
</script>

<template>
  <div class="space-y-1">
    <label v-if="label" class="block text-sm font-medium text-gray-700">
      {{ label }}
    </label>
    
    <template v-if="type === 'textarea'">
      <textarea
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        :placeholder="placeholder"
        :rows="rows"
        :maxlength="maxlength"
        class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 transition-colors resize-none"
        :class="[
          error
            ? 'border-red-500 focus:ring-red-200'
            : 'border-gray-300 focus:ring-blue-200',
          disabled
            ? 'bg-gray-200 pointer-events-none cursor-not-allowed text-gray-500'
            : ''
        ]"
        v-bind="$attrs"
      ></textarea>
      <div v-if="counter && maxlength" class="flex justify-end">
        <span class="text-xs text-gray-500">{{ modelValue?.length || 0 }}/{{ maxlength }}</span>
      </div>
    </template>
    
    <input
      v-else
      :type="type"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :placeholder="placeholder"
      class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 transition-colors"
      :class="[
        error
          ? 'border-red-500 focus:ring-red-200'
          : 'border-gray-300 focus:ring-blue-200',
        disabled
          ? 'bg-gray-200 pointer-events-none cursor-not-allowed text-gray-500'
          : ''
      ]"
      v-bind="$attrs"
    />
    
    <span v-if="error" class="text-xs text-red-500">{{ error }}</span>
  </div>
</template>
