<template>
  <div class="phone">
    <NavBar title="地址管理" />

    <div class="page-body addr-body">
      <div v-if="list.length" class="addr-list">
        <div v-for="item in list" :key="item.id" class="addr-card">
          <div class="addr-main" :class="{ selectable: selectMode }" @click="choose(item)">
            <div class="addr-text">
              <span class="addr-tag" :class="'tag' + (item.label || 0)">{{ labelText(item.label) }}</span>
              <span class="addr-detail">
                {{ item.provinceName }}{{ item.cityName }}{{ item.districtName }}{{ item.detail }}
              </span>
            </div>
            <div class="addr-person">
              <span class="person-name">{{ item.consignee }} {{ String(item.sex) === '1' ? '先生' : '女士' }}</span>
              <span class="person-phone">{{ item.phone }}</span>
            </div>
            <img class="edit-icon" :src="editImg" alt="edit" @click.stop="edit(item)" />
          </div>
          <div class="addr-foot">
            <label class="default-label" @click="setDefault(item)">
              <span class="radio" :class="{ checked: Number(item.isDefault) === 1 }"></span>
              设为默认地址
            </label>
          </div>
        </div>
      </div>

      <Empty v-else icon="" text-label="暂无收货地址" style="height: 240px" />
    </div>

    <div class="footer-bar safe-bottom">
      <button class="btn-primary add-btn" @click="add">＋ 添加收货地址</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import Empty from '../components/Empty.vue'
import { useOrderStore } from '../store'
import { api } from '../api'
import { toast } from '../utils/toast'
import editImg from '../assets/img/edit.png'

const router = useRouter()
const orderStore = useOrderStore()

const list = ref([])

const selectMode = computed(() => orderStore.addressBackUrl === '/order/submit')

function labelText(label) {
  switch (String(label)) {
    case '1':
      return '公司'
    case '2':
      return '家'
    case '3':
      return '学校'
    default:
      return '其他'
  }
}

async function getList() {
  try {
    const res = await api.addressList()
    list.value = (res && res.data) || []
  } catch (e) {
    toast('加载地址失败')
  }
}

function add() {
  router.push('/address/edit')
}

function edit(item) {
  router.push({ path: '/address/edit', query: { id: item.id } })
}

// 选择地址：仅在从提交订单页进入时可选择并返回（不改动默认地址）
async function choose(item) {
  if (!selectMode.value) return
  orderStore.selectedAddress = item
  router.replace('/order/submit')
}

async function setDefault(item) {
  try {
    await api.addressSetDefault(item.id)
    toast('默认地址设置成功')
    getList()
  } catch (e) {
    toast(e.message || '操作失败')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.addr-body {
  padding-bottom: 90px;
}

.addr-card {
  background: #ffffff;
  border-radius: 12px;
  margin: 10px 12px;
  padding: 16px 14px 8px;
}

.addr-main {
  position: relative;
  padding-right: 30px;
  cursor: pointer;
}

.addr-main.select {
  cursor: pointer;
}

.addr-text {
  display: flex;
  align-items: flex-start;
}

.addr-tag {
  flex-shrink: 0;
  width: 40px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  border-radius: 4px;
  background: #e1f1fe;
  font-size: 12px;
  color: #333333;
  margin-right: 8px;
}

.addr-tag.tag1 {
  background: #e1f1fe;
}
.addr-tag.tag2 {
  background: #fef8e7;
}
.addr-tag.tag3 {
  background: #e7fef8;
}

.addr-detail {
  flex: 1;
  font-size: 14px;
  color: #333333;
  line-height: 20px;
}

.addr-person {
  margin-top: 10px;
  font-size: 13px;
  color: #999999;
  padding-left: 48px;
}

.person-phone {
  margin-left: 14px;
}

.edit-icon {
  position: absolute;
  right: 0;
  top: 4px;
  width: 20px;
  height: 20px;
}

.addr-foot {
  border-top: 1px solid #f0f0f0;
  margin-top: 12px;
  height: 40px;
  line-height: 40px;
}

.default-label {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  color: #333333;
  cursor: pointer;
  height: 40px;
}

.radio {
  width: 16px;
  height: 16px;
  border: 1px solid #cccccc;
  border-radius: 50%;
  margin-right: 6px;
  position: relative;
}

.radio.checked {
  border-color: #ffc200;
}

.radio.checked::after {
  content: '';
  position: absolute;
  left: 2px;
  top: 2px;
  width: 10px;
  height: 10px;
  background: #ffc200;
  border-radius: 50%;
}

.add-btn {
  width: calc(100% - 24px);
  margin: 10px 12px;
  height: 48px;
  line-height: 48px;
  font-size: 16px;
  border-radius: 24px;
}
</style>