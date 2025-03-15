<template>
  <div class="h-[75dvh] overflow-y-auto">
    <div class="bg-white rounded-2xl shadow-md h-auto min-h-[300px] max-h-full overflow-y-auto">
      <div class="p-5">
        <h2 class="text-xl font-bold mb-4 bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
          Categories
        </h2>

        <!-- Search bar -->
        <div class="mb-5">
          <SearchBar :model-value="searchQuery" @update:model-value="$emit('search', $event)"
            placeholder="Search products..." class="w-full" />
        </div>

        <!-- Categories list with colored dots -->
        <div class="flex flex-col gap-1.5 max-h-[calc(80dvh-250px)] overflow-y-auto pr-1">
          <button @click="$emit('select-category', '')"
            class="text-left py-2 px-3 rounded-lg transition-all duration-200 text-[0.9rem] cursor-pointer flex items-center hover:bg-gray-50"
            :class="{ 'bg-indigo-50 text-indigo-700 font-medium': selectedCategory === '' }">
            <span
              class="w-2.5 h-2.5 rounded-full mr-2.5 bg-gradient-to-r from-indigo-600 via-emerald-500 to-red-500"></span>
            All Categories
          </button>

          <div class="h-px bg-gray-100 my-1.5"></div>

          <button v-for="category in categories" :key="category.id" @click="$emit('select-category', category.id)"
            class="text-left py-2 px-3 rounded-lg transition-all duration-200 text-[0.9rem] cursor-pointer flex items-center hover:bg-gray-50"
            :class="{
              'bg-indigo-50 text-indigo-700 font-medium': selectedCategory === category.id,
            }">
            <span class="w-2.5 h-2.5 rounded-full mr-2.5" :class="{
              'bg-slate-600 text-slate-600': category.color === 'slate',
              'bg-gray-600 text-gray-600': category.color === 'gray',
              'bg-zinc-600 text-zinc-600': category.color === 'zinc',
              'bg-neutral-600 text-neutral-600': category.color === 'neutral',
              'bg-stone-600 text-stone-600': category.color === 'stone',
              'bg-red-600 text-red-600': category.color === 'red',
              'bg-orange-600 text-orange-600': category.color === 'orange',
              'bg-amber-600 text-amber-600': category.color === 'amber',
              'bg-yellow-600 text-yellow-600': category.color === 'yellow',
              'bg-lime-600 text-lime-600': category.color === 'lime',
              'bg-green-600 text-green-600': category.color === 'green',
              'bg-emerald-600 text-emerald-600': category.color === 'emerald',
              'bg-teal-600 text-teal-600': category.color === 'teal',
              'bg-cyan-600 text-cyan-600': category.color === 'cyan',
              'bg-sky-600 text-sky-600': category.color === 'sky',
              'bg-blue-600 text-blue-600': category.color === 'blue',
              'bg-indigo-600 text-indigo-600': category.color === 'indigo',
              'bg-violet-600 text-violet-600': category.color === 'violet',
              'bg-purple-600 text-purple-600': category.color === 'purple',
              'bg-fuchsia-600 text-fuchsia-600': category.color === 'fuchsia',
              'bg-pink-600 text-pink-600': category.color === 'pink',
              'bg-rose-600 text-rose-600': category.color === 'rose',
            }"></span>
            {{ category.name }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SearchBar from '@/components/common/SearchBar.vue'

export default {
  name: 'CategoriesSidebar',
  components: {
    SearchBar,
  },
  props: {
    categories: {
      type: Array,
      required: true,
    },
    selectedCategory: {
      type: [String, Number],
      default: '',
    },
    searchQuery: {
      type: String,
      default: '',
    },
  },
  emits: ['select-category', 'search'],
}
</script>
