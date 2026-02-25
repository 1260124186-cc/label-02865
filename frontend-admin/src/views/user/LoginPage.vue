<template>
  <div class="login-page">
    <div class="login-header">
      <el-icon class="back-btn" @click="$router.back()"><ArrowLeft /></el-icon>
    </div>
    <div class="login-body">
      <div class="login-logo">
        <div class="logo-icon">📱</div>
        <h2>PhoneMall</h2>
        <p>手机商城</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginRef" @submit.prevent="handleLogin">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-button type="primary" class="submit-btn" size="large" @click="handleLogin" :loading="loading">登 录</el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerRef" @submit.prevent="handleRegister">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="请输入用户名(3-20位)" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码(6-20位)" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item prop="nickname">
              <el-input v-model="registerForm.nickname" placeholder="昵称(选填)" prefix-icon="UserFilled" size="large" />
            </el-form-item>
            <el-button type="primary" class="submit-btn" size="large" @click="handleRegister" :loading="loading">注 册</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)
const loginRef = ref(null)
const registerRef = ref(null)

const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', password: '', nickname: '' })

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  await loginRef.value.validate()
  loading.value = true
  try {
    await userStore.login(loginForm.value)
    router.push('/')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  await registerRef.value.validate()
  loading.value = true
  try {
    await userStore.register(registerForm.value)
    activeTab.value = 'login'
    loginForm.value.username = registerForm.value.username
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  max-width: 750px;
  margin: 0 auto;
  min-height: 100vh;
  background: #fff;
}

.login-header {
  padding: 12px 16px;
}

.back-btn {
  font-size: 20px;
  cursor: pointer;
  color: #333;
}

.login-body {
  padding: 20px 32px;
}

.login-logo {
  text-align: center;
  margin-bottom: 32px;

  .logo-icon {
    font-size: 48px;
    margin-bottom: 8px;
  }

  h2 {
    color: #E4393C;
    margin: 0;
    font-size: 24px;
  }

  p {
    color: #999;
    margin: 4px 0 0;
    font-size: 13px;
  }
}

.login-tabs {
  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }
  :deep(.el-tabs__active-bar) {
    background-color: #E4393C;
  }
  :deep(.el-tabs__item.is-active) {
    color: #E4393C;
  }
}

.submit-btn {
  width: 100%;
  border-radius: 24px;
  margin-top: 8px;
  background: #E4393C;
  border: none;
  font-size: 16px;

  &:hover {
    background: #c62f32;
  }
}

/* 去除浏览器自动填充的背景色 */
:deep(input:-webkit-autofill),
:deep(input:-webkit-autofill:hover),
:deep(input:-webkit-autofill:focus) {
  -webkit-box-shadow: 0 0 0 1000px #fff inset !important;
  -webkit-text-fill-color: #333 !important;
  transition: background-color 5000s ease-in-out 0s;
}
</style>
