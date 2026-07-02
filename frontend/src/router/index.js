import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '仪表盘' } },
  { path: '/devices', component: () => import('../views/DeviceManage.vue'), meta: { title: '设备管理' } },
  { path: '/data', component: () => import('../views/DataManage.vue'), meta: { title: '数据管理' } },
  { path: '/commands', component: () => import('../views/CommandManage.vue'), meta: { title: '命令管理' } },
  { path: '/ai', component: () => import('../views/AiAssistant.vue'), meta: { title: 'AI 助手' } },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
