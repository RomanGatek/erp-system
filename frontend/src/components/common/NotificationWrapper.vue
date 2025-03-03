<template>
  <Notifications position="bottom left" :duration="50000">
    <!-- Slot #body: uvnitř definujete HTML strukturu pro každou notifikaci -->
    <template #body="props">
      <!--  bg-red-50 text-red-700 -->
      <div
        class="relative flex items-start p-4 mb-4 rounded-lg shadow-md w-72 ml-4 text-sm"
        :class="{
          'bg-green-100 text-green-700': props.item.type === 'success',
          'bg-red-100 text-red-700': props.item.type === 'error',
          'bg-yellow-100 text-yellow-700': props.item.type === 'warn',
          'bg-blue-100 text-blue-700': props.item.type === 'info',
          'bg-gray-100 text-gray-700': !props.item.type || !['success', 'error', 'warn', 'info'].includes(props.item.type)
        }"
      >
        <!-- Ikona dle typu -->
        <div class="mr-3 mt-0.5">
          <!-- Success (check) -->
          <svg
            v-if="props.item.type === 'success'"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            class="w-5 h-5"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M5 13l4 4L19 7" />
          </svg>

          <!-- Info (i) -->
          <svg
            v-else-if="props.item.type === 'info'"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            class="w-5 h-5"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M13 16h-1v-4h-1m1-4h.01M12 6v.01"
            />
            <circle cx="12" cy="12" r="9" stroke="currentColor" />
          </svg>

          <!-- Warning (exclamation) -->
          <svg
            v-else-if="props.item.type === 'warn'"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            class="w-5 h-5"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M10.29 3.86l-6.6 11.45A1 1 0 004.54 17h14.92a1 1 0 00.85-1.54l-6.6-11.45a1 1 0 00-1.7 0z" />
            <path d="M12 9v4m0 4h.01" />
          </svg>

          <!-- Error (x) -->
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            class="w-5 h-5"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>

        <!-- Obsah notifikace (titulek, text) -->
        <div class="flex-1">
          <!-- Titulek -->
          <p class="font-semibold" v-if="props.item.title">
            {{ props.item.title }}
          </p>
          <!-- Text (lze i v-html, pokud chcete HTML) -->
          <p v-if="props.item.text">
            {{ props.item.text }}
          </p>
        </div>

        <!-- Tlačítko pro zavření notifikace (vpravo nahoře) -->
        <button
          class="absolute top-2 right-2 hover:cursor-pointer hover:opacity-75 focus:outline-none"
          @click="props.close"
          :class="{
            'text-green-700': props.item.type === 'success',
            'text-red-700': props.item.type === 'error',
            'text-yellow-700': props.item.type === 'warn',
            'text-blue-700': props.item.type === 'info',
            'text-gray-700': !props.item.type || !['success', 'error', 'warn', 'info'].includes(props.item.type)
          }"
        >
          &times;
        </button>
      </div>
    </template>
  </Notifications>
</template>

<script setup>
import { Notifications } from '@kyvg/vue3-notification'
</script>

<style>
/* Style pro zarovnání prvků notifikačního kontejneru */
.vue-notification-wrapper {
  margin-left: 1rem !important;
}
</style>
