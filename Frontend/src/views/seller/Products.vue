<template>
  <div class="seller-dashboard">
    <!-- Sidebar (same as Dashboard) -->
    <aside class="sidebar">
      <div class="sidebar-brand">
        <h1>MallConnect</h1>
        <p class="sidebar-badge">SELLER CENTER</p>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/seller/dashboard" class="nav-item">
          <el-icon><dashboard /></el-icon>
          <span>Dashboard</span>
        </router-link>
        <router-link to="/seller/products" class="nav-item active">
          <el-icon><inventory-2 /></el-icon>
          <span>Products</span>
        </router-link>
        <router-link to="/seller/orders" class="nav-item">
          <el-icon><shopping-cart /></el-icon>
          <span>Orders</span>
        </router-link>
        <router-link to="/seller/settings" class="nav-item">
          <el-icon><setting /></el-icon>
          <span>Settings</span>
        </router-link>
      </nav>
      <div class="sidebar-user">
        <div class="user-info">
          <div class="user-avatar">{{ sellerInitials }}</div>
          <div class="user-details"><strong>{{ sellerName }}</strong><span>{{ sellerTier }}</span></div>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-area">
      <header class="top-bar">
        <h2>Product Management</h2>
        <div class="top-actions">
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><plus /></el-icon> Add Product
          </el-button>
        </div>
      </header>

      <div class="dashboard-content">
        <el-table :data="products" stripe style="width: 100%" @selection-change="handleSelection">
          <el-table-column type="selection" width="50" />
          <el-table-column label="PRODUCT" width="280">
            <template #default="{ row }">
              <div class="product-cell">
                <img :src="row.image" :alt="row.name" class="product-thumb" />
                <div>
                  <strong>{{ row.name }}</strong>
                  <span class="product-sku">SKU: {{ row.sku }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="CATEGORY" width="140" />
          <el-table-column prop="price" label="PRICE" width="120" sortable>
            <template #default="{ row }">
              <strong>${{ row.price.toFixed(2) }}</strong>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="STOCK" width="100" sortable />
          <el-table-column prop="sold" label="SOLD" width="100" />
          <el-table-column label="STATUS" width="120">
            <template #default="{ row }">
              <el-switch v-model="row.active" active-text="Active" inactive-text="Draft" @change="handleToggleActive(row)" />
            </template>
          </el-table-column>
          <el-table-column label="ACTIONS" width="150" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" size="small" @click="handleEdit(row)">Edit</el-button>
              <el-button text type="danger" size="small" @click="handleDelete(row)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- Add Product Dialog -->
    <el-dialog v-model="showAddDialog" title="Add New Product" width="640px">
      <el-form :model="newProduct" label-position="top">
        <el-form-item label="Product Name" required>
          <el-input v-model="newProduct.name" size="large" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="Category" required>
              <el-select v-model="newProduct.categoryId" size="large" style="width: 100%">
                <el-option
                  v-for="cat in categories"
                  :key="cat.id"
                  :label="cat.name"
                  :value="cat.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Price ($)" required>
              <el-input-number v-model="newProduct.price" :min="0" :precision="2" size="large" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="Stock Quantity">
              <el-input-number v-model="newProduct.stock" :min="0" size="large" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Image URL">
              <el-input v-model="newProduct.imageUrl" size="large" placeholder="https://..." />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Description">
          <el-input v-model="newProduct.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">Cancel</el-button>
        <el-button type="primary" @click="addProduct">Save Product</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { sellerAPI, productsAPI } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const sellerName = computed(() => {
  const user = authStore.user
  if (user) return `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'Seller'
  return 'Seller'
})

const sellerTier = computed(() => {
  return authStore.user?.role || 'Seller'
})

const sellerInitials = computed(() => {
  const user = authStore.user
  if (user) {
    return `${(user.firstName?.charAt(0) || '')}${(user.lastName?.charAt(0) || '')}`.toUpperCase() || 'SE'
  }
  return 'SE'
})

const showAddDialog = ref(false)
const selectedProducts = ref([])
const loading = ref(true)

const newProduct = reactive({
  name: '', categoryId: '', price: 0, stock: 0, description: '', imageUrl: ''
})

const categories = ref([])

const products = ref([])

const fetchProducts = async () => {
  loading.value = true
  try {
    const data = await sellerAPI.getProducts({ page: 0, size: 50 })
    const list = data.content || data || []
    products.value = list.map(p => ({
      id: p.id,
      name: p.name,
      sku: p.id ? `SKU-${p.id.toString().padStart(4, '0')}` : 'N/A',
      category: p.categoryName || 'Uncategorized',
      price: Number(p.price || 0),
      stock: p.stock || 0,
      sold: 0,
      active: p.active !== undefined ? p.active : p.stock > 0,
      image: p.imageUrl || 'https://via.placeholder.com/48'
    }))
  } catch (err) {
    console.error('Error fetching seller products:', err)
    ElMessage.error('Failed to load products')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const data = await productsAPI.getCategories()
    categories.value = data || []
  } catch (err) {
    console.error('Error fetching categories:', err)
  }
}

const handleSelection = (val) => {
  selectedProducts.value = val
}

const handleEdit = (product) => {
  newProduct.name = product.name
  newProduct.price = product.price
  newProduct.stock = product.stock
  newProduct.description = product.description || ''
  newProduct.imageUrl = product.image || ''
  newProduct.categoryId = categories.value.find(c => c.name === product.category)?.id || ''
  showAddDialog.value = true
}

const addProduct = async () => {
  if (!newProduct.name || !newProduct.categoryId) {
    ElMessage.warning('Please fill required fields')
    return
  }
  try {
    await sellerAPI.createProduct({
      name: newProduct.name,
      categoryId: Number(newProduct.categoryId),
      price: Number(newProduct.price),
      stock: Number(newProduct.stock),
      description: newProduct.description,
      imageUrl: newProduct.imageUrl || null
    })
    showAddDialog.value = false
    ElMessage.success('Product added!')
    // Reset form
    Object.keys(newProduct).forEach(k => {
      newProduct[k] = (k === 'price' || k === 'stock' || k === 'categoryId') ? 0 : ''
    })
    fetchProducts()
  } catch (err) {
    ElMessage.error('Failed to add product: ' + (err.response?.data?.message || err.message))
  }
}

const handleDelete = async (product) => {
  try {
    await ElMessageBox.confirm(`Delete "${product.name}"?`, 'Confirm Delete', {
      type: 'warning',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel'
    })
    await sellerAPI.deleteProduct(product.id)
    ElMessage.success('Product deleted')
    fetchProducts()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('Failed to delete product')
    }
  }
}

const handleToggleActive = async (product) => {
  try {
    await sellerAPI.updateProduct(product.id, { active: product.active })
    ElMessage.success(product.active ? 'Product activated' : 'Product deactivated')
    fetchProducts()
  } catch (err) {
    ElMessage.error('Failed to update product status')
    product.active = !product.active // revert
  }
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>

<style scoped>
.seller-dashboard { display: flex; min-height: 100vh; background: #f7f9fc; }
.sidebar { width: 260px; background: white; border-right: 1px solid #eceef1; display: flex; flex-direction: column; position: fixed; top: 0; left: 0; height: 100vh; z-index: 50; }
.sidebar-brand { padding: 32px 24px; }
.sidebar-brand h1 { font-size: 22px; font-weight: 700; color: #0058bc; margin: 0 0 4px; }
.sidebar-badge { font-size: 10px; color: #74777d; letter-spacing: 0.08em; font-weight: 600; }
.sidebar-nav { flex: 1; padding: 0 12px; display: flex; flex-direction: column; gap: 4px; }
.nav-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 10px; color: #44474c; text-decoration: none; font-size: 15px; transition: all 0.2s; }
.nav-item:hover { background: #f2f4f7; }
.nav-item.active { background: #0058bc; color: white; font-weight: 600; }
.sidebar-user { padding: 16px; border-top: 1px solid #eceef1; display: flex; align-items: center; }
.user-info { display: flex; align-items: center; gap: 12px; }
.user-avatar { width: 40px; height: 40px; border-radius: 50%; background: #d2e4fb; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 700; color: #0058bc; }
.user-details strong { display: block; font-size: 14px; color: #191c1e; }
.user-details span { font-size: 11px; color: #74777d; }
.main-area { margin-left: 260px; flex: 1; display: flex; flex-direction: column; }
.top-bar { height: 72px; background: white; border-bottom: 1px solid #eceef1; padding: 0 32px; display: flex; align-items: center; justify-content: space-between; }
.top-bar h2 { font-size: 20px; font-weight: 600; color: #041627; margin: 0; }
.dashboard-content { padding: 32px; flex: 1; }
.product-cell { display: flex; align-items: center; gap: 12px; }
.product-thumb { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; background: #f2f4f7; }
.product-cell strong { display: block; font-size: 14px; color: #191c1e; }
.product-sku { font-size: 12px; color: #74777d; }
</style>
