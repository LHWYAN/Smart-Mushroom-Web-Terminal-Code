<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-bg-circle c1"></div>
      <div class="auth-bg-circle c2"></div>
      <div class="auth-bg-circle c3"></div>
    </div>

    <div class="auth-card-wrap">
      <div class="auth-card">
        <!-- 头部 -->
        <div class="auth-header">
          <div class="auth-logo">🍄</div>
          <h1 class="auth-title">智慧蘑菇农场</h1>
          <p class="auth-subtitle">欢迎回来，请登录您的账号</p>
        </div>

        <!-- 表单 -->
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="auth-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="auth-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 底部链接 -->
        <div class="auth-footer">
          没有账号？
          <router-link to="/register" class="auth-link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, setAuth } from '../api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, max: 32, message: '长度在 4 到 32 个字符', trigger: 'blur' },
  ],
}

async function handleLogin() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({
      username: form.username,
      password: form.password,
    })
    if (res.code === 0 && res.data) {
      setAuth(res.data.token, res.data.user || { username: form.username })
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.message || '登录失败，请检查用户名和密码')
    }
  } catch (e) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 背景装饰 */
.auth-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.auth-bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.35;
}

.c1 {
  width: 420px;
  height: 420px;
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  top: -120px;
  right: -80px;
}

.c2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, var(--color-accent), #b5d8f0);
  bottom: -60px;
  left: -60px;
}

.c3 {
  width: 180px;
  height: 180px;
  background: linear-gradient(135deg, #f0e0b0, #f5d080);
  top: 40%;
  left: 10%;
}

/* 卡片容器 */
.auth-card-wrap {
  position: relative;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.auth-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  border-radius: var(--radius-lg, 20px);
  border: 1px solid var(--border, #d5dbe0);
  box-shadow: 0 8px 32px rgba(200, 212, 205, 0.5);
  padding: 36px 32px 28px;
}

/* 头部 */
.auth-header {
  text-align: center;
  margin-bottom: 28px;
}

.auth-logo {
  font-size: 48px;
  line-height: 1;
  margin-bottom: 10px;
}

.auth-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-title, #1a2a35);
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 14px;
  color: var(--text-sub, #546e7a);
}

/* 表单 */
.auth-form {
  margin-bottom: 8px;
}

.auth-form :deep(.el-input__wrapper) {
  background: var(--bg-card-light, #f0f4f2);
  border-radius: var(--radius-sm, 12px);
  box-shadow: none;
  border: 1px solid transparent;
  transition: border-color 0.2s, background 0.2s;
}

.auth-form :deep(.el-input__wrapper:hover),
.auth-form :deep(.el-input__wrapper.is-focus) {
  background: #ffffff;
  border-color: var(--color-primary, #7ec8a3);
  box-shadow: 0 0 0 1px var(--color-primary, #7ec8a3);
}

.auth-form :deep(.el-input__inner) {
  color: var(--text-title, #1a2a35);
}

.auth-form :deep(.el-input__prefix-inner .el-icon) {
  color: var(--text-hint, #7e929f);
}

.auth-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-sm, 12px);
  background: linear-gradient(135deg, var(--color-primary, #7ec8a3), var(--color-primary-dark, #5daf8a));
  border: none;
  letter-spacing: 4px;
  transition: all 0.25s;
}

.auth-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(126, 200, 163, 0.5);
}

/* 底部 */
.auth-footer {
  text-align: center;
  font-size: 14px;
  color: var(--text-sub, #546e7a);
}

.auth-link {
  color: var(--color-primary-dark, #5daf8a);
  font-weight: 600;
  text-decoration: none;
}

.auth-link:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 500px) {
  .auth-card {
    padding: 28px 20px 24px;
  }
  .auth-logo {
    font-size: 40px;
  }
}
</style>
