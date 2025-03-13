<template>
  <transition
    name="modal-fade"
    appear
    @after-leave="afterLeave"
  >
    <div 
      v-if="product" 
      class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/30 backdrop-blur-[3px]"
      @click="$emit('close')"
    >
      <transition
        name="modal-zoom"
        appear
      >
        <div 
          class="bg-white rounded-2xl shadow-xl w-full max-w-4xl max-h-[90vh] overflow-hidden flex flex-col transform will-change-transform"
          @click.stop
          ref="modalContent"
        >
          <!-- Header with close button -->
          <div class="flex justify-between items-center p-5 border-b border-gray-100 sticky top-0 bg-white z-10">
            <h2 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
              {{ product.name }}
            </h2>
            <button 
              @click="$emit('close')" 
              class="text-gray-400 hover:text-gray-600 transition-colors focus:outline-none rounded-full hover:bg-gray-100 p-1"
              aria-label="Close"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <!-- Product detail content -->
          <div class="flex-grow overflow-y-auto p-6">
            <div class="grid grid-cols-1 md:grid-cols-[350px_1fr] gap-8">
              <!-- Product image -->
              <div class="flex flex-col space-y-4">
                <div class="bg-gray-50 rounded-xl overflow-hidden h-[300px] relative">
                  <!-- Main image display -->
                  <div v-if="product.images?.length > 0 || product.image" class="h-full relative">
                    <img
                      :src="currentImage"
                      :alt="product.name"
                      class="w-full h-full object-contain"
                      loading="eager"
                    />
                    
                    <!-- Image navigation arrows -->
                    <div v-if="hasMultipleImages" class="absolute inset-x-0 top-1/2 -translate-y-1/2 flex justify-between px-2">
                      <button 
                        @click.stop="prevImage" 
                        class="bg-white/90 hover:bg-white text-gray-800 rounded-full p-2 shadow-lg transition-colors hover:text-blue-600 transform hover:scale-110"
                        aria-label="Previous image"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
                        </svg>
                      </button>
                      <button 
                        @click.stop="nextImage" 
                        class="bg-white/90 hover:bg-white text-gray-800 rounded-full p-2 shadow-lg transition-colors hover:text-blue-600 transform hover:scale-110"
                        aria-label="Next image"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
                        </svg>
                      </button>
                    </div>
                    
                    <!-- Image counter -->
                    <div v-if="hasMultipleImages" class="absolute bottom-2 right-2 bg-black/50 text-white text-xs px-2 py-1 rounded-md">
                      {{ currentImageIndex + 1 }} / {{ totalImages }}
                    </div>
                  </div>

                  <!-- Fallback display if no image is available -->
                  <div
                    v-else
                    class="w-full h-full flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      class="w-16 h-16 text-gray-300"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="1.5"
                        d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"
                      />
                    </svg>
                  </div>
                  
                  <!-- Category tag -->
                  <span
                    v-if="product.productCategory?.name"
                    class="absolute top-4 right-4 px-3 py-1 text-xs font-medium rounded-md ring-1 ring-gray-500/10 ring-inset"
                    :class="{
                      'bg-slate-50 text-slate-600': product.productCategory?.color === 'slate',
                      'bg-gray-50 text-gray-600': product.productCategory?.color === 'gray',
                      'bg-zinc-50 text-zinc-600': product.productCategory?.color === 'zinc',
                      'bg-neutral-50 text-neutral-600': product.productCategory?.color === 'neutral',
                      'bg-stone-50 text-stone-600': product.productCategory?.color === 'stone',
                      'bg-red-50 text-red-600': product.productCategory?.color === 'red',
                      'bg-orange-50 text-orange-600': product.productCategory?.color === 'orange',
                      'bg-amber-50 text-amber-600': product.productCategory?.color === 'amber',
                      'bg-yellow-50 text-yellow-600': product.productCategory?.color === 'yellow',
                      'bg-lime-50 text-lime-600': product.productCategory?.color === 'lime',
                      'bg-green-50 text-green-600': product.productCategory?.color === 'green',
                      'bg-emerald-50 text-emerald-600': product.productCategory?.color === 'emerald',
                      'bg-teal-50 text-teal-600': product.productCategory?.color === 'teal',
                      'bg-cyan-50 text-cyan-600': product.productCategory?.color === 'cyan',
                      'bg-sky-50 text-sky-600': product.productCategory?.color === 'sky',
                      'bg-blue-50 text-blue-600': product.productCategory?.color === 'blue',
                      'bg-indigo-50 text-indigo-600': product.productCategory?.color === 'indigo',
                      'bg-violet-50 text-violet-600': product.productCategory?.color === 'violet',
                      'bg-purple-50 text-purple-600': product.productCategory?.color === 'purple',
                      'bg-fuchsia-50 text-fuchsia-600': product.productCategory?.color === 'fuchsia',
                      'bg-pink-50 text-pink-600': product.productCategory?.color === 'pink',
                      'bg-rose-50 text-rose-600': product.productCategory?.color === 'rose',
                    }"
                  >
                    {{ product.productCategory?.name }}
                  </span>
                </div>
                
                <!-- Thumbnail gallery -->
                <div v-if="hasMultipleImages" class="flex space-x-2 overflow-x-auto pb-2">
                  <button 
                    v-for="(image, idx) in allImages" 
                    :key="idx"
                    @click.stop="currentImageIndex = idx"
                    class="h-16 w-16 flex-shrink-0 rounded-md overflow-hidden border-2 transition-all duration-200"
                    :class="idx === currentImageIndex ? 'border-blue-500 shadow-md' : 'border-transparent opacity-80 hover:opacity-100'"
                  >
                    <img :src="image" :alt="`${product.name} - image ${idx + 1}`" class="h-full w-full object-cover" />
                  </button>
                </div>
                
                <!-- Product price and add to cart -->
                <div class="bg-gray-50 rounded-xl p-5">
                  <div class="flex justify-between items-start">
                    <div>
                      <div class="text-gray-500 text-sm mb-1">Price</div>
                      <div class="mb-1">
                        <span class="text-2xl font-bold text-gray-800">
                          {{ formatPrice(priceWithVat) }}
                          <span class="text-sm font-medium text-gray-500">Kč</span>
                        </span>
                        <span class="text-xs font-medium text-gray-500 ml-1">včetně DPH</span>
                      </div>
                      <div class="flex flex-col text-sm text-gray-500">
                        <span>{{ formatPrice(priceWithoutVat) }} Kč bez DPH</span>
                        <span>DPH (21%): {{ formatPrice(vatAmount) }} Kč</span>
                      </div>
                    </div>
                    <button 
                      @click="addToCart"
                      :disabled="isInCart"
                      class="bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white py-3 px-8 rounded-xl font-medium transition-colors shadow-sm"
                    >
                      {{ isInCart ? 'In Cart' : 'Add to Cart' }}
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- Product information -->
              <div class="space-y-6">
                <!-- Product description -->
                <div>
                  <h3 class="text-lg font-semibold mb-3">Description</h3>
                  <div class="text-gray-600 whitespace-pre-line">{{ product.description }}</div>
                  <div v-if="product.extendedDescription" class="text-gray-600 mt-3 whitespace-pre-line">
                    {{ product.extendedDescription }}
                  </div>
                </div>
                
                <!-- Product specifications -->
                <div v-if="product.specifications && product.specifications.length > 0">
                  <h3 class="text-lg font-semibold mb-3">Specifications</h3>
                  <div class="grid grid-cols-2 gap-3">
                    <div 
                      v-for="(spec, index) in product.specifications" 
                      :key="index"
                      class="flex"
                    >
                      <span class="text-gray-500 mr-2">{{ spec.name }}:</span>
                      <span class="text-gray-800 font-medium">{{ spec.value }}</span>
                    </div>
                  </div>
                </div>
                
                <!-- Product ratings & comments -->
                <div class="border-t pt-6 mt-6">
                  <h3 class="text-lg font-semibold mb-4">Ratings & Reviews</h3>
                  
                  <!-- Current rating -->
                  <div class="flex items-center space-x-2 mb-6">
                    <div class="flex">
                      <template v-for="star in 5" :key="star">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 24 24"
                          :fill="star <= (product.rating || 0) ? 'currentColor' : 'none'"
                          :stroke="star <= (product.rating || 0) ? 'none' : 'currentColor'"
                          class="w-5 h-5"
                          :class="star <= (product.rating || 0) ? 'text-amber-400' : 'text-gray-300'"
                        >
                          <path
                            fill-rule="evenodd"
                            d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.007 5.404.433c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.433 2.082-5.006z"
                            clip-rule="evenodd"
                          />
                        </svg>
                      </template>
                    </div>
                    <span class="text-lg font-medium text-gray-700">{{ product.rating || 0 }}/5</span>
                    <span class="text-gray-500">({{ product.reviews?.length || 0 }} reviews)</span>
                  </div>
                  
                  <!-- Add review form -->
                  <div class="bg-gray-50 rounded-xl p-5 mb-6">
                    <h4 class="font-medium mb-3">Leave a Review</h4>
                    <div class="flex items-center space-x-2 mb-3">
                      <div class="flex">
                        <template v-for="star in 5" :key="star">
                          <button 
                            @click="userRating = star"
                            class="focus:outline-none"
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              :fill="star <= userRating ? 'currentColor' : 'none'"
                              :stroke="star <= userRating ? 'none' : 'currentColor'"
                              class="w-6 h-6"
                              :class="star <= userRating ? 'text-amber-400' : 'text-gray-300 hover:text-amber-200'"
                            >
                              <path
                                fill-rule="evenodd"
                                d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.007 5.404.433c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.433 2.082-5.006z"
                                clip-rule="evenodd"
                              />
                            </svg>
                          </button>
                        </template>
                      </div>
                      <span class="text-sm text-gray-500">Select your rating</span>
                    </div>
                    <textarea
                      v-model="reviewComment"
                      placeholder="Write your comment here..."
                      class="w-full p-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      rows="3"
                    ></textarea>
                    <div class="flex justify-end mt-3">
                      <button 
                        @click="submitReview"
                        class="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-lg text-sm font-medium transition-colors disabled:bg-gray-300"
                        :disabled="!canSubmitReview"
                      >
                        Submit Review
                      </button>
                    </div>
                  </div>
                  
                  <!-- Reviews list -->
                  <div v-if="product.reviews && product.reviews.length > 0" class="space-y-4">
                    <div 
                      v-for="(review, index) in product.reviews" 
                      :key="index"
                      class="border-b border-gray-100 pb-4 last:border-b-0"
                    >
                      <div class="flex items-center justify-between mb-2">
                        <div class="flex items-center space-x-2">
                          <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-medium text-sm">
                            {{ review.user.charAt(0).toUpperCase() }}
                          </div>
                          <span class="font-medium">{{ review.user }}</span>
                        </div>
                        <div class="flex">
                          <template v-for="star in 5" :key="star">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              :fill="star <= review.rating ? 'currentColor' : 'none'"
                              :stroke="star <= review.rating ? 'none' : 'currentColor'"
                              class="w-4 h-4"
                              :class="star <= review.rating ? 'text-amber-400' : 'text-gray-300'"
                            >
                              <path
                                fill-rule="evenodd"
                                d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.007 5.404.433c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.433 2.082-5.006z"
                                clip-rule="evenodd"
                              />
                            </svg>
                          </template>
                        </div>
                      </div>
                      <p class="text-gray-600">{{ review.comment }}</p>
                      <div class="text-gray-400 text-xs mt-1">{{ formatDate(review.date) }}</div>
                    </div>
                  </div>
                  
                  <!-- No reviews state -->
                  <div v-else class="text-center py-8 text-gray-500">
                    No reviews yet. Be the first to review this product!
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'ProductDetailModal',
  props: {
    product: {
      type: Object,
      required: true
    },
    isInCart: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close', 'add-to-cart', 'submit-review', 'after-leave'],
  data() {
    return {
      userRating: 0,
      reviewComment: '',
      currentImageIndex: 0,
      VAT_RATE: 0.21, // 21% VAT rate for Czech Republic
    }
  },
  computed: {
    canSubmitReview() {
      return this.userRating > 0 && this.reviewComment.trim().length > 0
    },
    allImages() {
      // Combine product.image with product.images if they exist
      const images = []
      
      if (this.product.image) {
        images.push(this.product.image)
      }
      
      if (this.product.images && Array.isArray(this.product.images)) {
        images.push(...this.product.images.filter(img => img))
      }
      
      return images.length > 0 ? images : []
    },
    hasMultipleImages() {
      return this.allImages.length > 1
    },
    totalImages() {
      return this.allImages.length
    },
    currentImage() {
      if (this.allImages.length === 0) return null
      return this.allImages[this.currentImageIndex]
    },
    priceWithoutVat() {
      // Base price without VAT
      return this.product.buyoutPrice || this.product.price || 0
    },
    vatAmount() {
      // Calculate VAT amount
      return this.priceWithoutVat * this.VAT_RATE
    },
    priceWithVat() {
      // Calculate price with VAT
      return this.priceWithoutVat + this.vatAmount
    }
  },
  methods: {
    formatPrice(price) {
      return new Intl.NumberFormat('cs-CZ').format(price)
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return d.toLocaleDateString('cs-CZ')
    },
    addToCart() {
      this.$emit('add-to-cart', this.product)
    },
    submitReview() {
      if (!this.canSubmitReview) return
      
      this.$emit('submit-review', {
        productId: this.product.id,
        rating: this.userRating,
        comment: this.reviewComment,
        date: new Date().toISOString(),
        user: 'Guest' // In a real app, this would be the actual user's name
      })
      
      // Reset form
      this.userRating = 0
      this.reviewComment = ''
    },
    nextImage() {
      if (this.currentImageIndex < this.totalImages - 1) {
        this.currentImageIndex++
      } else {
        this.currentImageIndex = 0
      }
    },
    prevImage() {
      if (this.currentImageIndex > 0) {
        this.currentImageIndex--
      } else {
        this.currentImageIndex = this.totalImages - 1
      }
    },
    handleKeyDown(e) {
      if (e.key === 'Escape') {
        this.$emit('close')
      } else if (e.key === 'ArrowRight' && this.hasMultipleImages) {
        this.nextImage()
      } else if (e.key === 'ArrowLeft' && this.hasMultipleImages) {
        this.prevImage()
      }
    },
    handleClickOutside(e) {
      if (this.$refs.modalContent && !this.$refs.modalContent.contains(e.target)) {
        this.$emit('close')
      }
    },
    afterLeave() {
      // Emit event after animation completes
      this.$emit('after-leave')
    }
  },
  mounted() {
    console.log('ProductDetailModal mounted, product:', this.product?.name);
    
    // Prevent body scrolling when modal is open
    document.body.style.overflow = 'hidden'
    
    // Add ESC key event listener
    document.addEventListener('keydown', this.handleKeyDown)
    
    // Add click outside listener
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeUnmount() {
    console.log('ProductDetailModal unmounting');
    
    // Re-enable body scrolling when modal is closed
    document.body.style.overflow = ''
    
    // Remove event listeners
    document.removeEventListener('keydown', this.handleKeyDown)
    document.removeEventListener('click', this.handleClickOutside)
  }
}
</script>

<style scoped>
/* Modal animations */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-zoom-enter-active,
.modal-zoom-leave-active {
  transition: transform 0.3s cubic-bezier(0.2, 1, 0.3, 1), opacity 0.3s ease;
}

.modal-zoom-enter-from,
.modal-zoom-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* Fixed backdrop blur and z-index */
.fixed.inset-0 {
  backdrop-filter: blur(3px);
  z-index: 9999;
}

/* Hide scrollbar but keep scrollability for modal content */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

/* Add button style fixes */
button.bg-blue-600 {
  border: none;
  outline: none;
}

button.bg-blue-600:hover {
  background-color: #2563eb;
}

button.bg-blue-600:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

/* Thumbnail navigation */
.flex.space-x-2 button {
  transition: all 0.2s ease;
}

.flex.space-x-2 button:hover {
  transform: translateY(-2px);
}
</style>