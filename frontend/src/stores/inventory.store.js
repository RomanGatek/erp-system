import { defineStore } from 'pinia'
import { api } from '@/services/api'
import { useErrorStore } from './errors'
import { useNotifier } from './notifier'

export const useInventoryStore = defineStore('inventory', {
  state: () => ({
    loading: false,
    stockOrders: [],
    selectedOrder: null
  }),

  actions: {
    async fetchStockOrders() {
      this.loading = true
      const errorStore = useErrorStore()
      try {
        const response = await api.get('/inventory/orders')
        this.stockOrders = response.data
      } catch (err) {
        errorStore.handle(err)
      } finally {
        this.loading = false
      }
    },

    async createStockOrder(orderData) {
      this.loading = true
      const errorStore = useErrorStore()
      const notifier = useNotifier()
      try {
        const response = await api.post('/inventory/orders', {
          productId: parseInt(orderData.productId), // Convert to number
          quantity: parseInt(orderData.quantity),
          expectedDeliveryDate: orderData.expectedDeliveryDate,
          supplier: orderData.supplier,
          purchasePrice: parseFloat(orderData.purchasePrice),
          notes: orderData.notes?.trim()
        })

        if (response.data) {
          notifier.success('Stock order created successfully')
          await this.fetchStockOrders()
          return response.data
        }
      } catch (err) {
        errorStore.handle(err)
        return null
      } finally {
        this.loading = false
      }
    },

    async updateStockOrderStatus(orderId, status) {
      this.loading = true
      const errorStore = useErrorStore()
      const notifier = useNotifier()
      try {
        const response = await api.put(`/inventory/orders/${orderId}/status`, { status })
        if (response.data) {
          notifier.success('Stock order status updated successfully')
          await this.fetchStockOrders()
          return response.data
        }
      } catch (err) {
        errorStore.handle(err)
        return null
      } finally {
        this.loading = false
      }
    },

    async deleteStockOrder(orderId) {
      this.loading = true
      const errorStore = useErrorStore()
      const notifier = useNotifier()
      try {
        await api.delete(`/inventory/orders/${orderId}`)
        notifier.success('Stock order deleted successfully')
        await this.fetchStockOrders()
        return true
      } catch (err) {
        errorStore.handle(err)
        return false
      } finally {
        this.loading = false
      }
    }
  }
}) 
