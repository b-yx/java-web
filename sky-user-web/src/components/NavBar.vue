<template>
  <div class="nav-bar">
    <div v-if="showBack" class="nav-back" @click="goBack">
      <span>‹</span>
    </div>
    <div class="nav-title">{{ title }}</div>
    <div v-if="rightText" class="nav-right" @click="$emit('right')">{{ rightText }}</div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  title: { type: String, default: '' },
  showBack: { type: Boolean, default: true },
  rightText: { type: String, default: '' }
})

const emit = defineEmits(['right'])
const router = useRouter()

function goBack() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}
</script>