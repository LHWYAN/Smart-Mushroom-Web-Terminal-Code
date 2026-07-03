import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '../api'

const routes = [
  { path: '/', redirect: '/dashboard' },

  // ─── 无需鉴权 ─────────────────────────────
  { path: '/login', component: () => import('../views/Login.vue'), meta: { title: '登录', noAuth: true } },
  { path: '/register', component: () => import('../views/Register.vue'), meta: { title: '注册', noAuth: true } },

  // ─── 需要鉴权 ─────────────────────────────
  { path: '/dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '仪表盘' } },
  { path: '/devices', component: () => import('../views/DeviceManage.vue'), meta: { title: '设备管理' } },
  { path: '/data', component: () => import('../views/DataManage.vue'), meta: { title: '数据管理' } },
  { path: '/commands', component: () => import('../views/CommandManage.vue'), meta: { title: '命令管理' } },
  { path: '/ai', component: () => import('../views/AiAssistant.vue'), meta: { title: '种植问答' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// ─── 导航守卫 ─────────────────────────────
router.beforeEach((to, from, next) => {
  // 未登录且访问需要鉴权的页面 → 跳转到登录页
  if (!to.meta.noAuth && !isLoggedIn()) {
    next('/login')
  }
  // 已登录用户访问登录/注册页 → 跳转到首页
  else if ((to.path === '/login' || to.path === '/register') && isLoggedIn()) {
    next('/dashboard')
  }
  else {
    next()
  }
})

export default router
