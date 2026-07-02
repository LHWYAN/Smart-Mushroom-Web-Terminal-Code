<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="visible = true">插入命令</el-button>
      <el-input v-model="deviceFilter" placeholder="按设备ID过滤..." style="width:160px" clearable @change="load" />
      <span class="hint">共 {{ list.length }} 条</span>
    </div>

    <el-table :data="list" stripe border size="small" max-height="520">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="device_id" label="设备" width="100" />
      <el-table-column prop="command" label="命令" />
      <el-table-column prop="param_key" label="参数键" />
      <el-table-column prop="param_value" label="参数值" width="80" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'success' ? 'success' : row.status === 'fail' ? 'danger' : 'info'" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="create_time" label="时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 'pending'">
            <el-button size="small" type="success" @click="setStatus(row.id, 'success')">成功</el-button>
            <el-button size="small" type="warning" @click="setStatus(row.id, 'fail')">失败</el-button>
          </template>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" title="插入命令" width="420px">
      <el-form label-width="80px">
        <el-form-item label="设备ID"><el-input v-model="form.device_id" /></el-form-item>
        <el-form-item label="命令">
          <el-select v-model="form.command" style="width:100%">
            <el-option v-for="c in ['SetLamp','SetCond','SetVent','SetBuzzer']" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="参数键"><el-input v-model="form.param_key" placeholder="如 LampStatus" /></el-form-item>
        <el-form-item label="参数值">
          <el-select v-model="form.param_value" style="width:100%"><el-option label="ON" value="ON" /><el-option label="OFF" value="OFF" /></el-select>
        </el-form-item>
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
import { getCommands, insertCommand, updateCommandStatus, deleteCommand } from '../api'

const list = ref([])
const deviceFilter = ref('')
const visible = ref(false)
const form = ref({ device_id: 'roomone', command: 'SetLamp', param_key: 'LampStatus', param_value: 'ON' })

async function load() {
  const params = { limit: 100 }
  if (deviceFilter.value) params.device_id = deviceFilter.value
  const r = await getCommands(params)
  if (r.code === 0) list.value = r.data || []
}

async function save() {
  const r = await insertCommand(form.value)
  if (r.code === 0) { ElMessage.success('命令已创建'); visible.value = false; load() }
  else ElMessage.error(r.message)
}

async function setStatus(id, status) {
  const r = await updateCommandStatus(id, status)
  if (r.code === 0) { ElMessage.success('状态已更新'); load() }
}

async function remove(id) {
  await ElMessageBox.confirm(`确定删除第 ${id} 条命令吗？`, '提示', { type: 'warning' })
  const r = await deleteCommand(id)
  if (r.code === 0) { ElMessage.success('已删除'); load() }
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; align-items: center; margin-bottom: 12px; }
.hint { color: #556677; font-size: 13px; }
</style>
