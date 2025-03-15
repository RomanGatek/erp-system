<template>
  <div class="min-h-screen bg-gray-50/50 backdrop-blur-sm py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-400 bg-clip-text text-transparent">
          Dashboard Overview
        </h1>
        <p class="mt-2 text-sm text-gray-600">Monitor your business performance</p>
      </div>

      <!-- Date Range Filter -->
      <div class="mb-6">
        <div class="flex items-center gap-4">
          <div class="relative">
            <input type="date" v-model="startDate"
              class="px-4 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          <span class="text-gray-500">to</span>
          <div class="relative">
            <input type="date" v-model="endDate"
              class="px-4 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
          </div>
          <button @click="fetchData"
            class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors">
            Apply Filter
          </button>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Total Products -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Total Products</p>
              <p class="text-2xl font-bold text-gray-900">{{ stats.totalProducts }}</p>
            </div>
            <div class="p-3 bg-blue-50 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-500" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
              </svg>
            </div>
          </div>
          <div class="mt-4 flex items-center">
            <span class="text-green-500 text-sm font-medium">+{{ stats.productGrowth }}%</span>
            <span class="ml-2 text-sm text-gray-500">from last month</span>
          </div>
        </div>

        <!-- Total Orders -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Total Orders</p>
              <p class="text-2xl font-bold text-gray-900">{{ stats.totalOrders }}</p>
            </div>
            <div class="p-3 bg-purple-50 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-purple-500" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </div>
          </div>
          <div class="mt-4 flex items-center">
            <span class="text-green-500 text-sm font-medium">+{{ stats.orderGrowth }}%</span>
            <span class="ml-2 text-sm text-gray-500">from last month</span>
          </div>
        </div>

        <!-- Total Revenue -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Total Revenue</p>
              <p class="text-2xl font-bold text-gray-900">{{ formatCurrency(stats.totalRevenue) }}</p>
            </div>
            <div class="p-3 bg-green-50 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
          <div class="mt-4 flex items-center">
            <span class="text-green-500 text-sm font-medium">+{{ stats.revenueGrowth }}%</span>
            <span class="ml-2 text-sm text-gray-500">from last month</span>
          </div>
        </div>

        <!-- Average Order Value -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-600">Avg. Order Value</p>
              <p class="text-2xl font-bold text-gray-900">{{ formatCurrency(stats.averageOrderValue) }}</p>
            </div>
            <div class="p-3 bg-orange-50 rounded-xl">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-orange-500" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
          </div>
          <div class="mt-4 flex items-center">
            <span class="text-green-500 text-sm font-medium">+{{ stats.avgOrderGrowth }}%</span>
            <span class="ml-2 text-sm text-gray-500">from last month</span>
          </div>
        </div>
      </div>

      <!-- Charts Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Sales Trend Chart -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Sales Trend</h3>
          <canvas ref="salesChart"></canvas>
        </div>

        <!-- Product Performance Chart -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Product Performance</h3>
          <canvas ref="productChart"></canvas>
        </div>

        <!-- Order Status Chart -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Order Status Distribution</h3>
          <canvas ref="orderStatusChart"></canvas>
        </div>

        <!-- Top Products Table -->
        <div class="bg-white rounded-2xl p-6 shadow-lg ring-1 ring-gray-100/50">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Top Products</h3>
          <div class="overflow-x-auto">
            <table class="min-w-full">
              <thead>
                <tr>
                  <th class="px-4 py-2 text-left text-sm font-medium text-gray-500">Product</th>
                  <th class="px-4 py-2 text-left text-sm font-medium text-gray-500">Sales</th>
                  <th class="px-4 py-2 text-left text-sm font-medium text-gray-500">Revenue</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="product in topProducts" :key="product.id" class="border-t border-gray-100">
                  <td class="px-4 py-3 text-sm text-gray-900">{{ product.name }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ product.sales }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ formatCurrency(product.revenue) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Chart from 'chart.js/auto'

// Refs for chart canvases
const salesChart = ref(null)
const productChart = ref(null)
const orderStatusChart = ref(null)

// Date range filter
const startDate = ref(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0])
const endDate = ref(new Date().toISOString().split('T')[0])

// Mock data - replace with actual API calls
const stats = ref({
  totalProducts: 1248,
  productGrowth: 12.5,
  totalOrders: 856,
  orderGrowth: 8.3,
  totalRevenue: 2456789,
  revenueGrowth: 15.7,
  averageOrderValue: 2867,
  avgOrderGrowth: 5.2
})

const topProducts = ref([
  { id: 1, name: 'Product A', sales: 234, revenue: 45678 },
  { id: 2, name: 'Product B', sales: 189, revenue: 34567 },
  { id: 3, name: 'Product C', sales: 156, revenue: 23456 },
  { id: 4, name: 'Product D', sales: 145, revenue: 21234 },
  { id: 5, name: 'Product E', sales: 123, revenue: 19876 }
])

// Format currency
const formatCurrency = (value) => {
  return new Intl.NumberFormat('cs-CZ', {
    style: 'currency',
    currency: 'CZK'
  }).format(value)
}

// Initialize charts
const initCharts = () => {
  // Sales Trend Chart
  new Chart(salesChart.value, {
    type: 'line',
    data: {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
      datasets: [{
        label: 'Sales',
        data: [30, 45, 35, 50, 40, 60],
        borderColor: '#3B82F6',
        tension: 0.4
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          display: false
        }
      }
    }
  })

  // Product Performance Chart
  new Chart(productChart.value, {
    type: 'bar',
    data: {
      labels: ['Product A', 'Product B', 'Product C', 'Product D', 'Product E'],
      datasets: [{
        label: 'Sales',
        data: [234, 189, 156, 145, 123],
        backgroundColor: '#93C5FD'
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          display: false
        }
      }
    }
  })

  // Order Status Chart
  new Chart(orderStatusChart.value, {
    type: 'doughnut',
    data: {
      labels: ['Completed', 'Pending', 'Cancelled'],
      datasets: [{
        data: [65, 25, 10],
        backgroundColor: ['#10B981', '#F59E0B', '#EF4444']
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    }
  })
}

// Fetch data based on date range
const fetchData = async () => {
  // Implement API calls here
  console.log('Fetching data for range:', startDate.value, 'to', endDate.value)
}

// Initialize charts on component mount
onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 300px;
}
</style>