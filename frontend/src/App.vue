<script>
import CustomNavbar from '@/components/CustomNavbar.vue'
import CustomFooter from '@/components/CustomFooter.vue'
import { computed, watch } from 'vue'
import { useMeStore } from './stores/me.store.js'
import { NotificationWrapper } from '@/components'

export default {
  name: 'App',
  components: {
    NotificationWrapper,
    CustomNavbar,
    CustomFooter,
  },
  setup() {
    const meStore = useMeStore()
    const isAuthenticated = computed(() => meStore.user)
    return {
      isAuthenticated,
    }
  },
}

</script>

<template>
  <NotificationWrapper />
  <div id="app" class="min-h-screen flex flex-col relative bg-gray-50">
    <!-- Ambientní pozadí -->
    <div class="fixed inset-0 bg-gradient-pattern opacity-5"></div>
    <div class="fixed inset-0 bg-mesh opacity-10"></div>

    <!-- Hlavní obsah -->
    <div class="relative z-10 flex flex-col min-h-screen">
      <CustomNavbar v-if="isAuthenticated" class="flex-shrink-0" />

      <main class="flex-grow container mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <router-view v-slot="{ Component }" class="w-full h-full">
          <transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 translate-y-1"
            enter-to-class="opacity-100 translate-y-0" leave-active-class="transition ease-in duration-150"
            leave-from-class="opacity-100 translate-y-0" leave-to-class="opacity-0 translate-y-1" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
      <CustomFooter class="mt-auto" />
    </div>
  </div>
</template>
