<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-title">Tlias智能学习辅助系统</div>
      <div class="login-subtitle">企业级后台管理平台</div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        size="large"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <div style="display: flex; gap: 16px; width: 100%;">
            <el-button
              type="primary"
              :loading="loading"
              style="flex: 1;"
              @click="handleLogin"
            >
              登录
            </el-button>
            <el-button
              style="flex: 1;"
              @click="handleReset"
            >
              重置
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <div style="text-align: center; margin-top: 16px;">
        <span style="font-size: 13px; color: #909399;">
          测试账号: jinyong / 123456
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/dept'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const data = await login(loginForm)
    // Save token and user info
    localStorage.setItem('tlias_token', data.token)
    localStorage.setItem('tlias_user', JSON.stringify(data))
    ElMessage.success(`欢迎回来，${data.name}！`)
    router.push('/')
  } catch (err) {
    // Error message already shown by interceptor
  } finally {
    loading.value = false
  }
}

function handleReset() {
  loginForm.username = ''
  loginForm.password = ''
  loginFormRef.value?.clearValidate()
}
</script>
