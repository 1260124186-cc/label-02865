<template>
  <div class="cart-page">
    <div class="cart-header">
      <span class="header-title">购物车</span>
      <span class="header-count" v-if="cartStore.cartList.length">({{ cartStore.cartList.length }})</span>
    </div>

    <div v-if="!userStore.isLoggedIn" class="not-login card">
      <el-empty description="请先登录" :image-size="80">
        <el-button type="primary" @click="$router.push('/login')">去登录</el-button>
      </el-empty>
    </div>

    <div v-else-if="cartStore.cartList.length === 0" class="empty-cart">
      <el-empty description="购物车空空如也~" :image-size="100">
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </el-empty>
    </div>

    <div v-else class="cart-body">
      <div v-for="item in cartStore.cartList" :key="item.id" class="cart-item card">
        <el-checkbox
          :model-value="item.selected === 1"
          @change="toggleSelect(item)"
        />
        <img :src="item.product?.mainImage" class="item-img" @click="$router.push(`/product/${item.productId}`)" />
        <div class="item-info">
          <div class="item-name ellipsis-2">{{ item.product?.name }}</div>
          <div class="item-bottom">
            <span class="price">{{ item.product?.price }}</span>
            <div class="quantity-ctrl">
              <el-button size="small" :icon="Minus" circle @click="changeQty(item, -1)" :disabled="item.quantity <= 1" />
              <span class="qty-num">{{ item.quantity }}</span>
              <el-button size="small" :icon="Plus" circle @click="changeQty(item, 1)" />
            </div>
          </div>
        </div>
        <el-icon class="delete-btn" @click="removeItem(item.id)"><Delete /></el-icon>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="settle-bar safe-area-bottom" v-if="userStore.isLoggedIn && cartStore.cartList.length > 0">
      <el-checkbox
        :model-value="cartStore.isAllSelected"
        @change="handleSelectAll"
      >全选</el-checkbox>
      <div class="settle-info">
        <span>合计: </span>
        <span class="settle-price price">{{ cartStore.totalPrice }}</span>
      </div>
      <el-button
        type="primary"
        class="settle-btn"
        :disabled="cartStore.selectedItems.length === 0"
        @click="goSettle"
      >
        结算({{ cartStore.selectedItems.length }})
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { useUserStore } from '@/store/user'
import { selectAllCart, updateCart } from '@/api/cart'
import { Minus, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCart()
  }
})

async function toggleSelect(item) {
  const newSelected = item.selected === 1 ? 0 : 1
  item.selected = newSelected
  // 单个选中通过更新数量接口模拟（简化处理）
  await updateCart(item.id, item.quantity)
  cartStore.fetchCart()
}

async function changeQty(item, delta) {
  const newQty = item.quantity + delta
  if (newQty < 1) return
  await cartStore.updateQuantity(item.id, newQty)
}

async function removeItem(id) {
  await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
  await cartStore.removeItem(id)
  ElMessage.success('已删除')
}

function handleSelectAll(val) {
  cartStore.toggleSelectAll(val ? 1 : 0)
}

function goSettle() {
  router.push('/order/checkout')
}
</script>

<style lang="scss" scoped>
.cart-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 70px;
}

.cart-header {
  background: #fff;
  padding: 12px 16px;
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.header-count {
  color: #999;
  font-weight: normal;
  font-size: 14px;
}

.not-login, .empty-cart {
  margin: 24px 16px;
  padding: 40px 0;
}

.cart-body {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  position: relative;
}

.item-img {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 4px;
  background: #fafafa;
  cursor: pointer;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 70px;
}

.item-name {
  font-size: 13px;
  color: #333;
}

.item-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;

  .price { font-size: 16px; }
}

.quantity-ctrl {
  display: flex;
  align-items: center;
  gap: 8px;
}

.qty-num {
  min-width: 24px;
  text-align: center;
  font-size: 14px;
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  color: #999;
  cursor: pointer;
  font-size: 16px;

  &:hover { color: #E4393C; }
}

.settle-bar {
  position: fixed;
  bottom: 56px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 750px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 8px 16px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.06);
  z-index: 99;
}

.settle-info {
  flex: 1;
  text-align: right;
  margin-right: 12px;
  font-size: 14px;
}

.settle-price {
  font-size: 18px;
}

.settle-btn {
  border-radius: 20px;
  min-width: 100px;
  background: #E4393C;
  border: none;
}
</style>
