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

    async fetchSalesReport(startDate, endDate, orderType) {
      this.loading = true

      console.log(startDate)

      const [data, error] = await api.reports().getSalesReport({ startDate, endDate, orderType })
      this.error = error
      this.salesReport = data

      this.loading = false
    },

    async fetchOrderApprovalReports(startDate, endDate) {
      this.loading = true

      const [data, error] = await api.reports().getOrderApprovalReports({ startDate, endDate })
      this.error = error
      this.orderApprovalReports = data

      this.loading = false
    },

    async fetchProductPurchaseReports(startDate, endDate, limit) {
      this.loading = true

      const [data, error] = await api
        .reports()
        .getProductPurchaseReports({ startDate, endDate, limit })
      this.error = error
      this.productPurchaseReports = data

      this.loading = false
    },

    async fetchProductSalesReports(startDate, endDate, limit) {
      this.loading = true

      const [data, error] = await api
        .reports()
        .getProductSalesReports({ startDate, endDate, limit })
      this.error = error
      this.productSalesReports = data

      this.loading = false
    },
  },
})
