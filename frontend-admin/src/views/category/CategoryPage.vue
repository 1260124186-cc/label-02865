<template>
  <div class="category-page">
    <div class="category-header">
      <span class="header-title">商品分类</span>
    </div>
    <div class="category-body">
      <!-- 左侧分类列表 -->
      <div class="cat-sidebar">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="cat-sidebar-item"
          :class="{ active: activeCatId === cat.id }"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </div>
      </div>
      <!-- 右侧商品列表 -->
      <div class="cat-content">
        <div v-if="loading" class="loading-wrap">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        </div>
        <div v-else-if="products.length === 0" class="empty-wrap">
          <el-empty description="暂无商品" :image-size="80" />
        </div>
        <div v-else class="product-list">
          <div
            v-for="item in products"
            :key="item.id"
            class="product-item card"
            @click="$router.push(`/product/${item.id}`)"
          >
            <img :src="item.mainImage" class="item-img" />
            <div class="item-info">
              <div class="item-name ellipsis-2">{{ item.name }}</div>
              <div class="item-bottom">
                <span class="price">{{ item.price }}</span>
                <span class="item-sales">{{ item.sales }}人付款</span>
              </div>
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
import { getCategoryList } from '@/api/category'
import { getProductList } from '@/api/product'

const route = useRoute()
const categories = ref([])
const activeCatId = ref(null)
const products = ref([])
const loading = ref(false)

onMounted(async () => {
  const res = await getCategoryList()
  categories.value = res.data || []
  if (route.query.id) {
    activeCatId.value = Number(route.query.id)
  } else if (categories.value.length > 0) {
    activeCatId.value = categories.value[0].id
  }
  if (activeCatId.value) {
    loadProducts()
  }
})

function selectCategory(id) {
  activeCatId.value = id
  loadProducts()
}

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProductList({ categoryId: activeCatId.value, page: 1, size: 50 })
    products.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.category-page {
  background: #f5f5f5;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.category-header {
  background: #fff;
  padding: 12px 16px;
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.category-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.cat-sidebar {
  width: 90px;
  background: #fafafa;
  overflow-y: auto;
  flex-shrink: 0;
}

.cat-sidebar-item {
  padding: 14px 8px;
  text-align: center;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.2s;

  &.active {
    background: #fff;
    color: #E4393C;
    font-weight: bold;
    border-left-color: #E4393C;
  }
}

.cat-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.loading-wrap, .empty-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateX(2px);
  }
}

.item-img {
  width: 90px;
  height: 90px;
  object-fit: contain;
  border-radius: 4px;
  background: #fafafa;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-name {
  font-size: 13px;
  color: #333;
}

.item-bottom {
  display: flex;
  align-items: baseline;
  justify-content: space-between;

  .price { font-size: 16px; }
}

.item-sales {
  font-size: 11px;
  color: #999;
}
</style>
