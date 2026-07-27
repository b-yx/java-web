<template>
  <div class="page-container">
    <!-- Welcome Banner -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h1>欢迎回来，{{ userInfo.name || '用户' }}！</h1>
        <p>Tlias智能学习辅助系统 — 企业级后台管理平台</p>
        <div class="welcome-meta">
          <el-tag size="small" type="info" effect="plain">
            <el-icon style="margin-right: 4px;"><Clock /></el-icon>
            {{ currentTime }}
          </el-tag>
          <el-tag size="small" type="success" effect="plain">
            <el-icon style="margin-right: 4px;"><UserFilled /></el-icon>
            {{ userInfo.username }}
          </el-tag>
        </div>
      </div>
      <div class="welcome-illustration">
        <el-icon class="welcome-icon"><Monitor /></el-icon>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="summary-grid">
      <div class="summary-card" style="--card-color: #409EFF;">
        <div class="card-icon"><OfficeBuilding /></div>
        <div class="card-info">
          <div class="card-value">{{ stats.deptCount }}</div>
          <div class="card-label">部门总数</div>
        </div>
        <div class="card-footer">
          <el-button text size="small" @click="$router.push('/dept')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="summary-card" style="--card-color: #67C23A;">
        <div class="card-icon"><User /></div>
        <div class="card-info">
          <div class="card-value">{{ stats.empCount }}</div>
          <div class="card-label">员工总数</div>
        </div>
        <div class="card-footer">
          <el-button text size="small" @click="$router.push('/emp')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="summary-card" style="--card-color: #E6A23C;">
        <div class="card-icon"><Reading /></div>
        <div class="card-info">
          <div class="card-value">{{ stats.clazzCount }}</div>
          <div class="card-label">班级总数</div>
        </div>
        <div class="card-footer">
          <el-button text size="small" @click="$router.push('/clazz')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <div class="summary-card" style="--card-color: #8B5CF6;">
        <div class="card-icon"><UserFilled /></div>
        <div class="card-info">
          <div class="card-value">{{ stats.studentCount }}</div>
          <div class="card-label">学员总数</div>
        </div>
        <div class="card-footer">
          <el-button text size="small" @click="$router.push('/student')">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- Quick Links & Info -->
    <div class="dashboard-bottom">
      <!-- Quick Actions -->
      <div class="dashboard-card">
        <div class="dashboard-card-title">
          <el-icon style="margin-right: 6px; color: #409EFF;"><Lightning /></el-icon>
          快捷操作
        </div>
        <div class="quick-links">
          <div class="quick-link-item" @click="$router.push('/emp')">
            <el-icon style="color: #67C23A;"><Plus /></el-icon>
            <span>新增员工</span>
          </div>
          <div class="quick-link-item" @click="$router.push('/clazz')">
            <el-icon style="color: #E6A23C;"><Plus /></el-icon>
            <span>新增班级</span>
          </div>
          <div class="quick-link-item" @click="$router.push('/student')">
            <el-icon style="color: #8B5CF6;"><Plus /></el-icon>
            <span>新增学员</span>
          </div>
          <div class="quick-link-item" @click="$router.push('/dept')">
            <el-icon style="color: #409EFF;"><Edit /></el-icon>
            <span>管理部门</span>
          </div>
          <div class="quick-link-item" @click="$router.push('/report/emp')">
            <el-icon style="color: #F56C6C;"><DataAnalysis /></el-icon>
            <span>查看统计</span>
          </div>
          <div class="quick-link-item" @click="$router.push('/log')">
            <el-icon style="color: #909399;"><Document /></el-icon>
            <span>操作日志</span>
          </div>
        </div>
      </div>

      <!-- System Info -->
      <div class="dashboard-card">
        <div class="dashboard-card-title">
          <el-icon style="margin-right: 6px; color: #67C23A;"><InfoFilled /></el-icon>
          系统信息
        </div>
        <div class="system-info">
          <div class="info-row">
            <span class="info-label">系统名称</span>
            <span class="info-value">Tlias智能学习辅助系统</span>
          </div>
          <div class="info-row">
            <span class="info-label">当前版本</span>
            <span class="info-value">v1.0.0</span>
          </div>
          <div class="info-row">
            <span class="info-label">前端框架</span>
            <span class="info-value">Vue 3 + Element Plus</span>
          </div>
          <div class="info-row">
            <span class="info-label">当前用户</span>
            <span class="info-value">{{ userInfo.name || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">登录时间</span>
            <span class="info-value">{{ loginTime }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import {
  HomeFilled, OfficeBuilding, User, UserFilled, Reading,
  Clock, Monitor, ArrowRight, Plus, Edit, DataAnalysis,
  Document, Lightning, InfoFilled, Setting
} from '@element-plus/icons-vue'
import { getDeptList, getEmpList, getClazzList, getStudentList } from '@/api/dept'

const router = useRouter()

const userInfo = computed(() => {
  const raw = localStorage.getItem('tlias_user')
  return raw ? JSON.parse(raw) : { name: '用户', username: '' }
})

const loginTime = ref('')
const currentTime = ref('')
let timer = null

function updateTime() {
  const now = new Date()
  const pad = n => String(n).padStart(2, '0')
  currentTime.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

const stats = reactive({
  deptCount: 0,
  empCount: 0,
  clazzCount: 0,
  studentCount: 0
})

async function loadStats() {
  try {
    const deptData = await getDeptList()
    stats.deptCount = deptData?.length || 0
  } catch { stats.deptCount = 0 }

  try {
    const empData = await getEmpList({ page: 1, pageSize: 1 })
    stats.empCount = empData?.total || 0
  } catch { stats.empCount = 0 }

  try {
    const clazzData = await getClazzList({ page: 1, pageSize: 1 })
    stats.clazzCount = clazzData?.total || 0
  } catch { stats.clazzCount = 0 }

  try {
    const studentData = await getStudentList({ page: 1, pageSize: 1 })
    stats.studentCount = studentData?.total || 0
  } catch { stats.studentCount = 0 }
}

onMounted(() => {
  loginTime.value = currentTime.value
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadStats()
})

onBeforeUnmount(() => {
  clearInterval(timer)
})
</script>

<style scoped>
/* Welcome Banner */
.welcome-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #8B5CF6 0%, #A56DCC 100%);
  border-radius: 12px;
  padding: 32px 36px;
  margin-bottom: 24px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.25);
}

.welcome-text h1 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
}

.welcome-text p {
  font-size: 14px;
  opacity: 0.85;
  margin-bottom: 12px;
}

.welcome-meta {
  display: flex;
  gap: 8px;
}

.welcome-illustration {
  width: 80px;
  height: 80px;
  background: rgba(255,255,255,0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-icon {
  font-size: 40px;
}

/* Summary Grid */
.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.summary-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  border-left: 4px solid var(--card-color);
  display: flex;
  flex-direction: column;
}

.card-icon {
  font-size: 28px;
  color: var(--card-color);
  margin-bottom: 12px;
}

.card-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.card-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.card-footer .el-button {
  color: var(--card-color);
  font-size: 12px;
}

/* Dashboard Bottom */
.dashboard-bottom {
  display: flex;
  gap: 16px;
}

.dashboard-card {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}

.dashboard-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

/* Quick Links */
.quick-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.quick-link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  border-radius: 8px;
  cursor: pointer;
  background: #f8f9fc;
  transition: all 0.2s;
  font-size: 13px;
  color: #606266;
}

.quick-link-item:hover {
  background: #f0f2f7;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.quick-link-item .el-icon {
  font-size: 24px;
}

/* System Info */
.system-info {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: #909399;
}

.info-value {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
}
</style>
