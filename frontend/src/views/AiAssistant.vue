<template>
  <el-card shadow="never" class="ai-card">
    <template #header>
      <div class="ai-header">
        <h2>🤖 智慧蘑菇农场 AI 助手</h2>
        <p>基于 Dify + Ollama 大模型，可回答项目架构、数据流、设备控制等问题</p>
      </div>
    </template>

    <div class="quick-btns">
      <span class="label">快捷提问：</span>
      <el-button v-for="q in quickQuestions" :key="q" size="small" @click="ask(q)">{{ q }}</el-button>
    </div>

    <div class="input-row">
      <el-input v-model="question" type="textarea" :rows="3" placeholder="请输入问题..." />
      <el-button type="primary" :loading="loading" @click="ask(question)">发送</el-button>
    </div>

    <div class="answer-box">
      <div v-if="loading" class="thinking">AI 正在思考中...</div>
      <div v-else-if="answer" class="answer">{{ answer }}</div>
      <div v-else class="placeholder">AI 回答将显示在这里...</div>
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { aiChat } from '../api'

const question = ref('')
const answer = ref('')
const loading = ref(false)

const quickQuestions = [
  '请说明本项目的总体架构',
  '后端为什么通过AMQP消费华为云IoTDA数据？',
  '本项目有哪些数据库表？每张表有什么作用？',
  '设备端支持哪些传感器和控制命令？',
]

async function ask(q) {
  const text = (q || question.value).trim()
  if (!text) return ElMessage.warning('请输入问题')
  question.value = text
  loading.value = true
  answer.value = ''
  try {
    const r = await aiChat(text)
    if (r.code === 0) answer.value = r.data?.answer || '(空回答)'
    else answer.value = '调用失败：' + r.message
  } catch (e) {
    answer.value = '请求异常：' + e.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ai-card { background: linear-gradient(145deg, #1a2d42, #152433); border: 1px solid #2a4055; color: #e0e0e0; }
.ai-card :deep(.el-card__header) { border-color: #2a4055; }
.ai-header h2 { color: #00d4ff; font-size: 18px; }
.ai-header p { color: #8899aa; font-size: 13px; margin-top: 6px; }
.quick-btns { margin-bottom: 16px; display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.quick-btns .label { color: #556677; font-size: 12px; }
.input-row { display: flex; gap: 12px; margin-bottom: 16px; }
.input-row .el-button { align-self: flex-start; padding: 12px 24px; }
.answer-box { background: #0d1b2a; border: 1px solid #2a4055; border-radius: 8px; padding: 16px; min-height: 160px; }
.thinking { color: #00d4ff; }
.placeholder { color: #556677; }
.answer { white-space: pre-wrap; line-height: 1.8; font-size: 14px; }
</style>
