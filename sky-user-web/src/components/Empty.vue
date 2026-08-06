<template>
  <div class="empty-box">
    <img v-if="icon" :src="icon" alt="" />
    <div>{{ textLabel }}</div>
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '' },
  textLabel: { type: String, default: '暂无数据' }
})
</script>