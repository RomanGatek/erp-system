<template>
  <div
    class="bg-white rounded-xl overflow-hidden transition-all duration-300 shadow-sm hover:shadow-md hover:-translate-y-1 h-full flex flex-col group cursor-pointer"
    @click="openProductDetail"
  >
    <div class="relative h-[130px] overflow-hidden bg-gray-50">
      <template v-if="product.image && !showFallbackImage">
        <img
          :src="product.image"
          :alt="product.name"
          loading="lazy"
          decoding="async"
          @error="handleImageError"
          @load="handleImageLoad"
          :class="[
            'w-full h-full object-cover transition-transform duration-500 ease-in-out group-hover:scale-110',
            { 'opacity-0': !imageLoaded }
          ]"
        />
        <!-- Loading placeholder -->
        <div 
          v-if="!imageLoaded" 
          class="absolute inset-0 bg-gradient-to-br from-gray-50 to-gray-100 animate-pulse"
        >
          <div class="flex items-center justify-center h-full">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              class="w-8 h-8 text-gray-300"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="1.5"
                d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z"
              />
            </svg>
          </div>
        </div>
      </template>
      <div
        v-else
        class="w-full h-full flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          class="w-12 h-12 text-gray-300"
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
        class="absolute top-2 right-2 text-[0.65rem] pinline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-gray-500/10 ring-inset"
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

      <!-- Default label -->
      <span
        v-if="!product.image && !showFallbackImage"
        class="absolute bottom-2 left-2 text-[0.65rem] py-0.5 px-1.5 rounded-full font-medium bg-green-100 text-green-600"
      >
        default
      </span>
    </div>

    <div class="p-4 flex flex-col flex-grow">
      <h3 class="text-[0.95rem] font-semibold mb-1 leading-tight">{{ product.name }}</h3>
      <p class="text-[0.75rem] text-gray-500 mb-3 leading-relaxed flex-grow">
        {{ product.description }}
      </p>

      <div class="flex flex-col mt-auto">
        <div class="mb-2">
          <span class="font-bold text-[1rem] text-gray-800">
            {{ formatPrice(priceWithVat) }}
            <span class="text-[0.7rem] font-medium text-gray-500">Kč</span>
          </span>
        </div>
        <BaseButton
          @click.stop="$emit('add-to-cart', product)"
          :disabled="isInCart"
          :type="isInCart ? 'secondary' : 'primary'"
          class="w-full"
        >
          {{ isInCart ? 'In Cart' : 'Add to Cart' }}
        </BaseButton>
      </div>
    </div>
  </div>
</template>

<script>
import BaseButton from '@/components/common/BaseButton.vue'

export default {
  name: 'ProductCard',
  components: {
    BaseButton
  },
  props: {
    product: {
      type: Object,
      required: true,
    },
    isInCart: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      showFallbackImage: !this.product.image,
      imageLoaded: false,
      imageLoadAttempts: 0,
      VAT_RATE: 0.21, // 21% VAT rate for Czech Republic
    }
  },
  computed: {
    priceWithoutVat() {
      return this.product.buyoutPrice || this.product.price || 0
    },
    vatAmount() {
      return this.priceWithoutVat * this.VAT_RATE
    },
    priceWithVat() {
      return this.priceWithoutVat + this.vatAmount
    }
  },
  emits: ['add-to-cart', 'show-detail'],
  methods: {
    formatPrice(price) {
      return new Intl.NumberFormat('cs-CZ').format(price)
    },
    handleImageError() {
      this.imageLoadAttempts++
      if (this.imageLoadAttempts < 2) {
        // Retry loading once with cache busting
        const img = new Image()
        img.src = `${this.product.image}?retry=${Date.now()}`
        img.onload = () => {
          this.showFallbackImage = false
          this.imageLoaded = true
        }
        img.onerror = () => {
          this.showFallbackImage = true
          this.imageLoaded = false
        }
      } else {
        this.showFallbackImage = true
        this.imageLoaded = false
      }
    },
    handleImageLoad() {
      this.imageLoaded = true
    },
    openProductDetail(event) {
      // Prevent event bubbling
      event.preventDefault();
      event.stopPropagation();
      
      // Emit event to show product detail
      this.$emit('show-detail', this.product);
      
      // Log for debugging
      console.log('Opening product detail for:', this.product.name);
    }
  },
  mounted() {
    // Preload image if it's in viewport
    if (this.product.image && !this.showFallbackImage) {
      const observer = new IntersectionObserver(
        (entries) => {
          entries.forEach(entry => {
            if (entry.isIntersecting) {
              const img = new Image()
              img.src = this.product.image
              observer.disconnect()
            }
          })
        },
        {
          rootMargin: '50px',
        }
      )
      observer.observe(this.$el)
    }
  },
}
</script>

<style scoped>
/* Add hover effect to make it more obvious the card is clickable */
.cursor-pointer:hover {
  cursor: pointer;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}
</style>
