<template>
  <div class="product-detail">
    <div class="detail-header">
      <el-icon class="back-btn" @click="$router.back()"><ArrowLeft /></el-icon>
      <span>商品详情</span>
    </div>

    <div v-if="product" class="detail-body">
      <!-- 商品图片 -->
      <div class="img-section">
        <el-carousel height="320px" :autoplay="false">
          <el-carousel-item v-for="(img, idx) in allImages" :key="idx">
            <img :src="img" class="detail-img" />
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 价格信息 -->
      <div class="price-section card">
        <div class="price-row">
          <span class="current-price price">{{ product.price }}</span>
          <span v-if="product.originalPrice" class="price-original">¥{{ product.originalPrice }}</span>
          <span v-if="product.originalPrice" class="discount-tag">
            {{ Math.round((product.price / product.originalPrice) * 100) / 10 }}折
          </span>
        </div>
        <div class="product-title">{{ product.name }}</div>
        <div class="product-meta">
          <span>销量 {{ product.sales }}</span>
          <span>库存 {{ product.stock }}</span>
        </div>
      </div>

      <!-- 商品描述 -->
      <div class="desc-section card">
        <div class="desc-title">商品详情</div>
        <div class="desc-content" v-html="product.description"></div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-bar safe-area-bottom" v-if="product">
      <div class="bar-icons">
        <div class="bar-icon-item" @click="$router.push('/')">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </div>
        <div class="bar-icon-item" @click="$router.push('/cart')">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
        </div>
      </div>
      <div class="bar-buttons">
        <el-button class="btn-cart" @click="handleAddCart" :loading="addingCart">加入购物车</el-button>
        <el-button class="btn-buy" type="primary" @click="handleBuyNow" :loading="addingCart">立即购买</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api/product'
import { useCartStore } from '@/store/cart'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const addingCart = ref(false)

const allImages = computed(() => {
  if (!product.value) return []
  const imgs = [product.value.mainImage]
  try {
    const extra = JSON.parse(product.value.images || '[]')
    imgs.push(...extra)
  } catch {}
  return imgs
})

onMounted(async () => {
  const res = await getProductDetail(route.params.id)
  product.value = res.data
})

async function handleAddCart() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  addingCart.value = true
  try {
    await cartStore.addToCart(product.value.id)
    ElMessage.success('已加入购物车')
  } finally {
    addingCart.value = false
  }
}

async function handleBuyNow() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  addingCart.value = true
  try {
    await cartStore.addToCart(product.value.id)
    router.push('/cart')
  } finally {
    addingCart.value = false
  }
}
</script>

<style lang="scss" scoped>
.product-detail {
  max-width: 750px;
  margin: 0 auto;
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 60px;
}

.detail-header {
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

.img-section {
  .detail-img {
    width: 100%;
    height: 320px;
    object-fit: contain;
    background: #fff;
  }
}

.price-section {
  margin: 12px;
  padding: 16px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  font-size: 24px;
}

.discount-tag {
  background: #FFF0F0;
  color: #E4393C;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin-top: 12px;
  line-height: 1.4;
}

.product-meta {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.desc-section {
  margin: 12px;
  padding: 16px;
}

.desc-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.desc-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 750px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.08);
  z-index: 100;
}

.bar-icons {
  display: flex;
  gap: 16px;
  margin-right: 12px;
}

.bar-icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 10px;
  color: #666;
  cursor: pointer;
  gap: 2px;

  .el-icon { font-size: 20px; }
}

.bar-buttons {
  flex: 1;
  display: flex;
  gap: 0;

  .el-button + .el-button {
    margin-left: 0;
  }
}

.btn-cart {
  flex: 1;
  border-radius: 20px 0 0 20px;
  background: #F5A623;
  color: #fff;
  border: none;
  height: 40px;
  margin: 0;

  &:hover { background: #e69a1e; }
}

.btn-buy {
  flex: 1;
  border-radius: 0 20px 20px 0;
  background: #E4393C;
  border: none;
  height: 40px;
  margin: 0;

  &:hover { background: #c62f32; }
}
</style>
