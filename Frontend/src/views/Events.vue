<template>
  <div class="events-page">
    <div class="page-header">
      <h1 class="page-title">Mall Events</h1>
      <p class="page-subtitle">Discover exclusive sales, seasonal promotions, and special happenings</p>
    </div>

    <!-- Current Events -->
    <section v-if="!loading" class="events-section">
      <h2 class="section-title">Happening Now</h2>
      <div class="events-grid">
        <div v-for="event in currentEvents" :key="event.id" class="event-card" @click="$router.push('/products')">
          <div class="event-banner" :class="event.style">
            <el-icon :size="32"><component :is="event.icon" /></el-icon>
          </div>
          <div class="event-info">
            <span class="event-badge">{{ event.badge }}</span>
            <h3>{{ event.title }}</h3>
            <p>{{ event.description }}</p>
            <span class="event-cta">Explore Deals <el-icon><arrow-right /></el-icon></span>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products for Events -->
    <section v-if="!loading" class="products-section">
      <div class="section-header">
        <h2 class="section-title">Event Highlights</h2>
        <el-button text type="primary" @click="$router.push('/products')">
          View All <el-icon><arrow-right /></el-icon>
        </el-button>
      </div>
      <div class="products-grid">
        <el-card
          v-for="product in eventProducts"
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
            <p class="product-category">{{ product.categoryName || 'General' }}</p>
            <h4 class="product-name">{{ product.name }}</h4>
            <span class="price">${{ (product.price || 0).toFixed(2) }}</span>
          </div>
        </el-card>
      </div>
    </section>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <el-skeleton v-for="n in 6" :key="n" animated :rows="4" style="margin-bottom: 24px" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productsAPI } from '@/services/api'
import { Discount, Present, Timer, TrophyBase } from '@element-plus/icons-vue'

const loading = ref(true)
const eventProducts = ref([])

const currentEvents = [
  {
    id: 1,
    title: 'Summer Clearance',
    description: 'Up to 50% off on seasonal favorites. Limited time only.',
    icon: Discount,
    badge: 'SALE',
    style: 'sale'
  },
  {
    id: 2,
    title: 'New Arrivals Showcase',
    description: 'Be the first to shop the latest collections from top brands.',
    icon: TrophyBase,
    badge: 'NEW',
    style: 'new'
  },
  {
    id: 3,
    title: 'Flash Deals',
    description: 'Lightning deals updated daily. Grab them before they\'re gone.',
    icon: Timer,
    badge: 'HOT',
    style: 'hot'
  },
  {
    id: 4,
    title: 'Member Appreciation Week',
    description: 'Exclusive rewards and early access for our loyal members.',
    icon: Present,
    badge: 'EXCLUSIVE',
    style: 'exclusive'
  }
]

onMounted(async () => {
  try {
    const response = await productsAPI.getProducts({ page: 0, size: 6, sort: 'createdAt,desc' })
    eventProducts.value = response.content || response || []
  } catch (err) {
    console.error('Error fetching event products:', err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.events-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px;
}

.page-header {
  margin-bottom: 48px;
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

.section-title {
  font-size: 22px;
  font-weight: 700;
  color: #191c1e;
  margin: 0 0 24px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.events-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 64px;
}

.event-card {
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #eceef1;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.event-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.event-banner {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.event-banner.sale { background: linear-gradient(135deg, #ff6b6b, #ee5a24); }
.event-banner.new { background: linear-gradient(135deg, #0058bc, #0077e6); }
.event-banner.hot { background: linear-gradient(135deg, #f0932b, #f0622e); }
.event-banner.exclusive { background: linear-gradient(135deg, #6c5ce7, #a29bfe); }

.event-info {
  padding: 20px;
}

.event-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.05em;
  margin-bottom: 8px;
  background: #f0f2f5;
  color: #74777d;
}

.event-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #191c1e;
  margin: 0 0 8px 0;
}

.event-info p {
  font-size: 13px;
  color: #74777d;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.event-cta {
  font-size: 13px;
  font-weight: 600;
  color: #0058bc;
  display: flex;
  align-items: center;
  gap: 4px;
}

.products-section {
  margin-bottom: 64px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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
  font-size: 16px;
  font-weight: 600;
  color: #191c1e;
  margin: 0 0 8px 0;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #0058bc;
}

.loading-state {
  padding-top: 24px;
}

@media (max-width: 1024px) {
  .events-grid { grid-template-columns: repeat(2, 1fr); }
  .products-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 640px) {
  .events-grid { grid-template-columns: 1fr; }
  .products-grid { grid-template-columns: 1fr; }
}
</style>
