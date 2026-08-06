<template>
  <div class="phone">
    <!-- 店铺头部 -->
    <div class="rest-header">
      <div class="header-top">
        <span class="header-spacer"></span>
        <span class="header-my" @click="router.push('/my')">☰ 我的</span>
      </div>
      <div class="rest-top">
        <img class="rest-logo" :src="logoImg" alt="logo" />
        <div class="rest-info">
          <div class="rest-name">苍穹外卖</div>
          <div class="rest-meta">
            <span class="meta-item"><img :src="lenImg" />距离1.5km</span>
            <span class="meta-item"><img :src="moneyImg" />配送费6元</span>
            <span class="meta-item"><img :src="timeImg" />预计时长12min</span>
          </div>
        </div>
      </div>
      <div class="rest-desc">
        综合外送平台，定位"大众化的外送平台"，旨在为顾客打造专业的外送体验。
      </div>
      <div v-if="!shopOpen" class="closed-banner">店铺已打烊，休息一下，稍后再来～</div>
    </div>

    <!-- 分类 + 菜品 -->
    <div class="menu-wrap">
      <!-- 左侧分类 -->
      <div class="type-list">
        <div
          v-for="(item, index) in categoryList"
          :key="item.id"
          class="type-item"
          :class="{ active: typeIndex === index }"
          @click="selectCategory(item, index)"
        >
          {{ item.name }}
        </div>
      </div>

      <!-- 右侧菜品 -->
      <div v-if="dishItems.length" class="dish-list">
        <div v-for="item in dishItems" :key="item.id" class="dish-item">
          <img
            class="dish-img dish-pic"
            :src="safeImg(item.image)"
            @click="openDetail(item)"
            @error="onImgError"
            alt=""
          />
          <div class="dish-info">
            <div class="dish-name" @click="openDetail(item)">{{ item.name }}</div>
            <div class="dish-desc">{{ item.description || '暂无描述' }}</div>
            <div class="dish-sales">月销量0</div>
            <div class="dish-bottom">
              <div class="dish-price">￥{{ formatPrice(item.price) }}</div>
              <!-- 有规格：始终显示选择规格入口（含已加购数量）；无规格：直接加减 -->
              <div v-if="hasFlavor(item)" class="spec-row">
                <span v-if="item.dishNumber > 0" class="step-num spec-num">{{ item.dishNumber }}</span>
                <div class="spec-btn" @click="openFlavorPop(item)">选择规格</div>
              </div>
              <div v-else class="stepper">
                <span
                  class="step-btn"
                  @click.stop="minusFromList(item)"
                >
                  <img v-if="item.dishNumber > 0" :src="btnRed" alt="-" />
                </span>
                <span v-if="item.dishNumber > 0" class="step-num">{{ item.dishNumber }}</span>
                <span class="step-btn" @click.stop="addDirect(item)">
                  <img :src="btnAdd" alt="+" />
                </span>
              </div>
            </div>
          </div>
        </div>
        <div class="list-bottom-hint">—— 到底了 ——</div>
      </div>
      <div v-else class="no-dish">
        <Empty :text-label="categoryList.length ? '该分类下暂无菜品' : '加载中...'" />
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="footer-bar safe-bottom">
      <div class="checkout-bar">
        <div class="cart-badge" @click="toggleCart">
          <img :src="cartStore.list.length ? btnWaiterSel : btnWaiterNor" class="cart-icon" />
          <span v-if="cartStore.totalNumber" class="cart-num">{{ cartStore.totalNumber }}</span>
        </div>
        <div class="checkout-price">
          <span class="yen">￥</span>{{ formatPrice(cartStore.list.length ? cartStore.goodsAmount + deliveryFee : 0) }}
        </div>
        <button class="checkout-btn" @click="goOrder">
          {{ !shopOpen ? '店铺已打烊' : cartStore.list.length ? '去结算' : '购物车是空的' }}
        </button>
      </div>
    </div>
    <div class="cart-padding-space"></div>

    <!-- 购物车弹层 -->
    <Transition name="fade">
      <div v-if="cartStore.openCart" class="mask-box" @click.self="cartStore.openCart = false">
        <div class="cart-pop">
          <div class="cart-head">
            <span class="cart-tit">购物车</span>
            <span class="cart-clear" @click="clearCart">
              <img :src="clearImg" class="clear-icon" />清空
            </span>
          </div>
          <div v-if="cartStore.list.length" class="cart-body">
            <div v-for="item in cartStore.list" :key="item.id" class="cart-row">
              <img class="dish-img cart-pic" :src="safeImg(item.image)" @error="onImgError" alt="" />
              <div class="cart-info">
                <div class="cart-name">{{ item.name }}</div>
                <div class="cart-flavor">{{ item.dishFlavor || item.setmealId ? ' ' + (item.dishFlavor || '套餐') : '单点' }}</div>
                <div class="cart-price">￥{{ formatPrice(item.amount) }}</div>
                <div class="stepper">
                  <span class="step-btn" @click="cartStore.minus(item)"><img :src="btnRed" /></span>
                  <span class="step-num">{{ item.number }}</span>
                  <span class="step-btn" @click="cartPlusInCart(item)">
                    <img :src="btnAdd" />
                  </span>
                </div>
              </div>
            </div>
          </div>
          <Empty v-else icon="" text-label="购物车空空如也" style="padding: 40px 0" />
        </div>
      </div>
    </Transition>

    <!-- 多规格弹窗 -->
    <Transition name="fade">
      <div v-if="flavorPopOpen" class="mask-box" style="justify-content: center">
        <div class="flavor-pop">
          <div class="flavor-title">{{ flavorDish.name }}</div>
          <div class="flavor-body">
            <div v-for="group in flavorGroups" :key="group.name" class="flavor-group">
              <div class="flavor-name">{{ group.name }}</div>
              <div class="flavor-items">
                <span
                  v-for="val in group.values"
                  :key="val"
                  class="flavor-tag"
                  :class="{ active: selectedFlavors[group.name] === val }"
                  @click="selectedFlavors[group.name] = val"
                >
                  {{ val }}
                </span>
              </div>
            </div>
          </div>
          <div class="flavor-foot">
            <span class="flavor-price">￥{{ formatPrice(flavorDish.price) }}</span>
            <button class="btn-primary" @click="addWithFlavor">加入购物车</button>
          </div>
          <span class="flavor-close" @click="flavorPopOpen = false">✕</span>
        </div>
      </div>
    </Transition>

    <!-- 菜品详情弹窗 -->
    <Transition name="fade">
      <div v-if="detailOpen" class="mask-box" style="align-items: center">
        <div class="detail-pop" @click.stop>
          <img
            v-if="detailData.type !== 2"
            class="detail-pic"
            :src="safeImg(detailData.image)"
            @error="onImgError"
            alt=""
          />
          <div class="detail-body">
            <!-- 套餐详情：展示套餐包含的菜品 -->
            <template v-if="detailData.type === 2">
              <div class="detail-title">{{ detailData.name }}</div>
              <div class="meal-list">
                <div v-for="(meal, i) in dishMealData" :key="i" class="meal-row">
                  <img class="dish-img meal-pic" :src="safeImg(meal.image)" @error="onImgError" alt="" />
                  <div class="meal-info">
                    <div class="meal-name">{{ meal.name }}<span class="meal-copies"> x{{ meal.copies }}</span></div>
                    <div class="meal-desc">{{ meal.description }}</div>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="detail-title">{{ detailData.name }}</div>
              <div class="detail-desc">{{ detailData.description || '暂无描述' }}</div>
            </template>
            <div class="flavor-foot">
              <span class="flavor-price">￥{{ formatPrice(detailData.price) }}</span>
              <div v-if="detailAddCount > 0" class="stepper">
                <span class="step-btn" @click="minusDetail(detailData)"><img :src="btnRed" /></span>
                <span class="step-num">{{ detailAddCount }}</span>
                <span class="step-btn" @click="addFromDetail(detailData)"><img :src="btnAdd" /></span>
              </div>
              <button v-else class="btn-primary" @click="addFromDetail(detailData)">加入购物车</button>
            </div>
          </div>
          <span class="flavor-close" @click="detailOpen = false">✕</span>
        </div>
      </div>
    </Transition>

    <div v-if="loading" class="loading-mask">
      <img :src="loddingImg" alt="loading" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore, useCartStore } from '../store'
import { api } from '../api'
import { formatPrice } from '../utils/format'
import { toast } from '../utils/toast'
import Empty from '../components/Empty.vue'

import logoImg from '../assets/img/logo_ruiji.png'
import lenImg from '../assets/img/length.png'
import moneyImg from '../assets/img/money.png'
import timeImg from '../assets/img/time.png'
import btnAdd from '../assets/img/btn_add.png'
import btnRed from '../assets/img/btn_red.png'
import btnWaiterSel from '../assets/img/btn_waiter_sel.png'
import btnWaiterNor from '../assets/img/btn_waiter_nor.png'
import clearImg from '../assets/img/clear.png'
import loddingImg from '../assets/img/lodding.gif'
import placeholderImg from '../assets/dish-placeholder.svg'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const deliveryFee = 6 // 模拟配送费（元）
const shopOpen = computed(() => userStore.shopOpen)

const categoryList = ref([])
const typeIndex = ref(0)
const dishItems = ref([])
const loading = ref(true)

// 规格/详情弹窗
const flavorPopOpen = ref(false)
const flavorDish = ref({})
const flavorGroups = ref([])
const selectedFlavors = ref({})
const detailOpen = ref(false)
const detailData = ref({})
const dishMealData = ref([])
const detailAddCount = computed(() => {
  if (!detailData.value.id) return 0
  const d = detailData.value
  const items = cartStore.list.filter((c) =>
    Number(d.type) === 2
      ? String(c.setmealId) === String(d.id)
      : String(c.dishId) === String(d.id)
  )
  return items.reduce((s, c) => s + c.number, 0)
})

function hasFlavor(item) {
  return !!(item.flavors && item.flavors.length)
}

function safeImg(url) {
  return url || placeholderImg
}

function onImgError(e) {
  if (e.target.src !== placeholderImg) {
    e.target.src = placeholderImg
  }
}

function currentCategory() {
  return categoryList.value[typeIndex.value] || {}
}

async function loadCategories() {
  try {
    const [res1, res2] = await Promise.all([api.categoryList(1), api.categoryList(2)])
    categoryList.value = [...((res1 && res1.data) || []), ...((res2 && res2.data) || [])]
    if (categoryList.value.length) {
      await selectCategory(categoryList.value[0], 0)
    } else {
      loading.value = false
    }
  } catch (e) {
    toast(e.userMessage || '加载分类失败')
    loading.value = false
  }
}

async function selectCategory(item, index) {
  if (!item) return
  typeIndex.value = index
  loading.value = true
  try {
    let list = []
    if (Number(item.type) === 2) {
      const res = await api.setmealList(item.id)
      list = (res && res.data) || []
      list.forEach((d) => (d.type = 2))
    } else {
      const res = await api.dishList(item.id)
      list = (res && res.data) || []
      list.forEach((d) => (d.type = 1))
    }
    dishItems.value = list
    syncCartNumbers()
  } catch (e) {
    toast(e.userMessage || '加载菜品失败')
  } finally {
    loading.value = false
  }
}

// 用购物车数据回填菜品已点数量
function syncCartNumbers() {
  dishItems.value.forEach((item) => {
    const items = cartStore.list.filter(
      (c) => String(c.dishId || c.setmealId) === String(item.id)
    )
    item.dishNumber = items.reduce((s, c) => s + c.number, 0)
  })
}

// 店铺打烊时禁止加购/结算
function checkShopOpen() {
  if (!shopOpen.value) {
    toast('店铺已打烊，暂无法下单')
    return false
  }
  return true
}

// 取购物车中该菜品（同 id）的最后一个口味行，用于减号时精确匹配
function lastFlavorRow(item) {
  const rows = cartStore.list.filter((c) =>
    String(c.dishId || c.setmealId) === String(item.id)
  )
  return rows.length ? rows[rows.length - 1] : null
}

async function addDirect(item) {
  if (!checkShopOpen()) return
  try {
    await cartStore.add(item, '')
    syncCartNumbers()
  } catch (e) {
    toast(e.userMessage || (e.message && e.message.includes('401') ? '登录已过期' : '操作失败') || '操作失败')
  }
}

async function minusFromList(item) {
  if (!item.dishNumber) return
  try {
    const row = lastFlavorRow(item)
    await cartStore.minus({
      dishId: Number(item.type) === 2 ? null : item.id,
      setmealId: Number(item.type) === 2 ? item.id : null,
      dishFlavor: row ? row.dishFlavor : ''
    })
    syncCartNumbers()
  } catch (e) {
    toast('操作失败')
  }
}

// ---- 规格弹窗 ----
function openFlavorPop(item) {
  flavorDish.value = item
  flavorGroups.value = (item.flavors || []).map((f) => {
    let values = []
    try {
      values = JSON.parse(f.value)
    } catch (e) {
      values = String(f.value || '').split(',')
    }
    return { name: f.name, values: values.filter(Boolean) }
  })
  const sel = {}
  flavorGroups.value.forEach((g) => {
    if (g.values.length) sel[g.name] = g.values[0]
  })
  selectedFlavors.value = sel
  flavorPopOpen.value = true
}

async function addWithFlavor() {
  if (!checkShopOpen()) return
  const flavorText = flavorGroups.value
    .filter((g) => selectedFlavors.value[g.name])
    .map((g) => selectedFlavors.value[g.name])
    .join(',')
  if (!flavorText) {
    toast('请选择规格')
    return
  }
  try {
    const item = { ...flavorDish.value }
    await cartStore.add(item, flavorText)
    flavorPopOpen.value = false
    syncCartNumbers()
  } catch (e) {
    toast('加入购物车失败')
  }
}

// ---- 详情弹窗 ----
async function openDetail(item) {
  detailData.value = item
  if (Number(item.type) === 2) {
    try {
      const res = await api.setmealDish(item.id)
      dishMealData.value = (res && res.data) || []
    } catch (e) {
      dishMealData.value = []
    }
  } else {
    dishMealData.value = []
  }
  detailOpen.value = true
}

// 详情弹窗加入：带规格菜品先弹规格选择
async function addFromDetail(item) {
  if (Number(item.type) !== 2 && hasFlavor(item)) {
    openFlavorPop(item)
    return
  }
  if (!checkShopOpen()) return
  try {
    await cartStore.add(item, '')
    syncCartNumbers()
  } catch (e) {
    toast('加入购物车失败')
  }
}

async function minusDetail(item) {
  try {
    const row = lastFlavorRow(item)
    await cartStore.minus({
      dishId: Number(item.type) === 2 ? null : item.id,
      setmealId: Number(item.type) === 2 ? item.id : null,
      dishFlavor: row ? row.dishFlavor : ''
    })
    syncCartNumbers()
  } catch (e) {
    toast('操作失败')
  }
}

function toggleCart() {
  // 购物车为空也允许打开，让用户看到真实状态（弹层内会提示"购物车空空如也"）
  cartStore.openCart = !cartStore.openCart
}

async function clearCart() {
  try {
    await cartStore.clean()
    syncCartNumbers()
    toast('已清空购物车')
  } catch (e) {
    toast('清空失败')
  }
}

// 购物车弹层内加购（打烊时拦截）
function cartPlusInCart(item) {
  if (!checkShopOpen()) return
  cartStore.add(
    { id: item.dishId || item.setmealId, price: item.amount, image: item.image, name: item.name, type: item.setmealId ? 2 : 1 },
    item.dishFlavor
  )
}

function goOrder() {
  if (!checkShopOpen()) return
  if (!cartStore.list.length) {
    toast('请先添加菜品')
    return
  }
  router.push('/order/submit')
}

onMounted(async () => {
  try {
    await cartStore.fetch()
  } catch (e) {
    // 忽略
  }
  await loadCategories()
})
</script>

<style scoped>
.phone {
  background: #f6f6f6;
}

.rest-header {
  background: #ffffff;
  padding: 6px 14px 10px;
  flex-shrink: 0;
}

.header-top {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 24px;
}

.header-spacer {
  flex: 1;
}

.header-my {
  font-size: 13px;
  color: #666666;
  cursor: pointer;
}

.rest-top {
  display: flex;
  align-items: center;
}

.rest-logo {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  margin-right: 12px;
}

.rest-name {
  font-size: 19px;
  font-weight: 600;
}

.rest-meta {
  display: flex;
  margin-top: 6px;
  gap: 12px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  color: #999999;
}

.meta-item img {
  width: 14px;
  height: 14px;
  margin-right: 3px;
}

.rest-desc {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dotted #e5e5e5;
  font-size: 12px;
  color: #999999;
  line-height: 1.6;
}

.menu-wrap {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.type-list {
  width: 96px;
  flex-shrink: 0;
  overflow-y: auto;
  background: #f1f1f1;
}

.type-item {
  padding: 16px 6px;
  text-align: center;
  font-size: 13px;
  color: #666666;
  cursor: pointer;
  position: relative;
  line-height: 1.3;
}

.type-item.active {
  background: #ffffff;
  color: #333333;
  font-weight: 600;
}

.type-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 22px;
  background: #ffc200;
  border-radius: 0 3px 3px 0;
}

.dish-pic {
  width: 96px;
  height: 96px;
  border-radius: 8px;
  margin-right: 12px;
  flex-shrink: 0;
}

.dish-item {
  display: flex;
  background: #ffffff;
  margin: 10px 12px;
  border-radius: 12px;
  padding: 12px;
}

.dish-info {
  flex: 1;
  min-width: 0;
}

.dish-name {
  font-size: 15px;
  font-weight: 500;
  color: #333333;
}

.dish-desc {
  font-size: 12px;
  color: #999999;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dish-sales {
  font-size: 12px;
  color: #999999;
  margin-top: 4px;
}

.dish-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.dish-price {
  color: #ff6d00;
  font-size: 16px;
  font-weight: 600;
}

.spec-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.spec-num {
  min-width: 20px;
  text-align: center;
}

.spec-btn {
  border: 1px solid #ffc200;
  color: #ffb300;
  font-size: 12px;
  border-radius: 12px;
  padding: 3px 12px;
  cursor: pointer;
}

.closed-banner {
  margin-top: 10px;
  background: #fff3e0;
  color: #e65100;
  font-size: 12px;
  border-radius: 8px;
  padding: 6px 10px;
  text-align: center;
}

.no-dish {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
}

.list-bottom-hint {
  text-align: center;
  padding: 16px 0;
  color: #bbbbbb;
  font-size: 12px;
}

/* 底部结算 */
.checkout-bar {
  display: flex;
  align-items: center;
  border-radius: 40px;
  margin: 8px 10px 10px;
  background: #333333;
  padding: 8px 8px 8px 4px;
  color: #ffffff;
}

.cart-badge {
  position: relative;
  width: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.cart-icon {
  width: 44px;
  height: 44px;
}

.cart-num {
  position: absolute;
  top: -2px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  background: #ff4d4f;
  color: #fff;
  border-radius: 9px;
  font-size: 11px;
  padding: 0 4px;
}

.checkout-price {
  flex: 1;
  font-size: 17px;
  font-weight: 600;
}

.checkout-price .yen {
  font-size: 12px;
}

.checkout-btn {
  width: 100px;
  height: 40px;
  line-height: 40px;
  border: none;
  background: #ffc200;
  color: #333333;
  font-size: 15px;
  font-weight: 600;
  border-radius: 40px;
  cursor: pointer;
}

/* 购物车弹层 */
.cart-pop {
  /* 占满容器全宽：mask-box 是 column flex 且 align-items:flex-end，
     不设宽度会被收缩成贴右下角的窄条 */
  width: 100%;
  background: #ffffff;
  border-radius: 16px 16px 0 0;
  padding: 14px 14px 18px;
  max-height: 55vh;
  display: flex;
  flex-direction: column;
}

.cart-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.cart-tit {
  font-size: 16px;
  font-weight: 600;
}

.cart-clear {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  color: #999999;
  cursor: pointer;
}

.cart-clear .clear-icon {
  width: 16px;
  height: 16px;
  margin-right: 4px;
}

.cart-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.cart-row {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.cart-pic {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  margin-right: 10px;
}

.cart-info {
  flex: 1;
  min-width: 0;
}

.cart-name {
  font-size: 14px;
}

.cart-flavor {
  font-size: 11px;
  color: #999999;
  margin-top: 2px;
}

.cart-price {
  color: #ff6d00;
  font-size: 14px;
  font-weight: 600;
  margin-top: 2px;
}

/* 规格弹窗 */
.mask-box {
  align-items: flex-end;
}

.flavor-pop {
  width: 86%;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 12px;
  padding: 18px 16px;
  position: relative;
}

.flavor-title {
  font-size: 17px;
  font-weight: 600;
}

.flavor-body {
  max-height: 40vh;
  overflow-y: auto;
  margin-top: 12px;
}

.flavor-group {
  margin-bottom: 14px;
}

.flavor-name {
  font-size: 13px;
  color: #666666;
  margin-bottom: 8px;
}

.flavor-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.flavor-tag {
  border: 1px solid #e5e4e4;
  color: #333333;
  font-size: 13px;
  border-radius: 4px;
  padding: 4px 12px;
  cursor: pointer;
  background: #fafafa;
}

.flavor-tag.active {
  border-color: #ffc200;
  color: #b07d00;
  background: #fff8e1;
}

.flavor-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
}

.flavor-price {
  color: #ff6d00;
  font-size: 18px;
  font-weight: 600;
}

.flavor-close {
  position: absolute;
  top: 8px;
  right: 10px;
  color: #bbbbbb;
  font-size: 18px;
  cursor: pointer;
}

/* 详情弹窗 */
.detail-pop {
  width: 86%;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  position: relative;
}

.detail-pic {
  width: 100%;
  max-height: 260px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-title {
  font-size: 17px;
  font-weight: 600;
  margin-top: 10px;
}

.detail-desc {
  font-size: 13px;
  color: #666666;
  margin-top: 6px;
  line-height: 1.6;
}

.meal-list {
  max-height: 40vh;
  overflow-y: auto;
  margin-top: 10px;
}

.meal-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.meal-pic {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  margin-right: 10px;
}

.meal-name {
  font-size: 14px;
}

.meal-copies {
  color: #ff6d00;
}

.meal-desc {
  font-size: 12px;
  color: #999999;
  margin-top: 4px;
}

.margin-bar {
}
</style>