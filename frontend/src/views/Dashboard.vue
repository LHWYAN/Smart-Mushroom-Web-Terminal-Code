<template>
  <div>
    <el-row :gutter="12" class="sensor-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="item in cards" :key="item.label">
        <el-card shadow="hover" class="sensor-card" :style="{ '--accent': item.color }">
          <div class="icon">{{ item.icon }}</div>
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ item.value }}</div>
          <div class="unit">{{ item.unit }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" style="margin-top: 12px">
      <el-col :span="12"><el-card shadow="never" class="chart-card"><div ref="tempChartRef" style="height:240px"></div></el-card></el-col>
      <el-col :span="12"><el-card shadow="never" class="chart-card"><div ref="humiChartRef" style="height:240px"></div></el-card></el-col>
    </el-row>

    <el-card shadow="never" class="table-card" style="margin-top: 12px">
      <template #header><span>最新数据</span></template>
      <el-table :data="history" size="small" stripe max-height="280">
        <el-table-column prop="create_time" label="时间" width="170" />
        <el-table-column prop="Temp" label="温度" />
        <el-table-column prop="Humi" label="湿度" />
        <el-table-column prop="Lumi" label="光照" />
        <el-table-column prop="Smoke" label="烟雾" />
        <el-table-column prop="CO2" label="CO₂" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getLatest, getHistory } from '../api'

const latest = ref({})
const history = ref([])
const tempChartRef = ref(null)
const humiChartRef = ref(null)
let tempChart, humiChart, timer

const cards = computed(() => [
  { icon: '🌡️', label: '温度', value: latest.value.Temp || '--', unit: '°C', color: '#ff6b6b' },
  { icon: '💧', label: '湿度', value: latest.value.Humi || '--', unit: '%RH', color: '#4ecdc4' },
  { icon: '☀️', label: '光照', value: latest.value.Lumi || '---', unit: 'Lux', color: '#ffd93d' },
  { icon: '💨', label: '烟雾', value: latest.value.Smoke || '0', unit: '%', color: '#ff8a5c' },
  { icon: '🫁', label: 'CO₂', value: latest.value.CO2 || '---', unit: 'ppm', color: '#a29bfe' },
])

function renderCharts(rows) {
  const labels = rows.map(r => (r.create_time || '').split(' ')[1] || '')
  const tempData = rows.map(r => parseFloat(r.Temp) || 0)
  const humiData = rows.map(r => parseFloat(r.Humi) || 0)
  const darkAxis = { axisLine: { lineStyle: { color: '#2a4055' } }, axisLabel: { color: '#8899aa' }, splitLine: { lineStyle: { color: '#1a2d42' } } }

  if (!tempChart) tempChart = echarts.init(tempChartRef.value)
  tempChart.setOption({
    title: { text: '温度趋势', textStyle: { color: '#8899aa', fontSize: 13 } },
    grid: { left: 40, right: 16, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: labels, ...darkAxis },
    yAxis: { type: 'value', ...darkAxis },
    series: [{ type: 'line', data: tempData, smooth: true, areaStyle: { color: 'rgba(255,107,107,0.15)' }, lineStyle: { color: '#ff6b6b' }, itemStyle: { color: '#ff6b6b' } }],
  })

  if (!humiChart) humiChart = echarts.init(humiChartRef.value)
  humiChart.setOption({
    title: { text: '湿度趋势', textStyle: { color: '#8899aa', fontSize: 13 } },
    grid: { left: 40, right: 16, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: labels, ...darkAxis },
    yAxis: { type: 'value', ...darkAxis },
    series: [{ type: 'line', data: humiData, smooth: true, areaStyle: { color: 'rgba(78,205,196,0.15)' }, lineStyle: { color: '#4ecdc4' }, itemStyle: { color: '#4ecdc4' } }],
  })
}

async function load() {
  const l = await getLatest()
  if (l.code === 0 && l.data) latest.value = l.data
  const h = await getHistory({ limit: 50 })
  if (h.code === 0) {
    history.value = (h.data || []).slice(0, 20)
    renderCharts([...(h.data || [])].reverse())
  }
}

onMounted(() => { load(); timer = setInterval(load, 5000) })
onUnmounted(() => { clearInterval(timer); tempChart?.dispose(); humiChart?.dispose() })
</script>

<style scoped>
.sensor-card { text-align: center; background: linear-gradient(145deg, #1a2d42, #152433); border: 1px solid #2a4055; color: #e0e0e0; }
.sensor-card :deep(.el-card__body) { padding: 16px; }
.sensor-card .icon { font-size: 24px; }
.sensor-card .label { font-size: 12px; color: #8899aa; margin-top: 4px; }
.sensor-card .value { font-size: 26px; font-weight: 700; color: var(--accent); margin: 4px 0; }
.sensor-card .unit { font-size: 11px; color: #556677; }
.chart-card, .table-card { background: #152433; border: 1px solid #2a4055; color: #e0e0e0; }
.chart-card :deep(.el-card__body), .table-card :deep(.el-card__header) { background: #152433; color: #8899aa; border-color: #2a4055; }
.table-card :deep(.el-table) { --el-table-bg-color: #152433; --el-table-tr-bg-color: #152433; --el-table-header-bg-color: #1a2d42; --el-table-text-color: #e0e0e0; --el-table-header-text-color: #8899aa; --el-table-row-hover-bg-color: #1a2d4288; --el-table-border-color: #1a2d42; }
</style>
