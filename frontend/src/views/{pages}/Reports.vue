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

const asyncHook = ref(null)

const startDate = ref(null)
const endDate = ref(null)
const limit = ref(10)
const isSale = ref(true)

onBeforeMount(async () => {
  startDate.value = `${new Date().getFullYear()}-01-01`
  endDate.value = formatDate(new Date().toISOString())

  fetchData()
})

const fetchData = async () => {
  try {
    asyncHook.value = async () =>
      await Promise.all([
        reportStore.fetchSalesReport(
          formatDate(startDate.value),
          formatDate(endDate.value),
          isSale.value ? 'sell' : 'purchase',
        ),
        reportStore.fetchOrderApprovalReports(
          formatDate(startDate.value),
          formatDate(endDate.value),
        ),
        reportStore.fetchProductPurchaseReports(
          formatDate(startDate.value),
          formatDate(endDate.value, limit.value),
        ),
        reportStore.fetchProductSalesReports(
          formatDate(startDate.value),
          formatDate(endDate.value, limit.value),
        ),
      ])
  } catch (error) {
    console.error('Chyba pri načítaní reportov:', error)
  } finally {
    asyncHook.value()
  }
}

function formatDate(date) {
  return date.split('T')[0]
}
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

      <div class="flex items-end gap-4 justify-center my-4">
        <div>
          <DateTimePicker
            label="Start date"
            placeholder="Start date"
            v-model="startDate"
            :min-date="new Date('1970-01-01')"
          />
        </div>
        <div>
          <DateTimePicker
            label="End date"
            placeholder="End date"
            v-model="endDate"
            :min-date="new Date('1970-01-01')"
          />
        </div>
        <div>
          <BaseInput label="Limit" class="w-16" placeholder="Limit" v-model="limit" />
        </div>
        <label class="pb-3 inline-flex items-center cursor-pointer">
          <span
            class="mr-2 text-sm font-medium"
            :class="isSale ? 'text-gray-400' : 'text-green-500'"
            >Purchase</span
          >
          <input type="checkbox" class="sr-only peer" v-model="isSale" />
          <div
            class="relative w-10 h-5 bg-gray-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:start-[4px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-blue-600"
          ></div>
          <span
            class="ms-2 text-sm font-medium"
            :class="isSale ? 'text-green-500' : 'text-gray-400'"
            >Sell</span
          >
        </label>
        <Button @click.prevent="fetchData">Send</Button>
      </div>

      <div class="flex flex-wrap justify-center gap-10">
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Sales</p>
          <SalesChart v-if="reportStore.salesReport" :data="reportStore.salesReport" />
        </div>
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Most Purchased Products</p>
          <ProductPurchaseChart
            v-if="reportStore.productPurchaseReports"
            :data="reportStore.productPurchaseReports"
          />
        </div>
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Most Approval Orders</p>
          <OrderApprovalChart
            v-if="reportStore.orderApprovalReports"
            :data="reportStore.orderApprovalReports"
          />
        </div>
        <div class="w-xl rounded-lg border border-gray-100">
          <p class="bg-gray-100 px-2.5 py-1">Best Selling Products</p>
          <ProductSalesChart
            v-if="reportStore.productSalesReports"
            :data="reportStore.productSalesReports"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
