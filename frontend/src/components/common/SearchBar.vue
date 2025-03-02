<script setup>
defineProps({
  modelValue: String,
  placeholder: {
    type: String,
    default: 'Search...'
  }
})

defineEmits(['update:modelValue'])
</script>

<template>
  <div class="relative flex-grow">
    <div
      class="flex items-center pl-10 pr-4 py-2 bg-gray-50 rounded-lg focus-within:bg-white focus-within:ring-2 focus-within:ring-blue-500"
    >
      <svg
        class="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
        aria-hidden="true"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
        />
      </svg>

      <input
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        type="text"
        :placeholder="placeholder"
        class="outline-none w-full bg-transparent border-none p-0 focus:ring-0 text-sm placeholder-gray-400"
        aria-label="Search"
      />
    </div>
  </div>
</template>
