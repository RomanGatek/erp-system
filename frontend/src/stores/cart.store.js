import { defineStore } from 'pinia';

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    total: 0
  }),
  
  getters: {
    cartCount: (state) => state.items.length,
    cartTotal: (state) => state.items.reduce((sum, item) => sum + (item.price * item.quantity), 0),
    
    // Get all items in the cart
    cartItems: (state) => state.items,
    
    // Check if a product is already in the cart
    isInCart: (state) => (productId) => {
      return state.items.some(item => item.id === productId);
    },
    
    // Get quantity of a specific product in cart
    getItemQuantity: (state) => (productId) => {
      const item = state.items.find(item => item.id === productId);
      return item ? item.quantity : 0;
    },

    totalItems: (state) => {
      return state.items.reduce((total, item) => total + item.quantity, 0);
    }
  },
  
  actions: {
    // Metoda pro inicializaci košíku z localStorage při načtení aplikace
    async initializeCart() {
      try {
        const savedCart = localStorage.getItem('cart');
        if (savedCart) {
          this.items = JSON.parse(savedCart);
        }
      } catch (error) {
        console.error('Failed to load cart from storage:', error);
      }
    },

    saveCart() {
      try {
        localStorage.setItem('cart', JSON.stringify(this.items));
      } catch (error) {
        console.error('Failed to save cart to storage:', error);
      }
    },

    // Add an item to the cart
    addItem(product) {
      const existingItem = this.items.find(item => item.id === product.id);
      
      if (existingItem) {
        existingItem.quantity += 1;
      } else {
        this.items.push({
          ...product,
          quantity: 1
        });
      }
      
      // Uložit košík po změně
      this.saveCart();
    },
    
    removeItem(itemId) {
      this.items = this.items.filter(item => item.id !== itemId);
      this.saveCart();
    },
    
    // Update the quantity of an item in the cart
    updateItemQuantity(itemId, quantity) {
      const item = this.items.find(item => item.id === itemId);
      if (item) {
        item.quantity = quantity;
      }
      this.saveCart();
    },
    
    increaseQuantity(itemId) {
      const item = this.items.find(item => item.id === itemId);
      if (item) {
        item.quantity += 1;
      }
    
      this.saveCart();
    },
    
    decreaseQuantity(itemId) {
      const item = this.items.find(item => item.id === itemId);
      if (item && item.quantity > 1) {
        item.quantity -= 1;
      }
      this.saveCart();
    },
    
    // Clear all items from the cart
    clearCart() {
      this.items = [];
      this.saveCart();
    },
    
    updateTotal() {
      this.total = this.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    },
    
    init() {
      this.initializeCart();
    },
    
    clearItems() {
      this.items = [];
      this.total = 0;
      this.saveCart();
    }
  }
}); 