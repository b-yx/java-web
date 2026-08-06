<template>
  <div class="phone">
    <NavBar title="收银台" :show-back="false" />

    <div class="page-body pay-body">
      <div class="pay-card">
        <div class="store-name">苍穹外卖</div>
        <div class="pay-amount">
          <span class="yen">￥</span>{{ formatPrice(amount) }}
        </div>

        <div class="divider"></div>

        <div class="pay-row">
          <span>订单编号</span>
          <span class="row-value">{{ orderNumber }}</span>
        </div>
        <div class="pay-row">
          <span>下单时间</span>
          <span class="row-value">{{ orderTime }}</span>
        </div>
        <div class="pay-row">
          <span>支付方式</span>
          <span class="row-value">微信支付</span>
        </div>
      </div>

      <div class="mock-tip">
        <p>个人主体无法使用微信支付能力，本页面为<b>模拟支付</b>。</p>
        <p>点击下方按钮将直接调用后端「模拟支付」接口，把订单置为已支付（待接单）。</p>
      </div>

      <button class="pay-btn" :disabled="paying" @click="doPay">
        {{ paying ? '支付中...' : '确认支付（模拟）' }}
      </button>
      <button class="cancel-btn" @click="cancel">暂不支付，返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import { useOrderStore } from '../store'
import { api } from '../api'
import { formatPrice, formatDateTime } from '../utils/format'
import { toast } from '../utils/toast'

const router = useRouter()
const orderStore = useOrderStore()

const paying = ref(false)

const order = computed(() => orderStore.lastOrder || {})
const amount = computed(() => Number(order.value.orderAmount || 0))
const orderNumber = computed(() => order.value.orderNumber || '')
const orderTime = computed(() =>
  order.value.orderTime ? formatDateTime(order.value.orderTime) : ''
)

async function doPay() {
  if (!orderNumber.value) {
    toast('订单信息缺失')
    router.replace('/')
    return
  }
  if (paying.value) return
  paying.value = true
  try {
    await api.orderMockPay(orderNumber.value)
    toast('支付成功')
    router.replace('/order/success')
  } catch (e) {
    // 订单已处于支付后状态（后端返回"订单状态错误"）时也视为支付成功
    if (e.userMessage && e.userMessage.includes('订单状态错误')) {
      router.replace('/order/success')
    } else {
      toast(e.userMessage || e.message || '支付失败')
    }
  } finally {
    paying.value = false
  }
}

function cancel() {
  router.replace('/')
}

onMounted(() => {
  if (!orderNumber.value) {
    toast('没有待支付的订单')
    router.replace('/')
  }
})
</script>

<style scoped>
.pay-body {
  background: #f6f6f6;
  padding-top: 20px;
}

.pay-card {
  background: #ffffff;
  border-radius: 12px;
  margin: 0 12px;
  padding: 24px 16px;
  text-align: center;
}

.store-name {
  font-size: 14px;
  color: #666666;
}

.pay-amount {
  font-size: 32px;
  font-weight: 700;
  color: #333333;
  margin-top: 10px;
}

.pay-amount .yen {
  font-size: 18px;
}

.divider {
  height: 1px;
  background: #f0f0f0;
  margin: 20px 0 6px;
}

.pay-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #666666;
  padding: 8px 0;
}

.pay-row .row-value {
  color: #333333;
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mock-tip {
  margin: 16px 12px 0;
  font-size: 12px;
  color: #999999;
  line-height: 1.7;
}

.mock-tip b {
  color: #666666;
}

.pay-btn {
  display: block;
  width: calc(100% - 24px);
  margin: 28px 12px 0;
  height: 48px;
  line-height: 48px;
  border: none;
  border-radius: 24px;
  background: #07c160;
  color: #ffffff;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 2px;
  cursor: pointer;
}

.pay-btn[disabled] {
  opacity: 0.6;
}

.cancel-btn {
  display: block;
  width: calc(100% - 24px);
  margin: 14px 12px 24px;
  height: 46px;
  line-height: 46px;
  border: none;
  background: transparent;
  color: #666666;
  font-size: 14px;
  cursor: pointer;
}
</style>