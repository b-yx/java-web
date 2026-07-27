<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>数据统计管理</el-breadcrumb-item>
          <el-breadcrumb-item>学员学历统计</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">学员学历统计</div>
      </div>
    </div>

    <!-- Chart -->
    <div class="stat-container">
      <div class="stat-card" style="flex: 1;">
        <div class="stat-card-title">
          <el-icon style="vertical-align: middle; margin-right: 6px; color: #409EFF;">
            <PieChart />
          </el-icon>
          学员学历分布
        </div>
        <div ref="degreeChartRef" class="stat-chart" />
      </div>
      <div class="stat-card" style="flex: 1;">
        <div class="stat-card-title">
          <el-icon style="vertical-align: middle; margin-right: 6px; color: #67C23A;">
            <List />
          </el-icon>
          学历数据明细
        </div>
        <el-table :data="degreeData" stripe border style="width: 100%;">
          <el-table-column label="学历" prop="name" align="center" />
          <el-table-column label="人数" prop="value" align="center">
            <template #default="{ row }">
              <el-tag effect="plain" :type="tagType(row.name)">{{ row.value }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="占比" align="center">
            <template #default="{ row }">
              {{ calcPercent(row.value) }}%
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { PieChart, List } from '@element-plus/icons-vue'
import { getStudentDegreeData } from '@/api/dept'

const degreeChartRef = ref(null)
const degreeData = ref([])
let chartInstance = null

const degreeColorMap = {
  '初中': '#E6A23C',
  '高中': '#67C23A',
  '大专': '#409EFF',
  '本科': '#8B5CF6',
  '硕士': '#F56C6C'
}

function tagType(name) {
  if (name === '初中') return 'warning'
  if (name === '高中') return 'success'
  if (name === '大专') return 'primary'
  if (name === '本科') return 'danger'
  if (name === '硕士') return 'info'
  return ''
}

function calcPercent(value) {
  const total = degreeData.value.reduce((s, d) => s + d.value, 0)
  if (!total) return '0.00'
  return ((value / total) * 100).toFixed(2)
}

function initChart(data) {
  if (!degreeChartRef.value) return

  chartInstance = echarts.init(degreeChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}人 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { fontSize: 13 }
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: true,
        padAngle: 2,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          fontSize: 13,
          formatter: '{b}\n{c}人',
          color: '#666'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: data.map(item => ({
          name: item.name,
          value: item.value,
          itemStyle: {
            color: degreeColorMap[item.name] || '#909399'
          }
        }))
      }
    ]
  }

  chartInstance.setOption(option)
}

function handleResize() {
  chartInstance?.resize()
}

onMounted(async () => {
  await nextTick()

  try {
    const data = await getStudentDegreeData()
    degreeData.value = data || []
    initChart(data || [])
  } catch {
    degreeData.value = []
    initChart([{ name: '暂无数据', value: 1 }])
  }

  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
