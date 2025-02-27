<template>
  <div class="w-full max-w-7xl mx-auto px-4">
    <div class="flex justify-center mb-12 space-x-4">
      <button 
        @click="$emit('changeSection', 'hero')"
        class="px-6 py-3 bg-white bg-opacity-50 backdrop-blur-sm text-gray-700 rounded-lg font-medium hover:bg-opacity-70 transition-all duration-200 shadow-md hover:shadow-lg hover:-translate-y-0.5"
      >
        Back to Home
      </button>
      <button 
        @click="$emit('changeSection', 'about')"
        class="px-6 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-lg font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-lg hover:shadow-xl hover:-translate-y-0.5"
      >
        About Course
      </button>
    </div>
    <h2 class="text-4xl font-bold text-center mb-6 gradient-text">Who Are We?</h2>
    <p class="text-xl text-gray-600 text-center mb-16 max-w-3xl mx-auto leading-relaxed">
      We are proud students of the SDA JAVA Remote course, working together to create innovative solutions.
    </p>
    
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="member in teamMembers" :key="member.name" 
        class="bg-white rounded-lg p-6 shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 backdrop-blur-sm bg-opacity-60">
        <div class="flex flex-col items-center">
          <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-700 rounded-full flex items-center justify-center text-white text-lg font-bold mb-4">
            {{ getInitials(member.name) }}
          </div>
          <h3 class="text-base font-semibold text-gray-800 mb-2 text-center">{{ member.name }}</h3>
          <p class="text-sm text-gray-600 text-center">{{ member.role }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TeamView',
  props: {
    teamMembers: {
      type: Array,
      required: true
    }
  },
  methods: {
    getInitials(name) {
      return name
        .split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
    }
  }
}
</script> 