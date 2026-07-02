<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="header-left">
        <h1>智慧蘑菇农场 · IoT 监控系统</h1>
        <p>Spring Boot + Vue 3 前后端分离 · 3表架构 · 完整 CRUD</p>
      </div>
      <div class="header-right">
        <el-tag type="success" effect="dark" size="small">运行中</el-tag>
        <span>数据 <strong>{{ stats.total_records || 0 }}</strong> 条</span>
        <span>设备 <strong>{{ stats.total_devices || 0 }}</strong> 台</span>
        <span class="time">{{ updateTime }}</span>
      </div>
    </el-header>

    <el-container>
      <el-aside width="200px" class="aside">
        <el-menu :default-active="route.path" router background-color="#0d1b2a" text-color="#8899aa" active-text-color="#00d4ff">
          <el-menu-item index="/dashboard"><el-icon><Odometer /></el-icon><span>仪表盘</span></el-menu-item>
          <el-menu-item index="/devices"><el-icon><Monitor /></el-icon><span>设备管理</span></el-menu-item>
          <el-menu-item index="/data"><el-icon><DataLine /></el-icon><span>数据管理</span></el-menu-item>
          <el-menu-item index="/commands"><el-icon><List /></el-icon><span>命令管理</span></el-menu-item>
          <el-menu-item index="/ai"><el-icon><ChatDotRound /></el-icon><span>AI 助手</span></el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStatistics, getLatest } from './api'

const route = useRoute()
const stats = ref({})
const updateTime = ref('')
let timer = null

async function refreshHeader() {
  try {
    const s = await getStatistics()
    if (s.code === 0) stats.value = s.data || {}
    const l = await getLatest()
    if (l.code === 0 && l.data?.create_time) updateTime.value = '更新: ' + l.data.create_time
  } catch (_) {}
}

onMounted(() => {
  refreshHeader()
  timer = setInterval(refreshHeader, 5000)
})
onUnmounted(() => clearInterval(timer))
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { background: #0f1923; color: #e0e0e0; font-family: "Microsoft YaHei", sans-serif; }
.layout { min-height: 100vh; }
.header {
  background: linear-gradient(135deg, #1a2a3a, #0d1b2a);
  border-bottom: 2px solid #1e90ff33;
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 24px; height: 64px !important;
}
.header-left h1 { font-size: 18px; color: #00d4ff; }
.header-left p { font-size: 12px; color: #8899aa; margin-top: 4px; }
.header-right { display: flex; gap: 16px; align-items: center; font-size: 13px; color: #8899aa; }
.header-right strong { color: #00d4ff; }
.header-right .time { color: #556677; font-size: 12px; }
.aside { background: #0d1b2a; border-right: 1px solid #1a2d42; }
.main { background: #0f1923; padding: 16px; }
</style>
