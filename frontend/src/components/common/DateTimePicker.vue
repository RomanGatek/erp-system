<template>
  <div class="space-y-1">
    <label v-if="label" class="block text-sm font-medium text-gray-700">
      {{ label }}
    </label>
    <VueDatePicker
      v-model="dateValue"
      :format="format"
      :min-date="minDate"
      :placeholder="placeholder"
      :disabled="disabled"
      :enable-time-picker="true"
      :auto-apply="true"
      :text-input="true"
      locale="cs-CZ"
      :week-start="1"
      :month-name-format="'long'"
      :day-names="['Po', 'Út', 'St', 'Čt', 'Pá', 'So', 'Ne']"
      :dark="false"
      :now-button="true"
      menu-class-name="dp-menu"
      :error="!!error"
      class="dp-custom"
      @update:model-value="handleUpdate"
    >
      <template #trigger>
        <div
          class="w-full px-3 py-2 border rounded-lg focus:outline-none transition-colors cursor-pointer"
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
    type: [Date, String],
    default: null
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: 'Select date and time'
  },
  error: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  minDate: {
    type: Date,
    default: () => new Date()
  }
})

const emit = defineEmits(['update:modelValue'])

const dateValue = ref(props.modelValue ? new Date(props.modelValue) : null)

const format = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('cs-CZ', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formattedValue = computed(() => {
  return dateValue.value ? format(dateValue.value) : props.placeholder
})

const handleUpdate = (newValue) => {
  if (newValue) {
    emit('update:modelValue', newValue.toISOString())
  } else {
    emit('update:modelValue', null)
  }
}

watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    dateValue.value = new Date(newValue)
  } else {
    dateValue.value = null
  }
})
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

.dp-custom .dp__input {
  background-color: transparent;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  padding: 0.5rem 0.75rem;
  width: 100%;
  border: 1px solid var(--dp-border-color);
}

.dp-custom .dp__input:hover {
  border-color: var(--dp-hover-color);
}

.dp-custom .dp__input:focus {
  outline: none;
  border-color: var(--dp-primary-color);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}
</style> 