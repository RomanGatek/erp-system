<template>
  <div class="w-full max-w-7xl mx-auto px-4">
    <div class="flex justify-center mb-16 space-x-4">
      <button 
        @click="$emit('changeSection', 'hero')"
        class="group px-6 py-3 bg-white/15 backdrop-blur-lg text-gray-700 rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden"
      >
        <span class="relative z-10">Back to Home</span>
        <div class="absolute inset-0 bg-gradient-to-r from-gray-500/20 to-white/30 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
      </button>
      <button 
        @click="$emit('changeSection', 'team')"
        class="group px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden"
      >
        <span class="relative z-10">Meet the Team</span>
        <div class="absolute inset-0 bg-gradient-to-r from-blue-500 to-blue-300 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
      </button>
    </div>
    
    <h2 class="text-5xl font-bold text-center mb-6 bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-blue-400">
      About the Course
    </h2>
    
    <div class="group relative bg-white/10 backdrop-blur-2xl rounded-2xl p-8 transition-all duration-500 hover:bg-white/15 hover:shadow-[0_0_40px_rgba(59,130,246,0.15)] overflow-hidden border-b-[3px] border-transparent hover:border-blue-500/50 max-w-4xl mx-auto">
      <div class="relative z-10 space-y-8">
        <div class="flex items-center justify-center">
          <img src="/sda-logo.svg" alt="SDA Academy" class="h-20 opacity-90 transition-transform duration-500 group-hover:scale-105">
        </div>
        
        <h3 class="text-3xl font-semibold text-gray-800 text-center bg-clip-text text-transparent bg-gradient-to-r from-gray-800 to-gray-600">
          Final Team Project
        </h3>
        
        <p class="text-lg text-gray-600 leading-relaxed text-center max-w-3xl mx-auto">
          This ERP System represents our final collaborative project as part of the SDA Academy's JAVA Remote course. 
          It showcases our combined skills in both backend and frontend development.
        </p>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
          <div class="group/stat relative bg-white/5 backdrop-blur-xl rounded-xl p-6 transition-all duration-300 hover:bg-white/10">
            <div class="text-blue-500 text-5xl font-bold mb-3 transition-colors duration-300 group-hover/stat:text-blue-400">
              <span ref="monthsCount">0</span>
            </div>
            <div class="text-gray-600 font-medium">Months of Learning</div>
            <div class="absolute inset-0 bg-gradient-to-br from-blue-500/5 to-transparent opacity-0 group-hover/stat:opacity-100 transition-opacity duration-300 rounded-xl"></div>
          </div>
          
          <div class="group/stat relative bg-white/5 backdrop-blur-xl rounded-xl p-6 transition-all duration-300 hover:bg-white/10">
            <div class="text-blue-500 text-5xl font-bold mb-3 transition-colors duration-300 group-hover/stat:text-blue-400">
              <span ref="membersCount">0</span>
            </div>
            <div class="text-gray-600 font-medium">Team Members</div>
            <div class="absolute inset-0 bg-gradient-to-br from-blue-500/5 to-transparent opacity-0 group-hover/stat:opacity-100 transition-opacity duration-300 rounded-xl"></div>
          </div>
          
          <div class="group/stat relative bg-white/5 backdrop-blur-xl rounded-xl p-6 transition-all duration-300 hover:bg-white/10">
            <div class="text-blue-500 text-5xl font-bold mb-3 transition-colors duration-300 group-hover/stat:text-blue-400">
              <span ref="visionCount">0</span>
            </div>
            <div class="text-gray-600 font-medium">Shared Vision</div>
            <div class="absolute inset-0 bg-gradient-to-br from-blue-500/5 to-transparent opacity-0 group-hover/stat:opacity-100 transition-opacity duration-300 rounded-xl"></div>
          </div>
        </div>
      </div>

      <!-- Background Decoration -->
      <div class="absolute inset-0 bg-gradient-to-br from-blue-600/10 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const monthsCount = ref(null)
const membersCount = ref(null)
const visionCount = ref(null)

const animateNumber = (target, endValue, duration = 2) => {
  let currentValue = { value: 0 }
  
  gsap.to(currentValue, {
    value: endValue,
    duration: duration,
    ease: "power2.out",
    onUpdate: () => {
      if (target.value) {
        target.value.textContent = Math.round(currentValue.value)
      }
    }
  })
}

onMounted(() => {
  // Použijeme Intersection Observer pro spuštění animace až když je element viditelný
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        animateNumber(monthsCount, 12)
        animateNumber(membersCount, 8)
        animateNumber(visionCount, 1, 1.5) // kratší doba pro menší číslo
        observer.disconnect() // odpojíme observer po první animaci
      }
    })
  }, { threshold: 0.2 }) // spustí se když je viditelných 20% elementu

  if (monthsCount.value) {
    observer.observe(monthsCount.value)
  }
})
</script>

<style scoped>
.gradient-text {
  background: linear-gradient(to right, #3b82f6, #2563eb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* Přidáme anti-aliasing pro čísla */
.text-5xl {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style> 