import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 60000,
})

api.interceptors.response.use(
  (res) => res.data,
  (err) => Promise.reject(err),
)

export default api

export const getLatest = () => api.get('/latest')
export const getHistory = (params) => api.get('/history', { params })
export const getStatistics = () => api.get('/statistics')
export const getHealth = () => api.get('/health')

export const getDevices = () => api.get('/devices')
export const createDevice = (params) => api.post('/devices', null, { params })
export const updateDevice = (id, params) => api.put(`/devices/${id}`, null, { params })
export const deleteDevice = (id) => api.delete(`/devices/${id}`)

export const insertSensorData = (params) => api.post('/sensor-data', null, { params })
export const deleteSensorData = (id) => api.delete(`/sensor-data/${id}`)
export const batchDeleteSensorData = (params) => api.delete('/sensor-data', { params })

export const getCommands = (params) => api.get('/commands', { params })
export const insertCommand = (params) => api.post('/commands', null, { params })
export const updateCommandStatus = (id, status) => api.put(`/commands/${id}`, null, { params: { status } })
export const deleteCommand = (id) => api.delete(`/commands/${id}`)

export const aiChat = (question) => api.post('/ai/chat', { question })

// ─── 认证 / Token 管理 ─────────────────────────────
let authToken = localStorage.getItem('auth_token') || ''
let currentUser = JSON.parse(localStorage.getItem('auth_user') || 'null')

/** 设置认证信息（token + 用户对象），持久化到 localStorage 并更新 axios header */
export function setAuth(token, user) {
  authToken = token
  currentUser = user
  localStorage.setItem('auth_token', token || '')
  localStorage.setItem('auth_user', user ? JSON.stringify(user) : '')
  if (token) api.defaults.headers.common['Authorization'] = 'Bearer ' + token
  else delete api.defaults.headers.common['Authorization']
}

export function getAuthUser() { return currentUser }
export function isLoggedIn() { return !!authToken }

// 初始化：已有 token 则设置请求头
if (authToken) api.defaults.headers.common['Authorization'] = 'Bearer ' + authToken

// ─── 认证 API ──────────────────────────────────────
export const register = (data) => api.post('/auth/register', data)
export const login = (data) => api.post('/auth/login', data)
export const getProfile = () => api.get('/auth/profile')
export const logout = () => api.post('/auth/logout')
