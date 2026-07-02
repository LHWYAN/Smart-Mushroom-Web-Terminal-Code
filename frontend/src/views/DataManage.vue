<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="visible = true">手动插入</el-button>
      <el-button type="danger" @click="batchDelete">批量删除</el-button>
      <el-input v-model="deviceFilter" placeholder="按设备ID过滤..." style="width:160px" clearable @change="load" />
      <span class="hint">共 {{ list.length }} 条</span>
    </div>

    <el-table :data="list" stripe border size="small" max-height="520">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="device_id" label="设备" width="100" />
      <el-table-column prop="Temp" label="温度" width="70" />
      <el-table-column prop="Humi" label="湿度" width="70" />
      <el-table-column prop="Lumi" label="光照" width="70" />
      <el-table-column prop="Smoke" label="烟雾" width="70" />
      <el-table-column prop="CO2" label="CO₂" width="70" />
      <el-table-column prop="LampST" label="照明" width="70" />
      <el-table-column prop="CondST" label="空调" width="70" />
      <el-table-column prop="VentST" label="风扇" width="70" />
      <el-table-column prop="create_time" label="时间" width="170" />
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" title="手动插入数据" width="480px">
      <el-form label-width="70px">
        <el-form-item label="设备ID"><el-input v-model="form.device_id" /></el-form-item>
        <el-row :gutter="8">
          <el-col :span="12"><el-form-item label="温度"><el-input v-model="form.Temp" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="湿度"><el-input v-model="form.Humi" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="光照"><el-input v-model="form.Lumi" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="烟雾"><el-input v-model="form.Smoke" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="CO₂"><el-input v-model="form.CO2" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="照明"><el-select v-model="form.LampST" style="width:100%"><el-option label="OFF" value="OFF" /><el-option label="ON" value="ON" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHistory, insertSensorData, deleteSensorData, batchDeleteSensorData } from '../api'

const list = ref([])
const deviceFilter = ref('')
const visible = ref(false)
const form = ref({ device_id: 'roomone', Temp: '', Humi: '', Lumi: '', Smoke: '0', CO2: '400', LampST: 'OFF', CondST: 'OFF', VentST: 'OFF' })

async function load() {
  const params = { limit: 100 }
  if (deviceFilter.value) params.device_id = deviceFilter.value
  const r = await getHistory(params)
  if (r.code === 0) list.value = r.data || []
}

async function save() {
  const r = await insertSensorData(form.value)
  if (r.code === 0) { ElMessage.success('插入成功'); visible.value = false; load() }
  else ElMessage.error(r.message)
}

async function remove(id) {
  await ElMessageBox.confirm(`确定删除第 ${id} 条数据吗？`, '提示', { type: 'warning' })
  const r = await deleteSensorData(id)
  if (r.code === 0) { ElMessage.success('已删除'); load() }
}

async function batchDelete() {
  const device_id = prompt('按设备ID批量删除（留空不限）')
  if (device_id === null) return
  const before_time = prompt('按时间批量删除（格式: 2026-07-01 12:00:00，留空不限）')
  if (before_time === null) return
  const params = {}
  if (device_id) params.device_id = device_id
  if (before_time) params.before_time = before_time
  const r = await batchDeleteSensorData(params)
  if (r.code === 0) { ElMessage.success(`已删除 ${r.data.deleted} 条`); load() }
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; align-items: center; margin-bottom: 12px; }
.hint { color: #556677; font-size: 13px; }
</style>
