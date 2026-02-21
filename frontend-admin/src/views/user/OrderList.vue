<template>
  <div class="order-list-page">
    <div class="page-header">
      <el-icon class="back-btn" @click="$router.back()"><ArrowLeft /></el-icon>
      <span>我的订单</span>
    </div>

    <!-- 状态筛选 -->
    <div class="status-tabs">
      <div
        v-for="tab in statusTabs"
        :key="tab.value"
        class="status-tab"
        :class="{ active: currentStatus === tab.value }"
        @click="switchStatus(tab.value)"
      >
        {{ tab.label }}
      </div>
    </div>

    <div class="order-body">
      <div v-if="loading" class="loading-wrap">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
      </div>
      <div v-else-if="orders.length === 0" class="empty-wrap">
        <el-empty description="暂无订单" :image-size="80" />
      </div>
      <div v-else class="order-cards">
        <div v-for="order in orders" :key="order.id" class="order-card card">
          <div class="order-head">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
            <span class="order-status" :class="'status-' + order.status">{{ statusText(order.status) }}</span>
          </div>
          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <img :src="item.productImage" class="oi-img" />
              <div class="oi-info">
                <div class="oi-name ellipsis">{{ item.productName }}</div>
                <div class="oi-meta">
                  <span class="price">{{ item.price }}</span>
                  <span class="oi-qty">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="order-foot">
            <span class="order-total">共{{ totalQty(order) }}件 合计: <span class="price">{{ order.totalAmount }}</span></span>
            <div class="order-actions">
              <el-button v-if="order.status === 0" size="small" @click="handleCancel(order.id)">取消订单</el-button>
              <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order.id)">去付款</el-button>
              <el-button v-if="order.status === 2" type="primary" size="small" @click="handleConfirm(order.id)">确认收货</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderList, cancelOrder, confirmOrder, payOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const orders = ref([])
const loading = ref(false)
const currentStatus = ref(-1)

const statusTabs = [
  { label: '全部', value: -1 },
  { label: '待付款', value: 0 },
  { label: '待发货', value: 1 },
  { label: '待收货', value: 2 },
  { label: '已完成', value: 3 },
]

onMounted(() => {
  if (route.query.status !== undefined) {
    currentStatus.value = Number(route.query.status)
  }
  loadOrders()
})

function switchStatus(status) {
  currentStatus.value = status
  loadOrders()
}

async function loadOrders() {
  loading.value = true
  try {
    const params = { page: 1, size: 50 }
    if (currentStatus.value >= 0) {
      params.status = currentStatus.value
    }
    const res = await getOrderList(params)
    orders.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

function statusText(status) {
  return ['待付款', '待发货', '待收货', '已完成', '已取消'][status] || '未知'
}

function totalQty(order) {
  return (order.items || []).reduce((sum, i) => sum + i.quantity, 0)
}

async function handleCancel(id) {
  await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
  await cancelOrder(id)
  ElMessage.success('订单已取消')
  loadOrders()
}

async function handleConfirm(id) {
  await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info', confirmButtonText: '确定', cancelButtonText: '取消' })
  await confirmOrder(id)
  ElMessage.success('已确认收货')
  loadOrders()
}

async function handlePay(id) {
  await ElMessageBox.confirm('确认支付该订单？', '模拟支付', { confirmButtonText: '确认支付', cancelButtonText: '取消', type: 'info' })
  await payOrder(id)
  ElMessage.success('支付成功！')
  loadOrders()
}
</script>

<style lang="scss" scoped>
.order-list-page {
  max-width: 750px;
  margin: 0 auto;
  background: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  position: sticky;
  top: 0;
  z-index: 50;
  background: #fff;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  font-size: 16px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.back-btn {
  font-size: 20px;
  cursor: pointer;
}

.status-tabs {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.status-tab {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;

  &.active {
    color: #E4393C;
    border-bottom-color: #E4393C;
    font-weight: bold;
  }
}

.order-body {
  padding: 12px;
}

.loading-wrap, .empty-wrap {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.order-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  padding: 12px;
}

.order-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f5;
}

.order-no {
  font-size: 12px;
  color: #999;
}

.order-status {
  font-size: 13px;
  font-weight: bold;
  &.status-0 { color: #E4393C; }
  &.status-1 { color: #E6A23C; }
  &.status-2 { color: #409EFF; }
  &.status-3 { color: #67C23A; }
  &.status-4 { color: #999; }
}

.order-items {
  padding: 10px 0;
}

.order-item {
  display: flex;
  gap: 10px;
  padding: 6px 0;
}

.oi-img {
  width: 64px;
  height: 64px;
  object-fit: contain;
  border-radius: 4px;
  background: #fafafa;
  flex-shrink: 0;
}

.oi-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.oi-name {
  font-size: 13px;
  color: #333;
}

.oi-meta {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  .price { font-size: 14px; }
}

.oi-qty {
  font-size: 12px;
  color: #999;
}

.order-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
}

.order-total {
  font-size: 13px;
  color: #666;
  .price { font-size: 15px; }
}

.order-actions {
  display: flex;
  gap: 8px;
}
</style>
