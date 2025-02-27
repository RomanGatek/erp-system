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
        @click="$emit('changeSection', 'team')"
        class="px-6 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-lg font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-lg hover:shadow-xl hover:-translate-y-0.5"
      >
        Meet the Team
      </button>
    </div>
    
    <h2 class="text-4xl font-bold text-center mb-6 gradient-text">About the Course</h2>
    <div class="bg-white rounded-lg p-8 shadow-lg backdrop-blur-sm bg-opacity-60 max-w-4xl mx-auto">
      <div class="space-y-6">
        <div class="flex items-center justify-center mb-8">
          <img src="/sda-logo.svg" alt="SDA Academy" class="h-16 opacity-90">
        </div>
        <h3 class="text-2xl font-semibold text-gray-800 mb-4 text-center">
          Final Team Project
        </h3>
        <p class="text-lg text-gray-600 leading-relaxed text-center mb-6">
          This ERP System represents our final collaborative project as part of the SDA Academy's JAVA Remote course. 
          It showcases our combined skills in both backend and frontend development.
        </p>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="text-center p-4">
            <div class="text-blue-600 text-4xl font-bold mb-2">12</div>
            <div class="text-gray-600">Months of Learning</div>
          </div>
          <div class="text-center p-4">
            <div class="text-blue-600 text-4xl font-bold mb-2">8</div>
            <div class="text-gray-600">Team Members</div>
          </div>
          <div class="text-center p-4">
            <div class="text-blue-600 text-4xl font-bold mb-2">1</div>
            <div class="text-gray-600">Shared Vision</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CourseView'
}
</script> 