<template>
  <notifications position="bottom left" classes="my-notification" :duration="3000" />
  <div id="app" class="min-h-screen flex flex-col">
    <Navbar />
    <main class="flex-grow">
      <router-view />
    </main>
    <footer class="bg-gray-800 text-white text-center py-4">
      &copy; 2023 ERP System
    </footer>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";

export default {
  name: 'App',
  components: {
    Navbar
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
</style>
