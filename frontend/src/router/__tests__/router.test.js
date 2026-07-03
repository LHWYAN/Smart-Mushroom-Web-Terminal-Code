import { describe, it, expect, vi, beforeEach } from 'vitest'

const { isLoggedInMock } = vi.hoisted(() => ({
  isLoggedInMock: vi.fn(),
}))

vi.mock('../../api', () => ({
  isLoggedIn: isLoggedInMock,
}))

import router from '../index'

describe('router - 路由配置', () => {
  const routes = router.options.routes

  it('定义了 8 条路由', () => {
    expect(routes).toHaveLength(8)
  })

  it('根路径 / 重定向到 /dashboard', () => {
    const rootRoute = routes.find((r) => r.path === '/')
    expect(rootRoute).toBeDefined()
    expect(rootRoute.redirect).toBe('/dashboard')
  })

  it('/login 路由存在且 meta.noAuth 为 true', () => {
    const route = routes.find((r) => r.path === '/login')
    expect(route).toBeDefined()
    expect(route.meta.title).toBe('登录')
    expect(route.meta.noAuth).toBe(true)
  })

  it('/register 路由存在且 meta.noAuth 为 true', () => {
    const route = routes.find((r) => r.path === '/register')
    expect(route).toBeDefined()
    expect(route.meta.title).toBe('注册')
    expect(route.meta.noAuth).toBe(true)
  })

  it('/dashboard 路由存在且 meta.title 为 "仪表盘"', () => {
    const route = routes.find((r) => r.path === '/dashboard')
    expect(route).toBeDefined()
    expect(route.meta.title).toBe('仪表盘')
    expect(route.meta.noAuth).toBeUndefined()
  })

  it('/devices 路由存在且 meta.title 为 "设备管理"', () => {
    const route = routes.find((r) => r.path === '/devices')
    expect(route).toBeDefined()
    expect(route.meta.title).toBe('设备管理')
  })

  it('/data 路由存在且 meta.title 为 "数据管理"', () => {
    const route = routes.find((r) => r.path === '/data')
    expect(route).toBeDefined()
    expect(route.meta.title).toBe('数据管理')
  })

  it('/commands 路由存在且 meta.title 为 "命令管理"', () => {
    const route = routes.find((r) => r.path === '/commands')
    expect(route).toBeDefined()
    expect(route.meta.title).toBe('命令管理')
  })

  it('/ai 路由存在且 meta.title 为 "种植问答"', () => {
    const route = routes.find((r) => r.path === '/ai')
    expect(route).toBeDefined()
    expect(route.meta.title).toBe('种植问答')
  })

  it('所有非根路由都使用懒加载组件', () => {
    const businessRoutes = routes.filter((r) => r.path !== '/')
    businessRoutes.forEach((route) => {
      expect(route.component).toBeTypeOf('function')
    })
  })

  it('resolve("/") 返回根路径（重定向在实际导航时触发）', () => {
    const resolved = router.resolve('/')
    expect(resolved.fullPath).toBe('/')
  })
})

describe('router - 导航守卫', () => {
  beforeEach(() => {
    isLoggedInMock.mockReset()
  })

  it('未登录访问 /dashboard 重定向到 /login', async () => {
    isLoggedInMock.mockReturnValue(false)
    await router.push('/dashboard')
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('未登录访问 /login 允许进入', async () => {
    isLoggedInMock.mockReturnValue(false)
    await router.push('/login')
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('已登录访问 /login 重定向到 /dashboard', async () => {
    isLoggedInMock.mockReturnValue(true)
    await router.push('/devices')
    await router.push('/login')
    expect(router.currentRoute.value.path).toBe('/dashboard')
  })

  it('已登录访问 /register 重定向到 /dashboard', async () => {
    isLoggedInMock.mockReturnValue(true)
    await router.push('/register')
    expect(router.currentRoute.value.path).toBe('/dashboard')
  })

  it('已登录访问 /devices 允许进入', async () => {
    isLoggedInMock.mockReturnValue(true)
    await router.push('/devices')
    expect(router.currentRoute.value.path).toBe('/devices')
  })
})
