<template>
  <div class="p-6 h-[80dvh] max-h-[80dvh] overflow-hidden bg-gray-50">
    <div class="grid grid-cols-[250px_1fr_300px] gap-10 h-[calc(80dvh-3rem)] max-h-[calc(80dvh-3rem)] overflow-hidden">
      <!-- Categories Sidebar -->
      <CategoriesSidebar :categories="categories.items" :selected-category="selectedCategory"
        :search-query="searchQuery" @select-category="selectCategory" @search="updateSearchQuery" />

      <!-- Main Content -->
      <div ref="mainContentRef"
        class="bg-white rounded-2xl shadow-md flex flex-col h-full max-h-full overflow-y-auto overflow-x-hidden overscroll-contain scroll-smooth hide-scrollbar">
        <div
          class="flex justify-between items-center py-5 px-6 mb-2 sticky top-0 bg-white z-10 border-b border-gray-100">
          <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
            Product Catalog
          </h1>

          <div class="flex items-center gap-3">
            <XSelect v-model="sortOption" :options="sortOptions" label="Sort" />
          </div>
        </div>

        <!-- Loading state -->
        <div v-if="products.loading" class="flex flex-col items-center justify-center py-12 text-center px-6 flex-grow">
          <div class="w-10 h-10 border-4 border-gray-200 border-t-indigo-600 rounded-full animate-spin mb-4"></div>
          <p class="text-gray-500 font-medium">Loading products...</p>
        </div>

        <!-- Error state -->
        <div v-else-if="products.error"
          class="flex flex-col items-center justify-center py-12 text-center px-6 flex-grow">
          <div class="bg-red-50 p-4 rounded-xl mb-4 w-64 text-center">
            <p class="text-red-600">{{ products.error }}</p>
          </div>
          <button @click="loadProducts"
            class="mt-2 bg-indigo-600 text-white py-2 px-6 rounded-full font-medium hover:bg-indigo-700 transition-colors shadow-sm">
            Retry
          </button>
        </div>

        <!-- Empty state -->
        <div v-else-if="filteredProducts.length === 0"
          class="flex flex-col items-center justify-center py-12 text-center px-6 flex-grow">
          <div class="bg-amber-50 p-6 rounded-xl mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-amber-500 mx-auto mb-2" fill="none"
              viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M9.75 9.75l4.5 4.5m0-4.5l-4.5 4.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p class="text-amber-800 font-medium">No products found</p>
            <p class="text-amber-600 text-sm mt-1">Try adjusting your search or filters</p>
          </div>
        </div>

        <!-- Products grid -->
        <div v-else class="grid grid-cols-[repeat(auto-fill,minmax(180px,1fr))] gap-6 p-6 pt-2">
          <ProductCard v-for="product in displayedProducts" :key="product.id" :product="product"
            :is-in-cart="isProductInCart(product.id)" @add-to-cart="addToCart" @show-detail="showProductDetail" />
        </div>

        <!-- Loading indicator for infinite scroll -->
        <div v-if="displayCount < filteredProducts.length" ref="loadMoreTrigger"
          class="flex flex-col items-center justify-center py-4 mb-4">
          <div class="w-5 h-5 border-2 border-gray-200 border-t-indigo-600 rounded-full animate-spin mb-2"></div>
          <p class="text-sm text-gray-500">Loading more products...</p>
        </div>
      </div>

      <!-- Product Detail Modal -->
      <ProductDetailModal v-if="isModalVisible" :product="selectedProduct"
        :is-in-cart="selectedProduct ? isProductInCart(selectedProduct.id) : false" @close="closeProductDetail"
        @add-to-cart="addToCart" @submit-review="submitProductReview" @after-leave="onModalClosed" />

      <!-- Custom scroll controls - updated position and styling -->
      <div class="absolute right-[362.5px] top-1/2 -translate-y-1/2 z-10 flex flex-col gap-3">
        <button @click="scrollUp"
          class="w-5 h-5 rounded-full bg-gradient-to-r from-blue-600 to-blue-400 shadow-lg flex items-center justify-center text-white hover:from-blue-700 hover:to-blue-500 transition-colors transform hover:scale-105"
          aria-label="Scroll up">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-3 h-3">
            <path fill-rule="evenodd"
              d="M11.47 7.72a.75.75 0 011.06 0l7.5 7.5a.75.75 0 11-1.06 1.06L12 9.31l-6.97 6.97a.75.75 0 01-1.06-1.06l7.5-7.5z"
              clip-rule="evenodd" />
          </svg>
        </button>
        <button @click="scrollDown"
          class="w-5 h-5 rounded-full bg-gradient-to-r from-blue-600 to-blue-400 shadow-lg flex items-center justify-center text-white hover:from-blue-700 hover:to-blue-500 transition-colors transform hover:scale-105"
          aria-label="Scroll down">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-3 h-3">
            <path fill-rule="evenodd"
              d="M12.53 16.28a.75.75 0 01-1.06 0l-7.5-7.5a.75.75 0 011.06-1.06L12 14.69l6.97-6.97a.75.75 0 011.06 1.06l-7.5 7.5z"
              clip-rule="evenodd" />
          </svg>
        </button>
      </div>

      <!-- Shopping Cart -->
      <ShoppingCart :cart-items="cart.items" @increase-quantity="increaseQuantity" @decrease-quantity="decreaseQuantity"
        @remove-from-cart="removeFromCart" />
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useProductsStore } from '@/stores/products.store'
import { useCategoriesStore } from '@/stores/categories.store'
import { useCartStore } from '@/stores/cart.store'
import { useNotifier } from '@/stores/notifier.store'
import { useRouter, useRoute } from 'vue-router'
import CategoriesSidebar from '@/components/catalog/CategoriesSidebar.vue'
import ProductCard from '@/components/catalog/ProductCard.vue'
import ProductDetailModal from '@/components/catalog/ProductDetailModal.vue'
import ShoppingCart from '@/components/catalog/ShoppingCart.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import XSelect from '@/components/common/XSelect.vue'

export default {
  name: 'ProductCatalog',
  components: {
    CategoriesSidebar,
    ProductCard,
    ProductDetailModal,
    ShoppingCart,
    BaseInput,
    XSelect,
  },
  setup() {
    const products = useProductsStore()
    const categories = useCategoriesStore()
    const cart = useCartStore()
    const notifier = useNotifier()
    const router = useRouter()
    const route = useRoute()

    const searchQuery = ref('')
    const selectedCategory = ref('')
    const sortOption = ref('name')
    const displayCount = ref(12) // Start with more items
    const loadMoreObserver = ref(null)
    const loadMoreTrigger = ref(null)
    const mainContentRef = ref(null)
    const isLoading = ref(false)

    const sortOptions = [
      { value: 'name', label: 'Name' },
      { value: 'price-asc', label: 'Price: Low to High' },
      { value: 'price-desc', label: 'Price: High to Low' },
    ]

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

      if (selectedCategory.value) {
        result = result.filter(
          (product) =>
            product.productCategory?.id === selectedCategory.value ||
            product.category?.id === selectedCategory.value,
        )
      }

      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(
          (product) =>
            product.name.toLowerCase().includes(query) ||
            product.description.toLowerCase().includes(query),
        )
      }

      if (sortOption.value === 'name') {
        result.sort((a, b) => a.name.localeCompare(b.name))
      } else if (sortOption.value === 'price-asc') {
        result.sort((a, b) => (a.buyoutPrice || a.price) - (b.buyoutPrice || b.price))
      } else if (sortOption.value === 'price-desc') {
        result.sort((a, b) => (b.buyoutPrice || b.price) - (a.buyoutPrice || a.price))
      }

      return result
    })

    // Display only loaded products
    const displayedProducts = computed(() => {
      return filteredProducts.value.slice(0, displayCount.value)
    })

    // Setup intersection observer for infinite scroll
    const setupIntersectionObserver = () => {
      if (loadMoreObserver.value) {
        loadMoreObserver.value.disconnect()
        loadMoreObserver.value = null
      }

      if (displayCount.value >= filteredProducts.value.length) {
        return
      }

      loadMoreObserver.value = new IntersectionObserver(
        (entries) => {
          const [entry] = entries
          if (entry.isIntersecting && !isLoading.value) {
            loadMore()
          }
        },
        {
          rootMargin: '200px',
          threshold: 0.1
        }
      )

      if (loadMoreTrigger.value) {
        loadMoreObserver.value.observe(loadMoreTrigger.value)
      }
    }

    // Load more products
    const loadMore = async () => {
      if (isLoading.value || displayCount.value >= filteredProducts.value.length) return

      isLoading.value = true

      try {
        await new Promise(resolve => setTimeout(resolve, 300)) // Increase delay for smoother loading

        // Calculate how many items to load based on remaining
        const remainingItems = filteredProducts.value.length - displayCount.value
        const batchSize = Math.min(12, remainingItems)

        // Don't update if user is actively scrolling
        if (!isActivelyScrolling.value) {
          displayCount.value += batchSize
        } else {
          // Retry after scroll ends
          setTimeout(() => {
            if (!isActivelyScrolling.value) {
              displayCount.value += batchSize
              setupIntersectionObserver()
            }
          }, 500)
        }
      } finally {
        isLoading.value = false
        nextTick(() => {
          if (!isActivelyScrolling.value) {
            setupIntersectionObserver()
          }
        })
      }
    }

    // Track active scrolling to prevent interference with manual scrolling
    const isActivelyScrolling = ref(false)
    let scrollTimeout = null

    const handleScrollStart = () => {
      isActivelyScrolling.value = true
      clearTimeout(scrollTimeout)
    }

    const handleScrollEnd = () => {
      clearTimeout(scrollTimeout)
      scrollTimeout = setTimeout(() => {
        isActivelyScrolling.value = false
        setupIntersectionObserver()
      }, 150)
    }

    // Setup scroll event listeners
    const setupScrollListeners = () => {
      if (!mainContentRef.value) return

      mainContentRef.value.addEventListener('mousedown', () => {
        if (mainContentRef.value.querySelector(':hover')) {
          handleScrollStart()
        }
      })

      mainContentRef.value.addEventListener('scroll', () => {
        handleScrollStart()
        handleScrollEnd()
      }, { passive: true })

      mainContentRef.value.addEventListener('mouseup', handleScrollEnd)
      mainContentRef.value.addEventListener('mouseleave', handleScrollEnd)
    }

    // Category selection
    const selectCategory = (categoryId) => {
      searchQuery.value = ''
      selectedCategory.value = categoryId
      displayCount.value = 12
      nextTick(() => {
        setupIntersectionObserver()
      })
    }

    // Update search query
    const updateSearchQuery = (query) => {
      searchQuery.value = query
      displayCount.value = 12
      nextTick(() => {
        setupIntersectionObserver()
      })
    }

    // Watch for sort changes
    watch(sortOption, () => {
      displayCount.value = 12
      nextTick(() => {
        setupIntersectionObserver()
      })
    })

    // Custom scroll functions
    const scrollUp = () => {
      if (!mainContentRef.value) return

      const scrollAmount = mainContentRef.value.clientHeight * 0.75
      const targetPosition = mainContentRef.value.scrollTop - scrollAmount

      mainContentRef.value.scrollTo({
        top: targetPosition,
        behavior: 'smooth'
      })
    }

    const scrollDown = () => {
      if (!mainContentRef.value) return

      const scrollAmount = mainContentRef.value.clientHeight * 0.75
      const targetPosition = mainContentRef.value.scrollTop + scrollAmount

      mainContentRef.value.scrollTo({
        top: targetPosition,
        behavior: 'smooth'
      })
    }

    // Add keyboard navigation for scrolling
    const handleKeyDown = (e) => {
      if (!mainContentRef.value) return

      // Only handle keys if the catalog content is in focus/view
      const rect = mainContentRef.value.getBoundingClientRect()
      const isVisible = rect.top < window.innerHeight && rect.bottom > 0

      if (!isVisible) return

      if (e.key === 'ArrowDown') {
        e.preventDefault()
        scrollDown()
      } else if (e.key === 'ArrowUp') {
        e.preventDefault()
        scrollUp()
      } else if (e.key === 'PageDown') {
        e.preventDefault()
        scrollDown()
      } else if (e.key === 'PageUp') {
        e.preventDefault()
        scrollUp()
      } else if (e.key === 'Home') {
        e.preventDefault()
        mainContentRef.value.scrollTo({ top: 0, behavior: 'smooth' })
      } else if (e.key === 'End') {
        e.preventDefault()
        mainContentRef.value.scrollTo({ top: mainContentRef.value.scrollHeight, behavior: 'smooth' })
      }
    }

    // Setup event listeners
    onMounted(async () => {
      console.log("ProductCatalog mounted, current route:", route.path);

      // Nejprve inicializuji košík - zde načteme data z localStorage nebo API
      await cart.initializeCart();

      // Load products and categories
      await Promise.all([loadProducts(), loadCategories()]);
      console.log("Products loaded, count:", products.items.length);
      console.log("Cart loaded, items count:", cart.items.length);

      // Check if URL has a product after products are loaded
      await checkUrlForProduct();

      nextTick(() => {
        setupScrollListeners();
        setupIntersectionObserver();
        window.addEventListener('keydown', handleKeyDown);
      });
    })

    onUnmounted(() => {
      if (loadMoreObserver.value) {
        loadMoreObserver.value.disconnect()
      }

      if (mainContentRef.value) {
        mainContentRef.value.removeEventListener('mousedown', handleScrollStart)
        mainContentRef.value.removeEventListener('scroll', handleScrollEnd)
        mainContentRef.value.removeEventListener('mouseup', handleScrollEnd)
        mainContentRef.value.removeEventListener('mouseleave', handleScrollEnd)
      }

      window.removeEventListener('keydown', handleKeyDown)

      if (scrollTimeout) {
        clearTimeout(scrollTimeout)
      }
    })

    // Cart operations
    const addToCart = (product) => {
      cart.addItem(product)

      // Show notification when adding directly from catalog (not from modal)
      if (!selectedProduct.value || selectedProduct.value.id !== product.id) {
        notifier.success(
          `${product.name} byl přidán do košíku`,
          'Success',
          3000
        )
      }
    }

    const removeFromCart = (item) => {
      cart.removeItem(item.id)
    }

    const increaseQuantity = (item) => {
      // Check which method is available in the cart store
      if (typeof cart.updateItemQuantity === 'function') {
        cart.updateItemQuantity(item.id, item.quantity + 1)
      } else if (typeof cart.updateQuantity === 'function') {
        cart.updateQuantity(item.id, item.quantity + 1)
      } else if (typeof cart.increaseQuantity === 'function') {
        cart.increaseQuantity(item.id)
      } else {
        // Fallback: remove and add with updated quantity
        const updatedItem = { ...item, quantity: item.quantity + 1 }
        cart.removeItem(item.id)
        cart.addItem(updatedItem)
      }
    }

    const decreaseQuantity = (item) => {
      if (item.quantity <= 1) return

      // Check which method is available in the cart store
      if (typeof cart.updateItemQuantity === 'function') {
        cart.updateItemQuantity(item.id, item.quantity - 1)
      } else if (typeof cart.updateQuantity === 'function') {
        cart.updateQuantity(item.id, item.quantity - 1)
      } else if (typeof cart.decreaseQuantity === 'function') {
        cart.decreaseQuantity(item.id)
      } else {
        // Fallback: remove and add with updated quantity
        const updatedItem = { ...item, quantity: item.quantity - 1 }
        cart.removeItem(item.id)
        cart.addItem(updatedItem)
      }
    }

    const isProductInCart = (productId) => {
      return cart.items.some((item) => item.id === productId)
    }

    // Product detail functionality
    const selectedProduct = ref(null)
    const isModalVisible = ref(false)

    // Helper to create URL slug from product name
    const createSlug = (name) => {
      return name
        .toLowerCase()
        .replace(/[^\w\s-]/g, '') // Remove special characters
        .replace(/\s+/g, '-')     // Replace spaces with -
        .replace(/--+/g, '-')     // Replace multiple - with single -
        .trim();
    }

    const showProductDetail = (product) => {
      console.log('ProductCatalog: Showing product detail for:', product.name);

      // First set the product to ensure it's available when modal becomes visible
      selectedProduct.value = product;

      // Set visible flag to trigger animation immediately
      isModalVisible.value = true;

      // Update URL using history API instead of router to prevent re-renders
      // This is much less invasive than router.push
      const productSlug = createSlug(product.name);
      const newUrl = `/catalog/product/${productSlug}?id=${product.id}`;
      window.history.replaceState(
        { id: product.id },
        '',
        newUrl
      );
    }

    const closeProductDetail = () => {
      console.log('ProductCatalog: Closing product detail');

      // Hide the modal first to start animation
      isModalVisible.value = false;

      // Update URL only after animation starts, using history API
      // This should prevent any layout shifts during animation
      setTimeout(() => {
        window.history.replaceState({}, '', '/catalog');
      }, 50);
    }

    const onModalClosed = () => {
      console.log('ProductCatalog: Modal closed animation completed');
      // Clear the product only after animation completes
      selectedProduct.value = null;
    }

    // Check URL for product on initial load
    const checkUrlForProduct = async () => {
      // If URL has a product slug
      if (location.pathname.includes('/catalog/product/')) {
        // Extract id from query params
        const params = new URLSearchParams(location.search);
        const productId = params.get('id');
        console.log('Checking URL for product ID:', productId);

        if (!productId) {
          console.error('No product ID in URL');
          // No ID, redirect to catalog using history API
          window.history.replaceState({}, '', '/catalog');
          return;
        }

        // Ensure products are loaded
        if (products.items.length === 0) {
          await products.fetchProducts();
        }

        // Find the product by ID (ensure comparing the same type - string vs number issue)
        const product = products.items.find(p => String(p.id) === String(productId));

        if (product) {
          console.log('Found product by ID:', product.name);
          // Show the product without updating URL again
          selectedProduct.value = product;
          // Show modal
          isModalVisible.value = true;
        } else {
          console.error('Product not found for ID:', productId);
          // Product not found, redirect to catalog using history API
          notifier.error('Product not found', 'Error', 3000);
          window.history.replaceState({}, '', '/catalog');
        }
      }
    }

    const submitProductReview = (review) => {
      if (!selectedProduct.value) return

      // Create reviews array if it doesn't exist
      if (!selectedProduct.value.reviews) {
        selectedProduct.value.reviews = []
      }

      // Add review to product
      selectedProduct.value.reviews.push(review)

      // Recalculate product rating
      const totalRating = selectedProduct.value.reviews.reduce((sum, r) => sum + r.rating, 0)
      selectedProduct.value.rating = totalRating / selectedProduct.value.reviews.length

      // Here you would typically send this to an API
      console.log('Review submitted:', review)
    }

    // Create a function to handle popstate events for reuse
    const handlePopState = async (e) => {
      console.log('Browser navigation detected', e.state);

      if (location.pathname.includes('/catalog/product/')) {
        // Browser navigated to product detail
        await checkUrlForProduct();
      } else {
        // Browser navigated away from product detail
        isModalVisible.value = false;
        setTimeout(() => {
          selectedProduct.value = null;
        }, 250);
      }
    };

    // Handle browser back/forward buttons with popstate
    onMounted(() => {
      window.addEventListener('popstate', handlePopState);
    });

    onUnmounted(() => {
      window.removeEventListener('popstate', handlePopState);
    });

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
      mainContentRef,
      loadProducts,
      selectCategory,
      updateSearchQuery,
      addToCart,
      removeFromCart,
      increaseQuantity,
      decreaseQuantity,
      isProductInCart,
      sortOptions,
      scrollUp,
      scrollDown,
      isActivelyScrolling,
      selectedProduct,
      isModalVisible,
      showProductDetail,
      closeProductDetail,
      onModalClosed,
      submitProductReview,
    }
  },
}
</script>

<style scoped>
select {
  -webkit-appearance: none;
  -moz-appearance: none;
}

select::-ms-expand {
  display: none;
}

option {
  background: white;
  color: #374151;
  padding: 8px 16px;
  font-weight: 500;
}

@media (prefers-color-scheme: dark) {
  option {
    background: #1f2937;
    color: #f3f4f6;
  }
}


::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
  border: 2px solid #f1f1f1;
}

::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* Hide scrollbar but keep scrolling functionality */
.hide-scrollbar {
  -ms-overflow-style: none;
  /* IE and Edge */
  scrollbar-width: none;
  /* Firefox */
}

.hide-scrollbar::-webkit-scrollbar {
  display: none;
  /* Chrome, Safari and Opera */
  width: 0;
}

/* Make sure no scrollbar appears */
::-webkit-scrollbar {
  width: 0;
  display: none;
}
</style>
