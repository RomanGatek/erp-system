<script setup>
import { ref, watch, onMounted, computed } from 'vue'

const props = defineProps({
  error: {
    type: String,
    default: ''
  },
  success: {
    type: String,
    default: ''
  },
  warning: {
    type: String,
    default: ''
  },
  info: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  },
  duration: {
    type: Number,
    default: 5000 // 5 seconds
  },
  persistent: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['clearStatus'])
const isVisible = ref(false)
let timeoutId = null

const statusType = computed(() => {
  if (props.error) return 'error'
  if (props.success) return 'success'
  if (props.warning) return 'warning'
  if (props.info) return 'info'
  if (props.loading) return 'loading'
  return null
})

const statusConfig = computed(() => {
  const configs = {
    error: {
      bgClass: 'bg-red-50',
      textClass: 'text-red-700',
      borderClass: 'border-red-100',
      iconPath: 'M10 14.59l6.3-6.3a1 1 0 011.4 1.42l-7 7a1 1 0 01-1.4 0l-3-3a1 1 0 011.4-1.42l2.3 2.3z',
      message: props.error
    },
    success: {
      bgClass: 'bg-green-50',
      textClass: 'text-green-700',
      borderClass: 'border-green-100',
      iconPath: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
      message: props.success
    },
    warning: {
      bgClass: 'bg-yellow-50',
      textClass: 'text-yellow-700',
      borderClass: 'border-yellow-100',
      iconPath: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z',
      message: props.warning
    },
    info: {
      bgClass: 'bg-blue-50',
      textClass: 'text-blue-700',
      borderClass: 'border-blue-100',
      iconPath: 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
      message: props.info
    },
    loading: {
      bgClass: 'bg-blue-50',
      textClass: 'text-blue-700',
      borderClass: 'border-blue-100',
      message: 'Loading...'
    }
  }
  return statusType.value ? configs[statusType.value] : null
})

watch([() => props.error, () => props.success, () => props.warning, () => props.info, () => props.loading],
  ([newError, newSuccess, newWarning, newInfo, newLoading]) => {
    if (newError || newSuccess || newWarning || newInfo || newLoading) {
      showStatus()
    }
  }
)

function showStatus() {
  isVisible.value = true

  if (timeoutId) {
    clearTimeout(timeoutId)
  }

  if (!props.persistent && !props.loading) {
    timeoutId = setTimeout(() => {
      hideStatus()
    }, props.duration)
  }
}

function hideStatus() {
  isVisible.value = false
  emit('clearStatus')
}

onMounted(() => {
  if (statusType.value) {
    showStatus()
  }
})
</script>

<template>
  <transition
    enter-active-class="transform transition duration-300 ease-out"
    enter-from-class="translate-y-2 opacity-0"
    enter-to-class="translate-y-0 opacity-100"
    leave-active-class="transform transition duration-200 ease-in"
    leave-from-class="translate-y-0 opacity-100"
    leave-to-class="translate-y-2 opacity-0"
  >
    <div
      v-if="isVisible && statusConfig"
      :class="[
        'mb-4 px-4 py-3 rounded-lg border shadow-sm',
        statusConfig.bgClass,
        statusConfig.textClass,
        statusConfig.borderClass
      ]"
    >
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <!-- Loading Spinner -->
          <svg
            v-if="loading"
            class="animate-spin h-5 w-5 mr-2"
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
            />
            <path
              class="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>

          <!-- Status Icon -->
          <svg
            v-else
            class="h-5 w-5 mr-2"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              :d="statusConfig.iconPath"
            />
          </svg>

          <span class="text-sm font-medium">{{ statusConfig.message }}</span>
        </div>

        <!-- Close Button (only for non-loading states) -->
        <button
          v-if="!loading"
          @click="hideStatus"
          class="ml-4 inline-flex text-gray-400 hover:text-gray-500 focus:outline-none"
          aria-label="Dismiss"
        >
          <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
              clip-rule="evenodd"
            />
          </svg>
        </button>
      </div>
    </div>
  </transition>
</template>
