<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>系统信息管理</el-breadcrumb-item>
          <el-breadcrumb-item>部门管理</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">部门管理</div>
      </div>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <!-- Table -->
    <el-table
      :data="deptList"
      stripe
      style="width: 100%"
      v-loading="loading"
      border
    >
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="name" label="部门名称" min-width="200" />
      <el-table-column prop="updateTime" label="最后修改时间" min-width="220">
        <template #default="{ row }">
          {{ formatTime(row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
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

    <!-- Add / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '修改部门' : '新增部门'"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="deptFormRef"
        :model="deptForm"
        :rules="deptRules"
        label-width="80px"
        label-position="right"
      >
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="deptForm.name" placeholder="请输入部门名称" />
        </el-form-item>
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
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getDeptList, addDept, updateDept, deleteDept, getDeptById } from '@/api/dept'

const loading = ref(false)
const deptList = ref([])

// Dialog
const dialogVisible = ref(false)
const isEditing = ref(false)
const submitLoading = ref(false)
const deptFormRef = ref(null)
const currentEditId = ref(null)

const deptForm = reactive({
  name: ''
})

const deptRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

// Format datetime
function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// Load data
async function loadDeptList() {
  loading.value = true
  try {
    deptList.value = await getDeptList()
  } catch {
    deptList.value = []
  } finally {
    loading.value = false
  }
}

// Add
function handleAdd() {
  isEditing.value = false
  currentEditId.value = null
  deptForm.name = ''
  dialogVisible.value = true
}

// Edit
async function handleEdit(row) {
  isEditing.value = true
  currentEditId.value = row.id
  try {
    const data = await getDeptById(row.id)
    deptForm.name = data.name
  } catch {
    deptForm.name = row.name
  }
  dialogVisible.value = true
}

// Delete
function handleDelete(row) {
  ElMessageBox.confirm(
    `确定要删除部门「${row.name}」吗？`,
    '删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await deleteDept(row.id)
      ElMessage.success('删除成功')
      await loadDeptList()
    } catch {
      // handled by interceptor
    }
  }).catch(() => {})
}

// Submit
async function handleSubmit() {
  const valid = await deptFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEditing.value) {
      await updateDept({ id: currentEditId.value, name: deptForm.name })
      ElMessage.success('修改成功')
    } else {
      await addDept({ name: deptForm.name })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadDeptList()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadDeptList()
})
</script>
