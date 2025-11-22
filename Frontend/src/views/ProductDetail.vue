<template>
  <div class="product-detail-page">
    <!-- Breadcrumb -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">{{ $t('common.home') }}</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">{{ $t('common.products') }}</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <el-icon :size="48" color="#ba1a1a"><warning-filled /></el-icon>
      <h2>Product Not Found</h2>
      <p>{{ error }}</p>
      <el-button type="primary" @click="$router.push('/products')">Back to Products</el-button>
    </div>

    <!-- Product Content -->
    <template v-else>
      <div class="product-layout">
        <!-- Image Gallery -->
        <div class="product-gallery">
          <div class="main-image">
            <img :src="product.image" :alt="product.name" />
          </div>
          <div class="thumbnail-list">
            <div
              v-for="(thumb, idx) in product.thumbnails"
              :key="idx"
              :class="['thumbnail', { active: selectedImage === idx }]"
              @click="selectedImage = idx"
            >
              <img :src="thumb" :alt="`${product.name} view ${idx + 1}`" />
            </div>
          </div>
        </div>

        <!-- Product Info -->
        <div class="product-details">
          <span class="product-category">{{ product.category }}</span>
          <h1 class="product-name">{{ product.name }}</h1>

          <div class="rating-row">
            <el-rate v-model="product.rating" disabled size="small" />
            <span class="rating-value">{{ product.rating }}</span>
            <span class="review-count">({{ product.reviewCount }} reviews)</span>
            <span class="sold-count">{{ product.soldCount }} sold</span>
          </div>

          <div class="price-section">
            <span class="current-price">${{ product.price.toFixed(2) }}</span>
            <span v-if="product.originalPrice" class="original-price">${{ product.originalPrice.toFixed(2) }}</span>
            <span v-if="product.discount" class="discount-badge">-{{ product.discount }}% OFF</span>
          </div>

          <p class="product-description">{{ product.description }}</p>

          <!-- Quantity -->
          <div class="quantity-section">
            <h4 class="section-label">{{ $t('cart.quantity') }}</h4>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock || 99"
              size="large"
            />
            <span class="stock-info" :class="{ 'low-stock': product.stock <= 5 }">
              {{ product.stock > 0 ? `${product.stock} in stock` : 'Out of stock' }}
            </span>
          </div>

          <!-- Action Buttons -->
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              class="add-to-cart-btn"
              :disabled="!product.stock"
              @click="handleAddToCart"
            >
              <el-icon><shopping-bag /></el-icon>
              {{ $t('product.addToCart') }}
            </el-button>
            <el-button
              size="large"
              class="buy-now-btn"
              :disabled="!product.stock"
              @click="handleBuyNow"
            >
              Buy Now
            </el-button>
          </div>

          <!-- Shipping & Returns -->
          <div class="info-cards">
            <div class="info-card">
              <el-icon><truck /></el-icon>
              <div>
                <strong>Free Shipping</strong>
                <span>On orders over $99</span>
              </div>
            </div>
            <div class="info-card">
              <el-icon><refresh /></el-icon>
              <div>
                <strong>30-Day Returns</strong>
                <span>Hassle-free returns</span>
              </div>
            </div>
            <div class="info-card">
              <el-icon><shield /></el-icon>
              <div>
                <strong>Secure Payment</strong>
                <span>Encrypted checkout</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Seller Info -->
      <div class="seller-card">
        <div class="seller-info">
          <div class="seller-avatar">
            <el-icon><shop /></el-icon>
          </div>
          <div>
            <h3>{{ product.seller?.name || 'MallConnect Seller' }}</h3>
            <p>{{ product.seller?.description || 'Trusted seller on MallConnect' }}</p>
          </div>
        </div>
        <el-button size="large" @click="visitStore">
          Visit Store
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { productsAPI } from '@/services/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const selectedImage = ref(0)
const quantity = ref(1)
const loading = ref(true)
const error = ref(null)

const product = ref({
  id: null,
  name: '',
  category: '',
  price: 0,
  originalPrice: null,
  discount: null,
  rating: 4.5,
  reviewCount: 0,
  soldCount: 0,
  stock: 0,
  description: '',
  image: 'https://via.placeholder.com/600x600',
  thumbnails: [],
  seller: { id: null, name: '', description: '' },
  reviews: []
})

const fetchProduct = async () => {
  loading.value = true
  error.value = null
  try {
    const id = route.params.id
    const response = await productsAPI.getProduct(id)

    const imageUrl = response.imageUrl || 'https://via.placeholder.com/600x600'
    product.value = {
      id: response.id,
      name: response.name,
      category: response.categoryName || 'Uncategorized',
      price: response.price || 0,
      originalPrice: null,
      discount: null,
      rating: 4.5,
      reviewCount: 0,
      soldCount: 0,
      stock: response.stock || 0,
      description: response.description || '',
      image: imageUrl,
      thumbnails: [imageUrl, imageUrl, imageUrl, imageUrl],
      seller: {
        id: response.sellerId,
        name: response.sellerName || 'MallConnect Seller',
        description: 'Trusted seller on MallConnect'
      },
      reviews: []
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load product. Please try again.'
    console.error('Error fetching product:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProduct()
})

watch(() => route.params.id, () => {
  fetchProduct()
})

const handleAddToCart = () => {
  authStore.addToCart({
    id: product.value.id,
    name: product.value.name,
    price: product.value.price,
    image: product.value.image,
    stock: product.value.stock,
    category: product.value.category
  }, quantity.value)
  ElMessage.success(`Added ${quantity.value} item(s) to cart!`)
}

const handleBuyNow = () => {
  authStore.addToCart({
    id: product.value.id,
    name: product.value.name,
    price: product.value.price,
    image: product.value.image,
    stock: product.value.stock,
    category: product.value.category
  }, quantity.value)
  router.push('/checkout')
}

const visitStore = () => {
  const sellerName = product.value.seller?.name || ''
  if (sellerName) {
    router.push({ path: '/products', query: { seller: sellerName } })
  }
}
</script>

<style scoped>
.product-detail-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px 64px;
}

.breadcrumb {
  margin: 24px 0;
}

.loading-container {
  padding: 80px 0;
}

.error-container {
  text-align: center;
  padding: 80px 0;
}

.error-container h2 {
  margin: 16px 0 8px;
  color: #191c1e;
}

.error-container p {
  color: #74777d;
  margin-bottom: 24px;
}

.product-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 64px;
  margin-bottom: 48px;
}

.product-gallery {
  position: sticky;
  top: 100px;
  align-self: start;
}

.main-image {
  aspect-ratio: 1;
  background: #f2f4f7;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 16px;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-list {
  display: flex;
  gap: 12px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  transition: border-color 0.2s;
}

.thumbnail.active {
  border-color: #0058bc;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  display: flex;
  flex-direction: column;
}

.product-category {
  font-size: 12px;
  color: #74777d;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 8px;
}

.product-name {
  font-size: 36px;
  font-weight: 700;
  color: #041627;
  margin: 0 0 16px 0;
  line-height: 1.2;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}

.rating-value {
  font-weight: 700;
  color: #191c1e;
}

.review-count, .sold-count {
  color: #74777d;
  font-size: 14px;
}

.sold-count {
  margin-left: auto;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 24px;
}

.current-price {
  font-size: 36px;
  font-weight: 700;
  color: #0058bc;
}

.original-price {
  font-size: 20px;
  color: #c4c6cd;
  text-decoration: line-through;
}

.discount-badge {
  padding: 4px 10px;
  background: #ba1a1a;
  color: white;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 700;
}

.product-description {
  font-size: 16px;
  color: #44474c;
  line-height: 1.7;
  margin-bottom: 32px;
}

.section-label {
  font-size: 14px;
  font-weight: 600;
  color: #191c1e;
  margin: 0 0 12px 0;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.stock-info {
  color: #0058bc;
  font-weight: 600;
  font-size: 14px;
}

.stock-info.low-stock {
  color: #ba1a1a;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 40px;
}

.add-to-cart-btn {
  flex: 1;
  background: #041627;
  border-color: #041627;
  padding: 16px 32px;
  font-weight: 700;
  font-size: 16px;
}

.add-to-cart-btn:hover {
  background: #0058bc;
  border-color: #0058bc;
}

.buy-now-btn {
  flex: 1;
  border: 2px solid #0058bc;
  color: #0058bc;
  padding: 16px 32px;
  font-weight: 700;
  font-size: 16px;
}

.buy-now-btn:hover {
  background: #0058bc;
  color: white;
  border-color: #0058bc;
}

.info-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: #f7f9fc;
  border-radius: 12px;
}

.info-card {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.info-card .el-icon {
  font-size: 20px;
  color: #0058bc;
}

.info-card strong {
  display: block;
  color: #191c1e;
  font-size: 14px;
}

.info-card span {
  color: #74777d;
  font-size: 12px;
}

/* Seller Card */
.seller-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
  margin-bottom: 48px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.seller-avatar {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: #d2e4fb;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0058bc;
  font-size: 24px;
}

.seller-info h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: #191c1e;
}

.seller-info p {
  margin: 0;
  font-size: 14px;
  color: #74777d;
}

@media (max-width: 768px) {
  .product-layout {
    grid-template-columns: 1fr;
    gap: 32px;
  }
}
</style>
