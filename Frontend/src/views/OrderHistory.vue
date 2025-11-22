<template>
  <div class="orders-page">
    <h1 class="page-title">{{ $t('common.orders') }}</h1>

    <!-- Tabs -->
    <div class="order-tabs">
      <button
        v-for="tab in orderTabs"
        :key="tab.id"
        :class="['tab-btn', { active: activeTab === tab.id }]"
        @click="activeTab = tab.id"
      >
        {{ tab.label }}
        <span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
      </button>
    </div>

    <!-- Empty State -->
    <div v-if="filteredOrders.length === 0" class="empty-orders">
      <el-icon :size="64" color="#c4c6cd"><receipt /></el-icon>
      <h2>No orders found</h2>
      <p>{{ activeTab === 'all' ? 'You haven\'t placed any orders yet.' : `No ${activeTab} orders.` }}</p>
      <router-link to="/products">
        <el-button type="primary" size="large">Start Shopping</el-button>
      </router-link>
    </div>

    <!-- Orders List -->
    <div v-else class="orders-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <!-- Order Header -->
        <div class="order-header">
          <div class="order-meta">
            <div>
              <span class="meta-label">Order ID</span>
              <span class="meta-value">{{ order.id }}</span>
            </div>
            <div>
              <span class="meta-label">Placed On</span>
              <span class="meta-value">{{ order.date }}</span>
            </div>
            <div>
              <span class="meta-label">Total</span>
              <span class="meta-value price">${{ order.total.toFixed(2) }}</span>
            </div>
            <div>
              <span class="meta-label">Items</span>
              <span class="meta-value">{{ order.items.length }}</span>
            </div>
          </div>
          <div class="order-status-area">
            <span :class="['status-badge', order.status]">{{ order.status }}</span>
            <button class="expand-btn" @click="toggleOrder(order.id)">
              <el-icon :class="{ rotated: expandedOrders.includes(order.id) }"><arrow-down /></el-icon>
            </button>
          </div>
        </div>

        <!-- Tracking Progress -->
        <div class="tracking-bar">
          <div
            v-for="(step, idx) in trackingSteps"
            :key="idx"
            :class="['track-step', { completed: order.trackingIdx >= idx, current: order.trackingIdx === idx }]"
          >
            <div class="step-dot">
              <el-icon v-if="order.trackingIdx > idx"><check /></el-icon>
            </div>
            <span class="step-label">{{ step }}</span>
          </div>
        </div>

        <!-- Expanded Items -->
        <div v-if="expandedOrders.includes(order.id)" class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-image">
              <img :src="item.image" :alt="item.name" />
            </div>
            <div class="item-info">
              <h4>{{ item.name }}</h4>
              <p>Qty: {{ item.quantity }} | {{ item.category }}</p>
            </div>
            <span class="item-price">${{ item.price.toFixed(2) }}</span>
          </div>
          <div class="order-actions">
            <el-button @click="viewOrderDetail(order)">View Details</el-button>
            <el-button v-if="order.status === 'Pending' || order.status === 'Processing'" type="primary" plain>
              Track Package
            </el-button>
            <el-button v-if="order.status === 'Pending'" type="danger" plain @click="cancelOrder(order)">
              Cancel Order
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('all')
const expandedOrders = ref([])

const orderTabs = [
  { id: 'all', label: 'All Orders', count: null },
  { id: 'Pending', label: 'Pending', count: 2 },
  { id: 'Processing', label: 'Processing', count: 1 },
  { id: 'Shipped', label: 'Shipped', count: 1 },
  { id: 'Delivered', label: 'Delivered', count: 3 },
  { id: 'Cancelled', label: 'Cancelled', count: 0 }
]

const trackingSteps = ['Placed', 'Confirmed', 'Processing', 'Shipped', 'Delivered']

const orders = ref([
  {
    id: '#ORD-2024-8831', date: 'May 12, 2024', total: 299.00, status: 'Shipped', trackingIdx: 3,
    items: [{ id: 1, name: 'SonicFlow Headphones', quantity: 1, price: 299.00, category: 'Electronics', image: 'https://via.placeholder.com/80' }]
  },
  {
    id: '#ORD-2024-8832', date: 'May 12, 2024', total: 569.00, status: 'Processing', trackingIdx: 2,
    items: [
      { id: 1, name: 'Elite Leather Briefcase', quantity: 1, price: 349.00, category: 'Fashion', image: 'https://via.placeholder.com/80' },
      { id: 2, name: 'Ergonomic Mouse Pro', quantity: 2, price: 89.00, category: 'Electronics', image: 'https://via.placeholder.com/80' }
    ]
  },
  {
    id: '#ORD-2024-8815', date: 'May 8, 2024', total: 185.00, status: 'Delivered', trackingIdx: 4,
    items: [{ id: 1, name: 'Aura Satchel Bag', quantity: 1, price: 185.00, category: 'Fashion', image: 'https://via.placeholder.com/80' }]
  },
  {
    id: '#ORD-2024-8792', date: 'April 28, 2024', total: 520.00, status: 'Delivered', trackingIdx: 4,
    items: [{ id: 1, name: 'Stellar Chronograph', quantity: 1, price: 520.00, category: 'Fashion', image: 'https://via.placeholder.com/80' }]
  },
  {
    id: '#ORD-2024-8770', date: 'April 15, 2024', total: 107.00, status: 'Delivered', trackingIdx: 4,
    items: [
      { id: 1, name: 'Lumière Scented Candle', quantity: 2, price: 45.00, category: 'Home & Living', image: 'https://via.placeholder.com/80' }
    ]
  }
])

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(o => o.status === activeTab.value)
})

const toggleOrder = (id) => {
  const idx = expandedOrders.value.indexOf(id)
  if (idx > -1) expandedOrders.value.splice(idx, 1)
  else expandedOrders.value.push(id)
}

const viewOrderDetail = (order) => {
  ElMessage.info(`Viewing details for ${order.id}`)
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to cancel ${order.id}?`,
      'Cancel Order',
      { confirmButtonText: 'Yes, Cancel', cancelButtonText: 'No', type: 'warning' }
    )
    order.status = 'Cancelled'
    order.trackingIdx = 0
    ElMessage.success('Order cancelled')
  } catch {}
}
</script>

<style scoped>
.orders-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px 64px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #041627;
  margin: 0 0 32px 0;
}

/* Tabs */
.order-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 10px 20px;
  border: 1px solid #c4c6cd;
  background: white;
  border-radius: 9999px;
  font-size: 14px;
  font-weight: 500;
  color: #44474c;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-btn:hover {
  border-color: #0058bc;
  color: #0058bc;
}

.tab-btn.active {
  background: #0058bc;
  border-color: #0058bc;
  color: white;
}

.tab-count {
  padding: 1px 8px;
  background: rgba(255,255,255,0.2);
  border-radius: 9999px;
  font-size: 12px;
}

.tab-btn.active .tab-count {
  background: rgba(255,255,255,0.25);
}

/* Empty */
.empty-orders {
  text-align: center;
  padding: 80px;
  background: white;
  border-radius: 16px;
  border: 1px solid #eceef1;
}

.empty-orders h2 {
  color: #041627;
  margin: 16px 0 8px;
}

.empty-orders p {
  color: #74777d;
  margin: 0 0 24px;
}

/* Orders List */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px 28px;
}

.order-meta {
  display: flex;
  gap: 48px;
}

.order-meta > div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 11px;
  color: #74777d;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
}

.meta-value {
  font-size: 15px;
  font-weight: 600;
  color: #191c1e;
}

.meta-value.price {
  color: #0058bc;
}

.order-status-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-badge {
  padding: 4px 14px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.status-badge.Pending { background: #fff3e0; color: #e65100; }
.status-badge.Processing { background: #e3f2fd; color: #0058bc; }
.status-badge.Shipped { background: #e8f5e9; color: #2e7d32; }
.status-badge.Delivered { background: #e8f5e9; color: #1b5e20; }
.status-badge.Cancelled { background: #ffebee; color: #ba1a1a; }

.expand-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid #eceef1;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #74777d;
  transition: all 0.2s;
}

.expand-btn:hover {
  border-color: #0058bc;
  color: #0058bc;
}

.rotated {
  transform: rotate(180deg);
}

/* Tracking Bar */
.tracking-bar {
  display: flex;
  justify-content: space-between;
  padding: 20px 28px;
  border-top: 1px solid #f2f4f7;
  border-bottom: 1px solid #f2f4f7;
  position: relative;
}

.tracking-bar::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 48px;
  right: 48px;
  height: 2px;
  background: #eceef1;
  z-index: 0;
}

.track-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
  z-index: 1;
}

.step-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: white;
  border: 2px solid #c4c6cd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #c4c6cd;
}

.track-step.completed .step-dot {
  background: #0058bc;
  border-color: #0058bc;
  color: white;
}

.track-step.current .step-dot {
  border-color: #0058bc;
  color: #0058bc;
}

.step-label {
  font-size: 11px;
  color: #74777d;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.track-step.completed .step-label,
.track-step.current .step-label {
  color: #0058bc;
  font-weight: 600;
}

/* Order Items (Expanded) */
.order-items {
  padding: 20px 28px;
  border-top: 1px solid #f2f4f7;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
}

.order-item + .order-item {
  border-top: 1px solid #f7f9fc;
}

.item-image {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  background: #f2f4f7;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin: 0 0 2px;
  font-size: 15px;
  color: #191c1e;
}

.item-info p {
  margin: 0;
  font-size: 13px;
  color: #74777d;
}

.item-price {
  font-weight: 700;
  color: #0058bc;
}

.order-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f2f4f7;
}

@media (max-width: 768px) {
  .order-meta {
    gap: 24px;
    flex-wrap: wrap;
  }
  .tracking-bar {
    padding: 16px;
  }
  .step-label {
    display: none;
  }
}
</style>
