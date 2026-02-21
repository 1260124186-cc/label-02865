<template>
  <div class="user-page">
    <!-- 用户信息头部 -->
    <div class="user-header">
      <div v-if="userStore.isLoggedIn" class="user-info">
        <el-avatar :size="56" :src="userStore.userInfo?.avatar || ''">
          <el-icon :size="28"><User /></el-icon>
        </el-avatar>
        <div class="user-detail">
          <div class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
          <div class="user-id">ID: {{ userStore.userInfo?.userId || userStore.userInfo?.id }}</div>
        </div>
      </div>
      <div v-else class="login-prompt" @click="$router.push('/login')">
        <el-avatar :size="56"><el-icon :size="28"><User /></el-icon></el-avatar>
        <span class="login-text">点击登录</span>
      </div>
    </div>

    <!-- 订单区域 -->
    <div class="order-section card">
      <div class="section-head">
        <span class="section-title">我的订单</span>
        <span class="section-link" @click="goOrders(-1)">全部订单 ></span>
      </div>
      <div class="order-tabs">
        <div class="order-tab" @click="goOrders(0)">
          <el-icon :size="24"><Wallet /></el-icon>
          <span>待付款</span>
        </div>
        <div class="order-tab" @click="goOrders(1)">
          <el-icon :size="24"><Box /></el-icon>
          <span>待发货</span>
        </div>
        <div class="order-tab" @click="goOrders(2)">
          <el-icon :size="24"><Van /></el-icon>
          <span>待收货</span>
        </div>
        <div class="order-tab" @click="goOrders(3)">
          <el-icon :size="24"><Finished /></el-icon>
          <span>已完成</span>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section card">
      <div class="menu-item" @click="$router.push('/order/list')">
        <el-icon><Document /></el-icon>
        <span>全部订单</span>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </div>
      <div class="menu-item" @click="$router.push('/address')">
        <el-icon><LocationFilled /></el-icon>
        <span>收货地址</span>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </div>
      <div class="menu-item" v-if="userStore.isLoggedIn" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <span>退出登录</span>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

function goOrders(status) {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push({ path: '/order/list', query: status >= 0 ? { status } : {} })
}

async function handleLogout() {
  await ElMessageBox.confirm('确定退出登录？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
  userStore.logout()
}
</script>

<style lang="scss" scoped>
.user-page {
  background: #f5f5f5;
  min-height: 100vh;
}

.user-header {
  background: linear-gradient(135deg, #E4393C, #FF6B6E);
  padding: 32px 20px 28px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-detail {
  color: #fff;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
}

.user-id {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 4px;
}

.login-prompt {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
}

.login-text {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.order-section {
  margin: -12px 12px 12px;
  padding: 16px;
  position: relative;
  z-index: 1;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
}

.section-link {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.order-tabs {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.order-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 8px 0;
  cursor: pointer;
  font-size: 12px;
  color: #666;
  transition: color 0.2s;

  &:hover {
    color: #E4393C;
  }
}

.menu-section {
  margin: 0 12px;
  padding: 0 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    color: #E4393C;
  }

  .arrow {
    margin-left: auto;
    color: #ccc;
    font-size: 12px;
  }
}
</style>
