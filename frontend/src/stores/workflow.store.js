import { defineStore } from 'pinia'
import { useNotifier } from './notifier.store.js'
import api from '@/services/api'
import { setupSort } from '@/utils'

export const useWorkflowStore = defineStore('workflow', {
  state: () => ({
    /** @type {import('../../types/index.js').Order[]} */
    items: [],
    loading: false,
    error: null,
    /** @type {import('../../types/index.js').Order} */
    selectedOrder: null,
    /**@type {string} */
    currentFilter: 'all',
    searchQuery: '',
    sorting: setupSort('orderTime', 'desc'),
    type: 'all',
    pagination: { currentPage: 1, perPage: 10 },
    pendingUpdates: [],
    lastUpdateTime: 0,
  }),

  getters: {
    pendingOrders: (state) => state.items.filter((order) => order.status === 'PENDING'),
    confirmedOrders: (state) => state.items.filter((order) => order.status === 'CONFIRMED'),
    canceledOrders: (state) => state.items.filter((order) => order.status === 'CANCELED'),
    filtered: (state) => {
      return state.items.filter((order) => {
        const search = state.searchQuery.toLowerCase()

        // First check the status filter
        if (state.type.toLowerCase() !== 'all') {
          if (order.status.toLowerCase() !== state.type.toLowerCase()) {
            return false
          }
        }

        // Then apply search filter if exists
        if (search) {
          return (
            order.approvedBy?.username?.toLowerCase().includes(search) ||
            order.orderItems?.some(
              (orderItem) =>
                orderItem.name?.toLowerCase().includes(search) ||
                orderItem.description?.toLowerCase().includes(search),
            )
          )
        }

        return true
      })
    },
    sortedAndFiltered: (state) => {
      const filtered = state.filtered
      if (!state.sorting.field) return filtered

      return [...filtered].sort((a, b) => {
        let aVal = a[state.sorting.field]
        let bVal = b[state.sorting.field]

        // Special handling for specific fields
        if (state.sorting.field === 'orderItems') {
          aVal = a.orderItems?.length || 0
          bVal = b.orderItems?.length || 0
        } else if (state.sorting.field === 'orderTime') {
          aVal = new Date(aVal).getTime()
          bVal = new Date(bVal).getTime()
        }

        // Handle numeric values
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return state.sorting.direction === 'asc' ? aVal - bVal : bVal - aVal
        }

        // Handle string values
        aVal = String(aVal).toLowerCase()
        bVal = String(bVal).toLowerCase()

        if (state.sorting.direction === 'asc') {
          return aVal.localeCompare(bVal)
        } else {
          return bVal.localeCompare(aVal)
        }
      })
    },
    paginatedOrders: (state) => {
      const start = (state.pagination.currentPage - 1) * state.pagination.perPage
      const end = start + state.pagination.perPage
      return state.sortedAndFiltered.slice(start, end)
    },
  },

  actions: {
    async fetchOrders() {
      this.loading = true;
      try {
        // Získáme objednávky z API
        const [orders, error] = await api.orders().getAll();
        this.error = error;
        
        if (orders) {
          // Pro každou objednávku aktualizujeme ceny produktů
          for (const order of orders) {
            if (order.orderItems && order.orderItems.length > 0) {
              // Zde bychom mohli volat API pro získání aktuálních cen produktů
              // Ale pro jednoduchost předpokládáme, že API již vrací aktuální ceny
              
              // Přepočítáme celkovou cenu objednávky
              let totalCost = 0;
              for (const item of order.orderItems) {
                const itemTotal = (parseFloat(item.buyoutPrice) || 0) * (parseInt(item.needQuantity) || 0);
                totalCost += itemTotal;
              }
              
              // Aktualizujeme celkovou cenu objednávky
              order.cost = totalCost;
            }
          }
          
          this.items = orders;
        }
      } catch (err) {
        console.error('Error fetching orders:', err);
        this.error = err;
      } finally {
        this.loading = false;
      }
    },

    async getOrderById(id) {
      this.loading = true;
      try {
        // Získáme detail objednávky z API
        const [order, error] = await api.orders().getById(id);
        this.error = error;
        
        if (order) {
          // Aktualizujeme ceny produktů v objednávce
          if (order.orderItems && order.orderItems.length > 0) {
            // Zde bychom mohli volat API pro získání aktuálních cen produktů
            // Ale pro jednoduchost předpokládáme, že API již vrací aktuální ceny
            
            // Přepočítáme celkovou cenu objednávky
            let totalCost = 0;
            for (const item of order.orderItems) {
              const itemTotal = (parseFloat(item.buyoutPrice) || 0) * (parseInt(item.needQuantity) || 0);
              totalCost += itemTotal;
            }
            
            // Aktualizujeme celkovou cenu objednávky
            order.cost = totalCost;
          }
          
          this.selectedOrder = order;
        }
        
        return order;
      } catch (err) {
        console.error('Error fetching order details:', err);
        this.error = err;
        return null;
      } finally {
        this.loading = false;
      }
    },

    /**
     * Helper for updating order status
     * @param {number} orderId
     * @param {*} data
     * @param {string} status
     * @param {string} comment
     */
    updateOrderStatus(orderId, data, status, comment) {
      const index = this.items.findIndex((o) => o.id === orderId)
      const updateData = {
        ...data,
        status,
        comment,
        decisionTime: new Date().toISOString(),
      }

      if (index !== -1) {
        this.items[index] = { ...this.items[index], ...updateData }
      }

      if (this.selectedOrder?.id === orderId) {
        this.selectedOrder = { ...this.selectedOrder, ...updateData }
      }
    },

    async approveOrder(orderId, comment) {
      this.loading = true
      this.error = null
      const notifier = useNotifier()

      const order = this.items.find((o) => o.id === orderId)

      if (order.orderType === 'SELL') {
        if (!order?.orderItems?.length) {
          this.error = 'Order or order items not found'
          this.loading = false
          return false
        }
        // Check inventory directly from order data
        for (const orderItem of order.orderItems) {
          if (orderItem.stockedQuantity < orderItem.needQuantity) {
            notifier.error(`Insufficient stock for item: ${orderItem.name}`)
            this.loading = false
            return false
          }
        }
      }

      const [data, error] = await api.orders().confirm(orderId, comment)
      this.error = error

      if (data) {
        this.updateOrderStatus(orderId, data, 'CONFIRMED', comment)
        this.loading = false
        return true
      }

      this.loading = false
      return false
    },

    async rejectOrder(orderId, comment) {
      this.loading = true
      this.error = null

      const [data, error] = await api.orders().cancel(orderId, comment)
      this.error = error

      if (data) {
        this.updateOrderStatus(orderId, data, 'CANCELED', comment)
        this.loading = false
        return true
      }

      this.loading = false
      return false
    },

    async deleteOrder(orderId) {
      this.loading = true
      this.error = null

      const [data, error] = await api.orders().delete(orderId)
      this.error = error

      if (data) {
        this.items = this.items.filter((o) => o.id !== orderId)
        if (this.selectedOrder?.id === orderId) {
          this.selectedOrder = null
        }
        this.loading = false
        return true
      }

      this.loading = false
      return false
    },

    setSearch(query) {
      this.searchQuery = query
      this.pagination.currentPage = 1
    },

    setSorting(field) {
      if (this.sorting.field === field) {
        this.sorting.direction = this.sorting.direction === 'asc' ? 'desc' : 'asc'
      } else {
        this.sorting.field = field
        this.sorting.direction = 'asc'
      }
    },

    setPage(page) {
      this.pagination.currentPage = page
    },

    clearError() {
      this.error = null
    },
    async updateSelf({ updateId, entity }) {
      // Přidáme ochranu proti příliš častým aktualizacím (debounce)
      const now = Date.now();
      const timeSinceLastUpdate = now - this.lastUpdateTime;
      
      // Pokud od poslední aktualizace uplynulo méně než 500ms, ignorujeme tuto aktualizaci
      if (timeSinceLastUpdate < 500) {
        console.log('Ignoring update, too soon after last update');
        return;
      }
      
      // Only add the update if it's not already in the pending updates
      if (!this.pendingUpdates.some((update) => update.updateId === updateId)) {
        // Create a new array to ensure reactivity
        this.pendingUpdates = [...this.pendingUpdates, { updateId, entity, timestamp: now }];
        console.log('Added workflow pending update:', { updateId, entity });
        
        // Aktualizujeme objednávky při aktualizaci produktů nebo objednávek
        if (entity === 'orders' || entity === 'order' || entity === 'workflow' || 
            entity === 'products' || entity === 'product') {
          console.log('Refreshing orders due to WebSocket update for entity:', entity);
          this.lastUpdateTime = now;
          
          // Vynucené načtení aktuálních dat z API
          await this.fetchOrders();
          
          // Pokud máme vybranou objednávku, aktualizujeme i ji
          if (this.selectedOrder) {
            await this.getOrderById(this.selectedOrder.id);
          }
          
          // Remove this update from pending updates after processing
          this.pendingUpdates = this.pendingUpdates.filter((update) => update.updateId !== updateId);
        }
      }
    },
  },
})
