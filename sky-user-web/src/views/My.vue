<template>
  <div class="phone">
    <!-- 个人头部 -->
    <div class="my-header">
      <span class="my-back" @click="goHome">‹</span>
      <div class="my-info">
        <img class="avatar" :src="avatar" alt="avatar" />
        <div class="my-text">
          <div class="my-name">
            {{ userStore.displayName }}
            <img class="sex-icon" :src="boyImg" alt="" />
          </div>
          <div class="my-phone">{{ maskPhone(userStore.userInfo.phone) || '演示账号' }}</div>
        </div>
      </div>
    </div>

    <div class="page-body my-body">
      <!-- 地址 / 订单 -->
      <div class="card menu-card">
        <div class="menu-row" @click="goAddress">
          <img class="menu-icon" :src="addressImg" alt="" />
          <span class="menu-text">地址管理</span>
          <span class="menu-arrow">›</span>
        </div>
        <div class="menu-row" @click="$router.push('/order/history')">
          <img class="menu-icon" :src="orderImg" alt="" />
          <span class="menu-text">历史订单</span>
          <span class="menu-arrow">›</span>
        </div>
      </div>

      <!-- 最近订单 -->
      <div v-if="recentOrder" class="card recent-card">
        <div class="recent-title">最近订单</div>
        <div class="recent-row">
          <div class="recent-time">{{ formatDateTime(recentOrder.orderTime) }}</div>
          <div class="recent-status" :style="{ color: orderStatusColor(recentOrder.status) }">
            {{ orderStatusText(recentOrder.status) }}
          </div>
        </div>
        <div class="recent-items">
          <div v-for="d in recentOrder.orderDetailList" :key="d.name" class="recent-item">
            <span class="recent-name">{{ d.name }}</span><span class="recent-num">x{{ d.number }}</span>
          </div>
        </div>
        <div class="recent-sum">
          共{{ summary.count }}件商品，实付
          <span class="recent-amount">￥{{ formatPrice(summary.amount) }}</span>
        </div>
        <div v-if="Number(recentOrder.status) === 5" class="recent-actions">
          <button class="btn-plain again-btn" @click="orderAgain(recentOrder.id)">再来一单</button>
        </div>
      </div>
      <Empty v-else icon="" text-label="暂无订单，快去点餐吧" />

      <div class="logout-btn" @click="logout">退出登录</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Empty from '../components/Empty.vue'
import { useUserStore, useCartStore, useOrderStore } from '../store'
import { api } from '../api'
import { maskPhone, formatPrice, formatDateTime, orderStatusText, orderStatusColor, sumOrder } from '../utils/format'
import { toast } from '../utils/toast'
import boyImg from '../assets/img/boy.png'
import addressImg from '../assets/img/address.png'
import orderImg from '../assets/img/order.png'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const orderStore = useOrderStore()

// 从"我的"进入地址管理：不处于选择模式，返回地址列表而非订单页
function goAddress() {
  orderStore.addressBackUrl = '/my'
  router.push('/address/list')
}

// 返回点餐首页
function goHome() {
  router.replace('/')
}

const recentOrder = ref(null)
const summary = computed(() => {
  const list = (recentOrder.value && recentOrder.value.orderDetailList) || []
  const s = sumOrder(list)
  return { count: s.count, amount: s.amount }
})
const avatar = computed(() => userStore.userInfo.avatar || boyImg)

async function loadRecent() {
  try {
    const res = await api.orderHistory(1, 1, null)
    const records = (res.data && res.data.records) || []
    if (records.length) {
      recentOrder.value = records[0]
      // 近订单列表不带明细时补充
      if (!recentOrder.value.orderDetailList) {
        const detail = await api.orderDetail(records[0].id)
        recentOrder.value.orderDetailList = (detail.data && detail.data.orderDetailList) || []
      }
    }
  } catch (e) {
    recentOrder.value = null
  }
}

// 再来一单：先调接口成功，再刷新购物车（失败时原购物车不受影响）
async function orderAgain(id) {
  try {
    await api.orderAgain(id)
    await cartStore.fetch()
    toast('已加入购物车')
    router.replace('/')
  } catch (e) {
    toast('再来一单失败')
  }
}

function logout() {
  userStore.clear()
  cartStore.list = []
  router.replace('/login')
}

onMounted(() => {
  loadRecent()
})
</script>

<style scoped>
.my-header {
  background: #ffc200;
  padding: 36px 20px 30px;
  flex-shrink: 0;
  position: relative;
}

.my-back {
  position: absolute;
  left: 8px;
  top: 14px;
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5b4a11;
  font-size: 26px;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  line-height: 1;
  padding-bottom: 3px;
}

.my-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #ffffff;
  border: 2px solid rgba(255, 255, 255, 0.6);
  object-fit: cover;
  margin-right: 14px;
}

.my-name {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
  display: flex;
  align-items: center;
}

.my-icon-img {
  width: 18px;
  height: 18px;
  margin-left: 6px;
}

.my-phone {
  font-size: 13px;
  color: #5b4a11;
  margin-top: 6px;
}

.my-body {
  padding-bottom: 30px;
}

.menu-card {
  padding: 0 14px;
}

.menu-row {
  display: flex;
  align-items: center;
  height: 54px;
  cursor: pointer;
}

.menu-row + .menu-row {
  border-top: 1px dashed #ececec;
}

.menu-icon {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.menu-text {
  flex: 1;
  font-size: 15px;
}

.menu-arrow {
  color: #cccccc;
  font-size: 20px;
}

.recent-card {
  padding: 14px;
}

.recent-title {
  font-size: 15px;
  font-weight: 600;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.recent-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.recent-time {
  font-size: 13px;
  color: #333333;
}

.recent-status {
  font-size: 13px;
}

.recent-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666666;
  padding: 5px 0;
}

.recent-num {
  color: #999999;
}

.recent-sum {
  text-align: right;
  font-size: 13px;
  color: #666666;
  padding-top: 10px;
}

.recent-amount {
  font-weight: 600;
  color: #333333;
}

.recent-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
}

.again-btn {
  width: 108px;
}

.logout-btn {
  text-align: center;
  font-size: 14px;
  color: #999999;
  padding: 24px 0 8px;
  cursor: pointer;
}
</style>