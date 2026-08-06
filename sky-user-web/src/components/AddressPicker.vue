<template>
  <div v-if="visible" class="picker-mask" @click.self="close">
    <div class="picker-panel">
      <div class="picker-head">
        <span class="picker-cancel" @click="close">取消</span>
        <span class="picker-title">选择所在地区</span>
        <span class="picker-confirm" @click="confirm">确定</span>
      </div>
      <div class="picker-cols">
        <div class="picker-col">
          <div
            v-for="(item, ii) in provinceData"
            :key="item.value"
            class="picker-item"
            :class="{ active: indexArr[0] === ii }"
            @click="select(0, ii)"
          >
            {{ item.label }}
          </div>
        </div>
        <div class="picker-col">
          <div
            v-for="(item, ii) in cityList"
            :key="item.value"
            class="picker-item"
            :class="{ active: indexArr[1] === ii }"
            @click="select(1, ii)"
          >
            {{ item.label }}
          </div>
        </div>
        <div class="picker-col">
          <div
            v-for="(item, ii) in areaList"
            :key="item.value"
            class="picker-item"
            :class="{ active: indexArr[2] === ii }"
            @click="select(2, ii)"
          >
            {{ item.label }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import provinceData from '../utils/province'
import cityData from '../utils/city'
import areaData from '../utils/area'

const props = defineProps({
  // 初始值：[省value, 市value, 区value]
  defaultValue: { type: Array, default: () => [] }
})

const emit = defineEmits(['confirm'])

const visible = ref(false)
const indexArr = ref([0, 0, 0])

const cityList = computed(() => {
  const list = cityData[indexArr.value[0]]
  return Array.isArray(list) ? list : []
})

const areaList = computed(() => {
  const list = areaData[indexArr.value[0]]
  const sub = Array.isArray(list) ? list[indexArr.value[1]] : []
  return Array.isArray(sub) ? sub : []
})

function findIndex(list, value) {
  const idx = list.findIndex((i) => String(i.value) === String(value))
  return idx === -1 ? 0 : idx
}

function select(col, idx) {
  const next = [...indexArr.value]
  next[col] = idx
  if (col === 0) next[1] = 0
  if (col <= 1) next[2] = 0
  indexArr.value = next
}

function open() {
  const def = props.defaultValue || []
  const pi = findIndex(provinceData, def[0])
  indexArr.value = [pi, 0, 0]
  visible.value = true
  setTimeout(() => {
    const ci = findIndex(cityList.value, def[1])
    indexArr.value = [pi, ci, 0]
    setTimeout(() => {
      indexArr.value = [pi, ci, findIndex(areaList.value, def[2])]
    }, 0)
  }, 0)
}

function close() {
  visible.value = false
}

function confirm() {
  const [p, c, a] = indexArr.value
  const prov = provinceData[p]
  const city = cityList.value[c]
  const area = areaList.value[a]
  emit('confirm', {
    provinceName: prov.label,
    provinceCode: prov.value,
    cityName: city ? city.label : '',
    cityCode: city ? city.value : '',
    districtName: area ? area.label : '',
    districtCode: area ? area.value : '',
    label: (prov.label + ' ' + (city ? city.label : '') + ' ' + (area ? area.label : '')).trim()
  })
  close()
}

defineExpose({ open })
</script>

<style scoped>
.picker-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 300;
  max-width: 480px;
  margin: 0 auto;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.picker-panel {
  width: 100%;
  background: #ffffff;
  border-radius: 16px 16px 0 0;
  padding-bottom: 24px;
  max-height: 55vh;
  display: flex;
  flex-direction: column;
}

.picker-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  padding: 0 16px;
  border-bottom: 1px solid #efefef;
  flex-shrink: 0;
}

.picker-cancel {
  color: #999999;
  font-size: 15px;
}

.picker-confirm {
  color: #007aff;
  font-size: 15px;
}

.picker-title {
  font-size: 16px;
  font-weight: 500;
}

.picker-cols {
  display: flex;
  height: 280px;
}

.picker-col {
  flex: 1;
  overflow-y: auto;
  border-right: 1px solid #f2f2f2;
}

.picker-col:last-child {
  border-right: none;
}

.picker-item {
  height: 44px;
  line-height: 44px;
  text-align: center;
  font-size: 14px;
  color: #333333;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
}

.picker-item.active {
  color: #007aff;
  font-weight: 600;
  background: #f6faff;
}
</style>