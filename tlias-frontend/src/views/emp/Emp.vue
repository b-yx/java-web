<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>系统信息管理</el-breadcrumb-item>
          <el-breadcrumb-item>员工管理</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">员工管理</div>
      </div>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <div class="filter-bar">
        <el-input
          v-model="queryParams.name"
          placeholder="姓名"
          clearable
          style="width: 160px;"
        />
        <el-select
          v-model="queryParams.gender"
          placeholder="性别"
          clearable
          style="width: 120px;"
        >
          <el-option label="男" :value="1" />
          <el-option label="女" :value="2" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="入职开始日期"
          end-placeholder="入职结束日期"
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
    <div class="action-bar" style="margin-bottom: 16px;">
      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增员工
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedIds.length === 0"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- Table -->
    <el-table
      :data="empList"
      stripe
      style="width: 100%"
      v-loading="loading"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />

      <el-table-column label="头像" width="70" align="center">
        <template #default="{ row }">
          <div class="avatar-placeholder">
            {{ row.name?.[0] || '?' }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column label="性别" width="70" align="center">
        <template #default="{ row }">
          <el-tag :type="row.gender === 1 ? 'primary' : 'success'" size="small" effect="plain">
            {{ row.gender === 1 ? '男' : '女' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="deptName" label="所属部门" width="140" />
      <el-table-column label="职位" width="120">
        <template #default="{ row }">
          {{ jobMap[row.job] || '-' }}
        </template>
      </el-table-column>

      <el-table-column prop="entryDate" label="入职时间" width="120" />
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
        @size-change="loadEmpList"
        @current-change="loadEmpList"
      />
    </div>

    <!-- Add / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '修改员工' : '新增员工'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="empFormRef"
        :model="empForm"
        :rules="empRules"
        label-width="90px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="empForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="empForm.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="empForm.gender" placeholder="请选择" style="width: 100%;">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="job">
              <el-select v-model="empForm.job" placeholder="请选择" style="width: 100%;">
                <el-option v-for="(name, val) in jobMap" :key="val" :label="name" :value="Number(val)" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="deptId">
              <el-select v-model="empForm.deptId" placeholder="请选择" style="width: 100%;">
                <el-option
                  v-for="d in deptList"
                  :key="d.id"
                  :label="d.name"
                  :value="d.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职时间" prop="entryDate">
              <el-date-picker
                v-model="empForm.entryDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="empForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input-number v-model="empForm.salary" :min="0" :step="1000" style="width: 100%;" />
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
import { getEmpList, addEmp, updateEmp, deleteEmps, getEmpById } from '@/api/dept'
import { getDeptList } from '@/api/dept'

// ========== Data State ==========
const loading = ref(false)
const empList = ref([])
const total = ref(0)
const selectedIds = ref([])
const deptList = ref([])

// Job map (matches backend Emp.job)
const jobMap = {
  1: '班主任',
  2: '讲师',
  3: '学工主管',
  4: '教研主管',
  5: '咨询师'
}

// Query params
const queryParams = reactive({
  page: 1,
  pageSize: 5,
  name: '',
  gender: null,
  begin: null,
  end: null
})

// Date range (splits into begin/end for the API)
const dateRange = ref(null)

// Dialog state
const dialogVisible = ref(false)
const isEditing = ref(false)
const submitLoading = ref(false)
const empFormRef = ref(null)
const currentEditId = ref(null)

const empForm = reactive({
  name: '',
  username: '',
  gender: null,
  job: null,
  deptId: null,
  entryDate: null,
  phone: '',
  salary: 0
})

const empRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  job: [{ required: true, message: '请选择职位', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  entryDate: [{ required: true, message: '请选择入职时间', trigger: 'change' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确手机号', trigger: 'blur' }]
}

// ========== Methods ==========
function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function loadDeptList() {
  try {
    deptList.value = await getDeptList()
  } catch {
    deptList.value = []
  }
}

async function loadEmpList() {
  loading.value = true
  try {
    // Split date range
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

    const data = await getEmpList(params)
    total.value = data.total
    empList.value = data.rows || []
  } catch {
    empList.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadEmpList()
}

function handleReset() {
  queryParams.page = 1
  queryParams.name = ''
  queryParams.gender = null
  queryParams.begin = null
  queryParams.end = null
  dateRange.value = null
  loadEmpList()
}

function handleSelectionChange(rows) {
  selectedIds.value = rows.map(r => r.id)
}

// Add
function handleAdd() {
  isEditing.value = false
  currentEditId.value = null
  empForm.name = ''
  empForm.username = ''
  empForm.gender = null
  empForm.job = null
  empForm.deptId = null
  empForm.entryDate = null
  empForm.phone = ''
  empForm.salary = 0
  dialogVisible.value = true
}

// Edit
async function handleEdit(row) {
  isEditing.value = true
  currentEditId.value = row.id
  try {
    const data = await getEmpById(row.id)
    empForm.name = data.name || ''
    empForm.username = data.username || ''
    empForm.gender = data.gender ?? null
    empForm.job = data.job ?? null
    empForm.deptId = data.deptId ?? null
    empForm.entryDate = data.entryDate || null
    empForm.phone = data.phone || ''
    empForm.salary = data.salary ?? 0
  } catch {
    // Fallback to row data
    empForm.name = row.name
    empForm.username = row.username
    empForm.gender = row.gender
    empForm.job = row.job
    empForm.deptId = row.deptId
    empForm.entryDate = row.entryDate
    empForm.phone = row.phone || ''
    empForm.salary = row.salary || 0
  }
  dialogVisible.value = true
}

// Delete single
function handleDelete(row) {
  ElMessageBox.confirm(
    `确定要删除员工「${row.name}」吗？`,
    '删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await deleteEmps([row.id])
      ElMessage.success('删除成功')
      await loadEmpList()
    } catch {
      // handled by interceptor
    }
  }).catch(() => {})
}

// Batch delete
function handleBatchDelete() {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedIds.value.length} 名员工吗？`,
    '批量删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await deleteEmps(selectedIds.value)
      ElMessage.success('批量删除成功')
      await loadEmpList()
    } catch {
      // handled by interceptor
    }
  }).catch(() => {})
}

// Submit form
async function handleSubmit() {
  const valid = await empFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEditing.value) {
      await updateEmp({ id: currentEditId.value, ...empForm })
      ElMessage.success('修改成功')
    } else {
      await addEmp(empForm)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadEmpList()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadDeptList()
  loadEmpList()
})
</script>
