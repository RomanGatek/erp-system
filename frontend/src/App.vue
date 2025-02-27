<template>
  <notifications position="bottom left" classes="my-notification" :duration="3000" />
  <div id="app" class="min-h-screen flex flex-col relative bg-gray-50">
    <!-- Ambientní pozadí -->
    <div class="fixed inset-0 bg-gradient-pattern opacity-5"></div>
    <div class="fixed inset-0 bg-mesh opacity-10"></div>
    
    <!-- Hlavní obsah -->
    <div class="relative z-10 flex flex-col min-h-screen">

      <Navbar v-if="isAuthenticated" class="flex-shrink-0" />
      
      <main class="flex-grow container mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <router-view v-slot="{ Component }" class="w-full h-full">
          <transition
            enter-active-class="transition ease-out duration-200"
            enter-from-class="opacity-0 translate-y-1"
            enter-to-class="opacity-100 translate-y-0"
            leave-active-class="transition ease-in duration-150"
            leave-from-class="opacity-100 translate-y-0"
            leave-to-class="opacity-0 translate-y-1"
            mode="out-in"
          >
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
      <Footer class="mt-auto" />
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";
import Footer from "@/components/Footer.vue";
import { ref, computed } from "vue";
import { useMeStore } from "./stores/me";

export default {
  name: 'App',
  components: {
    Navbar,
    Footer
  },
  setup() {
    const meStore = useMeStore();
    const isAuthenticated = computed(() => meStore.user);

    return {
      isAuthenticated
    }
  }
}
</script>

<style>
.my-notification {
  margin-bottom: 1rem !important;
  margin-left: 1rem !important;
  border-radius: 0.5rem !important;
  padding: 1rem !important;
  color: white !important;
  font-size: 0.875rem !important;
}

.my-notification.success {
  background-color: #10B981 !important;
}

.my-notification.error {
  background-color: #EF4444 !important;
}

.my-notification.warning {
  background-color: #F59E0B !important;
}

.my-notification.info {
  background-color: #3B82F6 !important;
}

html {
  font-size: 15px;
  background-color: #f8fafc;
}

h1 {
  font-size: 2rem !important;
}

h2 {
  font-size: 1.75rem !important;
}

.text-lg {
  font-size: 1.125rem !important;
}

/* Ambientní efekty */
.bg-gradient-pattern {
  background: linear-gradient(
    45deg,
    #3b82f6 0%,
    #2563eb 25%,
    #1d4ed8 50%,
    #1e40af 75%,
    #1e3a8a 100%
  );
  filter: blur(100px);
  animation: gradientMove 15s ease infinite;
}

.bg-mesh {
  background-image: radial-gradient(
    circle at center,
    #3b82f620 0%,
    transparent 70%
  );
  background-size: 50px 50px;
  background-position: 0 0, 25px 25px;
  animation: meshFloat 20s linear infinite;
}

/* Animace */
@keyframes gradientMove {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes meshFloat {
  0% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.1);
  }
  100% {
    transform: translateY(0) scale(1);
  }
}

/* Vylepšení stínů a přechodů */
.shadow-lg {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1),
              0 4px 6px -2px rgba(0, 0, 0, 0.05),
              0 0 0 1px rgba(0, 0, 0, 0.02) !important;
}

/* Globální přechody */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

/* Vylepšení pro karty */
.bg-white {
  background-color: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(8px);
  transform: translateZ(0);
  will-change: transform;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(59, 130, 246, 0.5);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(59, 130, 246, 0.7);
}

/* Oprava layoutu */
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  position: relative;
  width: 100%;
  max-width: 90rem;
  margin: 0 auto;
}

/* Vylepšení animací */
.v-enter-active,
.v-leave-active {
  transition-property: opacity, transform;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
  transform: translateY(4px);
}
</style>
