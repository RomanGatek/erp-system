<template>
  <div class="left-sidebar">
    <div class="sidebar-container">
      <div class="sidebar-content">
        <h2 class="text-xl font-bold mb-4">Categories</h2>

        <!-- Search bar -->
        <div class="mb-4">
          <SearchBar
            :model-value="searchQuery"
            @update:model-value="$emit('search', $event)"
            placeholder="Search products..."
            class="w-full"
          />
        </div>

        <!-- Categories list with colored dots -->
        <div class="categories-list">
          <button
            @click="$emit('select-category', '')"
            class="category-item"
            :class="{ active: selectedCategory === '' }"
          >
            <span class="category-dot all-categories"></span>
            All Categories
          </button>
          <button
            v-for="category in categories"
            :key="category.id"
            @click="$emit('select-category', category.id)"
            class="category-item"
            :class="{ active: selectedCategory === category.id }"
          >
            <span
              class="category-dot"
              :class="{
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
              }"
            ></span>
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
    SearchBar
  },
  props: {
    categories: {
      type: Array,
      required: true
    },
    selectedCategory: {
      type: [String, Number],
      default: ''
    },
    searchQuery: {
      type: String,
      default: ''
    }
  },
  emits: ['select-category', 'search']
}
</script>

<style scoped>
.left-sidebar {
  height: calc(100vh - 64px - 3rem); /* Adjust for header height and padding */
  overflow-y: auto;
}

.sidebar-container {
  background-color: white;
  border-radius: 1rem;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
  height: auto;
  min-height: 300px;
  max-height: 100%;
  overflow-y: auto;
}

.sidebar-content {
  padding: 1.25rem;
}

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  max-height: calc(100vh - 250px); /* Adjust based on header and other elements */
  overflow-y: auto;
}

.category-item {
  text-align: left;
  padding: 0.4rem 0.6rem;
  border-radius: 0.375rem;
  transition: all 0.2s;
  font-size: 0.9rem;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.category-item:hover {
  background-color: #f3f4f6;
}

.category-item.active {
  background-color: #e0e7ff;
  color: #4f46e5;
  font-weight: 500;
}

.category-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
  display: inline-block;
}

.category-dot.all-categories {
  background: linear-gradient(135deg, #4f46e5 0%, #10b981 50%, #ef4444 100%);
}
</style> 