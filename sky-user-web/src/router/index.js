import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '../store'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'index',
    component: () => import('../views/Index.vue'),
    meta: { title: '点餐' }
  },
  {
    path: '/order/submit',
    name: 'orderSubmit',
    component: () => import('../views/OrderSubmit.vue'),
    meta: { title: '提交订单' }
  },
  {
    path: '/order/pay',
    name: 'mockPay',
    component: () => import('../views/MockPay.vue'),
    meta: { title: '收银台' }
  },
  {
    path: '/order/success',
    name: 'orderSuccess',
    component: () => import('../views/OrderSuccess.vue'),
    meta: { title: '提交成功' }
  },
  {
    path: '/my',
    name: 'my',
    component: () => import('../views/My.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/order/history',
    name: 'historyOrder',
    component: () => import('../views/HistoryOrder.vue'),
    meta: { title: '历史订单' }
  },
  {
    path: '/address/list',
    name: 'addressList',
    component: () => import('../views/AddressList.vue'),
    meta: { title: '地址管理' }
  },
  {
    path: '/address/edit',
    name: 'addressEdit',
    component: () => import('../views/AddressEdit.vue'),
    meta: { title: '新增收货地址' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 登录守卫：除登录页外均需登录
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  document.title = (to.meta.title ? to.meta.title + ' · ' : '') + '苍穹外卖'
  if (to.path !== '/login' && !userStore.token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router