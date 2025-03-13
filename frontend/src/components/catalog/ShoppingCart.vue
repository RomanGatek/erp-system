<template>
  <div class="h-[75dvh] overflow-y-auto">
    <div class="bg-white rounded-2xl shadow-md flex flex-col h-full shopping-cart">
      <div class="p-5">
        <h2
          class="text-xl font-bold mb-4 bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent"
        >
          Shopping Cart
        </h2>

        <div
          v-if="cartItems.length === 0"
          class="flex flex-col items-center justify-center py-8 gap-4"
        >
          <p class="text-gray-500 text-center">Your cart is empty</p>
          <div class="mt-4">
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
        </div>

        <div v-else>
          <div class="flex flex-col gap-3 max-h-[50dvh] overflow-y-auto mb-4">
            <div
              v-for="item in cartItems"
              :key="item.id"
              class="flex gap-3 p-3 bg-gray-50 rounded-lg relative"
            >
              <div class="w-[50px] h-[50px] rounded-md overflow-hidden flex-shrink-0 bg-gray-100">
                <template v-if="item.image">
                  <img
                    :src="item.image"
                    :alt="item.name"
                    @error="handleImageError"
                    class="w-full h-full object-cover"
                  />
                </template>
                <div v-else class="w-full h-full flex items-center justify-center bg-gray-100">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    class="w-6 h-6 text-gray-300"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="1.5"
                      d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"
                    />
                  </svg>
                </div>
              </div>
              <div class="flex-grow min-w-0">
                <h4
                  class="text-[0.85rem] font-medium mb-1 whitespace-nowrap overflow-hidden text-ellipsis"
                >
                  {{ item.name }}
                </h4>
                <div class="flex justify-between items-center">
                  <span class="text-[0.8rem] font-semibold"
                    >{{ formatPrice(item.buyoutPrice || item.price) }} Kč,-</span
                  >
                  <div class="flex items-center gap-1">
                    <button
                      @click="handleDecreaseQuantity(item)"
                      class="w-5 h-5 flex items-center justify-center bg-gray-200 border-none rounded text-xs cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
                      :disabled="item.quantity <= 1"
                    >
                      -
                    </button>
                    <span class="text-[0.8rem] w-5 text-center">{{ item.quantity }}</span>
                    <button
                      @click="handleIncreaseQuantity(item)"
                      class="w-5 h-5 flex items-center justify-center bg-gray-200 border-none rounded text-xs cursor-pointer"
                    >
                      +
                    </button>
                  </div>
                </div>
              </div>
              <button
                @click="handleRemoveFromCart(item)"
                class="absolute top-2 right-2 bg-transparent border-none text-gray-400 cursor-pointer p-1 hover:text-red-500"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  class="w-4 h-4"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>
          </div>

          <div class="border-t border-gray-200 pt-4 mt-2">
            <div class="flex justify-between font-semibold mb-4">
              <span>Total:</span>
              <span class="text-indigo-600">{{ formatPrice(totalPrice) }} Kč,-</span>
            </div>
            <BaseButton type="primary" class="w-full text-sm!"> Proceed to Checkout </BaseButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BaseButton from '../common/BaseButton.vue'

export default {
  components: {
    BaseButton,
  },
  name: 'ShoppingCart',
  props: {
    cartItems: {
      type: Array,
      required: true,
    },
  },
  emits: ['increase-quantity', 'decrease-quantity', 'remove-from-cart'],
  computed: {
    totalPrice() {
      return this.cartItems.reduce((total, item) => {
        return total + (item.buyoutPrice || item.price) * item.quantity
      }, 0)
    },
  },
  methods: {
    formatPrice(price) {
      return new Intl.NumberFormat('cs-CZ').format(price)
    },
    handleImageError(e) {
      e.target.parentNode.innerHTML = `
        <div class="w-full h-full flex items-center justify-center bg-gray-100">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            class="w-6 h-6 text-gray-300"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"
            />
          </svg>
        </div>
      `
    },
    handleIncreaseQuantity(item) {
      try {
        this.$emit('increase-quantity', item)
      } catch (error) {
        console.error('Error increasing quantity:', error)
      }
    },
    handleDecreaseQuantity(item) {
      try {
        this.$emit('decrease-quantity', item)
      } catch (error) {
        console.error('Error decreasing quantity:', error)
      }
    },
    handleRemoveFromCart(item) {
      try {
        this.$emit('remove-from-cart', item)
      } catch (error) {
        console.error('Error removing item from cart:', error)
      }
    },
  },
}
</script>
