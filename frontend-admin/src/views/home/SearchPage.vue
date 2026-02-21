<template>
  <div class="search-page">
    <div class="search-header">
      <el-icon class="back-btn" @click="$router.back()"><ArrowLeft /></el-icon>
      <div class="search-input-wrap">
        <el-input
          v-model="keyword"
          placeholder="搜索手机、平板、配件..."
          clearable
          @keyup.enter="doSearch"
          autofocus
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
      <el-button type="primary" @click="doSearch" :loading="loading">搜索</el-button>
    </div>

    <div class="search-results" v-if="searched">
      <div v-if="products.length === 0" class="empty-tip">
        <el-empty description="未找到相关商品" />
      </div>
      <div class="product-grid" v-else>
        <div
          v-for="item in products"
          :key="item.id"
          class="product-card card"
          @click="$router.push(`/product/${item.id}`)"
        >
          <img :src="item.mainImage" class="product-img" />
          <div class="product-info">
            <div class="product-name ellipsis-2">{{ item.name }}</div>
            <div class="product-price">
              <span class="price">{{ item.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getProductList } from '@/api/product'

const keyword = ref('')
const products = ref([])
const loading = ref(false)
const searched = ref(false)

async function doSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  searched.value = true
  try {
    const res = await getProductList({ keyword: keyword.value, page: 1, size: 20 })
    products.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.search-page {
  background: #f5f5f5;
  min-height: 100vh;
  max-width: 750px;
  margin: 0 auto;
}

.search-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.back-btn {
  font-size: 20px;
  cursor: pointer;
  color: #333;
}

.search-input-wrap {
  flex: 1;

  :deep(.el-input__wrapper) {
    height: 32px;
  }
}

.search-header .el-button {
  height: 32px;
  flex-shrink: 0;
}

.search-results {
  padding: 12px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.2s;
  &:hover { transform: translateY(-2px); }
}

.product-img {
  width: 100%;
  height: 150px;
  object-fit: contain;
  background: #fafafa;
}

.product-info {
  padding: 8px 10px 12px;
}

.product-name {
  font-size: 13px;
  height: 36px;
}

.product-price {
  margin-top: 6px;
  .price { font-size: 16px; }
}
</style>
