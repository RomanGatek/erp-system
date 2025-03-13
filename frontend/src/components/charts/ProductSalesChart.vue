<script setup>
import { onMounted } from 'vue'

const { data } = defineProps(['data'])

onMounted(() => {
  console.log(data)
})
</script>
<template>
  <div style="width: 100%; height: 400px">
    {{ data[0] }}
  </div>
</template>
