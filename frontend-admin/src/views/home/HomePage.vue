<template>
  <div class="home-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-inner" @click="$router.push('/search')">
        <el-icon><Search /></el-icon>
        <span>搜索手机、平板、配件...</span>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="banner-section">
      <el-carousel height="180px" :interval="5000" arrow="never">
        <el-carousel-item v-for="(banner, index) in enrichedBanners" :key="banner.id">
          <div
            class="banner-card"
            :class="'banner-theme-' + (index % 3)"
            @click="banner.productId && $router.push(`/product/${banner.productId}`)"
          >
            <div class="banner-text">
              <span class="banner-tag">{{ banner.tag }}</span>
              <h3 class="banner-title">{{ banner.title }}</h3>
              <p class="banner-desc">{{ banner.desc }}</p>
              <div class="banner-price" v-if="banner.price">
                <span class="banner-current-price">¥{{ banner.price }}</span>
                <span class="banner-original-price" v-if="banner.originalPrice">¥{{ banner.originalPrice }}</span>
              </div>
              <span class="banner-btn">立即查看 →</span>
            </div>
            <div class="banner-img-wrap">
              <img :src="banner.imageUrl" class="banner-img" />
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类导航 -->
    <div class="category-nav card">
      <div
        v-for="cat in categories"
        :key="cat.id"
        class="cat-item"
        @click="$router.push(`/category?id=${cat.id}`)"
      >
        <img :src="cat.icon || 'https://via.placeholder.com/48x48?text=' + cat.name" class="cat-icon" />
        <span>{{ cat.name }}</span>
      </div>
    </div>

    <!-- 热销推荐 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">🔥 热销排行</span>
        <span class="section-more" @click="$router.push('/category')">更多 ></span>
      </div>
      <div class="hot-scroll">
        <div
          v-for="item in hotProducts"
          :key="item.id"
          class="hot-item card"
          @click="$router.push(`/product/${item.id}`)"
        >
          <img :src="item.mainImage" class="hot-img" />
          <div class="hot-name ellipsis-2">{{ item.name }}</div>
          <div class="hot-price">
            <span class="price">{{ item.price }}</span>
            <span v-if="item.originalPrice" class="price-original">¥{{ item.originalPrice }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 为你推荐 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">✨ 为你推荐</span>
      </div>
      <div class="product-grid">
        <div
          v-for="item in recommendProducts"
          :key="item.id"
          class="product-card card"
          @click="$router.push(`/product/${item.id}`)"
        >
          <img :src="item.mainImage" class="product-img" />
          <div class="product-info">
            <div class="product-name ellipsis-2">{{ item.name }}</div>
            <div class="product-price">
              <span class="price">{{ item.price }}</span>
              <span class="product-sales">已售{{ item.sales }}件</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getBannerList } from '@/api/banner'
import { getCategoryList } from '@/api/category'
import { getHotProducts, getRecommendProducts } from '@/api/product'

const banners = ref([])
const categories = ref([])
const hotProducts = ref([])
const recommendProducts = ref([])

// Banner 文案配置（按 productId 映射）
const bannerCopyMap = {
  1: { tag: '新品首发', title: 'iPhone 15 Pro Max', desc: '钛金属设计，A17 Pro 芯片，强大超乎想象', price: '9999', originalPrice: '10999' },
  2: { tag: '热卖爆款', title: '华为 Mate 60 Pro', desc: '遥遥领先，麒麟芯片回归，卫星通话', price: '6999', originalPrice: '7999' },
  3: { tag: '性价之选', title: '小米14 Ultra', desc: '徕卡光学，骁龙8 Gen3，影像旗舰', price: '5999', originalPrice: '6499' }
}

const enrichedBanners = computed(() => {
  return banners.value.map(b => {
    const copy = bannerCopyMap[b.productId] || { tag: '精选推荐', title: '优质好物', desc: '点击查看详情' }
    return { ...b, ...copy }
  })
})

onMounted(async () => {
  const [b, c, h, r] = await Promise.all([
    getBannerList(),
    getCategoryList(),
    getHotProducts(),
    getRecommendProducts()
  ])
  banners.value = b.data || []
  categories.value = c.data || []
  hotProducts.value = h.data || []
  recommendProducts.value = r.data || []
})
</script>

<style lang="scss" scoped>
.home-page {
  background: #f5f5f5;
}

.search-bar {
  position: sticky;
  top: 0;
  z-index: 50;
  background: linear-gradient(135deg, #E4393C, #FF6B6E);
  padding: 8px 16px 12px;
}

.search-inner {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border-radius: 20px;
  padding: 8px 16px;
  color: #999;
  font-size: 14px;
  cursor: pointer;
}

.banner-section {
  padding: 0 12px;
  margin-top: 12px;

  :deep(.el-carousel__indicators) {
    .el-carousel__indicator .el-carousel__button {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: rgba(255,255,255,0.5);
    }
    .el-carousel__indicator.is-active .el-carousel__button {
      background: #fff;
      width: 14px;
      border-radius: 3px;
    }
  }
}

.banner-card {
  display: flex;
  align-items: center;
  height: 100%;
  border-radius: 12px;
  padding: 20px 16px;
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

.banner-theme-0 {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}
.banner-theme-1 {
  background: linear-gradient(135deg, #c0392b 0%, #e74c3c 50%, #ff6b6b 100%);
}
.banner-theme-2 {
  background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
}

.banner-text {
  flex: 1;
  z-index: 1;
  min-width: 0;
}

.banner-tag {
  display: inline-block;
  background: rgba(255,255,255,0.2);
  color: #fff;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-bottom: 8px;
  backdrop-filter: blur(4px);
}

.banner-title {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 6px;
  line-height: 1.3;
}

.banner-desc {
  color: rgba(255,255,255,0.75);
  font-size: 12px;
  margin: 0 0 10px;
  line-height: 1.4;
}

.banner-price {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 10px;
}

.banner-current-price {
  color: #ffd700;
  font-size: 20px;
  font-weight: 700;
}

.banner-original-price {
  color: rgba(255,255,255,0.45);
  font-size: 12px;
  text-decoration: line-through;
}

.banner-btn {
  display: inline-block;
  color: #fff;
  font-size: 12px;
  border: 1px solid rgba(255,255,255,0.4);
  padding: 4px 12px;
  border-radius: 14px;
  transition: all 0.2s;

  &:hover {
    background: rgba(255,255,255,0.15);
  }
}

.banner-img-wrap {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 12px;

  .banner-img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
    filter: drop-shadow(0 4px 12px rgba(0,0,0,0.3));
  }
}

.category-nav {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 16px 12px;
  margin: 12px;

  .cat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    font-size: 12px;
    color: #333;

    &:hover {
      color: #E4393C;
    }
  }

  .cat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    object-fit: cover;
  }
}

.section {
  margin: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.hot-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 8px;

  &::-webkit-scrollbar {
    display: none;
  }
}

.hot-item {
  flex-shrink: 0;
  width: 130px;
  padding: 8px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }
}

.hot-img {
  width: 100%;
  height: 114px;
  object-fit: contain;
  border-radius: 4px;
  background: #fafafa;
}

.hot-name {
  font-size: 12px;
  margin-top: 8px;
  height: 34px;
  color: #333;
}

.hot-price {
  margin-top: 4px;
  display: flex;
  align-items: baseline;
  gap: 4px;

  .price {
    font-size: 15px;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.2s;
  overflow: hidden;

  &:hover {
    transform: translateY(-2px);
  }
}

.product-img {
  width: 100%;
  height: 160px;
  object-fit: contain;
  background: #fafafa;
}

.product-info {
  padding: 8px 10px 12px;
}

.product-name {
  font-size: 13px;
  color: #333;
  height: 36px;
}

.product-price {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-top: 6px;

  .price {
    font-size: 16px;
  }
}

.product-sales {
  font-size: 11px;
  color: #999;
}
</style>
