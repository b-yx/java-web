<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>系统信息管理</el-breadcrumb-item>
          <el-breadcrumb-item>学员管理</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">学员管理</div>
      </div>
    </div>

    <!-- Filter Bar -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <div class="filter-bar">
        <el-input
          v-model="queryParams.name"
          placeholder="学员姓名"
          clearable
          style="width: 160px;"
        />
        <el-select
          v-model="queryParams.degree"
          placeholder="学历"
          clearable
          style="width: 120px;"
        >
          <el-option v-for="(name, val) in degreeMap" :key="val" :label="name" :value="Number(val)" />
        </el-select>
        <el-select
          v-model="queryParams.clazzId"
          placeholder="班级"
          clearable
          style="width: 180px;"
          filterable
        >
          <el-option
            v-for="c in clazzOptions"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
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
        新增学员
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
      :data="studentList"
      stripe
      style="width: 100%"
      v-loading="loading"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />

      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="no" label="学号" width="140" />
      <el-table-column prop="name" label="姓名" width="90" />
      <el-table-column label="性别" width="65" align="center">
        <template #default="{ row }">
          <el-tag :type="row.gender === 1 ? 'primary' : 'success'" size="small" effect="plain">
            {{ row.gender === 1 ? '男' : '女' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="学历" width="80" align="center">
        <template #default="{ row }">
          {{ degreeMap[row.degree] || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="clazzName" label="班级" min-width="140" />
      <el-table-column label="违纪" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.violationCount > 0" type="danger" size="small">
            违纪 {{ row.violationCount }} 次
          </el-tag>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="最后修改时间" min-width="170">
        <template #default="{ row }">
          {{ formatTime(row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="230" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>
            修改
          </el-button>
          <el-button type="warning" link size="small" @click="handleViolation(row)">
            <el-icon><WarningFilled /></el-icon>
            违纪
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
        @size-change="loadStudentList"
        @current-change="loadStudentList"
      />
    </div>

    <!-- Add / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '修改学员' : '新增学员'"
      width="640px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="studentFormRef"
        :model="studentForm"
        :rules="studentRules"
        label-width="100px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="studentForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" prop="no">
              <el-input v-model="studentForm.no" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="studentForm.gender" placeholder="请选择" style="width: 100%;">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="studentForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历" prop="degree">
              <el-select v-model="studentForm.degree" placeholder="请选择" style="width: 100%;">
                <el-option v-for="(name, val) in degreeMap" :key="val" :label="name" :value="Number(val)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="clazzId">
              <el-select v-model="studentForm.clazzId" placeholder="请选择" style="width: 100%;" filterable>
                <el-option
                  v-for="c in clazzOptions"
                  :key="c.id"
                  :label="c.name"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="studentForm.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业时间" prop="graduationDate">
              <el-date-picker
                v-model="studentForm.graduationDate"
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
            <el-form-item label="院校学生" prop="isCollege">
              <el-select v-model="studentForm.isCollege" placeholder="请选择" style="width: 100%;">
                <el-option label="是" :value="1" />
                <el-option label="否" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系地址" prop="address">
              <el-input v-model="studentForm.address" placeholder="请输入地址" />
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

    <!-- Violation Dialog -->
    <el-dialog
      v-model="violationVisible"
      title="违纪处理"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="violationFormRef"
        :model="violationForm"
        :rules="violationRules"
        label-width="80px"
      >
        <el-form-item label="学员姓名">
          <span>{{ violationForm.studentName }}</span>
        </el-form-item>
        <el-form-item label="当前扣分">
          <span style="color: #E6A23C; font-weight: 600;">{{ violationForm.currentScore }} 分</span>
        </el-form-item>
        <el-form-item label="扣除分数" prop="score">
          <el-input-number
            v-model="violationForm.score"
            :min="1"
            :max="100"
            :step="1"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="violationVisible = false">取消</el-button>
        <el-button type="warning" :loading="violationLoading" @click="handleViolationSubmit">
          确认违纪
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Edit, Search, WarningFilled } from '@element-plus/icons-vue'
import {
  getStudentList, addStudent, updateStudent, deleteStudents,
  getStudentById, violationStudent, getAllClazzs
} from '@/api/dept'

// ========== Data State ==========
const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const selectedIds = ref([])
const clazzOptions = ref([])

// Degree map
const degreeMap = {
  1: '初中',
  2: '高中',
  3: '大专',
  4: '本科',
  5: '硕士'
}

// Query params
const queryParams = reactive({
  page: 1,
  pageSize: 5,
  name: '',
  degree: null,
  clazzId: null
})

// Dialog state
const dialogVisible = ref(false)
const isEditing = ref(false)
const submitLoading = ref(false)
const studentFormRef = ref(null)
const currentEditId = ref(null)

const studentForm = reactive({
  name: '',
  no: '',
  gender: null,
  phone: '',
  degree: null,
  clazzId: null,
  idCard: '',
  isCollege: null,
  address: '',
  graduationDate: null
})

const studentRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  no: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确手机号', trigger: 'blur' }
  ],
  degree: [{ required: true, message: '请选择学历', trigger: 'change' }],
  clazzId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

// Violation state
const violationVisible = ref(false)
const violationLoading = ref(false)
const violationFormRef = ref(null)
const violationForm = reactive({
  studentId: null,
  studentName: '',
  currentScore: 0,
  score: 1
})

const violationRules = {
  score: [{ required: true, message: '请输入扣除分数', trigger: 'blur' }]
}

// ========== Methods ==========
function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function loadClazzOptions() {
  try {
    clazzOptions.value = await getAllClazzs()
  } catch {
    clazzOptions.value = []
  }
}

async function loadStudentList() {
  loading.value = true
  try {
    const params = {}
    Object.keys(queryParams).forEach(key => {
      if (queryParams[key] !== null && queryParams[key] !== '' && queryParams[key] !== undefined) {
        params[key] = queryParams[key]
      }
    })

    const data = await getStudentList(params)
    total.value = data.total
    studentList.value = data.rows || []
  } catch {
    studentList.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadStudentList()
}

function handleReset() {
  queryParams.page = 1
  queryParams.name = ''
  queryParams.degree = null
  queryParams.clazzId = null
  loadStudentList()
}

function handleSelectionChange(rows) {
  selectedIds.value = rows.map(r => r.id)
}

// Add
function handleAdd() {
  isEditing.value = false
  currentEditId.value = null
  studentForm.name = ''
  studentForm.no = ''
  studentForm.gender = null
  studentForm.phone = ''
  studentForm.degree = null
  studentForm.clazzId = null
  studentForm.idCard = ''
  studentForm.isCollege = null
  studentForm.address = ''
  studentForm.graduationDate = null
  dialogVisible.value = true
}

// Edit
async function handleEdit(row) {
  isEditing.value = true
  currentEditId.value = row.id
  try {
    const data = await getStudentById(row.id)
    studentForm.name = data.name || ''
    studentForm.no = data.no || ''
    studentForm.gender = data.gender ?? null
    studentForm.phone = data.phone || ''
    studentForm.degree = data.degree ?? null
    studentForm.clazzId = data.clazzId ?? null
    studentForm.idCard = data.idCard || ''
    studentForm.isCollege = data.isCollege ?? null
    studentForm.address = data.address || ''
    studentForm.graduationDate = data.graduationDate || null
  } catch {
    studentForm.name = row.name
    studentForm.no = row.no
    studentForm.gender = row.gender
    studentForm.phone = row.phone
    studentForm.degree = row.degree
    studentForm.clazzId = row.clazzId
    studentForm.idCard = row.idCard || ''
    studentForm.isCollege = row.isCollege ?? null
    studentForm.address = row.address || ''
    studentForm.graduationDate = row.graduationDate || null
  }
  dialogVisible.value = true
}

// Delete single
function handleDelete(row) {
  ElMessageBox.confirm(
    `确定要删除学员「${row.name}」吗？`,
    '删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await deleteStudents([row.id])
      ElMessage.success('删除成功')
      await loadStudentList()
    } catch {
      // handled by interceptor
    }
  }).catch(() => {})
}

// Batch delete
function handleBatchDelete() {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedIds.value.length} 名学员吗？`,
    '批量删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await deleteStudents(selectedIds.value)
      ElMessage.success('批量删除成功')
      await loadStudentList()
    } catch {
      // handled by interceptor
    }
  }).catch(() => {})
}

// Submit form
async function handleSubmit() {
  const valid = await studentFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEditing.value) {
      await updateStudent({ id: currentEditId.value, ...studentForm })
      ElMessage.success('修改成功')
    } else {
      await addStudent(studentForm)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadStudentList()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

// Violation
function handleViolation(row) {
  violationForm.studentId = row.id
  violationForm.studentName = row.name
  violationForm.currentScore = row.violationScore || 0
  violationForm.score = 1
  violationVisible.value = true
}

async function handleViolationSubmit() {
  const valid = await violationFormRef.value.validate().catch(() => false)
  if (!valid) return

  violationLoading.value = true
  try {
    await violationStudent(violationForm.studentId, violationForm.score)
    ElMessage.success('违纪处理成功')
    violationVisible.value = false
    await loadStudentList()
  } catch {
    // handled by interceptor
  } finally {
    violationLoading.value = false
  }
}

onMounted(() => {
  loadClazzOptions()
  loadStudentList()
})
</script>
