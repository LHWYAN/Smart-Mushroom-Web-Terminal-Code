<template>
  <div class="dashboard">
    <!-- 大棚 Header（对齐鸿蒙端） -->
    <section class="greenhouse-header">
      <div class="header-bg"></div>
      <div class="header-content">
        <div class="header-left">
          <h1 class="gh-name">1号蘑菇大棚</h1>
          <div class="gh-status">
            <span class="status-dot"></span>
            <span>运行正常</span>
          </div>
        </div>
        <div class="header-weather">
          <span class="weather-icon">⛅</span>
          <span>多云 18°C</span>
        </div>
      </div>
      <div v-if="alertCount > 0" class="alert-bar" @click="goAlerts">
        <span>⚠️ {{ alertCount }} 条待处理告警</span>
        <span class="alert-link">查看 ›</span>
      </div>
    </section>

    <!-- 概览统计 -->
    <GlassCard class="overview-card">
      <div class="overview-grid">
        <div v-for="item in overview" :key="item.label" class="overview-item">
          <div class="overview-accent" :style="{ background: item.color }"></div>
          <div class="overview-value">{{ item.value }}</div>
          <div class="overview-label">{{ item.label }}</div>
        </div>
      </div>
    </GlassCard>

    <!-- 实时传感器 -->
    <div class="sensor-list">
      <GlassCard v-for="s in sensors" :key="s.key" compact class="sensor-item">
        <div class="sensor-row">
          <div class="sensor-icon-wrap" :style="{ background: s.bg }">
            <span>{{ s.icon }}</span>
          </div>
          <div class="sensor-info">
            <div class="sensor-label">{{ s.label }}</div>
            <div class="sensor-value">
              {{ s.value }}<span class="sensor-unit">{{ s.unit }}</span>
            </div>
          </div>
          <span class="sensor-status" :class="s.statusClass"></span>
        </div>
      </GlassCard>
    </div>

    <!-- 趋势图表 -->
    <div class="chart-row">
      <GlassCard class="chart-wrap">
        <template #header>24 小时趋势 · 温度</template>
        <div ref="tempChartRef" class="chart-box"></div>
      </GlassCard>
      <GlassCard class="chart-wrap">
        <template #header>24 小时趋势 · 湿度</template>
        <div ref="humiChartRef" class="chart-box"></div>
      </GlassCard>
    </div>

    <!-- 最新数据 -->
    <GlassCard class="data-table-card">
      <template #header>最新采集记录</template>
      <el-table :data="history" size="small" stripe max-height="300">
        <el-table-column prop="create_time" label="时间" width="170" />
        <el-table-column prop="Temp" label="温度(°C)" width="90" />
        <el-table-column prop="Humi" label="湿度(%)" width="90" />
        <el-table-column prop="Lumi" label="光照" width="80" />
        <el-table-column prop="Smoke" label="烟雾" width="80" />
        <el-table-column prop="CO2" label="CO₂" width="80" />
        <el-table-column prop="LampST" label="照明" width="70" />
        <el-table-column prop="VentST" label="通风" width="70" />
      </el-table>
    </GlassCard>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import GlassCard from '../components/GlassCard.vue'
import { getLatest, getHistory, getStatistics } from '../api'

const router = useRouter()
const latest = ref({})
const history = ref([])
const stats = ref({})
const tempChartRef = ref(null)
const humiChartRef = ref(null)
let tempChart, humiChart, timer

const alertCount = computed(() => {
  const smoke = parseFloat(latest.value.Smoke) || 0
  return smoke > 30 ? 1 : 0
})

const overview = computed(() => [
  { label: '在线设备', value: stats.value.total_devices || 0, color: 'var(--color-success)' },
  { label: '数据记录', value: stats.value.total_records || 0, color: 'var(--color-accent)' },
  { label: '命令记录', value: stats.value.total_commands || 0, color: 'var(--color-warning)' },
])

function sensorStatus(val, type) {
  const n = parseFloat(val)
  if (isNaN(n)) return 'normal'
  if (type === 'temp') return n > 32 || n < 15 ? 'warn' : 'normal'
  if (type === 'smoke') return n > 30 ? 'danger' : n > 10 ? 'warn' : 'normal'
  if (type === 'co2') return n > 800 ? 'warn' : 'normal'
  return 'normal'
}

const sensors = computed(() => {
  const d = latest.value
  const list = [
    { key: 'temp', icon: '🌡️', label: '温度', value: d.Temp || '--', unit: '°C', bg: 'var(--sensor-temp)', type: 'temp' },
    { key: 'humi', icon: '💧', label: '空气湿度', value: d.Humi || '--', unit: '%RH', bg: 'var(--sensor-humi)', type: 'humi' },
    { key: 'co2', icon: '🫁', label: 'CO₂ 浓度', value: d.CO2 || '--', unit: 'ppm', bg: 'var(--sensor-co2)', type: 'co2' },
    { key: 'lumi', icon: '☀️', label: '光照强度', value: d.Lumi || '--', unit: 'Lux', bg: 'var(--sensor-lumi)', type: 'lumi' },
    { key: 'smoke', icon: '💨', label: '烟雾浓度', value: d.Smoke ?? '0', unit: '%', bg: 'var(--sensor-smoke)', type: 'smoke' },
  ]
  return list.map(s => {
    const st = sensorStatus(s.value, s.type)
    return { ...s, statusClass: st === 'danger' ? 'danger' : st === 'warn' ? 'warn' : 'ok' }
  })
})

function goAlerts() {
  router.push('/data')
}

const lightAxis = {
  axisLine: { lineStyle: { color: '#dce1e5' } },
  axisLabel: { color: '#7e929f', fontSize: 11 },
  splitLine: { lineStyle: { color: '#eef2f0', type: 'dashed' } },
}

function renderCharts(rows) {
  const labels = rows.map(r => (r.create_time || '').split(' ')[1] || '')
  const tempData = rows.map(r => parseFloat(r.Temp) || 0)
  const humiData = rows.map(r => parseFloat(r.Humi) || 0)

  if (!tempChart) tempChart = echarts.init(tempChartRef.value)
  tempChart.setOption({
    grid: { left: 44, right: 16, top: 16, bottom: 28 },
    xAxis: { type: 'category', data: labels, ...lightAxis },
    yAxis: { type: 'value', ...lightAxis },
    series: [{
      type: 'line', data: tempData, smooth: true, symbol: 'circle', symbolSize: 4,
      lineStyle: { color: '#f2b8b0', width: 2 },
      itemStyle: { color: '#f2b8b0' },
      areaStyle: { color: 'rgba(242,184,176,0.25)' },
    }],
  })

  if (!humiChart) humiChart = echarts.init(humiChartRef.value)
  humiChart.setOption({
    grid: { left: 44, right: 16, top: 16, bottom: 28 },
    xAxis: { type: 'category', data: labels, ...lightAxis },
    yAxis: { type: 'value', ...lightAxis },
    series: [{
      type: 'line', data: humiData, smooth: true, symbol: 'circle', symbolSize: 4,
      lineStyle: { color: '#a8d0f0', width: 2 },
      itemStyle: { color: '#a8d0f0' },
      areaStyle: { color: 'rgba(168,208,240,0.25)' },
    }],
  })
}

async function load() {
  const [l, h, s] = await Promise.all([
    getLatest(),
    getHistory({ limit: 50 }),
    getStatistics(),
  ])
  if (l.code === 0 && l.data) latest.value = l.data
  if (s.code === 0) stats.value = s.data || {}
  if (h.code === 0) {
    history.value = (h.data || []).slice(0, 20)
    renderCharts([...(h.data || [])].reverse())
  }
}

onMounted(() => {
  load()
  timer = setInterval(load, 5000)
  window.addEventListener('resize', () => { tempChart?.resize(); humiChart?.resize() })
})
onUnmounted(() => {
  clearInterval(timer)
  tempChart?.dispose()
  humiChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* Header */
.greenhouse-header {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 6px 24px rgba(192, 208, 200, 0.45);
}

.header-bg {
  position: absolute;
  inset: 0;
  background: var(--bg-header);
}

.header-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 22px 24px 18px;
  color: var(--text-white);
}

.gh-name {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.gh-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  font-size: 14px;
  color: var(--text-white-sub);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-success);
  box-shadow: 0 0 6px rgba(181, 224, 195, 0.8);
}

.header-weather {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-white-sub);
  padding-top: 4px;
}

.weather-icon {
  font-size: 22px;
}

.alert-bar {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 24px;
  background: rgba(245, 224, 176, 0.95);
  color: #8a6914;
  font-size: 13px;
  cursor: pointer;
}

.alert-link {
  font-weight: 600;
}

/* Overview */
.overview-card {
  margin-top: 0;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  text-align: center;
}

.overview-item {
  position: relative;
  padding: 8px 12px 12px;
}

.overview-item + .overview-item {
  border-left: 1px solid var(--border-light);
}

.overview-accent {
  width: 28px;
  height: 3px;
  border-radius: 2px;
  margin: 0 auto 10px;
}

.overview-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-title);
  line-height: 1.1;
}

.overview-label {
  font-size: 13px;
  color: var(--text-sub);
  margin-top: 4px;
}

/* Sensors */
.sensor-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sensor-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.sensor-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.sensor-info {
  flex: 1;
  min-width: 0;
}

.sensor-label {
  font-size: 13px;
  color: var(--text-sub);
}

.sensor-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-title);
  margin-top: 2px;
}

.sensor-unit {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-hint);
  margin-left: 4px;
}

.sensor-status {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.sensor-status.ok { background: var(--color-success-text); }
.sensor-status.warn { background: var(--color-warning-text); }
.sensor-status.danger { background: var(--color-danger-text); }

/* Charts */
.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.chart-box {
  height: 220px;
}

@media (max-width: 768px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
  .overview-value {
    font-size: 22px;
  }
}
</style>
