<script setup>
import { ref, computed } from 'vue';

defineOptions({
  name: 'ColorPicker',
});

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  label: {
    type: String,
    default: 'Color',
  },
  error: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['update:modelValue']);

const colors = [
  'slate', 'gray', 'zinc', 'neutral', 'stone',
  'red', 'orange', 'amber', 'yellow', 'lime',
  'green', 'emerald', 'teal', 'cyan', 'sky',
  'blue', 'indigo', 'violet', 'purple', 'fuchsia',
  'pink', 'rose'
];

const selectedColor = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
});

const getColorClass = (color) => {
  const baseClasses = 'w-8 h-8 rounded-md cursor-pointer transition-all duration-200 border';
  const selectedClasses = selectedColor.value === color ? 'ring-2 ring-offset-2 scale-110' : 'hover:scale-105';
  
  const colorClasses = {
    'slate': 'bg-slate-400 border-slate-500 ring-slate-500',
    'gray': 'bg-gray-400 border-gray-500 ring-gray-500',
    'zinc': 'bg-zinc-400 border-zinc-500 ring-zinc-500',
    'neutral': 'bg-neutral-400 border-neutral-500 ring-neutral-500',
    'stone': 'bg-stone-400 border-stone-500 ring-stone-500',
    'red': 'bg-red-400 border-red-500 ring-red-500',
    'orange': 'bg-orange-400 border-orange-500 ring-orange-500',
    'amber': 'bg-amber-400 border-amber-500 ring-amber-500',
    'yellow': 'bg-yellow-400 border-yellow-500 ring-yellow-500',
    'lime': 'bg-lime-400 border-lime-500 ring-lime-500',
    'green': 'bg-green-400 border-green-500 ring-green-500',
    'emerald': 'bg-emerald-400 border-emerald-500 ring-emerald-500',
    'teal': 'bg-teal-400 border-teal-500 ring-teal-500',
    'cyan': 'bg-cyan-400 border-cyan-500 ring-cyan-500',
    'sky': 'bg-sky-400 border-sky-500 ring-sky-500',
    'blue': 'bg-blue-400 border-blue-500 ring-blue-500',
    'indigo': 'bg-indigo-400 border-indigo-500 ring-indigo-500',
    'violet': 'bg-violet-400 border-violet-500 ring-violet-500',
    'purple': 'bg-purple-400 border-purple-500 ring-purple-500',
    'fuchsia': 'bg-fuchsia-400 border-fuchsia-500 ring-fuchsia-500',
    'pink': 'bg-pink-400 border-pink-500 ring-pink-500',
    'rose': 'bg-rose-400 border-rose-500 ring-rose-500',
  };

  return `${baseClasses} ${selectedClasses} ${colorClasses[color]}`;
};
</script>

<template>
  <div class="space-y-2">
    <label v-if="label" class="block text-sm font-medium text-gray-700">{{ label }}</label>
    
    <div class="grid grid-cols-5 sm:grid-cols-7 md:grid-cols-11 gap-2 p-3 bg-white rounded-lg border border-gray-200 shadow-sm">
      <div
        v-for="color in colors"
        :key="color"
        :class="getColorClass(color)"
        @click="selectedColor = color"
        :title="color.charAt(0).toUpperCase() + color.slice(1)"
      ></div>
    </div>
    
    <div v-if="selectedColor" class="flex items-center mt-2 space-x-2">
      <div 
        :class="{
          'bg-slate-50 text-slate-600': selectedColor === 'slate',
          'bg-gray-50 text-gray-600': selectedColor === 'gray',
          'bg-zinc-50 text-zinc-600': selectedColor === 'zinc',
          'bg-neutral-50 text-neutral-600': selectedColor === 'neutral',
          'bg-stone-50 text-stone-600': selectedColor === 'stone',
          'bg-red-50 text-red-600': selectedColor === 'red',
          'bg-orange-50 text-orange-600': selectedColor === 'orange',
          'bg-amber-50 text-amber-600': selectedColor === 'amber',
          'bg-yellow-50 text-yellow-600': selectedColor === 'yellow',
          'bg-lime-50 text-lime-600': selectedColor === 'lime',
          'bg-green-50 text-green-600': selectedColor === 'green',
          'bg-emerald-50 text-emerald-600': selectedColor === 'emerald',
          'bg-teal-50 text-teal-600': selectedColor === 'teal',
          'bg-cyan-50 text-cyan-600': selectedColor === 'cyan',
          'bg-sky-50 text-sky-600': selectedColor === 'sky',
          'bg-blue-50 text-blue-600': selectedColor === 'blue',
          'bg-indigo-50 text-indigo-600': selectedColor === 'indigo',
          'bg-violet-50 text-violet-600': selectedColor === 'violet',
          'bg-purple-50 text-purple-600': selectedColor === 'purple',
          'bg-fuchsia-50 text-fuchsia-600': selectedColor === 'fuchsia',
          'bg-pink-50 text-pink-600': selectedColor === 'pink',
          'bg-rose-50 text-rose-600': selectedColor === 'rose',
        }"
        class="inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset ring-gray-500/10"
      >
        {{ selectedColor.charAt(0).toUpperCase() + selectedColor.slice(1) }}
      </div>
      
      <button 
        v-if="selectedColor" 
        @click="selectedColor = ''" 
        class="text-xs text-gray-500 hover:text-gray-700"
        title="Clear selection"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
        </svg>
      </button>
    </div>
    
    <p v-if="error" class="mt-1 text-sm text-red-600">{{ error }}</p>
  </div>
</template> 