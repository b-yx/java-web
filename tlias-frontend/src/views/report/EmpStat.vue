<template>
  <div class="page-container">
    <!-- Header -->
    <div class="page-header">
      <div>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>数据统计管理</el-breadcrumb-item>
          <el-breadcrumb-item>员工信息统计</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="page-title" style="margin-top: 8px;">员工信息统计</div>
      </div>
    </div>

    <!-- Charts -->
    <div class="stat-container">
      <div class="stat-card">
        <div class="stat-card-title">
          <el-icon style="vertical-align: middle; margin-right: 6px; color: #E6A23C;">
            <Histogram />
          </el-icon>
          员工职位统计
        </div>
        <div ref="jobChartRef" class="stat-chart" />
      </div>
      <div class="stat-card">
        <div class="stat-card-title">
          <el-icon style="vertical-align: middle; margin-right: 6px; color: #409EFF;">
            <PieChart />
          </el-icon>
          员工性别统计
        </div>
        <div ref="genderChartRef" class="stat-chart" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Histogram, PieChart } from '@element-plus/icons-vue'
import { getEmpJobData, getEmpGenderData } from '@/api/dept'

const jobChartRef = ref(null)
const genderChartRef = ref(null)
let jobChartInstance = null
let genderChartInstance = null

// Init job bar chart (vertical)
function initJobChart(data) {
  if (!jobChartRef.value) return

  jobChartInstance = echarts.init(jobChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '5%',
      right: '10%',
      bottom: '10%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.jobList,
      axisLabel: {
        fontSize: 12,
        color: '#666'
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
        barWidth: '45%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#FAD14A' },
            { offset: 0.5, color: '#FA8C16' },
            { offset: 1, color: '#D4380D' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#FFEC3D' },
              { offset: 1, color: '#FA541C' }
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

  jobChartInstance.setOption(option)
}

// Init gender donut chart
function initGenderChart(data) {
  if (!genderChartRef.value) return

  genderChartInstance = echarts.init(genderChartRef.value)
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
        radius: ['45%', '70%'],
        avoidLabelOverlap: true,
        padAngle: 3,
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
            color: item.name.includes('男')
              ? new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                  { offset: 0, color: '#409EFF' },
                  { offset: 1, color: '#79BBFF' }
                ])
              : new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                  { offset: 0, color: '#67C23A' },
                  { offset: 1, color: '#95D475' }
                ])
          }
        }))
      }
    ]
  }

  genderChartInstance.setOption(option)
}

// Resize handler
function handleResize() {
  jobChartInstance?.resize()
  genderChartInstance?.resize()
}

onMounted(async () => {
  // Delay slightly to ensure DOM renders
  await nextTick()

  try {
    const [jobData, genderData] = await Promise.all([
      getEmpJobData(),
      getEmpGenderData()
    ])
    initJobChart(jobData)
    initGenderChart(genderData)
  } catch {
    // Render with fallback empty data
    initJobChart({ jobList: ['暂无数据'], dataList: [0] })
    initGenderChart([{ name: '暂无数据', value: 1 }])
  }

  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  jobChartInstance?.dispose()
  genderChartInstance?.dispose()
})
</script>
