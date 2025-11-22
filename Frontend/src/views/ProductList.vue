<template>
  <div class="product-list-page">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ $t('common.products') }}</h1>
        <p class="page-subtitle">Browse our curated collection from top sellers</p>
      </div>
    </div>

    <!-- Search & Filter Bar -->
    <div class="filter-bar">
      <el-input
        v-model="searchQuery"
        size="large"
        placeholder="Search products..."
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><search /></el-icon>
        </template>
      </el-input>

      <el-select v-model="selectedCategoryId" size="large" placeholder="All Categories" class="category-select" clearable>
        <el-option
          v-for="cat in categories"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>

      <el-select v-model="sortBy" size="large" class="sort-select">
        <el-option label="Newest" value="createdAt,desc" />
        <el-option label="Price: Low to High" value="price,asc" />
        <el-option label="Price: High to Low" value="price,desc" />
      </el-select>
    </div>

    <!-- Active Filters -->
    <div v-if="selectedCategoryId || searchQuery" class="active-filters">
      <el-tag
        v-if="selectedCategoryId"
        closable
        size="large"
        @close="selectedCategoryId = null"
      >
        {{ selectedCategoryName }}
      </el-tag>
      <el-tag
        v-if="searchQuery"
        closable
        size="large"
        @close="searchQuery = ''"
      >
        "{{ searchQuery }}"
      </el-tag>
      <span class="results-count">{{ totalResults }} results</span>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="products-grid">
      <el-card v-for="n in 8" :key="n" class="product-card" :body-style="{ padding: '0' }">
        <div class="product-image">
          <el-skeleton :rows="1" animated style="width: 100%; height: 100%" />
        </div>
        <div class="product-info">
          <el-skeleton :rows="3" animated />
        </div>
      </el-card>
    </div>

    <!-- Products Grid -->
    <div v-else class="products-grid">
      <el-card
        v-for="product in products"
        :key="product.id"
        class="product-card"
        :body-style="{ padding: '0' }"
        shadow="hover"
        @click="$router.push(`/products/${product.id}`)"
      >
        <div class="product-image">
          <img :src="product.imageUrl || 'https://via.placeholder.com/400x400'" :alt="product.name" />
        </div>
        <div class="product-info">
          <p class="product-category">{{ product.categoryName || 'Uncategorized' }}</p>
          <h3 class="product-name">{{ product.name }}</h3>
          <div class="product-footer">
            <span class="price">${{ (product.price || 0).toFixed(2) }}</span>
            <el-button
              type="primary"
              circle
              class="add-to-cart-btn"
              @click.stop="addToCart(product)"
            >
              <el-icon><shopping-bag /></el-icon>
            </el-button>
          </div>
        </div>
      </el-card>

      <div v-if="products.length === 0" class="no-results">
        <el-icon :size="48" color="#c4c6cd"><search /></el-icon>
        <p>No products found matching your criteria</p>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalResults > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="totalResults"
        layout="prev, pager, next"
        background
        @current-change="fetchProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { productsAPI } from '@/services/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const authStore = useAuthStore()

const searchQuery = ref('')
const selectedCategoryId = ref(null)
const sortBy = ref('createdAt,desc')
const currentPage = ref(1)
const pageSize = ref(12)
const totalResults = ref(0)
const loading = ref(false)
const products = ref([])
const categories = ref([])

const selectedCategoryName = computed(() => {
  const cat = categories.value.find(c => c.id === selectedCategoryId.value)
  return cat ? cat.name : ''
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const [sortField, sortDir] = (sortBy.value || 'createdAt,desc').split(',')
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: `${sortField},${sortDir}`
    }
    if (searchQuery.value) {
      params.keyword = searchQuery.value
    }
    if (selectedCategoryId.value) {
      params.categoryId = selectedCategoryId.value
    }

    const response = await productsAPI.getProducts(params)
    products.value = response.content || response
    totalResults.value = response.totalElements || response.length || 0
  } catch (err) {
    console.error('Error fetching products:', err)
    products.value = []
    totalResults.value = 0
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const response = await productsAPI.getCategories()
    categories.value = Array.isArray(response) ? response : (response.content || [])
  } catch (err) {
    console.error('Error fetching categories:', err)
    categories.value = []
  }
}

onMounted(() => {
  // If navigated from "Visit Store", pre-fill the search with seller name
  if (route.query.seller) {
    searchQuery.value = route.query.seller
  }
  fetchCategories()
  fetchProducts()
})

let searchTimer = null
watch(searchQuery, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    fetchProducts()
  }, 400)
})

watch(selectedCategoryId, () => {
  currentPage.value = 1
  fetchProducts()
})

watch(sortBy, () => {
  currentPage.value = 1
  fetchProducts()
})

const handleSearch = () => {
  currentPage.value = 1
  fetchProducts()
}

const addToCart = (product) => {
  authStore.addToCart({
    id: product.id,
    name: product.name,
    price: product.price,
    image: product.imageUrl || 'https://via.placeholder.com/400x400',
    stock: product.stock,
    category: product.categoryName
  })
  ElMessage.success('Added to cart!')
}
</script>

<style scoped>
.product-list-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #041627;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #74777d;
  margin: 0;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 250px;
}

.category-select {
  width: 200px;
}

.sort-select {
  width: 200px;
}

.active-filters {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.results-count {
  color: #74777d;
  font-size: 14px;
  margin-left: auto;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.product-card {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #eceef1;
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
}

.product-image {
  position: relative;
  aspect-ratio: 1;
  background: #f2f4f7;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.product-card:hover .product-image img {
  transform: scale(1.08);
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  color: white;
  letter-spacing: 0.05em;
}

.product-badge.hot { background: #ba1a1a; }
.product-badge.new { background: #0058bc; }

.wishlist-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.wishlist-btn:hover {
  transform: scale(1.1);
}

.product-info {
  padding: 20px;
}

.product-category {
  font-size: 11px;
  color: #74777d;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 4px 0;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
  color: #191c1e;
  margin: 0 0 8px 0;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.review-count {
  font-size: 13px;
  color: #74777d;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #0058bc;
}

.add-to-cart-btn {
  background: #041627;
  border-color: #041627;
}

.add-to-cart-btn:hover {
  background: #0058bc;
  border-color: #0058bc;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin: 48px 0;
}

@media (max-width: 1024px) {
  .products-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 768px) {
  .products-grid { grid-template-columns: repeat(2, 1fr); }
  .filter-bar { flex-direction: column; }
  .category-select, .sort-select { width: 100%; }
}

@media (max-width: 480px) {
  .products-grid { grid-template-columns: 1fr; }
}

.no-results {
  grid-column: 1 / -1;
  text-align: center;
  padding: 64px 0;
  color: #74777d;
}

.no-results p {
  margin-top: 16px;
  font-size: 16px;
}
</style>
