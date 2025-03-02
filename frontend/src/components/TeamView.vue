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
        @click="$emit('changeSection', 'about')"
        class="group px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden"
      >
        <span class="relative z-10">About Course</span>
        <div class="absolute inset-0 bg-gradient-to-r from-blue-500 to-blue-300 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
      </button>
    </div>

    <h2 class="text-5xl font-bold text-center mb-6 bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-blue-400">
      Our Development Team
    </h2>
    <p class="text-xl text-gray-600 text-center mb-16 max-w-3xl mx-auto leading-relaxed">
      Meet our talented team of developers working on this ERP system
    </p>

    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
      <div v-for="member in teamMembers" :key="member.name"
        class="group relative bg-white/10 backdrop-blur-2xl rounded-2xl p-6 transition-all duration-500 hover:bg-white/15 hover:shadow-[0_0_40px_rgba(59,130,246,0.15)] overflow-hidden border-b-[3px] border-transparent hover:border-blue-500/50"
      >
        <div class="relative z-10 flex flex-col items-center">
          <!-- Avatar Container -->
          <div class="relative w-16 h-16 mb-4">
            <!-- Default Avatar -->
            <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-400 rounded-2xl rotate-45 flex items-center justify-center transform group-hover:rotate-0 transition-transform duration-500">
              <span class="text-lg font-bold text-white -rotate-45 transform group-hover:rotate-0 transition-transform duration-500">
                {{ getInitials(member.name) }}
              </span>
            </div>

            <!-- GitHub Avatar Overlay -->
            <img
              v-if="member.github"
              :src="`${member.github}.png`"
              :alt="`${member.name}'s GitHub avatar`"
              class="absolute inset-0 w-full h-full rounded-2xl opacity-0 group-hover:opacity-100 transition-all duration-500 object-cover transform scale-95 group-hover:scale-100"
              @error="handleImageError"
            />
          </div>

          <!-- Info -->
          <h3 class="text-base font-semibold text-gray-800 mb-1 text-center">{{ member.name }}</h3>
          <p class="text-sm text-gray-500 text-center mb-2">{{ member.role }}</p>
          <p v-if="member.description || memberBios[member.name]"
             :class="[
               'text-sm text-gray-500 text-center mb-4 line-clamp-2',
               { 'animate-pulse': isLoading[member.name] }
             ]"
          >
            {{ member.description || memberBios[member.name] }}
          </p>

          <!-- GitHub Link -->
          <a v-if="member.github"
             :href="member.github"
             target="_blank"
             class="text-blue-500 hover:text-blue-600 transition-colors duration-300"
          >
            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
            </svg>
          </a>
        </div>

        <!-- Background Decoration -->
        <div class="absolute inset-0 bg-gradient-to-br from-blue-600/10 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

defineProps({
  teamMembers: {
    type: Array,
    required: true,
    default: () => [
      {
        name: 'Jiří',
        role: 'Lead Developer',
        github: 'https://github.com/cooffeeRequired',
        linkedin: null,
        email: null,
        description: 'Backend Developer with 2 years Java, 3 years JS/TS, 7 years PHP experience'
      }
    ]
  }
})

const memberBios = ref({})
const isLoading = ref({})

const getInitials = (name) => {
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
}

const handleImageError = (e) => {
  // Pokud se nepodaří načíst GitHub avatar, zůstane viditelný avatar s iniciály
  e.target.style.display = 'none'
}

const fetchGithubBio = async (githubUrl) => {
  try {
    const username = githubUrl.split('/').pop()
    const response = await axios.get(`https://api.github.com/users/${username}`)
    const data = await response.data;

    if (data.bio) {
      return data.bio.length > 100 ? data.bio.substring(0, 97) + '...' : data.bio
    }
    return null
  } catch (error) {
    console.error('Error fetching GitHub bio:', error)
    return null
  }
}

onMounted(async () => {
  for (const member of props.teamMembers) {
    if (member.github) {
      isLoading.value[member.name] = true
      const bio = await fetchGithubBio(member.github)
      if (bio) {
        memberBios.value[member.name] = bio
      }
      isLoading.value[member.name] = false
    }
  }
})
</script>

<style scoped>
.gradient-text {
  background: linear-gradient(to right, #3b82f6, #2563eb);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
