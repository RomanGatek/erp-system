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
    }
  },
  
  actions: {
    // Add an item to the cart
    addItem(product) {
      const existingItem = this.items.find(item => item.id === product.id);
      
      if (existingItem) {
        existingItem.quantity += product.quantity || 1;
      } else {
        this.items.push({...product, quantity: product.quantity || 1});
      }
      
      this.updateTotal();
      
      // Save cart to localStorage for persistence
      this.saveCart();
    },
    
    // Remove an item from the cart
    removeItem(productId) {
      this.items = this.items.filter(item => item.id !== productId);
      this.updateTotal();
      this.saveCart();
    },
    
    // Update the quantity of an item in the cart
    updateQuantity(productId, quantity) {
      const item = this.items.find(item => item.id === productId);
      if (item) {
        item.quantity = quantity;
        
        // Remove item if quantity is 0
        if (quantity <= 0) {
          this.removeItem(productId);
          return;
        }
        
        this.updateTotal();
        this.saveCart();
      }
    },
    
    // Clear all items from the cart
    clearCart() {
      this.items = [];
      this.total = 0;
      this.saveCart();
    },
    
    // Calculate the total price of all items in the cart
    updateTotal() {
      this.total = this.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    },
    
    // Save cart to localStorage
    saveCart() {
      localStorage.setItem('cart', JSON.stringify(this.items));
    },
    
    // Load cart from localStorage
    loadCart() {
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        try {
          this.items = JSON.parse(savedCart);
          this.updateTotal();
        } catch (e) {
          console.error('Failed to load cart from localStorage', e);
          this.clearCart();
        }
      }
    },
    
    // Initialize the cart
    init() {
      this.loadCart();
    }
  }
}); 