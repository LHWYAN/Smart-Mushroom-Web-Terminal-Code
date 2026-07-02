<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()">注册设备</el-button>
      <el-input v-model="search" placeholder="搜索设备ID..." style="width:200px" clearable @input="filterList" />
      <span class="hint">共 {{ filtered.length }} 台设备</span>
    </div>

    <el-table :data="filtered" stripe border size="small">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="device_id" label="设备ID" />
      <el-table-column prop="device_name" label="名称" />
      <el-table-column prop="device_type" label="类型" width="90" />
      <el-table-column prop="location" label="位置" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'online' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remarks" label="备注" />
      <el-table-column prop="create_time" label="创建时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">修改</el-button>
          <el-button size="small" type="danger" @click="remove(row.device_id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="editMode ? '修改设备' : '注册设备'" width="460px">
      <el-form label-width="80px">
        <el-form-item label="设备ID" required><el-input v-model="form.device_id" :disabled="editMode" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.device_name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.device_type" style="width:100%">
            <el-option label="传感器" value="sensor" /><el-option label="执行器" value="actuator" /><el-option label="网关" value="gateway" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remarks" /></el-form-item>
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
import { getDevices, createDevice, updateDevice, deleteDevice } from '../api'

const list = ref([])
const filtered = ref([])
const search = ref('')
const visible = ref(false)
const editMode = ref(false)
const form = ref({ device_id: '', device_name: '', device_type: 'sensor', location: '', remarks: '' })

function filterList() {
  const q = search.value.toLowerCase()
  filtered.value = q ? list.value.filter(d => d.device_id.toLowerCase().includes(q)) : list.value
}

async function load() {
  const r = await getDevices()
  if (r.code === 0) { list.value = r.data || []; filterList() }
}

function openDialog(row) {
  editMode.value = !!row
  form.value = row ? { ...row } : { device_id: '', device_name: '', device_type: 'sensor', location: '', remarks: '' }
  visible.value = true
}

async function save() {
  if (!form.value.device_id) return ElMessage.error('请输入设备ID')
  const params = { device_name: form.value.device_name, device_type: form.value.device_type, location: form.value.location, remarks: form.value.remarks }
  const r = editMode.value
    ? await updateDevice(form.value.device_id, params)
    : await createDevice({ device_id: form.value.device_id, ...params })
  if (r.code === 0) { ElMessage.success('保存成功'); visible.value = false; load() }
  else ElMessage.error(r.message)
}

async function remove(id) {
  await ElMessageBox.confirm(`确定删除设备 ${id} 吗？`, '提示', { type: 'warning' })
  const r = await deleteDevice(id)
  if (r.code === 0) { ElMessage.success('已删除'); load() }
  else ElMessage.error(r.message)
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; align-items: center; margin-bottom: 12px; }
.hint { color: #556677; font-size: 13px; }
</style>
