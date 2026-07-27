<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>日志管理</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">日志管理</div>
      </div>
      <div>
        <el-button type="primary" @click="loadLogList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- Table -->
    <el-table
      :data="logList"
      stripe
      style="width: 100%"
      v-loading="loading"
      border
      default-expand-all
    >
      <el-table-column type="index" label="序号" width="65" align="center" />

      <el-table-column prop="operateEmpName" label="操作人" width="100" align="center" />
      <el-table-column label="操作时间" width="170">
        <template #default="{ row }">
          {{ formatTime(row.operateTime) }}
        </template>
      </el-table-column>

      <el-table-column label="类名" min-width="220">
        <template #default="{ row }">
          <span style="font-size: 12px; font-family: monospace; color: #555;">
            {{ row.className }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="方法名" width="120" align="center">
        <template #default="{ row }">
          <el-tag size="small" effect="plain" type="info">{{ row.methodName }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="方法参数" min-width="260">
        <template #default="{ row }">
          <span style="font-size: 12px; color: #909399; word-break: break-all;">
            {{ row.methodParams || '-' }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="返回值" min-width="260">
        <template #default="{ row }">
          <span style="font-size: 12px; color: #909399; word-break: break-all;">
            {{ row.returnValue || '-' }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="耗时(ms)" width="90" align="center">
        <template #default="{ row }">
          <el-tag
            :type="row.costTime > 100 ? 'danger' : row.costTime > 50 ? 'warning' : 'success'"
            size="small"
            effect="plain"
          >
            {{ row.costTime }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadLogList"
        @current-change="loadLogList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getLogList } from '@/api/dept'

const loading = ref(false)
const logList = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 10
})

function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function loadLogList() {
  loading.value = true
  try {
    const data = await getLogList({ page: queryParams.page, pageSize: queryParams.pageSize })
    total.value = data.total
    logList.value = data.rows || []
  } catch {
    logList.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLogList()
})
</script>
