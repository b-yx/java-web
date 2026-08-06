<template>
  <div class="phone">
    <NavBar title="历史订单" />

    <div class="page-body">
      <!-- 状态筛选 -->
      <div class="status-tabs">
        <span
          v-for="(t, i) in tabs"
          :key="i"
          class="tab-item"
          :class="{ active: activeTab === i }"
          @click="switchTab(i)"
        >{{ t.label }}</span>
      </div>

      <div v-if="orders.length" class="order-list">
        <div v-for="item in orders" :key="item.id" class="order-card">
          <div class="order-head">
            <span class="order-time">{{ formatDateTime(item.orderTime) }}</span>
            <span class="order-status" :style="{ color: orderStatusColor(item.status) }">
              {{ orderStatusText(item.status) }}
            </span>
          </div>
          <div class="order-items">
            <div v-for="d in item.orderDetailList" :key="d.name" class="order-item">
              <span class="item-name">{{ d.name }}{{ d.dishFlavor ? '（' + d.dishFlavor + '）' : '' }}</span>
              <span class="item-num">x{{ d.number }}</span>
            </div>
            <div class="order-sum">
              共{{ summary(item.orderDetailList).count }}件商品，实付
              <span class="sum-price">￥{{ formatPrice(summary(item.orderDetailList).amount) }}</span>
            </div>
            <div class="order-actions">
              <button v-if="Number(item.status) === 1" class="btn-plain act-btn" @click="goPay(item)">去支付</button>
              <button v-if="Number(item.status) <= 2" class="btn-plain act-btn" @click="cancelOrder(item)">取消订单</button>
              <button v-if="Number(item.status) >= 2 && Number(item.status) <= 4" class="btn-plain act-btn" @click="remind(item)">催单</button>
              <button v-if="Number(item.status) === 5" class="btn-plain act-btn" @click="again(item.id)">再来一单</button>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div class="load-more">
          <span v-if="!finished" class="load-text" @click="loadMore">加载更多</span>
          <span v-else class="load-text">—— 已经到底啦 ——</span>
        </div>
      </div>

      <Empty v-else-if="!loading" icon="" text-label="暂无历史订单" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import Empty from '../components/Empty.vue'
import { useCartStore, useOrderStore } from '../store'
import { api } from '../api'
import { formatPrice, formatDateTime, orderStatusText, orderStatusColor, sumOrder } from '../utils/format'
import { toast } from '../utils/toast'

const router = useRouter()
const cartStore = useCartStore()
const orderStore = useOrderStore()

// 状态筛选：进行中 = 待接单(2) + 已接单(3) + 派送中(4)
const tabs = [
  { label: '全部', status: null },
  { label: '待付款', status: 1 },
  { label: '进行中', status: [2, 3, 4] },
  { label: '已完成', status: 5 },
  { label: '已取消', status: 6 }
]
const activeTab = ref(0)

const orders = ref([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const finished = ref(false)
const loading = ref(true)

function summary(list) {
  const s = sumOrder(list)
  return { count: s.count, amount: s.amount }
}

async function getOrders(reset = false) {
  if (reset) {
    page.value = 1
    orders.value = []
    finished.value = false
  }
  loading.value = true
  try {
    const tab = tabs[activeTab.value]
    const statuses = Array.isArray(tab.status) ? tab.status : [tab.status]
    let records = []
    let count = 0
    for (const st of statuses) {
      const res = await api.orderHistory(page.value, pageSize, st)
      const data = res.data || {}
      records = records.concat(data.records || [])
      count += Number(data.total || 0)
    }
    for (const rec of records) {
      if (!rec.orderDetailList || !rec.orderDetailList.length) {
        try {
          const detail = await api.orderDetail(rec.id)
          rec.orderDetailList = (detail.data && detail.data.orderDetailList) || []
        } catch (e) {
          rec.orderDetailList = []
        }
      }
    }
    orders.value = [...orders.value, ...records]
    total.value = count
    finished.value = orders.value.length >= total.value
  } catch (e) {
    toast(e.userMessage || '加载订单失败')
  } finally {
    loading.value = false
  }
}

function switchTab(i) {
  if (i === activeTab.value) return
  activeTab.value = i
  getOrders(true)
}

async function loadMore() {
  if (finished.value || loading.value) return
  page.value += 1
  getOrders()
}

// 再来一单：先调接口成功，再刷新购物车（失败时原购物车不受影响）
async function again(id) {
  try {
    await api.orderAgain(id)
    await cartStore.fetch()
    toast('已加入购物车')
    router.replace('/')
  } catch (e) {
    toast('再来一单失败')
  }
}

async function cancelOrder(item) {
  try {
    await api.orderCancel(item.id)
    toast('订单已取消')
    getOrders(true)
  } catch (e) {
    toast(e.userMessage || '取消失败')
  }
}

// 待付款订单去支付：复用收银台页面
async function goPay(item) {
  try {
    const detail = await api.orderDetail(item.id)
    const d = detail.data
    orderStore.setLastOrder({
      id: d.id,
      orderNumber: d.number,
      orderAmount: d.amount,
      orderTime: d.orderTime
    })
    router.push('/order/pay')
  } catch (e) {
    toast('获取订单详情失败')
  }
}

async function remind(item) {
  try {
    await api.orderReminder(item.id)
    toast('已催单，商家将尽快处理')
  } catch (e) {
    toast(e.userMessage || '催单失败')
  }
}

onMounted(() => {
  getOrders()
})
</script>

<style scoped>
.status-tabs {
  display: flex;
  background: #ffffff;
  padding: 8px 6px;
  gap: 4px;
  position: sticky;
  top: 0;
  z-index: 5;
}

.tab-item {
  flex: 1;
  text-align: center;
  font-size: 13px;
  color: #666666;
  padding: 6px 0;
  border-radius: 16px;
  cursor: pointer;
}

.tab-item.active {
  background: #fff8e1;
  color: #b07d00;
  font-weight: 600;
}

.order-list {
  padding-bottom: 30px;
}

.order-card {
  background: #ffffff;
  border-radius: 12px;
  margin: 10px 12px;
  padding: 12px 14px;
}

.order-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 38px;
  border-bottom: 1px dashed #efefef;
}

.order-time {
  font-size: 13px;
  color: #333333;
}

.order-status {
  font-size: 13px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666666;
  padding-top: 10px;
}

.order-name {
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-num {
  color: #999999;
}

.order-sum {
  text-align: right;
  font-size: 13px;
  color: #666666;
  padding-top: 12px;
}

.sum-price {
  font-weight: 600;
  color: #333333;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 10px;
}

.act-btn {
  width: 100px;
  height: 32px;
  line-height: 30px;
  font-size: 13px;
}

.load-more {
  text-align: center;
  padding: 16px 0 24px;
}

.load-text {
  font-size: 13px;
  color: #bbbbbb;
  cursor: pointer;
}
</style>