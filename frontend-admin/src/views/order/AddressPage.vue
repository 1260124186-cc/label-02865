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
          <div class="addr-top">
            <span class="addr-name">{{ addr.receiverName }}</span>
            <span class="addr-phone">{{ addr.receiverPhone }}</span>
            <span v-if="addr.isDefault" class="addr-default-tag">默认</span>
          </div>
          <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
          <div class="addr-actions">
            <el-button text size="small" type="danger" @click="handleDelete(addr.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-add">
      <el-button type="primary" class="add-btn" @click="showForm = true">+ 新增收货地址</el-button>
    </div>

    <el-dialog v-model="showForm" title="新增收货地址" width="90%" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="如：广东省" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="如：深圳市" />
        </el-form-item>
        <el-form-item label="区/县" prop="district">
          <el-input v-model="form.district" placeholder="如：南山区" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="2" placeholder="街道、楼栋、门牌号等" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault" :true-value="1" :false-value="0">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAddressList, saveAddress, deleteAddress } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

const addressList = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const formRef = ref(null)

const form = ref({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

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
    form.value = { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
    await loadList()
  } finally {
    saving.value = false
  }
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

.addr-card { padding: 14px; }

.addr-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.addr-name { font-weight: bold; font-size: 15px; }
.addr-phone { font-size: 13px; color: #666; }

.addr-default-tag {
  font-size: 10px;
  color: #E4393C;
  border: 1px solid #E4393C;
  padding: 0 4px;
  border-radius: 2px;
}

.addr-detail { font-size: 13px; color: #666; line-height: 1.4; }

.addr-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
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
</style>
