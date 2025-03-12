<template>
  <div class="product-catalog">
    <div class="catalog-layout">
      <!-- Categories Sidebar -->
      <CategoriesSidebar
        :categories="categories.items"
        :selected-category="selectedCategory"
        :search-query="searchQuery"
        @select-category="selectCategory"
        @search="updateSearchQuery"
      />

      <!-- Main Content -->
      <div class="main-content">
        <div class="content-header">
          <h1 class="text-2xl font-bold">Product Catalog</h1>
          <div class="sorting-container">
            <label for="sort" class="text-sm font-medium text-gray-700">Sort by:</label>
            <select id="sort" v-model="sortOption" class="ml-2 text-sm border-gray-300 rounded-md">
              <option value="name">Name</option>
              <option value="price-asc">Price: Low to High</option>
              <option value="price-desc">Price: High to Low</option>
            </select>
          </div>
        </div>

        <!-- Loading state -->
        <div v-if="products.loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>Loading products...</p>
        </div>

        <!-- Error state -->
        <div v-else-if="products.error" class="error-container">
          <p>{{ products.error }}</p>
          <button @click="loadProducts" class="retry-btn">Retry</button>
        </div>

        <!-- Empty state -->
        <div v-else-if="filteredProducts.length === 0" class="empty-container">
          <p>No products found.</p>
        </div>

        <!-- Products grid -->
        <div v-else class="product-grid">
          <ProductCard
            v-for="product in displayedProducts"
            :key="product.id"
            :product="product"
            :is-in-cart="isProductInCart(product.id)"
            @add-to-cart="addToCart"
          />
        </div>

        <!-- Load more button -->
        <div v-if="displayCount < filteredProducts.length" class="load-more-container">
          <button @click="loadMore" class="load-more-btn">Load More</button>
        </div>
      </div>

      <!-- Shopping Cart -->
      <ShoppingCart
        :cart-items="cart.items"
        @increase-quantity="increaseQuantity"
        @decrease-quantity="decreaseQuantity"
        @remove-from-cart="removeFromCart"
      />
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useProductsStore } from '@/stores/products.store'
import { useCategoriesStore } from '@/stores/categories.store'
import { useCartStore } from '@/stores/cart.store'
import CategoriesSidebar from '@/components/catalog/CategoriesSidebar.vue'
import ProductCard from '@/components/catalog/ProductCard.vue'
import ShoppingCart from '@/components/catalog/ShoppingCart.vue'

export default {
  name: 'ProductCatalog',
  components: {
    CategoriesSidebar,
    ProductCard,
    ShoppingCart,
  },
  setup() {
    const products = useProductsStore()
    const categories = useCategoriesStore()
    const cart = useCartStore()

    const searchQuery = ref('')
    const selectedCategory = ref('')
    const sortOption = ref('name')
    const displayCount = ref(12)
    const loadMoreObserver = ref(null)
    const loadMoreTrigger = ref(null)

    // Load initial data
    const loadProducts = async () => {
      await products.fetchProducts()
    }

    const loadCategories = async () => {
      await categories.fetchCategories()
    }

    // Filter and sort products
    const filteredProducts = computed(() => {
      let result = [...products.items]

      // Filter by category
      if (selectedCategory.value) {
        result = result.filter((product) => product.category.id === selectedCategory.value)
      }

      // Filter by search query
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(
          (product) =>
            product.name.toLowerCase().includes(query) ||
            product.description.toLowerCase().includes(query),
        )
      }

      // Sort products
      if (sortOption.value === 'name') {
        result.sort((a, b) => a.name.localeCompare(b.name))
      } else if (sortOption.value === 'price-asc') {
        result.sort((a, b) => a.price - b.price)
      } else if (sortOption.value === 'price-desc') {
        result.sort((a, b) => b.price - a.price)
      }

      return result
    })

    // Display only a subset of products
    const displayedProducts = computed(() => {
      return filteredProducts.value.slice(0, displayCount.value)
    })

    // Load more products
    const loadMore = () => {
      displayCount.value += 12
    }

    // Category selection
    const selectCategory = async (categoryId) => {
      // Reset search when changing categories
      searchQuery.value = ''

      // Set the selected category
      selectedCategory.value = categoryId

      // Reset display count
      displayCount.value = 12

      // Reset observer
      setupIntersectionObserver()
    }

    // Update search query
    const updateSearchQuery = (query) => {
      searchQuery.value = query
      displayCount.value = 12
      setupIntersectionObserver()
    }

    // Cart operations
    const addToCart = (product) => {
      cart.addItem(product)
    }

    const removeFromCart = (item) => {
      cart.removeItem(item.id)
    }

    const increaseQuantity = (item) => {
      cart.updateItemQuantity(item.id, item.quantity + 1)
    }

    const decreaseQuantity = (item) => {
      if (item.quantity > 1) {
        cart.updateItemQuantity(item.id, item.quantity - 1)
      }
    }

    const isProductInCart = (productId) => {
      return cart.items.some((item) => item.id === productId)
    }

    // Intersection Observer for infinite scroll
    const setupIntersectionObserver = () => {
      if (loadMoreObserver.value) {
        loadMoreObserver.value.disconnect()
      }

      loadMoreObserver.value = new IntersectionObserver(
        (entries) => {
          const [entry] = entries
          if (entry.isIntersecting && displayCount.value < filteredProducts.value.length) {
            loadMore()
          }
        },
        {
          rootMargin: '0px 0px 200px 0px',
        },
      )

      if (loadMoreTrigger.value) {
        loadMoreObserver.value.observe(loadMoreTrigger.value)
      }
    }

    // Lifecycle hooks
    onMounted(async () => {
      await Promise.all([loadProducts(), loadCategories()])
      setupIntersectionObserver()
    })

    onUnmounted(() => {
      if (loadMoreObserver.value) {
        loadMoreObserver.value.disconnect()
      }
    })

    // Watch for changes in sort option
    watch(sortOption, () => {
      displayCount.value = 12
      setupIntersectionObserver()
    })

    return {
      products,
      categories,
      cart,
      searchQuery,
      selectedCategory,
      sortOption,
      displayCount,
      filteredProducts,
      displayedProducts,
      loadMoreTrigger,
      loadProducts,
      loadMore,
      selectCategory,
      updateSearchQuery,
      addToCart,
      removeFromCart,
      increaseQuantity,
      decreaseQuantity,
      isProductInCart,
    }
  },
}
</script>

<style scoped>
.product-catalog {
  padding: 1.5rem;
  min-height: calc(100vh - 64px);
}

.catalog-layout {
  display: grid;
  grid-template-columns: 250px 1fr 300px;
  gap: 1.5rem;
  height: 100%;
}

.main-content {
  background-color: white;
  border-radius: 1rem;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 64px - 3rem);
  overflow-y: auto;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 10;
}

.sorting-container {
  display: flex;
  align-items: center;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 0;
  text-align: center;
}

.loading-spinner {
  border: 3px solid #e5e7eb;
  border-top: 3px solid #4f46e5;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.retry-btn {
  margin-top: 1rem;
  background-color: #4f46e5;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-weight: 500;
}

.load-more-container {
  display: flex;
  justify-content: center;
  margin: 1rem 0 2rem;
}

.load-more-btn {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #e5e7eb;
  padding: 0.5rem 1.5rem;
  border-radius: 0.375rem;
  font-weight: 500;
  transition: all 0.2s;
}

.load-more-btn:hover {
  background-color: #e5e7eb;
}

/* Responsive adjustments */
@media (max-width: 1200px) {
  .catalog-layout {
    grid-template-columns: 220px 1fr 280px;
  }
}

@media (max-width: 1024px) {
  .catalog-layout {
    grid-template-columns: 200px 1fr 250px;
  }
}
</style>
