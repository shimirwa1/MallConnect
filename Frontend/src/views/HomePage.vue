<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <span class="hero-badge">{{ $t('home.subtitle') }}</span>
        <h1 class="hero-title">{{ $t('home.title') }}</h1>
        <p class="hero-description">
          Discover the latest arrivals from world-renowned brands and enjoy exclusive member-only deals this season.
        </p>
        <div class="hero-buttons">
          <el-button type="primary" size="large" class="hero-button" @click="$router.push('/products')">
            {{ $t('home.exploreDeals') }}
          </el-button>
          <el-button size="large" class="hero-button-outline">
            View Events
          </el-button>
        </div>
      </div>
    </section>

    <!-- Category Grid -->
    <section class="category-section">
      <div class="section-header">
        <div>
          <h2 class="section-title">{{ $t('home.shopByCategory') }}</h2>
          <p class="section-subtitle">Curated collections for every lifestyle</p>
        </div>
        <el-button type="text" class="view-all-btn">
          View All Categories <el-icon><arrow-right /></el-icon>
        </el-button>
      </div>
      
      <div class="category-grid">
        <div class="category-card category-fashion">
          <div class="category-content">
            <h3>Fashion</h3>
            <p>Designer labels & runway essentials</p>
          </div>
        </div>
        
        <div class="category-card category-electronics">
          <div class="category-content">
            <h3>Electronics</h3>
            <p>The latest in tech</p>
          </div>
        </div>
        
        <div class="category-card category-beauty">
          <div class="category-content">
            <h3>Beauty</h3>
            <p>Indulge in self-care</p>
          </div>
        </div>
        
        <div class="category-card category-home">
          <div class="category-content">
            <h3>Home & Living</h3>
            <p>Curated spaces for modern life</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="products-section">
      <div class="section-header">
        <div>
          <h2 class="section-title">{{ $t('home.featuredProducts') }}</h2>
          <p class="section-subtitle">Handpicked just for you</p>
        </div>
        <el-button type="text" class="view-all-btn" @click="$router.push('/products')">
          View All Products <el-icon><arrow-right /></el-icon>
        </el-button>
      </div>
      
      <!-- Loading -->
      <div v-if="loading" class="products-grid">
        <el-card v-for="n in 4" :key="n" class="product-card" :body-style="{ padding: '0' }">
          <div class="product-image">
            <el-skeleton :rows="1" animated style="width: 100%; height: 100%" />
          </div>
          <div class="product-info">
            <el-skeleton :rows="3" animated />
          </div>
        </el-card>
      </div>

      <div v-else class="products-grid">
        <el-card
          v-for="(product, index) in featuredProducts"
          :key="product.id"
          class="product-card"
          :body-style="{ padding: '0' }"
          shadow="hover"
          @click="$router.push(`/products/${product.id}`)"
        >
          <div class="product-image">
            <img :src="product.imageUrl || 'https://via.placeholder.com/300'" :alt="product.name" />
            <span v-if="index < 2" :class="['product-badge', index === 0 ? 'hot' : 'new']">
              {{ index === 0 ? 'HOT' : 'NEW' }}
            </span>
          </div>
          <div class="product-info">
            <p class="product-category">{{ product.categoryName || 'Uncategorized' }}</p>
            <h4 class="product-name">{{ product.name }}</h4>
            <div class="product-price">
              <span class="price">${{ (product.price || 0).toFixed(2) }}</span>
              <el-button
                type="primary"
                circle
                size="small"
                class="add-to-cart-btn"
                @click.stop="addToCart(product)"
              >
                <el-icon><shopping-bag /></el-icon>
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <!-- Newsletter Section -->
    <section class="newsletter-section">
      <div class="newsletter-content">
        <el-icon class="newsletter-icon"><message /></el-icon>
        <h2 class="newsletter-title">{{ $t('home.joinInnerCircle') }}</h2>
        <p class="newsletter-description">
          Get expert shopping guides, exclusive invites to mall events, and early notification of seasonal sales.
        </p>
        <div class="newsletter-form">
          <el-input
            v-model="email"
            placeholder="Your professional email"
            size="large"
            class="newsletter-input"
          />
          <el-button type="primary" size="large" class="newsletter-button">
            {{ $t('home.subscribe') }}
          </el-button>
        </div>
        <p class="newsletter-privacy">We value your privacy. Unsubscribe at any time.</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { productsAPI } from '@/services/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const featuredProducts = ref([])
const loading = ref(false)

const fetchFeaturedProducts = async () => {
  loading.value = true
  try {
    const response = await productsAPI.getProducts({ page: 0, size: 4, sort: 'createdAt,desc' })
    featuredProducts.value = response.content || response || []
  } catch (err) {
    console.error('Error fetching featured products:', err)
    featuredProducts.value = []
  } finally {
    loading.value = false
  }
}

const addToCart = (product) => {
  authStore.addToCart({
    id: product.id,
    name: product.name,
    price: product.price,
    image: product.imageUrl || 'https://via.placeholder.com/300',
    stock: product.stock,
    category: product.categoryName
  })
  ElMessage.success('Added to cart!')
}

onMounted(() => {
  fetchFeaturedProducts()
})
</script>

<style scoped>
.home-page {
  max-width: 1280px;
  margin: 0 auto;
}

/* Hero Section */
.hero-section {
  height: 600px;
  background: linear-gradient(135deg, rgba(4, 22, 39, 0.9) 0%, rgba(0, 88, 188, 0.7) 100%);
  display: flex;
  align-items: center;
  padding: 0 32px;
  margin-bottom: 80px;
}

.hero-content {
  max-width: 800px;
}

.hero-badge {
  display: inline-block;
  padding: 8px 16px;
  background: #0058bc;
  color: white;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: white;
  line-height: 1.2;
  letter-spacing: -0.02em;
  margin-bottom: 24px;
}

.hero-description {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  margin-bottom: 32px;
}

.hero-buttons {
  display: flex;
  gap: 16px;
}

.hero-button {
  background-color: #0058bc;
  border-color: #0058bc;
  padding: 16px 32px;
  font-weight: 700;
}

.hero-button-outline {
  background: transparent;
  border: 1px solid white;
  color: white;
  padding: 16px 32px;
  font-weight: 700;
}

/* Category Section */
.category-section {
  padding: 0 32px;
  margin-bottom: 96px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 48px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 8px 0;
}

.section-subtitle {
  font-size: 16px;
  color: #44474c;
  margin: 0;
}

.view-all-btn {
  color: #0058bc;
  font-weight: 700;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  height: 500px;
}

.category-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  background-size: cover;
  background-position: center;
  transition: transform 0.7s;
}

.category-card:hover {
  transform: scale(1.05);
}

.category-fashion {
  grid-column: span 2;
  background-image: url('https://via.placeholder.com/800x500');
}

.category-electronics {
  background-image: url('https://via.placeholder.com/400x250');
}

.category-beauty {
  background-image: url('https://via.placeholder.com/400x250');
}

.category-home {
  grid-column: span 2;
  background-image: url('https://via.placeholder.com/800x250');
}

.category-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 32px;
  background: linear-gradient(to top, rgba(4, 22, 39, 0.8), transparent);
}

.category-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px 0;
}

.category-content p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

/* Products Section */
.products-section {
  padding: 0 32px;
  margin-bottom: 96px;
}

.products-nav {
  display: flex;
  gap: 8px;
}

.nav-btn {
  border: 1px solid #c4c6cd;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.product-card {
  border: 1px solid #c4c6cd;
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.product-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.product-image {
  position: relative;
  aspect-ratio: 1;
  background: #eceef1;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.product-badge.hot {
  background: #ba1a1a;
  color: white;
}

.product-badge.new {
  background: #0058bc;
  color: white;
}

.product-info {
  padding: 24px;
}

.product-category {
  font-size: 12px;
  color: #44474c;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 4px 0;
}

.product-name {
  font-size: 20px;
  font-weight: 600;
  color: #191c1e;
  margin: 0 0 16px 0;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: 600;
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

/* Newsletter Section */
.newsletter-section {
  padding: 96px 32px;
}

.newsletter-content {
  max-width: 1280px;
  margin: 0 auto;
  background: #1a2b3c;
  border-radius: 24px;
  padding: 64px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.newsletter-icon {
  font-size: 48px;
  color: #0058bc;
  margin-bottom: 24px;
}

.newsletter-title {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0 0 16px 0;
}

.newsletter-description {
  font-size: 18px;
  color: #8192a7;
  line-height: 1.6;
  max-width: 600px;
  margin: 0 0 40px 0;
}

.newsletter-form {
  display: flex;
  gap: 16px;
  width: 100%;
  max-width: 500px;
  margin-bottom: 24px;
}

.newsletter-input {
  flex: 1;
}

.newsletter-input .el-input__inner {
  background: white;
  border: none;
}

.newsletter-button {
  background: #0058bc;
  border-color: #0058bc;
  padding: 16px 32px;
  font-weight: 700;
}

.newsletter-privacy {
  font-size: 14px;
  color: rgba(129, 146, 167, 0.6);
  margin: 0;
}
</style>
