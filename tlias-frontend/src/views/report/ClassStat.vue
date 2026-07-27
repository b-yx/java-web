<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>数据统计管理</el-breadcrumb-item>
          <el-breadcrumb-item>班级人数统计</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">班级人数统计</div>
      </div>
    </div>

    <!-- Chart -->
    <div class="stat-container">
      <div class="stat-card" style="flex: 1;">
        <div class="stat-card-title">
          <el-icon style="vertical-align: middle; margin-right: 6px; color: #E6A23C;">
            <Histogram />
          </el-icon>
          各班级人数统计
        </div>
        <div ref="classChartRef" class="stat-chart" />
      </div>
      <div class="stat-card" style="flex: 0 0 360px;">
        <div class="stat-card-title">
          <el-icon style="vertical-align: middle; margin-right: 6px; color: #409EFF;">
            <List />
          </el-icon>
          班级人数明细
        </div>
        <el-table :data="tableData" stripe border style="width: 100%;" max-height="360">
          <el-table-column label="班级" prop="name" min-width="140" />
          <el-table-column label="人数" prop="count" width="80" align="center">
            <template #default="{ row }">
              <el-tag effect="plain" :type="countTag(row.count)">{{ row.count }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Histogram, List } from '@element-plus/icons-vue'
import { getStudentCountData } from '@/api/dept'

const classChartRef = ref(null)
let chartInstance = null

const chartData = ref({ clazzList: [], dataList: [] })

const tableData = computed(() => {
  return chartData.value.clazzList.map((name, i) => ({
    name,
    count: chartData.value.dataList[i] || 0
  }))
})

function countTag(count) {
  if (count >= 80) return 'danger'
  if (count >= 60) return 'warning'
  return 'success'
}

function initChart(data) {
  if (!classChartRef.value) return

  chartInstance = echarts.init(classChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: function(params) {
        const p = params[0]
        return `${p.name}<br/>人数: ${p.value} 人`
      }
    },
    grid: {
      left: '5%',
      right: '8%',
      bottom: '15%',
      top: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.clazzList,
      axisLabel: {
        fontSize: 11,
        color: '#666',
        rotate: data.clazzList.length > 6 ? 35 : 0,
        interval: 0
      },
      axisLine: { lineStyle: { color: '#ddd' } }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontSize: 12, color: '#666' },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [
      {
        type: 'bar',
        data: data.dataList,
        barWidth: '40%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 0.5, color: '#5AB0FF' },
            { offset: 1, color: '#79BBFF' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#1677FF' },
              { offset: 1, color: '#409EFF' }
            ])
          }
        },
        label: {
          show: true,
          position: 'top',
          color: '#666',
          fontSize: 13
        }
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
    const data = await getStudentCountData()
    chartData.value = data || { clazzList: [], dataList: [] }
    initChart(data || { clazzList: ['暂无数据'], dataList: [0] })
  } catch {
    chartData.value = { clazzList: [], dataList: [] }
    initChart({ clazzList: ['暂无数据'], dataList: [0] })
  }

  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
