<template>
  <div class="phone">
    <NavBar :title="isEdit ? '编辑收货地址' : '新增收货地址'" />

    <div class="page-body edit-body">
      <div class="card form-card">
        <!-- 联系人 + 性别 -->
        <div class="form-row">
          <span class="form-label">联系人</span>
          <input v-model="form.consignee" class="form-input" maxlength="5" placeholder="请输入联系人" />
          <div class="sex-group">
            <span
              v-for="it in sexes"
              :key="it.value"
              class="sex-item"
              @click="form.sex = it.value"
            >
              <img :src="form.sex === it.value ? radioSel : radioNor" class="radio-img" alt="" />
              {{ it.name }}
            </span>
          </div>
        </div>

        <!-- 手机号 -->
        <div class="form-row">
          <span class="form-label">手机号</span>
          <input v-model="form.phone" class="form-input" type="tel" maxlength="11" placeholder="请输入手机号" />
        </div>

        <!-- 所在地区 -->
        <div class="form-row" @click="pickerRef.open()">
          <span class="form-label">所在地区</span>
          <span class="form-input region-value" :class="{ placeholder: !regionText }">
            {{ regionText || '请选择省市区' }}
          </span>
          <span class="region-arrow">›</span>
        </div>

        <!-- 详细地址 -->
        <div class="form-row detail-row">
          <span class="form-label">详细地址</span>
          <input v-model="form.detail" class="form-input" maxlength="60" placeholder="详细地址：如道路、门牌号" />
        </div>

        <!-- 标签 -->
        <div class="form-row">
          <span class="form-label">标签</span>
          <div class="tag-group">
            <span
              v-for="tag in tags"
              :key="tag.value"
              class="tag-item"
              :class="{ active: Number(form.label) === tag.value }"
              @click="form.label = tag.value"
            >
              {{ tag.name }}
            </span>
          </div>
        </div>
      </div>

      <button class="btn-primary save-btn" :disabled="saving" @click="save">
        {{ saving ? '保存中...' : '保存地址' }}
      </button>
      <button v-if="isEdit" class="del-btn" @click="remove">删除地址</button>
    </div>

    <AddressPicker ref="pickerRef" :default-value="pickerDefault" @confirm="onRegionConfirm" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import AddressPicker from '../components/AddressPicker.vue'
import { api } from '../api'
import { toast } from '../utils/toast'
import radioSel from '../assets/img/icon-radio-selected.png'
import radioNor from '../assets/img/icon-radio.png'

const route = useRoute()
const router = useRouter()

// 后端约定：0 女 / 1 男
const sexes = [
  { value: '0', name: '女士' },
  { value: '1', name: '男士' }
]
const tags = [
  { value: 1, name: '公司' },
  { value: 2, name: '家' },
  { value: 3, name: '学校' }
]

const form = ref({
  id: null,
  consignee: '',
  phone: '',
  sex: '0',
  label: 2,
  provinceCode: '',
  provinceName: '',
  cityCode: '',
  cityName: '',
  districtCode: '',
  districtName: '',
  detail: ''
})

const saving = ref(false)
const pickerRef = ref(null)
const pickerDefault = ref([])

const isEdit = computed(() => !!route.query.id)
const regionText = computed(() => {
  if (!form.value.provinceName) return ''
  return [form.value.provinceName, form.value.cityName, form.value.districtName]
    .filter(Boolean)
    .join(' ')
})

async function loadDetail() {
  try {
    const res = await api.addressById(route.query.id)
    const d = res.data
    form.value = {
      id: d.id,
      consignee: d.consignee || '',
      phone: d.phone || '',
      sex: String(d.sex || '0'),
      label: Number(d.label || 2),
      provinceCode: d.provinceCode || '',
      provinceName: d.provinceName || '',
      cityCode: d.cityCode || '',
      cityName: d.cityName || '',
      districtCode: d.districtCode || '',
      districtName: d.districtName || '',
      detail: d.detail || ''
    }
    pickerDefault.value = [d.provinceCode, d.cityCode, d.districtCode].filter(Boolean)
  } catch (e) {
    toast('加载地址失败')
  }
}

function onRegionConfirm(payload) {
  form.value.provinceCode = payload.provinceCode
  form.value.provinceName = payload.provinceName
  form.value.cityCode = payload.cityCode
  form.value.cityName = payload.cityName
  form.value.districtCode = payload.districtCode
  form.value.districtName = payload.districtName
}

function validate() {
  if (!form.value.consignee) return '联系人不能为空'
  if (!/^1\d{10}$/.test(form.value.phone)) return '请输入正确的手机号'
  if (!form.value.provinceName) return '请选择所在地区'
  if (!form.value.detail) return '请输入详细地址'
  return ''
}

async function save() {
  const msg = validate()
  if (msg) {
    toast(msg)
    return
  }
  if (saving.value) return
  saving.value = true
  try {
    const payload = { ...form.value }
    if (isEdit.value) {
      await api.addressEdit(payload)
    } else {
      delete payload.id
      await api.addressAdd(payload)
    }
    toast('保存成功')
    router.replace('/address/list')
  } catch (e) {
    toast(e.userMessage || '保存失败')
  } finally {
    saving.value = false
  }
}

async function remove() {
  try {
    await api.addressDelete(form.value.id)
    toast('地址删除成功')
    router.replace('/address/list')
  } catch (e) {
    toast('删除失败')
  }
}

onMounted(() => {
  if (isEdit.value) loadDetail()
})
</script>

<style scoped>
.edit-body {
  padding-bottom: 40px;
}

.form-card {
  padding: 0 14px;
  margin-top: 12px;
}

.form-row {
  display: flex;
  align-items: center;
  min-height: 56px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.form-row:last-child {
  border-bottom: none;
}

.form-label {
  width: 84px;
  flex-shrink: 0;
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

.form-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333333;
  height: 40px;
  min-width: 0;
}

.form-input::-webkit-input-placeholder {
  color: #bbbbbb;
}

.sex-group {
  display: flex;
  align-items: center;
  gap: 18px;
  padding-right: 10px;
}

.sex-item {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  cursor: pointer;
}

.radio-img {
  width: 18px;
  height: 18px;
  margin-right: 5px;
}

.region-value {
  line-height: 40px;
}

.region-value.placeholder {
  color: #bbbbbb;
}

.region-arrow {
  color: #cccccc;
  font-size: 18px;
  padding-right: 8px;
}

.tag-group {
  display: flex;
  gap: 10px;
}

.tag-item {
  border: 1px solid #e5e4e4;
  border-radius: 4px;
  font-size: 12px;
  padding: 4px 12px;
  color: #333333;
  background: #ffffff;
  cursor: pointer;
}

.tag-item.active {
  border-color: #ffc200;
  background: #fff8e1;
  color: #b07d00;
}

.save-btn {
  display: block;
  width: calc(100% - 24px);
  margin: 30px 12px 0;
  height: 48px;
  line-height: 48px;
  font-size: 16px;
  border-radius: 24px;
}

.save-btn[disabled] {
  opacity: 0.6;
}

.del-btn {
  display: block;
  width: calc(100% - 24px);
  margin: 16px 12px 0;
  height: 46px;
  line-height: 46px;
  border: none;
  border-radius: 23px;
  background: #f6f6f6;
  color: #333333;
  font-size: 14px;
  cursor: pointer;
}
</style>