<template>
  <div class="phone">
    <NavBar title="提交成功" :show-back="false" />

    <div class="page-body success-body">
      <img class="success-icon" :src="successImg" alt="success" />
      <div class="success-title">支付成功</div>
      <div class="success-sub">预计{{ arrivalTime() }}送达</div>
      <div class="success-desc">后厨已开始疯狂备餐中，请耐心等待~</div>

      <div class="success-btns">
        <button class="btn-plain btn-common" @click="goIndex">返回首页</button>
        <button class="btn-primary btn-common" @click="goOrders">查看订单</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import { arrivalTime } from '../utils/format'
import successImg from '../assets/img/success.png'

const router = useRouter()

function goIndex() {
  router.replace('/')
}

function goOrders() {
  router.replace('/order/history')
}
</script>

<style scoped>
.success-body {
  background: #ffffff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.success-icon {
  width: 140px;
  height: 134px;
}

.success-title {
  font-size: 24px;
  font-weight: 600;
  color: #333333;
  margin-top: 8px;
}

.success-sub {
  font-size: 16px;
  color: #666666;
  margin-top: 16px;
}

.success-desc {
  font-size: 13px;
  color: #999999;
  margin-top: 8px;
}

.success-btns {
  display: flex;
  margin-top: 48px;
  gap: 20px;
}

.btn-common {
  width: 120px;
  height: 42px;
  line-height: 42px;
}
</style>