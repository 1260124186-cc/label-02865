<template>
  <div class="mobile-layout">
    <div class="mobile-content">
      <router-view />
    </div>
    <div class="tab-bar safe-area-bottom">
      <div
        v-for="tab in tabs"
        :key="tab.path"
        class="tab-item"
        :class="{ active: currentTab === tab.path }"
        @click="switchTab(tab.path)"
      >
        <el-icon :size="22"><component :is="tab.icon" /></el-icon>
        <span>{{ tab.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const tabs = [
  { path: '/', label: '首页', icon: 'HomeFilled' },
  { path: '/category', label: '分类', icon: 'Grid' },
  { path: '/cart', label: '购物车', icon: 'ShoppingCart' },
  { path: '/user', label: '我的', icon: 'User' },
]

const currentTab = computed(() => route.path)

function switchTab(path) {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.mobile-layout {
  max-width: 750px;
  margin: 0 auto;
  min-height: 100vh;
  background: #f5f5f5;
  position: relative;
}

.mobile-content {
  padding-bottom: 60px;
  min-height: calc(100vh - 60px);
}

.tab-bar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 750px;
  height: 56px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-around;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  cursor: pointer;
  color: #999;
  font-size: 11px;
  transition: color 0.2s;
  position: relative;

  &.active {
    color: #E4393C;
  }

  &:hover {
    color: #E4393C;
  }

  span {
    line-height: 1;
  }
}
</style>
