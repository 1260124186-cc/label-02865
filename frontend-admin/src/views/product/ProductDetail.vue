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

      <!-- 评分信息 -->
      <div class="rating-section card" v-if="productRating">
        <div class="desc-title">商品评分</div>
        <div class="rating-content">
          <div class="rating-summary">
            <span class="rating-score">{{ averageRating }}</span>
            <div class="rating-stars">
              <el-rate v-model="displayRating" disabled show-score="false" />
            </div>
            <span class="rating-count">({{ productRating.reviewCount }}条评价)</span>
          </div>
          <div class="rating-distribution">
            <div class="rating-item" v-for="n in 5" :key="n">
              <span class="rating-label">{{ n }}星</span>
              <div class="rating-bar">
                <div class="rating-bar-fill" :style="{ width: (getRatingCount(n) / productRating.reviewCount * 100) + '%' }"></div>
              </div>
              <span class="rating-num">{{ getRatingCount(n) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 评价列表 -->
      <div class="review-section card">
        <div class="desc-title">用户评价 ({{ reviewTotal }})</div>
        <div class="review-list">
          <div class="review-item" v-for="review in reviews" :key="review.id">
            <div class="review-header">
              <div class="review-user">
                <img :src="review.avatar || '/img/default-avatar.png'" class="user-avatar" />
                <span class="user-name">{{ review.nickname || '匿名用户' }}</span>
              </div>
              <el-rate v-model="review.rating" disabled show-score="false" />
            </div>
            <div class="review-content">
              <p>{{ review.content || '用户未填写评价' }}</p>
              <div class="review-images" v-if="review.images">
                <img
                  v-for="(img, idx) in JSON.parse(review.images)"
                  :key="idx"
                  :src="img"
                  class="review-image"
                />
              </div>
            </div>
            <div class="review-footer">
              <span class="review-time">{{ formatTime(review.createTime) }}</span>
            </div>
          </div>
          <div class="no-reviews" v-if="reviews.length === 0">
            暂无评价，快来购买后评价吧~
          </div>
        </div>
        <div class="load-more" v-if="hasMoreReviews">
          <el-button type="primary" link @click="loadMoreReviews">加载更多</el-button>
        </div>
      </div>

      <!-- 提交评价 -->
      <div class="submit-review-section card" v-if="userStore.isLoggedIn">
        <div class="desc-title">发表评价</div>
        <div class="review-form">
          <div class="form-item">
            <label>评分：</label>
            <el-rate v-model="reviewForm.rating" show-score="false" />
          </div>
          <div class="form-item">
            <label>评价内容：</label>
            <el-input
              v-model="reviewForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入评价内容..."
            />
          </div>
          <div class="form-item">
            <label>上传图片：</label>
            <el-upload
              :file-list="imageList"
              :on-success="handleUploadSuccess"
              :on-remove="handleUploadRemove"
              :before-upload="beforeUpload"
              list-type="picture-card"
              action="/api/upload/image"
            >
              <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>
          <div class="form-item">
            <el-button type="primary" @click="submitReview" :loading="submittingReview">提交评价</el-button>
          </div>
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
import { getProductReviews, createReview } from '@/api/review'
import { useCartStore } from '@/store/cart'
import { useUserStore } from '@/store/user'
import { ElMessage, ElUpload } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const addingCart = ref(false)
const reviews = ref([])
const productRating = ref(null)
const reviewPage = ref(1)
const reviewPageSize = ref(10)
const reviewTotal = ref(0)
const loadingReviews = ref(false)
const reviewForm = ref({
  rating: 5,
  content: ''
})
const imageList = ref([])
const submittingReview = ref(false)

const allImages = computed(() => {
  if (!product.value) return []
  const imgs = [product.value.mainImage]
  try {
    const extra = JSON.parse(product.value.images || '[]')
    imgs.push(...extra)
  } catch {}
  return imgs
})

const averageRating = computed(() => {
  if (!productRating.value || productRating.value.reviewCount === 0) return '暂无评分'
  return (productRating.value.totalRating / productRating.value.reviewCount).toFixed(1)
})

const displayRating = computed(() => {
  if (!productRating.value || productRating.value.reviewCount === 0) return 0
  return Math.round(productRating.value.totalRating / productRating.value.reviewCount)
})

const hasMoreReviews = computed(() => {
  return reviews.value.length < reviewTotal.value
})

onMounted(async () => {
  const res = await getProductDetail(route.params.id)
  product.value = res.data
  await loadReviews()
})

async function loadReviews() {
  if (loadingReviews.value) return

  loadingReviews.value = true
  try {
    const res = await getProductReviews(route.params.id, reviewPage.value, reviewPageSize.value)
    reviews.value = res.data.reviews
    reviewTotal.value = res.data.total
    productRating.value = res.data.rating
  } catch (e) {
    ElMessage.error('加载评价失败')
  } finally {
    loadingReviews.value = false
  }
}

async function loadMoreReviews() {
  if (loadingReviews.value || !hasMoreReviews.value) return

  reviewPage.value++
  loadingReviews.value = true
  try {
    const res = await getProductReviews(route.params.id, reviewPage.value, reviewPageSize.value)
    reviews.value = [...reviews.value, ...res.data.reviews]
  } catch (e) {
    ElMessage.error('加载更多失败')
    reviewPage.value--
  } finally {
    loadingReviews.value = false
  }
}

function getRatingCount(rating) {
  if (!productRating.value) return 0
  switch (rating) {
    case 1: return productRating.value.rating1Count || 0
    case 2: return productRating.value.rating2Count || 0
    case 3: return productRating.value.rating3Count || 0
    case 4: return productRating.value.rating4Count || 0
    case 5: return productRating.value.rating5Count || 0
    default: return 0
  }
}

function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
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

async function submitReview() {
  if (!reviewForm.value.rating) {
    ElMessage.warning('请选择评分')
    return
  }
  if (!reviewForm.value.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }

  submittingReview.value = true
  try {
    const reviewData = {
      productId: product.value.id,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content.trim(),
      images: JSON.stringify(imageList.value.map(item => item.url))
    }

    await createReview(reviewData)
    ElMessage.success('评价提交成功')

    // 重置表单
    reviewForm.value.rating = 5
    reviewForm.value.content = ''
    imageList.value = []

    // 刷新评价列表
    reviewPage.value = 1
    await loadReviews()
  } catch (e) {
    ElMessage.error('提交评价失败')
  } finally {
    submittingReview.value = false
  }
}

function handleUploadSuccess(response, file, fileList) {
  if (response.code === 200) {
    imageList.value.push({
      url: response.data
    })
  } else {
    ElMessage.error('图片上传失败')
  }
}

function handleUploadRemove(file, fileList) {
  const index = imageList.value.findIndex(item => item.url === file.response.data)
  if (index !== -1) {
    imageList.value.splice(index, 1)
  }
}

function beforeUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 格式图片')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
  }

  return isJPG && isLt2M
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

.rating-section {
  margin: 12px;
  padding: 16px;

  .rating-content {
    .rating-summary {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .rating-score {
        font-size: 32px;
        font-weight: bold;
        color: #E4393C;
      }

      .rating-stars {
        .el-rate {
          font-size: 18px;
        }
      }

      .rating-count {
        font-size: 14px;
        color: #999;
      }
    }

    .rating-distribution {
      .rating-item {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        .rating-label {
          width: 40px;
          font-size: 12px;
          color: #666;
        }

        .rating-bar {
          flex: 1;
          height: 8px;
          background: #f5f5f5;
          border-radius: 4px;
          overflow: hidden;

          .rating-bar-fill {
            height: 100%;
            background: linear-gradient(90deg, #E4393C, #FF6B6B);
            border-radius: 4px;
            transition: width 0.3s ease;
          }
        }

        .rating-num {
          width: 40px;
          text-align: right;
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}

.review-section {
  margin: 12px;
  padding: 16px;

  .review-list {
    .review-item {
      padding: 16px 0;
      border-bottom: 1px solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .review-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .review-user {
          display: flex;
          align-items: center;
          gap: 8px;

          .user-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            object-fit: cover;
          }

          .user-name {
            font-size: 14px;
            color: #333;
          }
        }

        .el-rate {
          font-size: 14px;
        }
      }

      .review-content {
        p {
          font-size: 14px;
          color: #666;
          line-height: 1.6;
          margin-bottom: 12px;
        }

        .review-images {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;

          .review-image {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 4px;
            cursor: pointer;
            transition: transform 0.2s ease;

            &:hover {
              transform: scale(1.05);
            }
          }
        }
      }

      .review-footer {
        margin-top: 12px;

        .review-time {
          font-size: 12px;
          color: #999;
        }
      }
    }

    .no-reviews {
      text-align: center;
      padding: 40px 0;
      color: #999;
      font-size: 14px;
    }
  }

  .load-more {
    text-align: center;
    margin-top: 16px;

    .el-button {
      font-size: 14px;
    }
  }
}

.submit-review-section {
  margin: 12px;
  padding: 16px;

  .review-form {
    .form-item {
      margin-bottom: 16px;

      label {
        display: block;
        font-size: 14px;
        color: #333;
        margin-bottom: 8px;
      }

      .el-input {
        width: 100%;
      }

      .el-upload--picture-card {
        width: 80px;
        height: 80px;
        line-height: 80px;
      }

      .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
      }
    }
  }
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
