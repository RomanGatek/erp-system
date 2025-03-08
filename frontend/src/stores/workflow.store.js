import { defineStore } from 'pinia';
import { useNotifier } from './notifier';
import { api } from '@/services/api';
import { setupSort } from '@/utils/table-utils.js';

export const useWorkflowStore = defineStore('workflow', {
  state: () => ({
    items: [],
    loading: false,
    error: null,
    selectedOrder: null,
    searchQuery: '',
    sorting: setupSort('orderTime', 'desc'),
    type: 'all',
    pagination: { currentPage: 1, perPage: 10 },
  }),

  getters: {
    pendingOrders: (state) => state.items.filter(order => order.status === 'PENDING'),
    confirmedOrders: (state) => state.items.filter(order => order.status === 'CONFIRMED'),
    canceledOrders: (state) => state.items.filter(order => order.status === 'CANCELED'),
    filtered: (state) => {
      return state.items.filter(order => {
        const search = state.searchQuery.toLowerCase();

        // First check the status filter
        if (state.type.toLowerCase() !== 'all') {
          const statusMap = {
            'pending': 'PENDING',
            'confirmed': 'CONFIRMED',
            'canceled': 'CANCELED'
          };
          const targetStatus = statusMap[state.type.toLowerCase()];
          if (!targetStatus || order.status !== targetStatus) {
            return false;
          }
        }

        // Then apply search filter if exists
        if (search) {
          return order.approvedBy?.username?.toLowerCase().includes(search) ||
            order.orderItems?.some(orderItem =>
              orderItem.name?.toLowerCase().includes(search) ||
              orderItem.description?.toLowerCase().includes(search)
            );
        }

        return true;
      });
    },
    sortedAndFiltered: (state) => {
      const filtered = state.filtered;
      if (!state.sorting.field) return filtered;

      return [...filtered].sort((a, b) => {
        let aVal = a[state.sorting.field];
        let bVal = b[state.sorting.field];

        // Special handling for specific fields
        if (state.sorting.field === 'orderItems') {
          aVal = a.orderItems?.length || 0;
          bVal = b.orderItems?.length || 0;
        } else if (state.sorting.field === 'orderTime') {
          aVal = new Date(aVal).getTime();
          bVal = new Date(bVal).getTime();
        }

        // Handle numeric values
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return state.sorting.direction === 'asc' ? aVal - bVal : bVal - aVal;
        }

        // Handle string values
        aVal = String(aVal).toLowerCase();
        bVal = String(bVal).toLowerCase();

        if (state.sorting.direction === 'asc') {
          return aVal.localeCompare(bVal);
        } else {
          return bVal.localeCompare(aVal);
        }
      });
    },
    paginatedOrders: (state) => {
      const start = (state.pagination.currentPage - 1) * state.pagination.perPage;
      const end = start + state.pagination.perPage;
      return state.sortedAndFiltered.slice(start, end);
    }
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

      try {
        // Get the order with its items
        const order = this.items.find(o => o.id === orderId);

        var response;

        if (order.orderType === 'SELL') {
          if (!order?.orderItems?.length) {
            throw new Error('Order or order items not found');
          }
          // Check inventory directly from order data
          for (const orderItem of order.orderItems) {
            if (orderItem.stockedQuantity < orderItem.needQuantity) {
              notifier.error(`Insufficient stock for item: ${orderItem.name}`);
              return false;
            }
          }

          console.log("Comment: ", comment)

          response = await api.put(`/orders/${orderId}/confirm`, comment);
        } else if (order.orderType === 'PURCHASE') {
          response = await api.put(`/orders/${orderId}/confirm`, comment);
        }
        if (response.data) {
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
        // Get the order
        const order = this.items.find(o => o.id === orderId);
        if (!order) {
          throw new Error('Order not found');
        }

        const response = await api.put(`/orders/${orderId}/cancel`, comment);

        if (response.data) {
          // Update local state
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
