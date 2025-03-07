import { defineStore } from 'pinia';
import { useNotifier } from './notifier';
import { api } from '@/services/api';
import { setupSort } from '@/utils/table-utils.js';
import { __paginate } from '@/utils/pagination.js';
import { useInventoryStore } from './storage.store.js';

export const useWorkflowStore = defineStore('workflow', {
  state: () => ({
    items: [],
    loading: false,
    error: null,
    selectedOrder: null,
    searchQuery: '',
    sorting: setupSort('orderTime', 'desc'),
    pagination: { currentPage: 1, perPage: 10 }
  }),
  
  getters: {
    pendingOrders: (state) => state.items.filter(order => order.status === 'PENDING'),
    confirmedOrders: (state) => state.items.filter(order => order.status === 'CONFIRMED'),
    canceledOrders: (state) => state.items.filter(order => order.status === 'CANCELED'),
    
    filteredOrders: (state) => (filter, searchQuery) => {
      if (!Array.isArray(state.items)) {
        return [];
      }
      
      let filtered = filter === 'all' 
        ? state.items
        : state.items.filter(order => order.status.toLowerCase() === filter);
      
      if (searchQuery?.trim()) {
        const query = searchQuery.toLowerCase();
        filtered = filtered.filter(order => 
          order.id.toString().includes(query) ||
          (order.product?.name?.toLowerCase().includes(query))
        );
      }
      
      return filtered;
    },
    
    paginatedOrders: (state) => __paginate(state)
  },
  
  actions: {
    async fetchOrders() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await api.get('/orders');
        if (Array.isArray(response.data)) {
          this.items = response.data;
        } else {
          console.error('Expected array but received:', response);
          this.items = [];
          this.error = 'Received invalid data format from server';
        }
      } catch (err) {
        console.error('Error fetching orders:', err);
        this.error = err.response?.data?.message || 'Failed to fetch orders';
        throw err;
      } finally {
        this.loading = false;
      }
    },
    
    async getOrderById(id) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await api.get(`/orders/${id}`);
        if (response.data?.id) {
          this.selectedOrder = response.data;
          return response.data;
        }
      } catch (err) {
        this.error = err.response?.data?.message || `Failed to fetch order #${id}`;
        this.selectedOrder = null;
        throw err;
      } finally {
        this.loading = false;
      }
    },
    
    async approveOrder(orderId, comment) {
      this.loading = true;
      this.error = null;
      const notifier = useNotifier();
      const inventoryStore = useInventoryStore();
      
      try {
        // Nejdřív zkontrolujeme dostupnost na skladě
        const order = this.items.find(o => o.id === orderId);
        if (!order?.product?.id) {
          throw new Error('Order or product not found');
        }
        
        // Načteme aktuální stav skladu
        await inventoryStore.fetchItems();
        const inventoryItem = inventoryStore.items.find(
          item => item.product.id === order.product.id
        );
        
        if (!inventoryItem || inventoryItem.quantity < order.amount) {
          notifier.error('Nedostatek zboží na skladě pro schválení objednávky');
          return false;
        }
        
        // Schválíme objednávku
        const response = await api.put(`/orders/${orderId}/confirm`, comment);
        
        if (response.data) {
          // Aktualizujeme stav skladu
          await api.put(`/inventory/${inventoryItem.id}/release`, {
            params: { quantity: order.amount }
          });
          
          // Aktualizujeme lokální stav
          const index = this.items.findIndex(o => o.id === orderId);
          if (index !== -1) {
            this.items[index] = {
              ...this.items[index],
              ...response.data,
              status: 'CONFIRMED',
              comment: comment,
              decisionTime: new Date().toISOString()
            };
          }
          
          if (this.selectedOrder?.id === orderId) {
            this.selectedOrder = {
              ...this.selectedOrder,
              ...response.data,
              status: 'CONFIRMED',
              comment: comment,
              decisionTime: new Date().toISOString()
            };
          }
          
          await inventoryStore.fetchItems(); // Obnovíme stav skladu
          return true;
        }
        return false;
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to approve order';
        throw err;
      } finally {
        this.loading = false;
      }
    },
    
    async rejectOrder(orderId, comment) {
      this.loading = true;
      this.error = null;

      try {
        const response = await api.put(`/orders/${orderId}/cancel`, comment);
        
        if (response.status >= 200 && response.status < 300) {
          const index = this.items.findIndex(o => o.id === orderId);
          if (index !== -1) {
            this.items[index] = {
              ...this.items[index],
              ...response.data,
              status: 'CANCELED',
              comment: comment,
              decisionTime: new Date().toISOString()
            };
          }
          
          if (this.selectedOrder?.id === orderId) {
            this.selectedOrder = {
              ...this.selectedOrder,
              ...response.data,
              status: 'CANCELED',
              comment: comment,
              decisionTime: new Date().toISOString()
            };
          }
          return true;
        }
        return false;
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to reject order';
        throw err;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteOrder(orderId) {
      this.loading = true;
      this.error = null;
      
      try {
        await api.delete(`/orders/${orderId}`);
        this.items = this.items.filter(o => o.id !== orderId);
        
        if (this.selectedOrder?.id === orderId) {
          this.selectedOrder = null;
        }
        return true;
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to delete order';
        throw err;
      } finally {
        this.loading = false;
      }
    },
    
    setSearch(query) {
      this.searchQuery = query;
      this.pagination.currentPage = 1;
    },
    
    setSorting(field) {
      if (this.sorting.field === field) {
        this.sorting.direction = this.sorting.direction === 'asc' ? 'desc' : 'asc';
      } else {
        this.sorting.field = field;
        this.sorting.direction = 'asc';
      }
    },
    
    setPage(page) {
      this.pagination.currentPage = page;
    },
    
    clearError() {
      this.error = null;
    }
  }
}); 
