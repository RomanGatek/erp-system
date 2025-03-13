<script setup>
const { data } = defineProps(['data'])
</script>
<template>
  <div style="width: 100%; height: 400px">
    <p>{{ data[0] }}</p>
  </div>
</template>
