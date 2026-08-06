<template>
  <div class="phone">
    <NavBar title="提交订单" />

    <div class="page-body">
      <!-- 收货地址 -->
      <div class="address-card" @click="goAddress">
        <div v-if="!defaultAddress.id" class="address-empty">
          请选择收货地址
        </div>
        <div v-else class="address-filled">
          <div class="address-line">{{ defaultAddressText }}</div>
          <div class="address-person">
            <span class="address-name">{{ defaultAddress.consignee }}</span>
            <span class="address-phone">{{ maskPhone(defaultAddress.phone) }}</span>
          </div>
        </div>
        <span class="arrow">›</span>
      </div>
      <div class="deliver-hint">预计{{ arrivalTime() }}送达</div>

      <!-- 订单明细 -->
      <div class="card">
        <div class="card-title">订单明细</div>
        <div v-if="cartStore.list.length" class="order-items">
          <div v-for="item in cartStore.list" :key="item.id" class="order-item">
            <img class="dish-img item-pic" :src="safeImg(item.image)" @error="onImgError" alt="" />
            <div class="item-info">
              <div class="item-name">
                {{ item.name }}
                <span v-if="item.dishFlavor" class="item-flavor">{{ item.dishFlavor }}</span>
              </div>
              <div class="item-sub">×{{ item.number }}</div>
            </div>
            <div class="item-price">￥{{ formatPrice(item.number * item.amount) }}</div>
          </div>
        </div>
        <div v-else>
          <Empty text-label="购物车是空的，无法下单" />
        </div>
      </div>

      <!-- 备注 -->
      <div class="card remark-card">
        <div class="card-title">备注</div>
        <textarea
          v-model="remark"
          class="remark-input"
          maxlength="50"
          placeholder="请输入您需要备注的信息（限50字）"
        ></textarea>
      </div>
    </div>

    <!-- 底部结算 -->
    <div class="footer-bar safe-bottom">
      <div class="order-foot">
        <div class="foot-left">
          <div class="foot-items">共{{ totalNumber }}件商品</div>
          <div class="foot-price">
            ￥{{ formatPrice(totalPrice) }}
            <span class="foot-delivery">（含配送费￥{{ deliveryFee }}）</span>
          </div>
        </div>
        <button class="foot-btn" :disabled="submitting" @click="submitOrder">
          {{ submitting ? '提交中...' : '去支付' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import Empty from '../components/Empty.vue'
import { useCartStore, useOrderStore, useUserStore } from '../store'
import { api } from '../api'
import { formatPrice, maskPhone, arrivalTime, pad } from '../utils/format'
import { toast } from '../utils/toast'
import placeholderImg from '../assets/dish-placeholder.svg'

const router = useRouter()
const cartStore = useCartStore()
const orderStore = useOrderStore()
const userStore = useUserStore()

const deliveryFee = 6
const remark = ref('')
const submitting = ref(false)
const defaultAddress = ref({})

const totalCount = computed(() => cartStore.totalNumber)
const goodsAmount = computed(() => cartStore.goodsAmount)
const totalPrice = computed(() =>
  cartStore.list.length ? goodsAmount.value + deliveryFee : 0
)
const defaultAddressText = computed(() => {
  const a = defaultAddress.value
  if (!a.id) return ''
  return (a.provinceName || '') + (a.cityName || '') + (a.districtName || '') + (a.detail || '')
})

function safeImg(u) {
  return u || placeholderImg
}
function onImgError(e) {
  if (e.target.src !== placeholderImg) e.target.src = placeholderImg
}

async function loadAddress() {
  // 优先使用从地址管理页选择回来的地址
  if (orderStore.selectedAddress && orderStore.selectedAddress.id) {
    defaultAddress.value = orderStore.selectedAddress
    return
  }
  try {
    const res = await api.addressGetDefault()
    defaultAddress.value = (res && res.data) || {}
    return
  } catch (e) {
    // 没有默认地址：取地址列表第一条作为当前地址，方便快速下单
  }
  try {
    const res = await api.addressList()
    const list = (res && res.data) || []
    if (list.length) {
      defaultAddress.value = list[0]
    }
  } catch (e) {
    defaultAddress.value = {}
  }
}

function goAddress() {
  orderStore.addressBackUrl = '/order/submit'
  router.push('/address/list')
}

function estimatedTime() {
  const d = new Date()
  d.setTime(d.getTime() + 3600000)
  return (
    d.getFullYear() +
    '-' +
    pad(d.getMonth() + 1) +
    '-' +
    pad(d.getDate()) +
    ' ' +
    pad(d.getHours()) +
    ':' +
    pad(d.getMinutes()) +
    ':00'
  )
}

async function submitOrder() {
  if (!cartStore.list.length) {
    toast('购物车是空的，无法下单')
    return
  }
  // 提交前重新获取店铺营业状态（防止页面停留期间状态变化）
  await userStore.refreshInfo()
  if (!userStore.shopOpen) {
    toast('店铺已打烊，暂无法下单')
    return
  }
  if (!defaultAddress.value.id) {
    toast('请选择收货地址')
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    const payload = {
      addressBookId: defaultAddress.value.id,
      payMethod: 1,
      remark: remark.value,
      estimatedDeliveryTime: estimatedTime(),
      deliveryStatus: 1,
      tablewareNumber: 0,
      tablewareStatus: 0,
      packAmount: 0,
      amount: Number(totalPrice.value.toFixed(2))
    }
    const res = await api.orderSubmit(payload)
    const data = res.data
    orderStore.setLastOrder({
      id: data.id,
      orderNumber: data.orderNumber,
      orderAmount: data.orderAmount,
      orderTime: data.orderTime
    })
    // 本次选中的地址已消费，清空避免下次下单误用
    orderStore.selectedAddress = null
    cartStore.fetch()
    router.push('/order/pay')
  } catch (e) {
    toast(e.userMessage || e.message || '下单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    await cartStore.fetch()
  } catch (e) {
    // ignore
  }
  await loadAddress()
})
</script>

<style scoped>
.address-card {
  background: #ffffff;
  margin: 10px 12px;
  border-radius: 12px;
  padding: 16px 14px;
  position: relative;
  cursor: pointer;
}

.address-empty {
  color: #999999;
  font-size: 15px;
}

.address-filled {
  padding-right: 18px;
}

.address-line {
  font-size: 15px;
  color: #333333;
  line-height: 1.5;
}

.address-person {
  margin-top: 8px;
  font-size: 13px;
  color: #666666;
}

.address-phone {
  margin-left: 12px;
}

.arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  color: #bbbbbb;
}

.deliver-hint {
  padding: 0 14px 6px;
  font-size: 12px;
  color: #999999;
}

.card {
  padding: 14px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 8px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-pic {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  margin-right: 12px;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
}

.item-flavor {
  font-size: 11px;
  color: #999999;
  margin-left: 6px;
}

.item-sub {
  font-size: 13px;
  color: #999999;
  margin-top: 4px;
}

.item-price {
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

.remark-input {
  width: 100%;
  height: 72px;
  border: none;
  outline: none;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  color: #333333;
  background: transparent;
}

.order-foot {
  display: flex;
  align-items: center;
  padding: 10px 12px 12px;
}

.foot-left {
  flex: 1;
}

.foot-items {
  font-size: 13px;
  color: #666666;
}

.foot-price {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
  margin-top: 2px;
}

.foot-delivery {
  font-size: 11px;
  color: #999999;
  font-weight: 400;
}

.foot-btn {
  width: 120px;
  height: 44px;
  line-height: 44px;
  border: none;
  border-radius: 22px;
  background: #ffc200;
  color: #333333;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.foot-btn[disabled] {
  opacity: 0.6;
}
</style>