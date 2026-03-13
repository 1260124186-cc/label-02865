<template>
  <div class="address-page">
    <div class="page-header">
      <el-icon class="back-btn" @click="$router.back()"><ArrowLeft /></el-icon>
      <span>收货地址</span>
    </div>

    <div class="address-body">
      <div v-if="loading" class="loading-wrap">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
      </div>
      <div v-else-if="addressList.length === 0" class="empty-wrap">
        <el-empty description="暂无收货地址" :image-size="80" />
      </div>
      <div v-else class="addr-list">
        <div v-for="addr in addressList" :key="addr.id" class="addr-card card">
          <div class="addr-content">
            <div class="addr-top">
              <span class="addr-name">{{ addr.receiverName }}</span>
              <span class="addr-phone">{{ addr.receiverPhone }}</span>
              <span v-if="addr.isDefault" class="addr-default-tag">默认</span>
            </div>
            <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
          </div>
          <div class="addr-divider"></div>
          <div class="addr-actions">
            <div class="addr-action-btn" @click="handleEdit(addr)">
              <el-icon :size="14"><Edit /></el-icon>
              <span>编辑</span>
            </div>
            <div class="addr-action-divider"></div>
            <div class="addr-action-btn addr-action-delete" @click="handleDelete(addr.id)">
              <el-icon :size="14"><Delete /></el-icon>
              <span>删除</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-add">
      <el-button type="primary" class="add-btn" @click="openCreate">+ 新增收货地址</el-button>
    </div>

    <el-dialog
      v-model="showForm"
      :title="dialogTitle"
      width="100%"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
      class="addr-dialog"
      :append-to-body="true"
      :show-close="true"
      align-center
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" hide-required-asterisk class="addr-form">
        <el-form-item prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="手机号" maxlength="11" />
        </el-form-item>
        <div class="region-row">
          <el-form-item prop="province" class="region-item">
            <el-input v-model="form.province" placeholder="省份" />
          </el-form-item>
          <el-form-item prop="city" class="region-item">
            <el-input v-model="form.city" placeholder="城市" />
          </el-form-item>
          <el-form-item prop="district" class="region-item">
            <el-input v-model="form.district" placeholder="区/县" />
          </el-form-item>
        </div>
        <el-form-item prop="detail">
          <el-input v-model="form.detail" placeholder="详细地址：街道、楼栋、门牌号等" />
        </el-form-item>
        <div class="default-check">
          <el-checkbox v-model="form.isDefault" :true-value="1" :false-value="0">设为默认地址</el-checkbox>
        </div>
      </el-form>
      <template #footer>
        <div class="h5-dialog-footer">
          <el-button class="h5-btn-cancel" @click="showForm = false">取消</el-button>
          <el-button class="h5-btn-confirm" type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue'
import { getAddressList, saveAddress, deleteAddress } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

const addressList = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const formRef = ref(null)
const dialogTitle = ref('新增收货地址')

const emptyForm = () => ({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0
})

const form = ref(emptyForm())

const rules = {
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

async function loadList() {
  loading.value = true
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveAddress(form.value)
    ElMessage.success('保存成功')
    showForm.value = false
    await loadList()
  } finally {
    saving.value = false
  }
}

function openCreate() {
  dialogTitle.value = '新增收货地址'
  form.value = emptyForm()
  formRef.value?.resetFields()
  showForm.value = true
}

function handleEdit(addr) {
  dialogTitle.value = '编辑收货地址'
  form.value = { ...addr }
  formRef.value?.resetFields()
  showForm.value = true
}

function handleDialogClosed() {
  dialogTitle.value = '新增收货地址'
  form.value = emptyForm()
  formRef.value?.resetFields()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
  await deleteAddress(id)
  ElMessage.success('已删除')
  await loadList()
}

onMounted(() => loadList())
</script>

<style lang="scss" scoped>
.address-page {
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

.back-btn { font-size: 20px; cursor: pointer; }

.address-body { padding: 12px; }

.loading-wrap, .empty-wrap {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.addr-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.addr-card {
  padding: 0;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.addr-content {
  padding: 14px 16px;
}

.addr-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.addr-name { font-weight: bold; font-size: 15px; color: #333; }
.addr-phone { font-size: 13px; color: #666; }

.addr-default-tag {
  font-size: 10px;
  color: #fff;
  background: #E4393C;
  padding: 1px 6px;
  border-radius: 2px;
  line-height: 1.4;
}

.addr-detail { font-size: 13px; color: #888; line-height: 1.5; }

.addr-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 0 16px;
}

.addr-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 8px 16px;
  gap: 0;
}

.addr-action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 14px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;

  &:hover {
    color: #333;
    background: #f5f5f5;
  }
}

.addr-action-delete {
  color: #E4393C;

  &:hover {
    color: #c62828;
    background: #fff0f0;
  }
}

.addr-action-divider {
  width: 1px;
  height: 16px;
  background: #e8e8e8;
}

.bottom-add {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 750px;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.06);
  z-index: 100;
}

.add-btn {
  width: 100%;
  border-radius: 20px;
  background: #E4393C;
  border: none;
  height: 40px;
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
