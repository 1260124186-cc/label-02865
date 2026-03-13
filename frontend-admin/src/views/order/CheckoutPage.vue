<template>
  <div class="checkout-page">
    <div class="page-header">
      <el-icon class="back-btn" @click="$router.back()"><ArrowLeft /></el-icon>
      <span>确认订单</span>
    </div>

    <!-- 收货地址 -->
    <div class="address-section card" @click="showAddressPicker = true">
      <div v-if="selectedAddress" class="address-info">
        <div class="address-top">
          <span class="addr-name">{{ selectedAddress.receiverName }}</span>
          <span class="addr-phone">{{ selectedAddress.receiverPhone }}</span>
          <span v-if="selectedAddress.isDefault" class="addr-default-tag">默认</span>
        </div>
        <div class="addr-detail">{{ fullAddress(selectedAddress) }}</div>
      </div>
      <div v-else class="no-address">
        <el-icon><LocationFilled /></el-icon>
        <span>请添加收货地址</span>
      </div>
      <el-icon class="arrow-icon"><ArrowRight /></el-icon>
    </div>

    <!-- 商品列表 -->
    <div class="goods-section card">
      <div v-for="item in checkoutItems" :key="item.id" class="goods-item">
        <img :src="item.product?.mainImage" class="gi-img" />
        <div class="gi-info">
          <div class="gi-name ellipsis-2">{{ item.product?.name }}</div>
          <div class="gi-bottom">
            <span class="price">{{ item.product?.price }}</span>
            <span class="gi-qty">x{{ item.quantity }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 金额汇总 -->
    <div class="summary-section card">
      <div class="summary-row">
        <span>商品金额</span>
        <span>¥{{ checkoutTotalPrice }}</span>
      </div>
      <div class="summary-row">
        <span>运费</span>
        <span class="free-ship">免运费</span>
      </div>
      <div class="summary-row total-row">
        <span>合计</span>
        <span class="price total-price">{{ checkoutTotalPrice }}</span>
      </div>
    </div>

    <!-- 底部提交栏 -->
    <div class="submit-bar safe-area-bottom">
      <div class="submit-info">
        <span>合计: </span>
        <span class="price submit-price">{{ checkoutTotalPrice }}</span>
      </div>
      <el-button type="primary" class="submit-btn" :loading="submitting" @click="handleSubmit">
        提交订单
      </el-button>
    </div>

    <!-- 地址选择弹窗 -->
    <el-drawer v-model="showAddressPicker" title="选择收货地址" direction="btt" size="70%">
      <div class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="addr-card"
          :class="{ selected: selectedAddress?.id === addr.id }"
          @click="pickAddress(addr)"
        >
          <div class="addr-card-top">
            <span class="addr-name">{{ addr.receiverName }}</span>
            <span class="addr-phone">{{ addr.receiverPhone }}</span>
            <span v-if="addr.isDefault" class="addr-default-tag">默认</span>
          </div>
          <div class="addr-card-detail">{{ fullAddress(addr) }}</div>
          <el-icon class="addr-delete" @click.stop="handleDeleteAddr(addr.id)"><Delete /></el-icon>
        </div>
        <div v-if="addressList.length === 0" class="no-addr-tip">暂无收货地址</div>
      </div>
      <div class="addr-drawer-footer">
        <el-button type="primary" class="add-addr-btn" @click="openAddressForm">+ 新增收货地址</el-button>
      </div>
    </el-drawer>

    <!-- 新增地址弹窗 -->
    <el-dialog v-model="showAddressForm" title="新增收货地址" width="100%" :close-on-click-modal="false" @closed="handleAddrDialogClosed" class="addr-dialog" :append-to-body="true" :show-close="true" align-center>
      <el-form :model="addrForm" :rules="addrRules" ref="addrFormRef" label-position="top" hide-required-asterisk class="addr-form">
        <el-form-item prop="receiverName">
          <el-input v-model="addrForm.receiverName" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item prop="receiverPhone">
          <el-input v-model="addrForm.receiverPhone" placeholder="手机号" maxlength="11" />
        </el-form-item>
        <div class="region-row">
          <el-form-item prop="province" class="region-item">
            <el-input v-model="addrForm.province" placeholder="省份" />
          </el-form-item>
          <el-form-item prop="city" class="region-item">
            <el-input v-model="addrForm.city" placeholder="城市" />
          </el-form-item>
          <el-form-item prop="district" class="region-item">
            <el-input v-model="addrForm.district" placeholder="区/县" />
          </el-form-item>
        </div>
        <el-form-item prop="detail">
          <el-input v-model="addrForm.detail" placeholder="详细地址：街道、楼栋、门牌号等" />
        </el-form-item>
        <div class="default-check">
          <el-checkbox v-model="addrForm.isDefault" :true-value="1" :false-value="0">设为默认地址</el-checkbox>
        </div>
      </el-form>
      <template #footer>
        <div class="h5-dialog-footer">
          <el-button class="h5-btn-cancel" @click="showAddressForm = false">取消</el-button>
          <el-button class="h5-btn-confirm" type="primary" :loading="savingAddr" @click="handleSaveAddr">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { getAddressList, saveAddress, deleteAddress } from '@/api/address'
import { createOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

// 判断是否为"立即购买"模式
const isBuyNow = computed(() => !!cartStore.buyNowItem)

// 结算商品列表：立即购买用 buyNowItem，否则用购物车选中项
const checkoutItems = computed(() => {
  if (isBuyNow.value) {
    return [cartStore.buyNowItem]
  }
  return cartStore.selectedItems
})

// 结算总价
const checkoutTotalPrice = computed(() => {
  return checkoutItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * item.quantity
  }, 0).toFixed(2)
})

const addressList = ref([])
const selectedAddress = ref(null)
const showAddressPicker = ref(false)
const showAddressForm = ref(false)
const submitting = ref(false)
const savingAddr = ref(false)
const addrFormRef = ref(null)

const addrForm = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0
})

const addrRules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区/县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

function fullAddress(addr) {
  return `${addr.province}${addr.city}${addr.district} ${addr.detail}`
}

async function loadAddresses() {
  const res = await getAddressList()
  addressList.value = res.data || []
  // 自动选中默认地址
  if (!selectedAddress.value && addressList.value.length > 0) {
    selectedAddress.value = addressList.value.find(a => a.isDefault === 1) || addressList.value[0]
  }
}

function pickAddress(addr) {
  selectedAddress.value = addr
  showAddressPicker.value = false
}

function openAddressForm() {
  showAddressPicker.value = false
  // 等 drawer 关闭动画结束后再打开 dialog，避免页面闪动
  setTimeout(() => {
    showAddressForm.value = true
  }, 350)
}

async function handleSaveAddr() {
  await addrFormRef.value.validate()
  savingAddr.value = true
  try {
    await saveAddress(addrForm.value)
    ElMessage.success('地址保存成功')
    showAddressForm.value = false
    await loadAddresses()
    // 如果是第一个地址，自动选中
    if (addressList.value.length === 1) {
      selectedAddress.value = addressList.value[0]
    }
    // 保存后重新打开地址选择抽屉
    showAddressPicker.value = true
  } finally {
    savingAddr.value = false
  }
}

function handleAddrDialogClosed() {
  addrForm.value = { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
  addrFormRef.value?.resetFields()
}

async function handleDeleteAddr(id) {
  await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
  await deleteAddress(id)
  ElMessage.success('已删除')
  if (selectedAddress.value?.id === id) {
    selectedAddress.value = null
  }
  await loadAddresses()
}

async function handleSubmit() {
  if (!selectedAddress.value) {
    showAddressPicker.value = true
    ElMessage.warning('请先选择收货地址')
    return
  }
  if (checkoutItems.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }
  submitting.value = true
  try {
    await createOrder({ addressId: selectedAddress.value.id })
    ElMessage.success('下单成功！')
    await cartStore.fetchCart()
    router.replace('/order/list')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (checkoutItems.value.length === 0) {
    ElMessage.warning('请先选择要结算的商品')
    router.replace('/cart')
    return
  }
  loadAddresses()
})

onUnmounted(() => {
  // 离开结算页时清除立即购买数据
  cartStore.clearBuyNowItem()
})
</script>

<style lang="scss" scoped>
.checkout-page {
  max-width: 750px;
  margin: 0 auto;
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 70px;
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

.address-section {
  margin: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  border-left: 3px solid #E4393C;
}

.address-info {
  flex: 1;
}

.address-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.addr-name {
  font-weight: bold;
  font-size: 15px;
}

.addr-phone {
  font-size: 13px;
  color: #666;
}

.addr-default-tag {
  font-size: 10px;
  color: #E4393C;
  border: 1px solid #E4393C;
  padding: 0 4px;
  border-radius: 2px;
}

.addr-detail {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
}

.no-address {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
  font-size: 14px;
}

.arrow-icon {
  color: #ccc;
  font-size: 16px;
  flex-shrink: 0;
}

.goods-section {
  margin: 12px;
  padding: 12px;
}

.goods-item {
  display: flex;
  gap: 10px;
  padding: 8px 0;

  & + & {
    border-top: 1px solid #f5f5f5;
  }
}

.gi-img {
  width: 70px;
  height: 70px;
  object-fit: contain;
  border-radius: 4px;
  background: #fafafa;
  flex-shrink: 0;
}

.gi-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.gi-name {
  font-size: 13px;
  color: #333;
}

.gi-bottom {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  .price { font-size: 15px; }
}

.gi-qty {
  font-size: 12px;
  color: #999;
}

.summary-section {
  margin: 12px;
  padding: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
  padding: 6px 0;
}

.free-ship {
  color: #67C23A;
}

.total-row {
  border-top: 1px solid #f5f5f5;
  padding-top: 12px;
  margin-top: 6px;
  font-weight: bold;
  color: #333;
}

.total-price {
  font-size: 18px;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 750px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 8px 16px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.08);
  z-index: 100;
}

.submit-info {
  flex: 1;
  text-align: right;
  margin-right: 12px;
  font-size: 14px;
}

.submit-price {
  font-size: 18px;
}

.submit-btn {
  border-radius: 20px;
  min-width: 120px;
  background: #E4393C;
  border: none;
  height: 40px;
  font-size: 15px;
}

.addr-card {
  padding: 14px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;

  &.selected {
    border-color: #E4393C;
    background: #FFF5F5;
  }

  &:hover {
    border-color: #E4393C;
  }
}

.addr-card-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.addr-card-detail {
  font-size: 13px;
  color: #666;
  padding-right: 30px;
}

.addr-delete {
  position: absolute;
  top: 14px;
  right: 14px;
  color: #999;
  cursor: pointer;
  font-size: 16px;

  &:hover { color: #E4393C; }
}

.no-addr-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}

.addr-drawer-footer {
  padding: 16px;
}

.add-addr-btn {
  width: 100%;
  border-radius: 20px;
  background: #E4393C;
  border: none;
}

.addr-form-wrap {
  .addr-form {
    :deep(.el-form-item__label) {
      display: none;
    }
  }
}

.addr-form {
  :deep(.el-form-item__label) {
    display: none;
  }
}

.region-row {
  display: flex;
  gap: 8px;
  .region-item {
    flex: 1;
    min-width: 0;
    overflow: visible;

    :deep(.el-form-item__error) {
      font-size: 11px;
      padding-top: 1px;
      white-space: nowrap;
    }
  }
}

.default-check {
  padding: 0;
}
</style>
