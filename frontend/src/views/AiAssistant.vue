<template>
  <div>
    <h2 class="page-title">种植问答</h2>
    <p class="page-title-desc">查阅项目说明、设备操作与种植相关知识，辅助日常管理</p>

    <GlassCard>
      <div class="quick-section">
        <span class="quick-label">常见问题</span>
        <div class="quick-tags">
          <el-tag
            v-for="q in quickQuestions"
            :key="q"
            class="quick-tag"
            effect="plain"
            round
            @click="ask(q)"
          >{{ q }}</el-tag>
        </div>
      </div>

      <div class="input-section">
        <el-input
          v-model="question"
          type="textarea"
          :rows="3"
          placeholder="输入您的问题，例如：平菇出菇期适宜温度是多少？"
          resize="none"
          @keydown.ctrl.enter="ask(question)"
        />
        <el-button type="primary" :loading="loading" class="send-btn" @click="ask(question)">
          发送
        </el-button>
      </div>

      <div class="answer-section">
        <div v-if="loading" class="answer-loading">
          <span class="dot-pulse"></span>
          正在整理回答...
        </div>
        <div v-else-if="answerHtml" class="answer-text markdown-body" v-html="answerHtml"></div>
        <div v-else class="answer-empty">回答将显示在这里</div>
      </div>
    </GlassCard>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { ElMessage } from 'element-plus'
import GlassCard from '../components/GlassCard.vue'
import { aiChat } from '../api'
import { stripThinking } from '../utils/aiAnswer'

marked.setOptions({ breaks: true, gfm: true })

const question = ref('')
const answer = ref('')
const loading = ref(false)

const answerHtml = computed(() => {
  if (!answer.value) return ''
  const cleaned = stripThinking(answer.value)
  if (!cleaned) return ''
  return DOMPurify.sanitize(marked.parse(cleaned))
})

const quickQuestions = [
  '平菇、香菇、金针菇适宜的温度和湿度是多少？',
  '蘑菇大棚 CO₂ 浓度多少合适？',
  '如何判断蘑菇是否可以采收？',
  '大棚湿度过高应该怎么处理？',
  '蘑菇常见病虫害有哪些，如何预防？',
]

async function ask(q) {
  const text = (q || question.value).trim()
  if (!text) return ElMessage.warning('请输入问题')
  question.value = text
  loading.value = true
  answer.value = ''
  try {
    const r = await aiChat(text)
    if (r.code === 0) {
      answer.value = r.data?.answer || '(暂无回答)'
    } else {
      answer.value = '请求失败：' + r.message
    }
  } catch {
    answer.value = '网络异常，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.quick-section {
  margin-bottom: 18px;
}

.quick-label {
  font-size: 13px;
  color: var(--text-sub);
  display: block;
  margin-bottom: 10px;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-tag {
  cursor: pointer;
  border-color: var(--border) !important;
  color: var(--text-body) !important;
  background: var(--bg-card-light) !important;
  transition: all 0.2s;
}

.quick-tag:hover {
  border-color: var(--color-primary) !important;
  color: var(--color-primary-dark) !important;
}

.input-section {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.send-btn {
  align-self: flex-start;
  padding: 12px 28px;
}

.answer-section {
  background: var(--bg-card-light);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  padding: 18px 20px;
  min-height: 160px;
}

.answer-loading {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-sub);
  font-size: 14px;
}

.dot-pulse {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-primary);
  animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.4; transform: scale(0.9); }
  50% { opacity: 1; transform: scale(1.1); }
}

.answer-empty {
  color: var(--text-hint);
  font-size: 14px;
}

.answer-text {
  line-height: 1.75;
  font-size: 14px;
  color: var(--text-body);
  word-break: break-word;
}

/* Markdown 渲染样式 */
.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3) {
  color: var(--text-title);
  font-weight: 600;
  margin: 1em 0 0.5em;
  line-height: 1.4;
}

.markdown-body :deep(h1) { font-size: 1.35em; }
.markdown-body :deep(h2) { font-size: 1.2em; }
.markdown-body :deep(h3) { font-size: 1.05em; }

.markdown-body :deep(p) {
  margin: 0.6em 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 1.4em;
  margin: 0.5em 0;
}

.markdown-body :deep(li) {
  margin: 0.25em 0;
}

.markdown-body :deep(code) {
  font-family: Consolas, "Courier New", monospace;
  font-size: 0.9em;
  background: #e8f0ec;
  color: var(--color-primary-dark);
  padding: 2px 6px;
  border-radius: 4px;
}

.markdown-body :deep(pre) {
  background: #1a2a35;
  color: #e8ecef;
  padding: 14px 16px;
  border-radius: var(--radius-sm);
  overflow-x: auto;
  margin: 0.8em 0;
}

.markdown-body :deep(pre code) {
  background: none;
  color: inherit;
  padding: 0;
}

.markdown-body :deep(blockquote) {
  border-left: 3px solid var(--color-primary);
  padding: 4px 0 4px 14px;
  margin: 0.8em 0;
  color: var(--text-sub);
  background: rgba(126, 200, 163, 0.08);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 0.8em 0;
  font-size: 13px;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  border: 1px solid var(--border);
  padding: 8px 10px;
  text-align: left;
}

.markdown-body :deep(th) {
  background: var(--bg-card);
  font-weight: 600;
  color: var(--text-title);
}

.markdown-body :deep(a) {
  color: var(--color-primary-dark);
  text-decoration: none;
}

.markdown-body :deep(a:hover) {
  text-decoration: underline;
}

.markdown-body :deep(hr) {
  border: none;
  border-top: 1px solid var(--border-light);
  margin: 1em 0;
}

.markdown-body :deep(> *:first-child) {
  margin-top: 0;
}

.markdown-body :deep(> *:last-child) {
  margin-bottom: 0;
}
</style>
