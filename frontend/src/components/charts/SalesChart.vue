<script setup>
import { onMounted } from 'vue'

const { data } = defineProps(['data'])
onMounted(() => {
  console.log(data)
})
</script>
<template>
  <div style="width: 100%; height: 400px">
    <ul>
      <li>totalSales: {{ data.totalSales }}</li>
      <li>orderCount: {{ data.orderCount }}</li>
      <li>averageOrderValue: {{ data.averageOrderValue }}</li>
      <li>dailyAverageSales: {{ data.dailyAverageSales }}</li>
    </ul>
  </div>
</template>
