import { defineStore } from 'pinia';
import { useNotifier } from './notifier';
import { api } from '@/services/api';
import {filter, setupSort} from '@/utils/table-utils.js';
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
    pagination: { currentPage: 1, perPage: 10 },
  }),

  getters: {
    pendingOrders: (state) => state.items.filter(order => order.status === 'PENDING'),
    confirmedOrders: (state) => state.items.filter(order => order.status === 'CONFIRMED'),
    canceledOrders: (state) => state.items.filter(order => order.status === 'CANCELED'),
    filtered: (state) => filter(state, (order) => {
      const search = state.searchQuery.toLocaleLowerCase();
      /** @type {{id: number, inventoryItemId: number, productId: number, stockedQuantity: number, needQuantity: number, name: string, buyoutPrice: number, purchasePrice: number, description: string}[]} */
      const orderItems = order.orderItems;
      return order.approvedBy.username.includes(search) ||
        orderItems.some(orderItem =>
          orderItem.name.includes(search)
          || orderItem.description.includes(search)
        );
    }),
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
        console.log({...this.items[0]})
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
        // Get the order with its items
        const order = this.items.find(o => o.id === orderId);
        if (!order?.orderItems?.length) {
          throw new Error('Order or order items not found');
        }

        // Check inventory for each item
        await inventoryStore.fetchItems();
        for (const orderItem of order.orderItems) {
          const inventoryItem = inventoryStore.items.find(
            item => item.id === orderItem.inventoryItemId
          );

          if (!inventoryItem || inventoryItem.quantity < orderItem.needQuantity) {
            notifier.error(`Insufficient stock for item: ${orderItem.name}`);
            return false;
          }
        }

        // Approve the order
        const response = await api.put(`/orders/${orderId}/confirm`, comment);

        if (response.data) {
          // Update inventory for each item
          for (const orderItem of order.orderItems) {
            await api.put(`/inventory/${orderItem.inventoryItemId}/release`, {
              params: { quantity: orderItem.needQuantity }
            });
          }

          // Update local state
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

          await inventoryStore.fetchItems(); // Refresh inventory state
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
