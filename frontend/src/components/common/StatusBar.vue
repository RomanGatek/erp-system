<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  error: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// Definování emitovaných událostí
const emit = defineEmits(['clearError'])

const isErrorVisible = ref(false)
let timeoutId = null

// Sledování změn v error prop pro aktivaci časovače
watch(() => props.error, (newVal) => {
  if (newVal) {
    showError()
  }
})

function showError() {
  isErrorVisible.value = true

  // Zrušit předchozí časovač, pokud existuje
  if (timeoutId) {
    clearTimeout(timeoutId)
  }

  // Nastavit nový časovač na 15000ms pouze pro error
  timeoutId = setTimeout(() => {
    isErrorVisible.value = false
    emit('clearError') // Emitujeme událost místo přímého nastavení props
  }, 15000)
}

// Při prvním načtení komponenty, pokud je error true
onMounted(() => {
  if (props.error) {
    showError()
  }
})
</script>

<template>
  <div
    v-if="(error && isErrorVisible) || loading"
    class="mb-4 p-3 rounded-lg text-sm flex items-center justify-between"
    :class="error ? 'bg-red-50 text-red-700' : 'bg-blue-50 text-blue-700'"
  >
    <div class="flex items-center">
      <!-- Error icon -->
      <svg
        v-if="error"
        class="h-5 w-5 mr-2"
        viewBox="0 0 20 20"
        fill="currentColor"
      >
        <path
          fill-rule="evenodd"
          d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
          clip-rule="evenodd"
        />
      </svg>
      <!-- Loading spinner -->
      <svg
        v-if="loading"
        class="animate-spin h-5 w-5 mr-2"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
      >
        <circle
          class="opacity-25"
          cx="12"
          cy="12"
          r="10"
          stroke="currentColor"
          stroke-width="4"
        ></circle>
        <path
          class="opacity-75"
          fill="currentColor"
          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        ></path>
      </svg>
      <span>{{ error || 'Loading ...' }}</span>
    </div>
  </div>
</template>
