<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>系统信息管理</el-breadcrumb-item>
          <el-breadcrumb-item>班级管理</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">班级管理</div>
      </div>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <div class="filter-bar">
        <el-input
          v-model="queryParams.name"
          placeholder="班级名称"
          clearable
          style="width: 180px;"
        />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="结课开始日期"
          end-placeholder="结课结束日期"
          value-format="YYYY-MM-DD"
          style="width: 300px;"
        />
        <div class="filter-actions">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            清空
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- Action Bar -->
    <div class="action-bar">
      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增班级
      </el-button>
    </div>

    <!-- Table -->
    <el-table
      :data="clazzList"
      stripe
      style="width: 100%"
      v-loading="loading"
      border
    >
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="name" label="班级名称" min-width="160" />
      <el-table-column prop="room" label="教室" width="80" align="center" />
      <el-table-column label="学科" width="100" align="center">
        <template #default="{ row }">
          {{ subjectMap[row.subject] || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="beginDate" label="开课时间" width="120" />
      <el-table-column prop="endDate" label="结课时间" width="120" />
      <el-table-column prop="masterName" label="班主任" width="100" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag
            :type="statusType(row.status)"
            size="small"
            effect="plain"
          >
            {{ row.status || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后修改时间" min-width="180">
        <template #default="{ row }">
          {{ formatTime(row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>
            修改
          </el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
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
        @size-change="loadClazzList"
        @current-change="loadClazzList"
      />
    </div>

    <!-- Add / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '修改班级' : '新增班级'"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="clazzFormRef"
        :model="clazzForm"
        :rules="clazzRules"
        label-width="90px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级名称" prop="name">
              <el-input v-model="clazzForm.name" placeholder="请输入班级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教室" prop="room">
              <el-input v-model="clazzForm.room" placeholder="请输入教室" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学科" prop="subject">
              <el-select v-model="clazzForm.subject" placeholder="请选择" style="width: 100%;">
                <el-option v-for="(name, val) in subjectMap" :key="val" :label="name" :value="Number(val)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班主任" prop="masterId">
              <el-select v-model="clazzForm.masterId" placeholder="请选择" style="width: 100%;">
                <el-option
                  v-for="e in masterList"
                  :key="e.id"
                  :label="e.name"
                  :value="e.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开课时间" prop="beginDate">
              <el-date-picker
                v-model="clazzForm.beginDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结课时间" prop="endDate">
              <el-date-picker
                v-model="clazzForm.endDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Edit, Search } from '@element-plus/icons-vue'
import { getClazzList, addClazz, updateClazz, deleteClazz, getClazzById, getEmpList } from '@/api/dept'

// ========== Data State ==========
const loading = ref(false)
const clazzList = ref([])
const total = ref(0)
const masterList = ref([])

// Subject map
const subjectMap = {
  1: 'Java',
  2: '前端',
  3: '大数据',
  4: 'Python'
}

// Status type map for tags
function statusType(status) {
  if (status === '未开班') return 'info'
  if (status === '已开班') return 'warning'
  if (status === '已结课') return 'success'
  return 'info'
}

// Query params
const queryParams = reactive({
  page: 1,
  pageSize: 5,
  name: '',
  begin: null,
  end: null
})

const dateRange = ref(null)

// Dialog state
const dialogVisible = ref(false)
const isEditing = ref(false)
const submitLoading = ref(false)
const clazzFormRef = ref(null)
const currentEditId = ref(null)

const clazzForm = reactive({
  name: '',
  room: '',
  subject: null,
  masterId: null,
  beginDate: null,
  endDate: null
})

const clazzRules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  room: [{ required: true, message: '请输入教室', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
  beginDate: [{ required: true, message: '请选择开课时间', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结课时间', trigger: 'change' }]
}

// ========== Methods ==========
function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function loadMasterList() {
  try {
    const data = await getEmpList({ page: 1, pageSize: 999 })
    masterList.value = data.rows || []
  } catch {
    masterList.value = []
  }
}

async function loadClazzList() {
  loading.value = true
  try {
    if (dateRange.value) {
      queryParams.begin = dateRange.value[0]
      queryParams.end = dateRange.value[1]
    } else {
      queryParams.begin = null
      queryParams.end = null
    }

    const params = {}
    Object.keys(queryParams).forEach(key => {
      if (queryParams[key] !== null && queryParams[key] !== '' && queryParams[key] !== undefined) {
        params[key] = queryParams[key]
      }
    })

    const data = await getClazzList(params)
    total.value = data.total
    clazzList.value = data.rows || []
  } catch {
    clazzList.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadClazzList()
}

function handleReset() {
  queryParams.page = 1
  queryParams.name = ''
  queryParams.begin = null
  queryParams.end = null
  dateRange.value = null
  loadClazzList()
}

// Add
function handleAdd() {
  isEditing.value = false
  currentEditId.value = null
  clazzForm.name = ''
  clazzForm.room = ''
  clazzForm.subject = null
  clazzForm.masterId = null
  clazzForm.beginDate = null
  clazzForm.endDate = null
  dialogVisible.value = true
}

// Edit
async function handleEdit(row) {
  isEditing.value = true
  currentEditId.value = row.id
  try {
    const data = await getClazzById(row.id)
    clazzForm.name = data.name || ''
    clazzForm.room = data.room || ''
    clazzForm.subject = data.subject ?? null
    clazzForm.masterId = data.masterId ?? null
    clazzForm.beginDate = data.beginDate || null
    clazzForm.endDate = data.endDate || null
  } catch {
    clazzForm.name = row.name
    clazzForm.room = row.room
    clazzForm.subject = row.subject
    clazzForm.masterId = row.masterId
    clazzForm.beginDate = row.beginDate
    clazzForm.endDate = row.endDate
  }
  dialogVisible.value = true
}

// Delete
function handleDelete(row) {
  ElMessageBox.confirm(
    `确定要删除班级「${row.name}」吗？`,
    '删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await deleteClazz(row.id)
      ElMessage.success('删除成功')
      await loadClazzList()
    } catch {
      // handled by interceptor
    }
  }).catch(() => {})
}

// Submit
async function handleSubmit() {
  const valid = await clazzFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEditing.value) {
      await updateClazz({ id: currentEditId.value, ...clazzForm })
      ElMessage.success('修改成功')
    } else {
      await addClazz(clazzForm)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadClazzList()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadMasterList()
  loadClazzList()
})
</script>
