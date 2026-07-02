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
