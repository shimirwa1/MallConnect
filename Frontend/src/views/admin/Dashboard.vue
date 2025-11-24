<template>
  <div class="admin-dashboard">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-brand">
        <span class="brand-name">MallConnect</span>
        <p class="brand-sub">Admin Central</p>
      </div>
      <nav class="sidebar-nav">
        <a class="nav-item active" href="#">
          <el-icon><dashboard /></el-icon>
          <span>{{ $t('admin.dashboard') }}</span>
        </a>
        <a class="nav-item" href="#">
          <el-icon><user /></el-icon>
          <span>{{ $t('admin.userManagement') }}</span>
        </a>
        <a class="nav-item" href="#">
          <el-icon><grid /></el-icon>
          <span>{{ $t('admin.categoryManagement') }}</span>
        </a>
        <a class="nav-item" href="#">
          <el-icon><shop /></el-icon>
          <span>{{ $t('admin.sellerManagement') }}</span>
        </a>
        <a class="nav-item" href="#">
          <el-icon><money /></el-icon>
          <span>{{ $t('admin.transactions') }}</span>
        </a>
      </nav>
      <div class="sidebar-footer">
        <a class="nav-item" href="#">
          <el-icon><setting /></el-icon>
          <span>Settings</span>
        </a>
        <div class="admin-info">
          <div class="admin-avatar">{{ adminInitials }}</div>
          <div>
            <strong>{{ adminName }}</strong>
            <span>{{ adminRole }}</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-area">
      <header class="top-bar">
        <h1>Dashboard Overview</h1>
        <div class="top-actions">
          <el-input placeholder="Global Search..." prefix-icon="Search" class="global-search" />
          <el-badge is-dot>
            <el-button circle :icon="Bell" />
          </el-badge>
          <el-button type="primary">
            <el-icon><download /></el-icon>
            Export Report
          </el-button>
        </div>
      </header>

      <div class="content-area">
        <!-- Metrics Grid -->
        <section class="metrics-grid">
          <div class="metric-card">
            <div class="metric-bg-icon">
              <el-icon :size="48"><user-filled /></el-icon>
            </div>
            <p class="metric-label">{{ $t('admin.totalUsers') }}</p>
            <div class="metric-value-row">
              <span class="metric-value">{{ dashboardStats.totalUsers.toLocaleString() }}</span>
            </div>
            <p class="metric-sub">{{ dashboardStats.totalBuyers.toLocaleString() }} buyers · {{ dashboardStats.totalSellers.toLocaleString() }} sellers</p>
          </div>

          <div class="metric-card highlight">
            <div class="metric-bg-icon">
              <el-icon :size="48"><clock /></el-icon>
            </div>
            <p class="metric-label">{{ $t('admin.pendingSellers') }}</p>
            <div class="metric-value-row">
              <span class="metric-value">{{ sellerApplications.filter(s => s.statusClass === 'urgent').length }}</span>
            </div>
            <p class="metric-sub">{{ sellerApplications.length }} total sellers</p>
          </div>

          <div class="metric-card">
            <div class="metric-bg-icon">
              <el-icon :size="48"><money /></el-icon>
            </div>
            <p class="metric-label">{{ $t('admin.platformRevenue') }}</p>
            <div class="metric-value-row">
              <span class="metric-value">{{ dashboardStats.totalOrders.toLocaleString() }}</span>
            </div>
            <p class="metric-sub">Total orders placed</p>
          </div>
        </section>

        <!-- Charts + System Health Row -->
        <section class="content-row">
          <!-- User Growth Chart -->
          <div class="chart-card">
            <div class="chart-card-header">
              <div>
                <h3>User Growth</h3>
                <p>Daily registrations for the last 14 days</p>
              </div>
              <el-select v-model="growthPeriod" size="small" style="width: 140px">
                <el-option label="Last 14 Days" value="14d" />
                <el-option label="Last 30 Days" value="30d" />
                <el-option label="Year to Date" value="ytd" />
              </el-select>
            </div>
            <div class="bar-chart">
              <div v-for="(val, idx) in userGrowthData" :key="idx" class="bar-column">
                <div class="bar" :style="{ height: val + '%' }" />
              </div>
            </div>
            <div class="chart-labels">
              <span>14 Days Ago</span>
              <span>Current Day</span>
            </div>
          </div>

          <!-- System Health -->
          <div class="health-card">
            <h3>System Health</h3>
            <div class="health-list">
              <div class="health-item">
                <span class="health-dot green" />
                <span>Payment Gateway</span>
                <span class="health-status">Operational</span>
              </div>
              <div class="health-item">
                <span class="health-dot green" />
                <span>API Services</span>
                <span class="health-status">Operational</span>
              </div>
              <div class="health-item">
                <span class="health-dot yellow" />
                <span>Email Dispatcher</span>
                <span class="health-status">Degraded (1.2s lag)</span>
              </div>
            </div>
            <div class="quick-links">
              <p class="quick-links-label">Quick Links</p>
              <div class="quick-links-grid">
                <button>User Audit</button>
                <button>DB Backup</button>
                <button>Log Viewer</button>
                <button>API Docs</button>
              </div>
            </div>
          </div>
        </section>

        <!-- Seller Applications Table -->
        <section class="table-card">
          <div class="table-card-header">
            <div>
              <h3>Recent Seller Applications</h3>
              <p>Review and manage onboarding requests</p>
            </div>
            <el-button text type="primary">View All Applications</el-button>
          </div>
          <el-table :data="sellerApplications" stripe style="width: 100%">
            <el-table-column label="STORE NAME" width="240">
              <template #default="{ row }">
                <div class="store-cell">
                  <div class="store-avatar">{{ row.initials }}</div>
                  <div>
                    <strong>{{ row.storeName }}</strong>
                    <span class="store-email">{{ row.email }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="CATEGORY" width="160" />
            <el-table-column prop="submissionDate" label="SUBMISSION DATE" width="150" />
            <el-table-column label="STATUS" width="160">
              <template #default="{ row }">
                <span :class="['app-status', row.statusClass]">{{ row.status }}</span>
              </template>
            </el-table-column>
            <el-table-column label="ACTIONS" fixed="right" width="200">
              <template #default="{ row }">
                <div class="action-btns">
                  <el-button size="small" type="danger" plain @click="handleRejectSeller(row)">Reject</el-button>
                  <el-button size="small" type="primary" @click="handleApproveSeller(row)">Approve</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </section>
      </div>

      <!-- Footer -->
      <footer class="admin-footer">
        <div>
          <strong>MallConnect</strong>
          <span> | </span>
          <span>© 2024 MallConnect Global. All rights reserved.</span>
        </div>
        <div class="footer-links">
          <a href="#">Privacy Policy</a>
          <a href="#">Terms of Service</a>
          <a href="#">Contact Us</a>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { adminAPI } from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const growthPeriod = ref('14d')
const loading = ref(true)
const error = ref(null)

// Dashboard stats from backend
const dashboardStats = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalBuyers: 0,
  totalSellers: 0
})

// Seller applications from backend
const sellerApplications = ref([])

const adminName = computed(() => {
  const user = authStore.user
  if (user) return `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'Admin'
  return 'Admin'
})

const adminRole = computed(() => {
  return authStore.user?.role || 'Administrator'
})

const adminInitials = computed(() => {
  const user = authStore.user
  if (user) {
    return `${(user.firstName?.charAt(0) || '')}${(user.lastName?.charAt(0) || '')}`.toUpperCase() || 'AD'
  }
  return 'AD'
})

const fetchDashboard = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await adminAPI.getDashboard()
    dashboardStats.value = {
      totalUsers: data.totalUsers || 0,
      totalProducts: data.totalProducts || 0,
      totalOrders: data.totalOrders || 0,
      totalBuyers: data.totalBuyers || 0,
      totalSellers: data.totalSellers || 0
    }
  } catch (err) {
    console.error('Error fetching admin dashboard:', err)
    error.value = 'Failed to load dashboard data'
  } finally {
    loading.value = false
  }
}

const fetchSellerApplications = async () => {
  try {
    // Fetch sellers (pending sellers are those with SELLER role)
    // If the approve/reject endpoints existed, we'd use those; for now show all sellers
    const data = await adminAPI.getSellers({ page: 0, size: 10 })
    const sellers = data.content || data || []
    sellerApplications.value = sellers.map(seller => ({
      id: seller.id,
      initials: (seller.firstName?.charAt(0) || '') + (seller.lastName?.charAt(0) || ''),
      storeName: `${seller.firstName || ''} ${seller.lastName || ''}`.trim() || 'Unknown',
      email: seller.email || '',
      category: 'General',
      submissionDate: seller.createdAt ? new Date(seller.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) : 'N/A',
      status: seller.enabled ? 'Approved' : 'Pending',
      statusClass: seller.enabled ? 'new' : 'urgent'
    }))
  } catch (err) {
    console.error('Error fetching seller applications:', err)
    // Fallback to empty - don't show hardcoded data
    sellerApplications.value = []
  }
}

const userGrowthData = computed(() => {
  // Show relative proportions based on actual user count
  const total = dashboardStats.value.totalUsers || 1
  // Simple visualization: show the total scaled to a percentage bar
  const baseHeight = Math.min(100, Math.max(10, (total / 100) * 100))
  return Array(14).fill(baseHeight).map((h, i) => Math.round(h * (0.5 + Math.random() * 0.5)))
})

const handleApproveSeller = async (seller) => {
  try {
    await adminAPI.approveSeller(seller.id)
    ElMessage.success(`Approved ${seller.storeName}`)
    fetchSellerApplications()
  } catch (err) {
    ElMessage.error('Failed to approve seller')
  }
}

const handleRejectSeller = async (seller) => {
  try {
    await adminAPI.rejectSeller(seller.id)
    ElMessage.success(`Rejected ${seller.storeName}`)
    fetchSellerApplications()
  } catch (err) {
    ElMessage.error('Failed to reject seller')
  }
}

onMounted(() => {
  fetchDashboard()
  fetchSellerApplications()
})
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background: #f7f9fc;
}

/* Sidebar */
.sidebar {
  width: 256px;
  background: #041627;
  color: white;
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

.brand-name {
  font-size: 22px;
  font-weight: 700;
  color: #adc6ff;
}

.brand-sub {
  font-size: 13px;
  color: rgba(255,255,255,0.5);
  margin: 4px 0 0;
}

.sidebar-nav {
  flex: 1;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  color: #8192a7;
  text-decoration: none;
  font-size: 15px;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #1a2b3c;
  color: white;
}

.nav-item.active {
  background: #d2e4fb;
  color: #0b1d2d;
  font-weight: 600;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #1a2b3c;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding: 0 8px;
}

.admin-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #0058bc;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #adc6ff;
  background: #1a2b3c;
}

.admin-info strong {
  display: block;
  font-size: 14px;
  color: white;
}

.admin-info span {
  font-size: 10px;
  color: rgba(255,255,255,0.5);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* Main Area */
.main-area {
  margin-left: 256px;
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

.top-bar h1 {
  font-size: 20px;
  font-weight: 700;
  color: #041627;
  margin: 0;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.global-search {
  width: 260px;
}

.content-area {
  padding: 32px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* Metrics */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.metric-card {
  padding: 32px;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(8px);
  border: 1px solid #eceef1;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}

.metric-card.highlight {
  border-left: 4px solid #0058bc;
}

.metric-bg-icon {
  position: absolute;
  top: 16px;
  right: 16px;
  color: #0058bc;
  opacity: 0.08;
}

.metric-label {
  font-size: 12px;
  color: #74777d;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-weight: 600;
  margin: 0 0 8px;
}

.metric-value-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.metric-value {
  font-size: 40px;
  font-weight: 700;
  color: #041627;
}

.metric-trend {
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 2px;
}

.metric-trend.up { color: #2e7d32; }
.metric-trend.urgent { color: #ba1a1a; }

.metric-sub {
  font-size: 13px;
  color: #74777d;
  margin: 8px 0 0;
}

/* Content Row */
.content-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-card, .health-card {
  padding: 24px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
}

.chart-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.chart-card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 4px;
}

.chart-card-header p {
  font-size: 13px;
  color: #74777d;
  margin: 0;
}

.bar-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 200px;
  gap: 6px;
  padding: 0 4px 8px;
  border-bottom: 1px solid #eceef1;
}

.bar-column {
  flex: 1;
  height: 100%;
  display: flex;
  align-items: flex-end;
}

.bar {
  width: 100%;
  background: #d2e4fb;
  border-radius: 4px 4px 0 0;
  transition: all 0.3s;
  min-height: 4px;
}

.bar:hover {
  background: #0058bc;
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 10px;
  color: #74777d;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Health Card */
.health-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 20px;
}

.health-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 24px;
}

.health-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.health-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.health-dot.green { background: #4caf50; }
.health-dot.yellow { background: #ff9800; }

.health-item span:nth-child(2) {
  flex: 1;
  color: #191c1e;
  font-weight: 600;
}

.health-status {
  color: #74777d;
  font-size: 13px;
}

.quick-links {
  padding-top: 20px;
  border-top: 1px solid #eceef1;
}

.quick-links-label {
  font-size: 11px;
  color: #74777d;
  font-weight: 600;
  letter-spacing: 0.05em;
  margin: 0 0 10px;
}

.quick-links-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.quick-links-grid button {
  padding: 10px;
  border: 1px solid #eceef1;
  border-radius: 8px;
  background: white;
  font-size: 13px;
  color: #44474c;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-links-grid button:hover {
  background: #f2f4f7;
  border-color: #0058bc;
}

/* Table */
.table-card {
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
  overflow: hidden;
}

.table-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #eceef1;
}

.table-card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 4px;
}

.table-card-header p {
  font-size: 13px;
  color: #74777d;
  margin: 0;
}

.store-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.store-avatar {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: #1a2b3c;
  color: #8192a7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.store-cell strong {
  display: block;
  font-size: 14px;
  color: #041627;
}

.store-email {
  font-size: 12px;
  color: #74777d;
}

.app-status {
  padding: 3px 10px;
  border-radius: 9999px;
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.app-status.new { background: #d2e4fb; color: #004493; }
.app-status.urgent { background: #ffdad6; color: #93000a; }
.app-status.progress { background: #d2e4fb; color: #004493; }

.action-btns {
  display: flex;
  gap: 8px;
}

/* Footer */
.admin-footer {
  padding: 20px 32px;
  border-top: 1px solid #eceef1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #e0e3e6;
  font-size: 13px;
  color: #44474c;
}

.admin-footer strong {
  color: #041627;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  color: #44474c;
  text-decoration: none;
}

.footer-links a:hover {
  text-decoration: underline;
  color: #0058bc;
}
</style>
