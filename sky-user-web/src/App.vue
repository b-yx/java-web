<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from './store'

const userStore = useUserStore()

onMounted(() => {
  // 启动时若有 token 则刷新一次用户信息（登录态保持）
  if (userStore.token) {
    userStore.refreshInfo()
  }
})
</script>