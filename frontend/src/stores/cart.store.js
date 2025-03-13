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
        console.log('Initializing cart from storage...');
        const savedCart = localStorage.getItem('cart');
        if (savedCart) {
          this.items = JSON.parse(savedCart);
          console.log('Cart loaded from storage, items:', this.items.length);
        } else {
          console.log('No saved cart found in storage');
        }
      } catch (error) {
        console.error('Failed to load cart from storage:', error);
      }
    },

    // Metoda pro uložení košíku do localStorage po každé změně
    saveCart() {
      try {
        localStorage.setItem('cart', JSON.stringify(this.items));
        console.log('Cart saved to storage, items:', this.items.length);
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
    
    // Remove an item from the cart
    removeItem(itemId) {
      this.items = this.items.filter(item => item.id !== itemId);
      
      // Uložit košík po změně
      this.saveCart();
    },
    
    // Update the quantity of an item in the cart
    updateItemQuantity(itemId, quantity) {
      const item = this.items.find(item => item.id === itemId);
      if (item) {
        item.quantity = quantity;
      }
      
      // Uložit košík po změně
      this.saveCart();
    },
    
    // Increase the quantity of an item in the cart
    increaseQuantity(itemId) {
      const item = this.items.find(item => item.id === itemId);
      if (item) {
        item.quantity += 1;
      }
      
      // Uložit košík po změně
      this.saveCart();
    },
    
    // Decrease the quantity of an item in the cart
    decreaseQuantity(itemId) {
      const item = this.items.find(item => item.id === itemId);
      if (item && item.quantity > 1) {
        item.quantity -= 1;
      }
      
      // Uložit košík po změně
      this.saveCart();
    },
    
    // Clear all items from the cart
    clearCart() {
      this.items = [];
      
      // Uložit košík po změně (vymazat z localStorage)
      this.saveCart();
    },
    
    // Calculate the total price of all items in the cart
    updateTotal() {
      this.total = this.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    },
    
    // Initialize the cart
    init() {
      this.initializeCart();
    },
    
    // Add clearItems method if it doesn't exist
    clearItems() {
      this.items = [];
      this.total = 0;
      this.saveCart();
    }
  }
}); 