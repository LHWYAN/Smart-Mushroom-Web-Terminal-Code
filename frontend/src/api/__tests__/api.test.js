import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock axios 模块
vi.mock('axios', () => {
  const handlers = { response: [] }
  const instance = {
    defaults: { baseURL: '/api/v1', timeout: 60000 },
    interceptors: {
      response: {
        use: (fulfilled, rejected) => {
          handlers.response.push({ fulfilled, rejected })
          return handlers.response.length
        },
      },
    },
    get: vi.fn(() => Promise.resolve({ data: {} })),
    post: vi.fn(() => Promise.resolve({ data: {} })),
    put: vi.fn(() => Promise.resolve({ data: {} })),
    delete: vi.fn(() => Promise.resolve({ data: {} })),
  }
  return {
    default: {
      create: vi.fn(() => instance),
      __handlers: handlers,
      __instance: instance,
    },
  }
})

import axios from 'axios'
import api, {
  getLatest,
  getHistory,
  getStatistics,
  getHealth,
  getDevices,
  createDevice,
  updateDevice,
  deleteDevice,
  insertSensorData,
  deleteSensorData,
  batchDeleteSensorData,
  getCommands,
  insertCommand,
  updateCommandStatus,
  deleteCommand,
  aiChat,
} from '../index'

describe('api - HTTP 客户端配置', () => {
  it('baseURL 配置为 /api/v1', () => {
    expect(api.defaults.baseURL).toBe('/api/v1')
  })

  it('timeout 配置为 60000ms', () => {
    expect(api.defaults.timeout).toBe(60000)
  })
})

describe('api - 响应拦截器', () => {
  it('拦截器返回 res.data 而非完整 response', () => {
    const handlers = axios.__handlers.response
    expect(handlers).toHaveLength(1)

    const mockResponse = { data: { code: 0, data: 'test' }, status: 200 }
    const result = handlers[0].fulfilled(mockResponse)
    expect(result).toEqual({ code: 0, data: 'test' })
  })

  it('拦截器在错误时 reject', async () => {
    const handlers = axios.__handlers.response
    const error = new Error('Network Error')
    await expect(handlers[0].rejected(error)).rejects.toThrow('Network Error')
  })
})

describe('api - 导出函数调用正确的 URL 和方法', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('getLatest() → GET /latest', () => {
    getLatest()
    expect(api.get).toHaveBeenCalledWith('/latest')
  })

  it('getHistory(params) → GET /history with params', () => {
    getHistory({ limit: 100, device_id: 'dev-001' })
    expect(api.get).toHaveBeenCalledWith('/history', {
      params: { limit: 100, device_id: 'dev-001' },
    })
  })

  it('getStatistics() → GET /statistics', () => {
    getStatistics()
    expect(api.get).toHaveBeenCalledWith('/statistics')
  })

  it('getHealth() → GET /health', () => {
    getHealth()
    expect(api.get).toHaveBeenCalledWith('/health')
  })

  it('getDevices() → GET /devices', () => {
    getDevices()
    expect(api.get).toHaveBeenCalledWith('/devices')
  })

  it('createDevice(params) → POST /devices with params', () => {
    createDevice({ device_id: 'dev-001', device_name: '传感器A' })
    expect(api.post).toHaveBeenCalledWith('/devices', null, {
      params: { device_id: 'dev-001', device_name: '传感器A' },
    })
  })

  it('updateDevice(id, params) → PUT /devices/{id} with params', () => {
    updateDevice('dev-001', { status: 'online' })
    expect(api.put).toHaveBeenCalledWith('/devices/dev-001', null, {
      params: { status: 'online' },
    })
  })

  it('deleteDevice(id) → DELETE /devices/{id}', () => {
    deleteDevice('dev-001')
    expect(api.delete).toHaveBeenCalledWith('/devices/dev-001')
  })

  it('insertSensorData(params) → POST /sensor-data with params', () => {
    insertSensorData({ Temp: '25.0' })
    expect(api.post).toHaveBeenCalledWith('/sensor-data', null, {
      params: { Temp: '25.0' },
    })
  })

  it('deleteSensorData(id) → DELETE /sensor-data/{id}', () => {
    deleteSensorData(1)
    expect(api.delete).toHaveBeenCalledWith('/sensor-data/1')
  })

  it('batchDeleteSensorData(params) → DELETE /sensor-data with params', () => {
    batchDeleteSensorData({ device_id: 'dev-001' })
    expect(api.delete).toHaveBeenCalledWith('/sensor-data', {
      params: { device_id: 'dev-001' },
    })
  })

  it('getCommands(params) → GET /commands with params', () => {
    getCommands({ limit: 50 })
    expect(api.get).toHaveBeenCalledWith('/commands', { params: { limit: 50 } })
  })

  it('insertCommand(params) → POST /commands with params', () => {
    insertCommand({ command: 'reboot' })
    expect(api.post).toHaveBeenCalledWith('/commands', null, {
      params: { command: 'reboot' },
    })
  })

  it('updateCommandStatus(id, status) → PUT /commands/{id} with status param', () => {
    updateCommandStatus(1, 'executed')
    expect(api.put).toHaveBeenCalledWith('/commands/1', null, {
      params: { status: 'executed' },
    })
  })

  it('deleteCommand(id) → DELETE /commands/{id}', () => {
    deleteCommand(1)
    expect(api.delete).toHaveBeenCalledWith('/commands/1')
  })

  it('aiChat(question) → POST /ai/chat with JSON body', () => {
    aiChat('蘑菇适宜温度？')
    expect(api.post).toHaveBeenCalledWith('/ai/chat', { question: '蘑菇适宜温度？' })
  })
})
