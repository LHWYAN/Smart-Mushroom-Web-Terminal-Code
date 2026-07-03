<template>
  <div class="app-shell">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <div class="nav-brand">
        <span class="brand-icon">🍄</span>
        <div>
          <div class="brand-name">智慧蘑菇农场</div>
          <div class="brand-sub">Web 监控终端</div>
        </div>
      </div>

      <nav class="nav-tabs">
        <router-link
          v-for="tab in tabs"
          :key="tab.path"
          :to="tab.path"
          class="nav-tab"
          :class="{ active: route.path === tab.path }"
        >
          <span class="tab-icon">{{ tab.icon }}</span>
          <span>{{ tab.label }}</span>
        </router-link>
      </nav>

      <div class="nav-stats">
        <span class="stat-item">数据 <em>{{ stats.total_records || 0 }}</em></span>
        <span class="stat-item">设备 <em>{{ stats.total_devices || 0 }}</em></span>
        <span v-if="updateTime" class="stat-time">{{ updateTime }}</span>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStatistics, getLatest } from './api'

const route = useRoute()
const stats = ref({})
const updateTime = ref('')
let timer = null

const tabs = [
  { path: '/dashboard', label: '首页', icon: '🏠' },
  { path: '/devices', label: '设备', icon: '📱' },
  { path: '/data', label: '数据', icon: '📊' },
  { path: '/commands', label: '命令', icon: '📋' },
  { path: '/ai', label: '问答', icon: '💬' },
]

async function refreshHeader() {
  try {
    const s = await getStatistics()
    if (s.code === 0) stats.value = s.data || {}
    const l = await getLatest()
    if (l.code === 0 && l.data?.create_time) {
      updateTime.value = l.data.create_time.split(' ')[1] || l.data.create_time
    }
  } catch (_) {}
}

onMounted(() => {
  refreshHeader()
  timer = setInterval(refreshHeader, 5000)
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: var(--bg-page);
}

.top-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 24px;
  height: 60px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-light);
  box-shadow: 0 2px 8px rgba(200, 212, 205, 0.25);
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.brand-icon {
  font-size: 28px;
  line-height: 1;
}

.brand-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-title);
  line-height: 1.2;
}

.brand-sub {
  font-size: 11px;
  color: var(--text-hint);
}

.nav-tabs {
  display: flex;
  gap: 4px;
  flex: 1;
}

.nav-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  text-decoration: none;
  font-size: 14px;
  color: var(--text-sub);
  transition: all 0.2s;
}

.nav-tab:hover {
  background: var(--bg-card-light);
  color: var(--text-title);
}

.nav-tab.active {
  background: var(--el-color-primary-light-9);
  color: var(--color-primary-dark);
  font-weight: 600;
}

.tab-icon {
  font-size: 16px;
}

.nav-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: var(--text-sub);
  flex-shrink: 0;
}

.nav-stats em {
  font-style: normal;
  font-weight: 700;
  color: var(--color-primary-dark);
}

.stat-time {
  font-size: 12px;
  color: var(--text-hint);
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 20px 32px;
}

@media (max-width: 900px) {
  .top-nav {
    flex-wrap: wrap;
    height: auto;
    padding: 12px 16px;
    gap: 12px;
  }
  .nav-tabs {
    order: 3;
    width: 100%;
    overflow-x: auto;
  }
  .nav-stats {
    display: none;
  }
}
</style>
