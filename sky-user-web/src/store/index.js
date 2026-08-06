import { defineStore } from 'pinia'
import { api } from '../api'

const TOKEN_KEY = 'sky_token'
const USER_KEY = 'sky_user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    userInfo: JSON.parse(localStorage.getItem(USER_KEY) || '{}'),
    // 店铺营业状态（1营业 0打烊），未获取到时默认为营业
    shopOpen: true
  }),
  getters: {
    displayName: (state) => state.userInfo.nickName || state.userInfo.name || '餐友',
    displayPhone: (state) => state.userInfo.phone || ''
  },
  actions: {
    setLogin(token, userInfo) {
      this.token = token
      this.userInfo = userInfo || {}
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(this.userInfo))
    },
    // 浏览器端无法通过微信授权拿到昵称/头像，这里允许手动设置一个展示名
    setNickName(name) {
      this.userInfo.nickName = name
      localStorage.setItem(USER_KEY, JSON.stringify(this.userInfo))
    },
    clear() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
    async refreshInfo() {
      try {
        const res = await api.shopStatus()
        this.shopOpen = Number(res && res.data) === 1
        return res
      } catch (e) {
        return null
      }
    }
  }
})

export const useCartStore = defineStore('cart', {
  state: () => ({
    list: [],
    openCart: false
  }),
  getters: {
    totalNumber: (state) => state.list.reduce((s, i) => s + (i.number || 0), 0),
    // 商品小计（不含配送费）
    goodsAmount: (state) =>
      state.list.reduce((s, i) => s + (i.number || 0) * (i.amount || 0), 0)
  },
  actions: {
    async fetch() {
      if (!useUserStore().token) return
      const res = await api.cartList()
      this.list = (res && res.data) || []
    },
    // item 来自菜品/套餐列表：{id, price, image, name, type, flavors, dishFlavor...}
    async add(item, flavorText) {
      const payload = {
        number: 1,
        amount: item.price,
        name: item.name,
        image: item.image,
        dishFlavor: flavorText || ''
      }
      if (item.type === 2) {
        payload.setmealId = item.id
      } else {
        payload.dishId = item.id
      }
      await api.cartAdd(payload)
      await this.fetch()
    },
    async minus(item) {
      const payload = { dishFlavor: item.dishFlavor || '' }
      if (item.setmealId) {
        payload.setmealId = item.setmealId
      } else {
        payload.dishId = item.dishId
      }
      await api.cartSub(payload)
      await this.fetch()
    },
    async clean() {
      await api.cartClean()
      this.list = []
      this.openCart = false
    }
  }
})

export const useOrderStore = defineStore('order', {
  state: () => ({
    // 最近一次提交的订单信息，供收银台/成功页使用
    lastOrder: null,
    // 从提交订单页进入地址管理时使用，选择地址后返回
    addressBackUrl: '/',
    // 地址管理页选中的地址，回填到提交订单页
    selectedAddress: null
  }),
  actions: {
    setLastOrder(order) {
      this.lastOrder = order
    }
  }
})