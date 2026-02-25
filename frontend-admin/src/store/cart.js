import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartList, addCart as addCartApi, updateCart as updateCartApi, deleteCart as deleteCartApi, selectAllCart } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartList = ref([])
  const loading = ref(false)
  const buyNowItem = ref(null)

  const totalCount = computed(() => cartList.value.reduce((sum, item) => sum + item.quantity, 0))
  const selectedItems = computed(() => cartList.value.filter(item => item.selected === 1))
  const totalPrice = computed(() => {
    return selectedItems.value.reduce((sum, item) => {
      return sum + (item.product?.price || 0) * item.quantity
    }, 0).toFixed(2)
  })
  const isAllSelected = computed(() => cartList.value.length > 0 && cartList.value.every(item => item.selected === 1))

  async function fetchCart() {
    loading.value = true
    try {
      const res = await getCartList()
      cartList.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  async function addToCart(productId, quantity = 1) {
    await addCartApi({ productId, quantity })
    await fetchCart()
  }

  async function updateQuantity(id, quantity) {
    await updateCartApi(id, quantity)
    await fetchCart()
  }

  async function removeItem(id) {
    await deleteCartApi(id)
    await fetchCart()
  }

  async function toggleSelectAll(selected) {
    await selectAllCart(selected)
    await fetchCart()
  }

  // 立即购买：设置选中商品（不加入购物车），直接用于结算
  function setBuyNowItem(product, quantity = 1) {
    buyNowItem.value = {
      id: 'buy-now',
      productId: product.id,
      quantity,
      selected: 1,
      product
    }
  }

  function clearBuyNowItem() {
    buyNowItem.value = null
  }

  return { cartList, loading, totalCount, selectedItems, totalPrice, isAllSelected, fetchCart, addToCart, updateQuantity, removeItem, toggleSelectAll, buyNowItem, setBuyNowItem, clearBuyNowItem }
})
