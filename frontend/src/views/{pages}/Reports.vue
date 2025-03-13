<script setup>
import {
  SalesChart,
  OrderApprovalChart,
  ProductPurchaseChart,
  ProductSalesChart,
  DateTimePicker,
  BaseInput,
} from '@/components'
import Button from '@/components/ui/Button.vue'
import { useReportsStore } from '@/stores/reports.store'
import { onBeforeMount, ref } from 'vue'

const reportStore = useReportsStore()

const salesReport = ref(null)
const orderApprovalReports = ref(null)
const productPurchaseReports = ref(null)
const productSalesReports = ref(null)

onBeforeMount(async () => {
  try {
    const [sales, orderApproval, productPurchase, productSales] = await Promise.all([
      reportStore.fetchSalesReport(),
      reportStore.fetchOrderApprovalReports(),
      reportStore.fetchProductPurchaseReports(),
      reportStore.fetchProductSalesReports(),
    ])

    salesReport.value = sales
    orderApprovalReports.value = orderApproval
    productPurchaseReports.value = productPurchase
    productSalesReports.value = productSales
  } catch (error) {
    console.error('Chyba pri načítaní reportov:', error)
  }
})
</script>

<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-100">
      <!-- Status bar -->
      <!-- <StatusBar
        :error="errorStore.errors.general"
        :loading="loading"
        class="mb-4"
        @clear-error="errorStore.clearServerErrors()"
      /> -->

      <h2 class="text-2xl font-bold text-gray-800 mb-6">Reports</h2>

      <div class="flex items-center justify-center my-4">
        <div><DateTimePicker placeholder="Start date" /></div>
        <div><DateTimePicker placeholder="End date" /></div>
        <div><BaseInput /></div>
        <Button>Send</Button>
      </div>

      <div class="flex flex-wrap justify-center gap-10">
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Sales</p>
          <SalesChart v-if="salesReport" :data="salesReport" />
        </div>
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Most Purchased Products</p>
          <ProductPurchaseChart v-if="productPurchaseReports" :data="productPurchaseReports" />
        </div>
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Most Approval Orders</p>
          <OrderApprovalChart v-if="orderApprovalReports" :data="orderApprovalReports" />
        </div>
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Best Selling Products</p>
          <ProductSalesChart v-if="productSalesReports" :data="productSalesReports" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
