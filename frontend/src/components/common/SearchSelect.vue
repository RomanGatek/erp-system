<template>
  <div class="relative">
    <!-- Input Field -->
    <BaseInput
      v-model="searchQuery"
      :placeholder="placeholder"
      :label="label"
      @focus="showDropdown = true"
      @blur="handleBlur"
      @keydown.down.prevent="navigateDown"
      @keydown.up.prevent="navigateUp"
      @keydown.enter.prevent="selectHighlighted"
      @keydown.esc="closeDropdown"
      :error="error"
      v-bind="$attrs"
    >
      <template #append>
        <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
          <svg
            class="w-5 h-5 text-gray-400 transition-transform duration-200"
            :class="{ 'rotate-180': showDropdown }"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </div>
      </template>
    </BaseInput>

    <!-- Dropdown -->
    <transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0 translate-y-1"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-1"
    >
      <div
        v-if="showDropdown"
        class="absolute z-50 w-full bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg mt-1 shadow-lg max-h-60 overflow-y-auto divide-y divide-gray-100"
      >
        <!-- Empty State -->
        <div v-if="filteredItems.length === 0" class="p-4 text-center text-gray-500">
          No results found
        </div>

        <!-- Items List -->
        <div
          v-for="(item, index) in filteredItems"
          :key="item[returnField]"
          class="relative flex items-center p-3 cursor-pointer transition-all duration-200"
          :class="[
            highlightedIndex === index ? 'bg-blue-50' : 'hover:bg-gray-50',
            selectedOption && selectedOption[returnField] === item[returnField] ? 'bg-blue-50/50' : ''
          ]"
          @mouseenter="highlightedIndex = index"
          @click="selectItem(item)"
        >
          <!-- Avatar or Initial -->
          <div
            v-if="item.avatar"
            class="w-8 h-8 rounded-full mr-3 overflow-hidden border border-gray-200"
          >
            <img
              :src="item.avatar"
              :alt="item[by]"
              class="w-full h-full object-cover transition-transform duration-200 hover:scale-110"
            />
          </div>
          <div
            v-else
            class="w-8 h-8 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center mr-3"
          >
            <span class="text-white font-medium">{{ item[by].charAt(0).toUpperCase() }}</span>
          </div>

          <!-- Item Text -->
          <div class="flex-1 min-w-0">
            <div class="text-sm font-medium text-gray-900 truncate">{{ item[by] }}</div>
            <div v-if="item.description" class="text-xs text-gray-500 truncate">
              {{ item.description }}
            </div>
          </div>

          <!-- Selected Check Mark -->
          <svg
            v-if="selectedOption && selectedOption[returnField] === item[returnField]"
            class="w-5 h-5 text-blue-500 ml-3"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import BaseInput from './BaseInput.vue'

const props = defineProps({
  items: {
    type: Array,
    required: true
  },
  returnField: {
    type: String,
    default: 'value'
  },
  modelValue: {
    type: Object,
    default: () => ({})
  },
  by: {
    type: String,
    default: 'label'
  },
  label: {
    type: String,
    default: 'Select item'
  },
  placeholder: {
    type: String,
    default: 'Search...'
  },
  error: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const searchQuery = ref('')
const showDropdown = ref(false)
const selectedOption = ref(null)
const highlightedIndex = ref(0)

const filteredItems = computed(() => {
  const query = searchQuery.value.toLowerCase()
  return props.items.filter(item => 
    item[props.by].toLowerCase().includes(query)
  )
})

// Keyboard Navigation
const navigateDown = () => {
  if (highlightedIndex.value < filteredItems.value.length - 1) {
    highlightedIndex.value++
    ensureHighlightedInView()
  }
}

const navigateUp = () => {
  if (highlightedIndex.value > 0) {
    highlightedIndex.value--
    ensureHighlightedInView()
  }
}

const selectHighlighted = () => {
  if (filteredItems.value[highlightedIndex.value]) {
    selectItem(filteredItems.value[highlightedIndex.value])
  }
}

const ensureHighlightedInView = () => {
  nextTick(() => {
    const dropdown = document.querySelector('.overflow-y-auto')
    const highlighted = dropdown?.children[highlightedIndex.value]
    if (dropdown && highlighted) {
      const dropdownRect = dropdown.getBoundingClientRect()
      const highlightedRect = highlighted.getBoundingClientRect()

      if (highlightedRect.bottom > dropdownRect.bottom) {
        dropdown.scrollTop += highlightedRect.bottom - dropdownRect.bottom
      } else if (highlightedRect.top < dropdownRect.top) {
        dropdown.scrollTop -= dropdownRect.top - highlightedRect.top
      }
    }
  })
}

const selectItem = (item) => {
  selectedOption.value = item
  searchQuery.value = item[props.by]
  emit('update:modelValue', item)
  closeDropdown()
}

const closeDropdown = () => {
  showDropdown.value = false
  highlightedIndex.value = 0
}

const handleBlur = () => {
  setTimeout(() => {
    closeDropdown()
  }, 200)
}

// Reset highlight index when filtered items change
watch(filteredItems, () => {
  highlightedIndex.value = 0
})

// Initialize selected value
onMounted(() => {
  if (props.modelValue && Object.keys(props.modelValue).length > 0) {
    selectedOption.value = props.modelValue
    searchQuery.value = props.modelValue[props.by]
  }
})

// Cleanup
onBeforeUnmount(() => {
  closeDropdown()
})
</script>

<style scoped>
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.5);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background-color: rgba(156, 163, 175, 0.7);
}
</style>
