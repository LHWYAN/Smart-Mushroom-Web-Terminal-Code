<template>
  <div>
    <h2 class="page-title">命令管理</h2>
    <p class="page-title-desc">记录与跟踪设备控制命令的下发与执行状态</p>

    <GlassCard class="data-table-card">
      <div class="page-toolbar">
        <el-button type="primary" @click="visible = true">下发命令</el-button>
        <el-input v-model="deviceFilter" placeholder="按设备 ID 过滤..." style="width:180px" clearable @change="load" />
        <span class="hint">共 {{ list.length }} 条</span>
      </div>

      <el-table :data="list" stripe size="small" max-height="520">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="device_id" label="设备" width="100" />
        <el-table-column prop="command" label="命令" min-width="100">
          <template #default="{ row }"><code class="cmd-code">{{ row.command }}</code></template>
        </el-table-column>
        <el-table-column prop="param_key" label="参数键" width="110" />
        <el-table-column prop="param_value" label="参数值" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'success' ? 'success' : row.status === 'fail' ? 'danger' : 'warning'"
              size="small" effect="light"
            >{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button size="small" text type="success" @click="setStatus(row.id, 'success')">成功</el-button>
              <el-button size="small" text type="warning" @click="setStatus(row.id, 'fail')">失败</el-button>
            </template>
            <el-button size="small" text type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </GlassCard>

    <el-dialog v-model="visible" title="下发控制命令" width="420px">
      <el-form label-width="80px">
        <el-form-item label="设备 ID"><el-input v-model="form.device_id" /></el-form-item>
        <el-form-item label="命令">
          <el-select v-model="form.command" style="width:100%" @change="onCommandChange">
            <el-option v-for="c in commands" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="参数键"><el-input v-model="form.param_key" /></el-form-item>
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
import GlassCard from '../components/GlassCard.vue'
import { getCommands, insertCommand, updateCommandStatus, deleteCommand } from '../api'

const list = ref([])
const deviceFilter = ref('')
const visible = ref(false)
const form = ref({ device_id: 'roomone', command: 'SetLamp', param_key: 'LampStatus', param_value: 'ON' })

const commands = [
  { value: 'SetLamp', label: 'SetLamp · 照明灯' },
  { value: 'SetCond', label: 'SetCond · 空调' },
  { value: 'SetVent', label: 'SetVent · 排风扇' },
  { value: 'SetBuzzer', label: 'SetBuzzer · 蜂鸣器' },
]

const paramKeyMap = { SetLamp: 'LampStatus', SetCond: 'CondStatus', SetVent: 'VentStatus', SetBuzzer: 'BuzzerStatus' }

function statusLabel(s) {
  return { pending: '待执行', success: '成功', fail: '失败' }[s] || s
}

function onCommandChange(cmd) {
  form.value.param_key = paramKeyMap[cmd] || ''
}

async function load() {
  const params = { limit: 100 }
  if (deviceFilter.value) params.device_id = deviceFilter.value
  const r = await getCommands(params)
  if (r.code === 0) list.value = r.data || []
}

async function save() {
  const r = await insertCommand(form.value)
  if (r.code === 0) { ElMessage.success('命令已记录'); visible.value = false; load() }
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
.cmd-code {
  font-size: 12px;
  background: var(--bg-card-light);
  padding: 2px 8px;
  border-radius: 6px;
  color: var(--color-primary-dark);
}
</style>
