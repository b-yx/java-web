<template>
  <div class="layout-container">
    <!-- Header -->
    <header class="layout-header">
      <div class="header-left">
        <el-icon class="logo-icon"><Monitor /></el-icon>
        <span>Tlias智能学习辅助系统</span>
      </div>
      <div class="header-right">
        <el-button text @click="handleChangePwd">
          <el-icon><EditPen /></el-icon>
          修改密码
        </el-button>
        <el-divider direction="vertical" style="background: rgba(255,255,255,0.3); height: 20px;" />
        <div class="user-info">
          <el-icon><UserFilled /></el-icon>
          <span>{{ currentUser?.name || '用户' }}</span>
        </div>
        <el-button text @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>
    </header>

    <!-- Body -->
    <div class="layout-body">
      <!-- Sidebar -->
      <aside class="layout-sidebar">
        <el-menu
          :default-active="activeMenu"
          :collapse="false"
          background-color="#ffffff"
          text-color="#303133"
          active-text-color="#8B5CF6"
          router
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统信息管理</span>
            </template>
            <el-menu-item index="/dept">
              <el-icon><HomeFilled /></el-icon>
              <span>部门管理</span>
            </el-menu-item>
            <el-menu-item index="/emp">
              <el-icon><User /></el-icon>
              <span>员工管理</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="clazz-student">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>班级学员管理</span>
            </template>
            <el-menu-item index="/clazz">
              <el-icon><Reading /></el-icon>
              <span>班级管理</span>
            </el-menu-item>
            <el-menu-item index="/student">
              <el-icon><UserFilled /></el-icon>
              <span>学员管理</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="stat">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>数据统计管理</span>
            </template>
            <el-menu-item index="/report/emp">
              <el-icon><DataAnalysis /></el-icon>
              <span>员工信息统计</span>
            </el-menu-item>
            <el-menu-item index="/report/student">
              <el-icon><PieChart /></el-icon>
              <span>学员学历统计</span>
            </el-menu-item>
            <el-menu-item index="/report/class">
              <el-icon><Histogram /></el-icon>
              <span>班级人数统计</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/log">
            <el-icon><Document /></el-icon>
            <span>日志管理</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- Main Content -->
      <main class="layout-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Monitor, UserFilled, Setting, DataAnalysis,
  EditPen, SwitchButton, HomeFilled, User,
  Reading, PieChart, Histogram, Document
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const currentUser = computed(() => {
  const raw = localStorage.getItem('tlias_user')
  return raw ? JSON.parse(raw) : null
})

function handleChangePwd() {
  ElMessage.info('修改密码功能开发中...')
}

function handleLogout() {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    localStorage.removeItem('tlias_token')
    localStorage.removeItem('tlias_user')
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>
