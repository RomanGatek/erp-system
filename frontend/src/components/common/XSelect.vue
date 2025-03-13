<template>
  <div class="relative">
    <button 
      @click="isOpen = !isOpen"
      @blur="closeDelayed"
      class="flex items-center gap-2 px-3 py-1.5 text-sm font-medium border border-gray-200 rounded-lg hover:border-indigo-300 transition-all"
      :class="{
        'border-indigo-300': isOpen
      }"
    >
      <span class="text-gray-600">{{ label }}:</span>
      <span class="text-gray-700 hover:text-indigo-600">{{ selectedLabel }}</span>
      <svg 
        class="w-4 h-4 text-gray-500 transition-transform ml-1" 
        :class="{ 'rotate-180': isOpen }"
        fill="none" 
        stroke="currentColor" 
        viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
      </svg>
    </button>
    
    <!-- Dropdown Menu -->
    <div 
      v-if="isOpen"
      class="absolute right-0 mt-1 py-2 w-48 bg-white rounded-lg shadow-lg border border-gray-100 z-50"
    >
      <button
        v-for="option in options"
        :key="option.value"
        @mousedown.prevent="selectOption(option.value)"
        class="w-full px-4 py-2 text-left text-sm hover:bg-indigo-50 hover:text-indigo-600 transition-colors"
        :class="{
          'text-indigo-600 bg-indigo-50': modelValue === option.value,
          'text-gray-700': modelValue !== option.value
        }"
      >
        {{ option.label }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'XSelect',
  props: {
    modelValue: {
      type: [String, Number],
      required: true
    },
    options: {
      type: Array,
      required: true,
      validator: (options) => {
        return options.every(option => 
          typeof option === 'object' && 
          'value' in option && 
          'label' in option
        )
      }
    },
    label: {
      type: String,
      default: 'Select'
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const isOpen = ref(false)

    const selectedLabel = computed(() => {
      const option = props.options.find(opt => opt.value === props.modelValue)
      return option ? option.label : ''
    })

    const selectOption = (value) => {
      emit('update:modelValue', value)
      isOpen.value = false
    }

    const closeDelayed = () => {
      setTimeout(() => {
        isOpen.value = false
      }, 200)
    }

    return {
      isOpen,
      selectedLabel,
      selectOption,
      closeDelayed
    }
  }
}
</script> 