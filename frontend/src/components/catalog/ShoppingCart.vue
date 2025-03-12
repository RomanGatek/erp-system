<template>
  <div class="right-sidebar">
    <div class="sidebar-container">
      <div class="sidebar-content">
        <h2 class="text-xl font-bold mb-4">Shopping Cart</h2>
        
        <div v-if="cartItems.length === 0" class="empty-cart">
          <p class="text-gray-500 text-center">Your cart is empty</p>
          <div class="empty-cart-icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" class="w-12 h-12 text-gray-300">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z" />
            </svg>
          </div>
        </div>
        
        <div v-else class="cart-content">
          <div class="cart-items">
            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <div class="cart-item-image">
                <img :src="item.image" :alt="item.name" />
              </div>
              <div class="cart-item-details">
                <h4 class="cart-item-name">{{ item.name }}</h4>
                <div class="cart-item-price-qty">
                  <span class="cart-item-price">{{ formatPrice(item.price) }} Kč,-</span>
                  <div class="cart-item-quantity">
                    <button 
                      @click="$emit('decrease-quantity', item)" 
                      class="quantity-btn"
                      :disabled="item.quantity <= 1"
                    >
                      -
                    </button>
                    <span class="quantity-value">{{ item.quantity }}</span>
                    <button 
                      @click="$emit('increase-quantity', item)" 
                      class="quantity-btn"
                    >
                      +
                    </button>
                  </div>
                </div>
              </div>
              <button 
                @click="$emit('remove-from-cart', item)" 
                class="remove-item-btn"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" class="w-4 h-4">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>
          
          <div class="cart-summary">
            <div class="cart-total">
              <span>Total:</span>
              <span class="total-price">{{ formatPrice(totalPrice) }} Kč,-</span>
            </div>
            <button class="checkout-btn">
              Proceed to Checkout
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ShoppingCart',
  props: {
    cartItems: {
      type: Array,
      required: true
    }
  },
  emits: ['increase-quantity', 'decrease-quantity', 'remove-from-cart'],
  computed: {
    totalPrice() {
      return this.cartItems.reduce((total, item) => {
        return total + (item.price * item.quantity);
      }, 0);
    }
  },
  methods: {
    formatPrice(price) {
      return new Intl.NumberFormat('cs-CZ').format(price);
    }
  }
}
</script>

<style scoped>
.right-sidebar {
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

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem 0;
  gap: 1rem;
}

.empty-cart-icon {
  margin-top: 1rem;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: calc(100vh - 250px);
  overflow-y: auto;
  margin-bottom: 1rem;
}

.cart-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem;
  background-color: #f9fafb;
  border-radius: 0.5rem;
  position: relative;
}

.cart-item-image {
  width: 50px;
  height: 50px;
  border-radius: 0.375rem;
  overflow: hidden;
  flex-shrink: 0;
}

.cart-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-details {
  flex-grow: 1;
  min-width: 0;
}

.cart-item-name {
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 0.25rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cart-item-price-qty {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-item-price {
  font-size: 0.8rem;
  font-weight: 600;
}

.cart-item-quantity {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.quantity-btn {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e5e7eb;
  border: none;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  cursor: pointer;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-value {
  font-size: 0.8rem;
  width: 20px;
  text-align: center;
}

.remove-item-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: none;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  padding: 0.25rem;
}

.remove-item-btn:hover {
  color: #ef4444;
}

.cart-summary {
  border-top: 1px solid #e5e7eb;
  padding-top: 1rem;
  margin-top: 0.5rem;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  font-weight: 600;
  margin-bottom: 1rem;
}

.total-price {
  color: #4f46e5;
}

.checkout-btn {
  width: 100%;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 0.375rem;
  padding: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.checkout-btn:hover {
  background-color: #4338ca;
}
</style> 