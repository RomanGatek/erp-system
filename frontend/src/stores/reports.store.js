import { defineStore } from 'pinia'
import api from '@/services/api'

export const useReportsStore = defineStore('reports', {
  state: () => ({
    salesReport: null,
    productSalesReports: [],
    productPurchaseReports: [],
    orderApprovalReports: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchReports() {
      this.loading = true
      const [data, error] = await api.reports().getAll()
      this.error = error

      this.loading = false
    },

    async fetchSalesReport() {
      this.loading = true

      const [data, error] = await api.reports().getSalesReport()
      this.error = error
      this.salesReport = data

      this.loading = false
    },

    async fetchOrderApprovalReports() {
      this.loading = true

      const [data, error] = await api.reports().getOrderApprovalReports()
      this.error = error
      this.orderApprovalReports = data

      this.loading = false
    },

    async fetchProductPurchaseReports() {
      this.loading = true

      const [data, error] = await api.reports().getProductPurchaseReports()
      this.error = error
      this.productPurchaseReports = data

      this.loading = false
    },

    async fetchProductSalesReports() {
      this.loading = true

      const [data, error] = await api.reports().getProductSalesReports()
      this.error = error
      this.productSalesReports = data

      this.loading = false
    },
  },
})
