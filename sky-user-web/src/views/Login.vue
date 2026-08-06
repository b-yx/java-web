<template>
  <div class="phone login-page">
    <div class="login-bg">
      <div class="brand">
        <img class="brand-logo" :src="logoImg" alt="logo" />
        <div class="brand-title">苍穹外卖</div>
        <div class="brand-sub">综合外送平台 · 只为让你省时省心</div>
      </div>

      <div class="login-card">
        <div class="form-item">
          <label>昵称（选填）</label>
          <input
            v-model="nickName"
            type="text"
            maxlength="12"
            placeholder="演示昵称，不填则默认「餐友」"
            class="input"
          />
        </div>

        <button class="btn-primary login-btn" :disabled="loading" @click="doLogin">
          {{ loading ? '登录中...' : '微信一键登录（模拟）' }}
        </button>

        <p class="notice">
          说明：个人主体无法使用微信登录与微信支付，本页面为浏览器端<b>模拟登录</b>，
          登录后即可正常点餐、下单。
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore, useCartStore } from '../store'
import { api } from '../api'
import { toast } from '../utils/toast'
import logoImg from '../assets/img/logo_ruiji.png'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const nickName = ref('')
const loading = ref(false)

async function doLogin() {
  if (loading.value) return
  loading.value = true
  try {
    // 模拟微信登录：code 以 mock: 开头，后端不再请求微信接口
    const res = await api.login('mock:' + (nickName.value || 'browser-demo-user'))
    const data = res.data
    userStore.setLogin(data.token, {
      id: data.id,
      nickName: nickName.value || '餐友',
      avatar: ''
    })
    try {
      await cartStore.fetch()
    } catch (e) {
      // 购物车拉取失败不阻塞登录
    }
    toast('登录成功')
    router.push(route.query.redirect || '/')
  } catch (e) {
    toast(e.userMessage || e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  background: #ffc200;
}

.login-bg {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #ffc200 0%, #ffb300 60%, #f6f6f6 60.5%);
  padding: 0 28px;
}

.brand {
  text-align: center;
  margin-bottom: 36px;
}

.brand-logo {
  width: 96px;
  height: 96px;
  border-radius: 24px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  display: block;
  margin: 0 auto;
}

.brand-title {
  font-size: 30px;
  font-weight: 600;
  color: #7a4a00;
  margin-top: 16px;
  letter-spacing: 4px;
}

.brand-sub {
  font-size: 13px;
  color: #8a6a2c;
  margin-top: 8px;
}

.login-card {
  width: 100%;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 28px 24px 24px;
  margin-top: 30px;
}

.form-item {
  margin-bottom: 22px;
}

.form-item label {
  display: block;
  font-size: 13px;
  color: #666666;
  margin-bottom: 8px;
}

.input {
  width: 100%;
  height: 44px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 15px;
  outline: none;
}

.input:focus {
  border-color: #ffc200;
}

.login-btn {
  width: 100%;
  height: 48px;
  line-height: 48px;
  font-size: 16px;
  border-radius: 24px;
}

.login-btn[disabled] {
  opacity: 0.7;
}

.notice {
  margin-top: 16px;
  font-size: 12px;
  color: #999999;
  line-height: 1.6;
}

.notice b {
  color: #666666;
}
</style>