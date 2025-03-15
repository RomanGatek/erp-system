<template>
  <div class="space-y-1">
    <label v-if="label" class="block text-xs font-medium text-gray-700">
      {{ label }}
    </label>
    <VueDatePicker
      v-model="dateValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :enable-time-picker="false"
      :auto-apply="true"
      :text-input="false"
      locale="cs-CZ"
      :week-start="1"
      :day-names="['Po', 'Út', 'St', 'Čt', 'Pá', 'So', 'Ne']"
      :dark="false"
      menu-class-name="dp-menu"
      :error="!!error"
      class="dp-custom"
      :model-type="'yyyy-MM'"
      month-picker
      hide-offset-dates
      :year-range="[currentYear, currentYear + 10]"
      :disable-past="true"
      :format="formatDate"
      @update:model-value="handleUpdate"
    >
      <template #trigger>
        <div
          class="w-full px-3 py-1.5 text-sm border rounded-lg focus:outline-none transition-colors cursor-pointer"
          :class="[
            error
              ? 'border-red-500 hover:border-red-600'
              : 'border-gray-300 hover:border-gray-400',
            disabled
              ? 'bg-gray-200 pointer-events-none cursor-not-allowed text-gray-500'
              : ''
          ]"
        >
          {{ formattedValue }}
        </div>
      </template>
    </VueDatePicker>
    <span v-if="error" class="text-xs text-red-500">{{ error }}</span>
  </div>
</template>

<script setup>
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import { computed, ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: 'MM/YY'
  },
  error: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

// Get current year for year range
const currentYear = new Date().getFullYear()

// Handle the date value
const dateValue = ref(null)

// Convert MM/YY format to date object
watch(() => props.modelValue, (newValue) => {
  if (newValue && /^\d{2}\/\d{2}$/.test(newValue)) {
    const [month, shortYear] = newValue.split('/')
    const year = parseInt(`20${shortYear}`)
    
    // Create a date object for the first day of the selected month/year
    dateValue.value = new Date(year, parseInt(month) - 1, 1)
  } else {
    dateValue.value = null
  }
}, { immediate: true })

// Format the date for display
const formatDate = (date) => {
  if (!date) return ''
  const month = date.getMonth() + 1
  const year = date.getFullYear().toString().substr(2, 2)
  return `${month.toString().padStart(2, '0')}/${year}`
}

// Computed property for the formatted value
const formattedValue = computed(() => {
  if (dateValue.value) {
    return formatDate(dateValue.value)
  }
  return props.placeholder
})

// Handle update from the date picker
const handleUpdate = (newValue) => {
  if (newValue) {
    const date = new Date(newValue)
    const month = date.getMonth() + 1
    const year = date.getFullYear().toString().substr(2, 2)
    emit('update:modelValue', `${month.toString().padStart(2, '0')}/${year}`)
  } else {
    emit('update:modelValue', '')
  }
}
</script>

<style>
.dp-custom {
  --dp-background-color: #fff;
  --dp-text-color: #374151;
  --dp-hover-color: #f3f4f6;
  --dp-hover-text-color: #111827;
  --dp-hover-icon-color: #111827;
  --dp-primary-color: #3b82f6;
  --dp-primary-text-color: #fff;
  --dp-secondary-color: #f3f4f6;
  --dp-border-color: #e5e7eb;
  --dp-menu-border-width: 1px;
  --dp-border-radius: 0.5rem;
  --dp-padding: 0.5rem;
  --dp-z-index: 20;
}

.dp-menu {
  border-radius: 0.5rem !important;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1) !important;
  border: 1px solid var(--dp-border-color) !important;
}
</style> 