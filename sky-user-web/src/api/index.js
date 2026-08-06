import axios from 'axios'
import { useUserStore } from '../store'
import router from '../router'

// 浏览器端前端请求封装
// 开发/预览时走 vite 代理 /api -> http://localhost:8080
// 也可以设置环境变量 VITE_API_BASE 指向真实后端地址
const BASE = import.meta.env.VITE_API_BASE || '/api'

const instance = axios.create({
  baseURL: BASE,
  timeout: 20000
})

instance.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers['authentication'] = userStore.token
  }
  return config
})

instance.interceptors.response.use(
  (res) => {
    const data = res.data
    if (data && data.code === 1) {
      return data
    }
    return Promise.reject(new Error((data && data.msg) || '操作失败'))
  },
  (err) => {
    if (err.response && err.response.status === 401) {
      const userStore = useUserStore()
      userStore.clear()
      const current = router.currentRoute.value
      if (current.path !== '/login') {
        router.replace({ path: '/login', query: { redirect: current.fullPath } })
      }
    }
    let message = '网络异常，请稍后重试'
    if (err.response && err.response.data && err.response.data.msg) {
      message = err.response.data.msg
    } else if (err.message) {
      message = err.message
    }
    err.userMessage = message
    return Promise.reject(err)
  }
)

export const api = {
  // 用户
  login: (code) => instance.post('/user/user/login', { code }),

  // 店铺
  shopStatus: () => instance.get('/user/shop/status'),

  // 分类、菜品、套餐
  categoryList: (type) => instance.get('/user/category/list', { params: { type } }),
  dishList: (categoryId) => instance.get('/user/dish/list', { params: { categoryId } }),
  setmealList: (categoryId) => instance.get('/user/setmeal/list', { params: { categoryId } }),
  setmealDish: (id) => instance.get(`/user/setmeal/dish/${id}`),

  // 购物车
  cartAdd: (data) => instance.post('/user/shoppingCart/add', data),
  cartSub: (data) => instance.post('/user/shoppingCart/sub', data),
  cartList: () => instance.get('/user/shoppingCart/list'),
  cartClean: () => instance.delete('/user/shoppingCart/clean'),

  // 订单
  orderSubmit: (data) => instance.post('/user/order/submit', data),
  orderMockPay: (orderNumber) => instance.put('/user/order/mockPay', { orderNumber }),
  orderHistory: (page, pageSize, status) =>
    instance.get('/user/order/historyOrders', { params: { page, pageSize, status } }),
  orderDetail: (id) => instance.get(`/user/order/orderDetail/${id}`),
  orderCancel: (id) => instance.put(`/user/order/cancel/${id}`),
  orderAgain: (id) => instance.post(`/user/order/repetition/${id}`),
  orderReminder: (id) => instance.get(`/user/order/reminder/${id}`),

  // 地址簿
  addressList: () => instance.get('/user/addressBook/list'),
  addressById: (id) => instance.get(`/user/addressBook/${id}`),
  addressAdd: (data) => instance.post('/user/addressBook', data),
  addressEdit: (data) => instance.put('/user/addressBook', data),
  addressDelete: (id) => instance.delete('/user/addressBook', { params: { id } }),
  addressSetDefault: (id) => instance.put('/user/addressBook/default', { id }),
  addressGetDefault: () => instance.get('/user/addressBook/default')
}