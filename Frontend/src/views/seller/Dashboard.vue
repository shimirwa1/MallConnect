<template>
  <div class="seller-dashboard">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-brand">
        <h1>MallConnect</h1>
        <p class="sidebar-badge">SELLER CENTER</p>
      </div>

      <nav class="sidebar-nav">
        <router-link to="/seller/dashboard" class="nav-item active">
          <el-icon><dashboard /></el-icon>
          <span>Dashboard</span>
        </router-link>
        <router-link to="/seller/products" class="nav-item">
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
          <div class="user-avatar">
            <img src="https://via.placeholder.com/40" alt="Seller avatar" />
          </div>
          <div class="user-details">
            <strong>Alex Chen</strong>
            <span>Premium Seller</span>
          </div>
        </div>
        <el-icon><more-filled /></el-icon>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-area">
      <!-- Top Bar -->
      <header class="top-bar">
        <h2>Dashboard Overview</h2>
        <div class="top-actions">
          <el-input
            v-model="searchQuery"
            placeholder="Search orders, products..."
            prefix-icon="Search"
            class="search-input"
          />
          <el-badge is-dot>
            <el-button circle :icon="Bell" />
          </el-badge>
          <el-button circle :icon="QuestionFilled" />
        </div>
      </header>

      <!-- Dashboard Content -->
      <div class="dashboard-content">
        <!-- Stats Cards -->
        <section class="stats-grid">
          <div class="stat-card">
            <div class="stat-header">
              <div class="stat-icon revenue">
                <el-icon><money /></el-icon>
              </div>
              <span class="stat-trend up">+12.5%</span>
            </div>
            <div class="stat-body">
              <p class="stat-label">{{ $t('seller.totalRevenue') }}</p>
              <h3 class="stat-value">$128,430.00</h3>
            </div>
            <div class="stat-bar"><div class="bar-fill" style="width: 75%" /></div>
          </div>

          <div class="stat-card">
            <div class="stat-header">
              <div class="stat-icon orders">
                <el-icon><shopping-bag /></el-icon>
              </div>
              <span class="stat-trend up">+4.2%</span>
            </div>
            <div class="stat-body">
              <p class="stat-label">Total Orders</p>
              <h3 class="stat-value">1,240</h3>
            </div>
            <div class="stat-bar"><div class="bar-fill alt" style="width: 50%" /></div>
          </div>

          <div class="stat-card">
            <div class="stat-header">
              <div class="stat-icon views">
                <el-icon><view /></el-icon>
              </div>
              <span class="stat-trend down">-1.8%</span>
            </div>
            <div class="stat-body">
              <p class="stat-label">{{ $t('seller.productViews') }}</p>
              <h3 class="stat-value">48.2k</h3>
            </div>
            <div class="stat-bar"><div class="bar-fill dim" style="width: 80%" /></div>
          </div>
        </section>

        <!-- Charts + Health Row -->
        <section class="charts-row">
          <!-- Sales Trend Chart -->
          <div class="chart-card">
            <div class="chart-header">
              <div>
                <h4>Sales Trend</h4>
                <p>Weekly performance analysis</p>
              </div>
              <el-select v-model="chartPeriod" size="small" style="width: 140px">
                <el-option label="Last 7 Days" value="7d" />
                <el-option label="Last 30 Days" value="30d" />
              </el-select>
            </div>
            <div class="chart-area">
              <div class="chart-bars">
                <div v-for="(bar, idx) in salesData" :key="idx" class="chart-bar-wrap">
                  <div class="chart-bar" :style="{ height: bar + '%' }">
                    <div class="bar-tooltip">${{ (bar * 80).toFixed(0) }}</div>
                  </div>
                  <span class="bar-label">{{ ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'][idx] }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Store Health -->
          <div class="health-card">
            <h4>Store Health</h4>
            <div class="health-metric">
              <span class="health-label">Customer Rating</span>
              <span class="health-value">4.8 / 5.0</span>
            </div>
            <div class="stars">
              <el-icon v-for="i in 5" :key="i" :color="i <= 4 ? '#0058bc' : '#c4c6cd'" :size="20">
                <star-filled v-if="i <= 4" /><star v-else />
              </el-icon>
            </div>
            <el-divider />
            <h5 class="section-label">TOP PERFORMING CATEGORIES</h5>
            <div class="category-list">
              <div class="category-item">
                <span class="cat-dot" style="background: #0058bc" />
                <span class="cat-name">Home Electronics</span>
                <span class="cat-pct">42%</span>
              </div>
              <div class="category-item">
                <span class="cat-dot" style="background: #e1c29b" />
                <span class="cat-name">Fashion & Apparel</span>
                <span class="cat-pct">31%</span>
              </div>
              <div class="category-item">
                <span class="cat-dot" style="background: #8192a7" />
                <span class="cat-name">Kitchenware</span>
                <span class="cat-pct">18%</span>
              </div>
            </div>
            <el-button class="download-btn" size="large" style="width: 100%; margin-top: 20px">
              Download Report
            </el-button>
          </div>
        </section>

        <!-- Recent Orders Table -->
        <section class="table-card">
          <div class="table-header">
            <h4>Recent Orders</h4>
            <el-button text type="primary">View All Orders</el-button>
          </div>
          <el-table :data="recentOrders" stripe style="width: 100%">
            <el-table-column prop="id" label="ORDER ID" width="180" />
            <el-table-column label="CUSTOMER" width="200">
              <template #default="{ row }">
                <div class="customer-cell">
                  <div class="customer-avatar">{{ row.customerInitials }}</div>
                  <span>{{ row.customer }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="product" label="PRODUCT" />
            <el-table-column prop="date" label="DATE" width="140" />
            <el-table-column prop="amount" label="AMOUNT" width="120" sortable>
              <template #default="{ row }">
                <strong>${{ row.amount }}</strong>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="STATUS" width="130">
              <template #default="{ row }">
                <span :class="['status-tag', row.statusClass]">{{ row.status }}</span>
              </template>
            </el-table-column>
            <el-table-column label="ACTION" width="80">
              <template #default>
                <el-button text type="primary"><el-icon><more-filled /></el-icon></el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="table-footer">
            <span>Showing 1-10 of 254 orders</span>
            <el-pagination small layout="prev, pager, next" :total="254" />
          </div>
        </section>
      </div>

      <!-- Footer -->
      <footer class="dashboard-footer">
        <div>
          <strong>MallConnect</strong>
          <p>© 2024 MallConnect Global. All rights reserved.</p>
        </div>
        <div class="footer-links">
          <a href="#">Privacy Policy</a>
          <a href="#">Store Directory</a>
          <a href="#">Contact Us</a>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const searchQuery = ref('')
const chartPeriod = ref('7d')
const salesData = ref([45, 60, 55, 80, 70, 90, 65])

const recentOrders = ref([
  { id: '#ORD-2024-8831', customer: 'Jane Doe', customerInitials: 'JD', product: 'Wireless Headphones X1', date: 'May 12, 2024', amount: '299.00', status: 'Shipped', statusClass: 'success' },
  { id: '#ORD-2024-8832', customer: 'Mike Kaiser', customerInitials: 'MK', product: 'Mechanical Keyboard RGB', date: 'May 12, 2024', amount: '159.50', status: 'Pending', statusClass: 'warning' },
  { id: '#ORD-2024-8833', customer: 'Sarah Lee', customerInitials: 'SL', product: 'Ergonomic Mouse Pro', date: 'May 11, 2024', amount: '89.00', status: 'Processing', statusClass: 'info' }
])
</script>

<style scoped>
.seller-dashboard {
  display: flex;
  min-height: 100vh;
  background: #f7f9fc;
}

/* Sidebar */
.sidebar {
  width: 260px;
  background: white;
  border-right: 1px solid #eceef1;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 50;
}

.sidebar-brand {
  padding: 32px 24px;
}

.sidebar-brand h1 {
  font-size: 22px;
  font-weight: 700;
  color: #0058bc;
  margin: 0 0 4px 0;
}

.sidebar-badge {
  font-size: 10px;
  color: #74777d;
  letter-spacing: 0.08em;
  font-weight: 600;
}

.sidebar-nav {
  flex: 1;
  padding: 0 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  color: #44474c;
  text-decoration: none;
  font-size: 15px;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #f2f4f7;
}

.nav-item.active {
  background: #0058bc;
  color: white;
  font-weight: 600;
}

.sidebar-user {
  padding: 16px;
  border-top: 1px solid #eceef1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: #d2e4fb;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details strong {
  display: block;
  font-size: 14px;
  color: #191c1e;
}

.user-details span {
  font-size: 11px;
  color: #74777d;
}

/* Main Area */
.main-area {
  margin-left: 260px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.top-bar {
  height: 72px;
  background: white;
  border-bottom: 1px solid #eceef1;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 40;
}

.top-bar h2 {
  font-size: 20px;
  font-weight: 600;
  color: #041627;
  margin: 0;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 260px;
}

/* Dashboard Content */
.dashboard-content {
  padding: 32px;
  flex: 1;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  padding: 24px;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(8px);
  border: 1px solid #eceef1;
  border-radius: 16px;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-icon.revenue { background: rgba(0,88,188,0.1); color: #0058bc; }
.stat-icon.orders { background: rgba(254,221,181,0.5); color: #584326; }
.stat-icon.views { background: rgba(178,200,222,0.5); color: #004493; }

.stat-trend {
  font-size: 12px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 9999px;
}

.stat-trend.up { background: #e8f5e9; color: #2e7d32; }
.stat-trend.down { background: #ffdad6; color: #ba1a1a; }

.stat-label {
  font-size: 13px;
  color: #74777d;
  margin: 0 0 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #041627;
  margin: 0;
}

.stat-bar {
  margin-top: 12px;
  height: 4px;
  background: #eceef1;
  border-radius: 2px;
  overflow: hidden;
}

.bar-fill { height: 100%; background: #0058bc; border-radius: 2px; }
.bar-fill.alt { background: #e1c29b; }
.bar-fill.dim { background: #8192a7; }

/* Charts Row */
.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

.chart-card, .health-card {
  padding: 24px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.chart-header h4 {
  font-size: 18px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 4px;
}

.chart-header p {
  font-size: 13px;
  color: #74777d;
  margin: 0;
}

.chart-area {
  height: 240px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 100%;
  gap: 12px;
  padding: 0 8px 24px;
  border-bottom: 1px solid #eceef1;
}

.chart-bar-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.chart-bar {
  width: 100%;
  max-width: 48px;
  background: #0058bc;
  border-radius: 6px 6px 0 0;
  transition: height 0.6s ease;
  position: relative;
}

.chart-bar:hover {
  background: #004493;
}

.chart-bar:hover .bar-tooltip {
  opacity: 1;
}

.bar-tooltip {
  position: absolute;
  top: -28px;
  left: 50%;
  transform: translateX(-50%);
  background: #041627;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  white-space: nowrap;
  opacity: 0;
  transition: opacity 0.2s;
}

.bar-label {
  margin-top: 8px;
  font-size: 11px;
  color: #74777d;
  font-weight: 500;
}

/* Health Card */
.health-card h4 {
  font-size: 18px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 16px;
}

.health-metric {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.health-label { color: #44474c; font-size: 14px; }
.health-value { color: #0058bc; font-weight: 700; }

.stars {
  display: flex;
  gap: 2px;
  margin: 8px 0 16px;
}

.section-label {
  font-size: 11px;
  color: #74777d;
  letter-spacing: 0.05em;
  margin: 0 0 12px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.cat-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.cat-name { flex: 1; color: #44474c; }
.cat-pct { font-weight: 700; color: #191c1e; }

/* Table Card */
.table-card {
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eceef1;
}

.table-header h4 {
  font-size: 18px;
  font-weight: 600;
  color: #041627;
  margin: 0;
}

.customer-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.customer-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #d2e4fb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: #0058bc;
}

.status-tag {
  padding: 3px 10px;
  border-radius: 9999px;
  font-size: 11px;
  font-weight: 600;
}

.status-tag.success { background: #e8f5e9; color: #2e7d32; }
.status-tag.warning { background: #fff3e0; color: #e65100; }
.status-tag.info { background: #e3f2fd; color: #0058bc; }

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid #eceef1;
  font-size: 13px;
  color: #74777d;
}

/* Footer */
.dashboard-footer {
  padding: 24px 32px;
  border-top: 1px solid #eceef1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #e0e3e6;
}

.dashboard-footer strong {
  font-size: 16px;
  color: #041627;
}

.dashboard-footer p {
  font-size: 13px;
  color: #44474c;
  margin: 4px 0 0;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  font-size: 13px;
  color: #44474c;
  text-decoration: none;
}

.footer-links a:hover {
  text-decoration: underline;
  color: #0058bc;
}
</style>
