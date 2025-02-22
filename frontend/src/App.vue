<template>
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
