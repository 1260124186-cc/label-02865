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

      <!-- 评分统计 -->
      <div class="rating-section card" v-if="averageRating > 0">
        <div class="rating-header">
          <span class="rating-title">用户评价</span>
          <div class="rating-summary">
            <span class="rating-score">{{ averageRating.toFixed(1) }}</span>
            <div class="rating-stars">
              <el-rate v-model="averageRating" disabled show-score text-color="#ff9900" />
            </div>
            <span class="rating-count">({{ totalReviews }}条)</span>
          </div>
        </div>
      </div>

      <!-- 评价列表 -->
      <div class="reviews-section card" v-if="reviews.length > 0">
        <div class="reviews-list">
          <div class="review-item" v-for="review in reviews" :key="review.id">
            <div class="review-header">
              <div class="review-user">
                <el-avatar :size="32" :src="review.avatar || ''">
                  {{ review.nickname ? review.nickname.charAt(0) : 'U' }}
                </el-avatar>
                <span class="user-name">{{ review.nickname || '匿名用户' }}</span>
              </div>
              <div class="review-rating">
                <el-rate v-model="review.rating" disabled show-score text-color="#ff9900" />
              </div>
            </div>
            <div class="review-content">
              <p v-if="review.content">{{ review.content }}</p>
              <div class="review-images" v-if="review.images">
                <img v-for="(img, idx) in JSON.parse(review.images || '[]')"
                     :key="idx"
                     :src="img"
                     class="review-image" />
              </div>
              <div class="review-time">{{ formatTime(review.createTime) }}</div>
            </div>
            <div class="merchant-reply" v-if="review.merchantReply">
              <div class="reply-label">商家回复</div>
              <div class="reply-content">{{ review.merchantReply }}</div>
              <div class="reply-time">{{ formatTime(review.replyTime) }}</div>
            </div>
          </div>
        </div>
        <div class="load-more" v-if="hasMore" @click="loadMoreReviews">
          <span>加载更多</span>
        </div>
        <div class="no-more" v-if="!hasMore && reviews.length > 0">
          <span>没有更多评价了</span>
        </div>
      </div>

      <!-- 暂无评价 -->
      <div class="no-reviews card" v-if="reviews.length === 0">
        <el-empty description="暂无评价" :image-size="80" />
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
          <el-badge :value="cartStore.totalCount > 0 ? cartStore.totalCount : ''" :hidden="cartStore.totalCount === 0" :max="99">
            <el-icon><ShoppingCart /></el-icon>
          </el-badge>
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
import { getProductReviews, getAverageRating } from '@/api/review'
import { useCartStore } from '@/store/cart'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const addingCart = ref(false)
const reviews = ref([])
const averageRating = ref(0)
const totalReviews = ref(0)
const currentPage = ref(1)
const hasMore = ref(true)
const loadingReviews = ref(false)

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
  await loadRating()
  await loadReviews()
})

async function loadRating() {
  try {
    const res = await getAverageRating(route.params.id)
    averageRating.value = res.data.averageRating
    totalReviews.value = res.data.totalCount
  } catch (e) {
    console.error('Failed to load rating', e)
  }
}

async function loadReviews() {
  if (loadingReviews.value) return
  loadingReviews.value = true
  try {
    const res = await getProductReviews(route.params.id, {
      page: currentPage.value,
      size: 10
    })
    if (currentPage.value === 1) {
      reviews.value = res.data.records
    } else {
      reviews.value = [...reviews.value, ...res.data.records]
    }
    hasMore.value = res.data.current < res.data.pages
  } catch (e) {
    console.error('Failed to load reviews', e)
  } finally {
    loadingReviews.value = false
  }
}

function loadMoreReviews() {
  if (hasMore.value && !loadingReviews.value) {
    currentPage.value++
    loadReviews()
  }
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`

  return date.toLocaleDateString('zh-CN')
}

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
    cartStore.setBuyNowItem(product.value)
    router.push('/order/checkout')
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

.rating-section {
  margin: 12px;
  padding: 16px;
}

.rating-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating-score {
  font-size: 20px;
  font-weight: bold;
  color: #ff9900;
}

.rating-count {
  font-size: 12px;
  color: #999;
}

.reviews-section {
  margin: 12px;
  padding: 16px 16px 8px;
}

.reviews-list {
  .review-item {
    padding: 12px 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }
  }
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.review-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.review-rating {
  :deep(.el-rate) {
    --el-rate-icon-size: 14px;
  }
}

.review-content {
  p {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
    margin: 0 0 8px 0;
  }
}

.review-images {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.review-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.merchant-reply {
  margin-top: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  border-left: 3px solid #E4393C;
}

.reply-label {
  font-size: 12px;
  color: #E4393C;
  font-weight: bold;
  margin-bottom: 6px;
}

.reply-content {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.reply-time {
  font-size: 11px;
  color: #999;
  margin-top: 6px;
}

.load-more {
  padding: 12px;
  text-align: center;
  cursor: pointer;
  color: #999;
  font-size: 14px;

  &:hover {
    color: #E4393C;
  }
}

.no-more {
  padding: 12px;
  text-align: center;
  color: #ccc;
  font-size: 12px;
}

.no-reviews {
  margin: 12px;
  padding: 24px 16px;
}
</style>
