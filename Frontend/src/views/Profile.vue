<template>
  <div class="profile-page">
    <h1 class="page-title">{{ $t('common.profile') }}</h1>

    <div class="profile-layout">
      <!-- Sidebar Navigation -->
      <aside class="profile-sidebar">
        <div class="user-card">
          <div class="user-avatar">
            <span>{{ userInitials }}</span>
          </div>
          <h3>{{ user.firstName }} {{ user.lastName }}</h3>
          <p>{{ user.email }}</p>
        </div>

        <nav class="profile-nav">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            :class="['nav-item', { active: activeTab === tab.id }]"
            @click="activeTab = tab.id"
          >
            <el-icon><component :is="tab.icon" /></el-icon>
            <span>{{ tab.label }}</span>
          </button>
        </nav>
      </aside>

      <!-- Main Content -->
      <div class="profile-content">
        <!-- Personal Info Tab -->
        <div v-if="activeTab === 'info'" class="content-card">
          <h2>Personal Information</h2>
          <el-form :model="user" label-position="top" class="profile-form">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item :label="$t('auth.firstName')">
                  <el-input v-model="user.firstName" size="large" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('auth.lastName')">
                  <el-input v-model="user.lastName" size="large" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="Email">
              <el-input v-model="user.email" size="large" disabled />
            </el-form-item>
            <el-form-item label="Phone">
              <el-input v-model="user.phone" size="large" />
            </el-form-item>
            <el-button type="primary" size="large" @click="saveProfile">
              Save Changes
            </el-button>
          </el-form>
        </div>

        <!-- Addresses Tab -->
        <div v-if="activeTab === 'addresses'" class="content-card">
          <div class="card-header">
            <h2>Saved Addresses</h2>
            <el-button type="primary" @click="showAddressForm = true">
              <el-icon><plus /></el-icon> Add Address
            </el-button>
          </div>

          <div class="address-grid">
            <div v-for="addr in addresses" :key="addr.id" class="address-card">
              <div v-if="addr.isDefault" class="default-badge">Default</div>
              <h4>{{ addr.label }}</h4>
              <p>{{ addr.fullName }}</p>
              <p>{{ addr.street }}</p>
              <p>{{ addr.city }}, {{ addr.state }} {{ addr.zip }}</p>
              <p>{{ addr.phone }}</p>
              <div class="address-actions">
                <el-button text size="small" type="primary">Edit</el-button>
                <el-button text size="small" type="danger">Delete</el-button>
              </div>
            </div>

            <!-- Add Address Card -->
            <div class="address-card add-card" @click="showAddressForm = true">
              <el-icon :size="32" color="#c4c6cd"><plus /></el-icon>
              <span>Add New Address</span>
            </div>
          </div>

          <!-- Address Form Dialog -->
          <el-dialog v-model="showAddressForm" title="Add New Address" width="500px">
            <el-form :model="newAddress" label-position="top">
              <el-form-item label="Address Label">
                <el-select v-model="newAddress.label" size="large" style="width: 100%">
                  <el-option label="Home" value="Home" />
                  <el-option label="Work" value="Work" />
                  <el-option label="Other" value="Other" />
                </el-select>
              </el-form-item>
              <el-form-item label="Full Name">
                <el-input v-model="newAddress.fullName" size="large" />
              </el-form-item>
              <el-form-item label="Street Address">
                <el-input v-model="newAddress.street" size="large" />
              </el-form-item>
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-form-item label="City">
                    <el-input v-model="newAddress.city" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="State">
                    <el-input v-model="newAddress.state" size="large" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-form-item label="ZIP Code">
                    <el-input v-model="newAddress.zip" size="large" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="Phone">
                    <el-input v-model="newAddress.phone" size="large" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            <template #footer>
              <el-button @click="showAddressForm = false">Cancel</el-button>
              <el-button type="primary" @click="addAddress">Save Address</el-button>
            </template>
          </el-dialog>
        </div>

        <!-- Security Tab -->
        <div v-if="activeTab === 'security'" class="content-card">
          <h2>Change Password</h2>
          <el-form :model="passwordForm" label-position="top" class="profile-form">
            <el-form-item label="Current Password">
              <el-input v-model="passwordForm.current" type="password" size="large" show-password />
            </el-form-item>
            <el-form-item label="New Password">
              <el-input v-model="passwordForm.new" type="password" size="large" show-password />
            </el-form-item>
            <el-form-item label="Confirm New Password">
              <el-input v-model="passwordForm.confirm" type="password" size="large" show-password />
            </el-form-item>
            <el-button type="primary" size="large" @click="changePassword">
              Update Password
            </el-button>
          </el-form>
        </div>

        <!-- Payment Methods Tab -->
        <div v-if="activeTab === 'payments'" class="content-card">
          <h2>Payment Methods</h2>
          <div class="payment-list">
            <div v-for="card in paymentCards" :key="card.id" class="payment-card-item">
              <div class="card-icon">
                <el-icon :size="24"><credit-card /></el-icon>
              </div>
              <div class="card-details">
                <strong>{{ card.brand }} •••• {{ card.last4 }}</strong>
                <span>Expires {{ card.expiry }}</span>
              </div>
              <el-button text type="danger" size="small">Remove</el-button>
            </div>
            <div class="add-payment-btn">
              <el-button size="large">
                <el-icon><plus /></el-icon> Add Payment Method
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { userAPI } from '@/services/api'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const activeTab = ref('info')
const showAddressForm = ref(false)
const profileLoading = ref(false)

// Initialize user data from auth store (populated at login)
const user = reactive({
  firstName: authStore.user?.firstName || '',
  lastName: authStore.user?.lastName || '',
  email: authStore.user?.email || '',
  phone: authStore.user?.phone || ''
})

const userInitials = computed(() => {
  const first = user.firstName?.[0] || ''
  const last = user.lastName?.[0] || ''
  return (first + last).toUpperCase() || 'U'
})

const tabs = [
  { id: 'info', label: 'Personal Info', icon: 'User' },
  { id: 'addresses', label: 'Addresses', icon: 'Location' },
  { id: 'security', label: 'Security', icon: 'Lock' },
  { id: 'payments', label: 'Payments', icon: 'CreditCard' }
]

const addresses = ref([])
const newAddress = reactive({
  label: 'Home', fullName: '', street: '', city: '', state: '', zip: '', phone: ''
})

const passwordForm = reactive({
  current: '', new: '', confirm: ''
})

const paymentCards = ref([])

// Fetch full profile from backend
const fetchProfile = async () => {
  profileLoading.value = true
  try {
    const data = await userAPI.getProfile()
    if (data) {
      user.firstName = data.firstName || user.firstName
      user.lastName = data.lastName || user.lastName
      user.email = data.email || user.email
      user.phone = data.phone || user.phone
    }
  } catch (err) {
    console.error('Failed to load profile:', err)
  } finally {
    profileLoading.value = false
  }
}

// Fetch addresses
const fetchAddresses = async () => {
  try {
    const data = await userAPI.getAddresses()
    if (Array.isArray(data)) {
      addresses.value = data
    }
  } catch (err) {
    console.error('Failed to load addresses:', err)
  }
}

const saveProfile = async () => {
  try {
    await userAPI.updateProfile({
      firstName: user.firstName,
      lastName: user.lastName,
      phone: user.phone
    })
    // Update auth store so navbar etc. reflects changes
    if (authStore.user) {
      authStore.user.firstName = user.firstName
      authStore.user.lastName = user.lastName
      authStore.user.phone = user.phone
      localStorage.setItem('user', JSON.stringify(authStore.user))
    }
    ElMessage.success('Profile updated successfully!')
  } catch (err) {
    ElMessage.error(err.response?.data?.message || 'Failed to update profile')
  }
}

const addAddress = async () => {
  try {
    await userAPI.addAddress({
      label: newAddress.label,
      fullName: newAddress.fullName,
      street: newAddress.street,
      city: newAddress.city,
      state: newAddress.state,
      zip: newAddress.zip,
      phone: newAddress.phone
    })
    showAddressForm.value = false
    ElMessage.success('Address added!')
    Object.keys(newAddress).forEach(k => newAddress[k] = '')
    newAddress.label = 'Home'
    fetchAddresses()
  } catch (err) {
    ElMessage.error(err.response?.data?.message || 'Failed to add address')
  }
}

const changePassword = async () => {
  if (passwordForm.new !== passwordForm.confirm) {
    ElMessage.error('Passwords do not match')
    return
  }
  try {
    await userAPI.changePassword({
      currentPassword: passwordForm.current,
      newPassword: passwordForm.new
    })
    ElMessage.success('Password changed successfully!')
    passwordForm.current = ''
    passwordForm.new = ''
    passwordForm.confirm = ''
  } catch (err) {
    ElMessage.error(err.response?.data?.message || 'Failed to change password')
  }
}

onMounted(() => {
  fetchProfile()
  fetchAddresses()
})
</script>

<style scoped>
.profile-page {
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

.profile-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 32px;
  align-items: start;
}

/* Sidebar */
.profile-sidebar {
  position: sticky;
  top: 100px;
}

.user-card {
  padding: 32px 24px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
  text-align: center;
  margin-bottom: 16px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #041627, #0058bc);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.user-avatar span {
  font-size: 28px;
  font-weight: 700;
  color: white;
}

.user-card h3 {
  margin: 0 0 4px;
  color: #191c1e;
  font-size: 18px;
}

.user-card p {
  margin: 0;
  color: #74777d;
  font-size: 14px;
}

.profile-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: none;
  background: none;
  border-radius: 10px;
  color: #44474c;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
  text-align: left;
}

.nav-item:hover {
  background: #f2f4f7;
}

.nav-item.active {
  background: #d2e4fb;
  color: #004493;
  font-weight: 600;
}

/* Content */
.profile-content {
  min-height: 600px;
}

.content-card {
  padding: 32px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
}

.content-card h2 {
  font-size: 22px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 24px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-header h2 {
  margin: 0;
}

.profile-form {
  max-width: 600px;
}

/* Address Grid */
.address-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.address-card {
  position: relative;
  padding: 20px;
  border: 1px solid #eceef1;
  border-radius: 12px;
  transition: box-shadow 0.2s;
}

.address-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.address-card h4 {
  margin: 0 0 8px;
  color: #191c1e;
}

.address-card p {
  margin: 2px 0;
  color: #44474c;
  font-size: 14px;
}

.default-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 2px 10px;
  background: #d2e4fb;
  color: #004493;
  border-radius: 9999px;
  font-size: 11px;
  font-weight: 600;
}

.address-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.add-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  border: 2px dashed #c4c6cd;
  min-height: 180px;
  color: #74777d;
  font-size: 14px;
}

.add-card:hover {
  border-color: #0058bc;
  color: #0058bc;
}

/* Payment List */
.payment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-card-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border: 1px solid #eceef1;
  border-radius: 12px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  background: #f2f4f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0058bc;
}

.card-details {
  flex: 1;
}

.card-details strong {
  display: block;
  color: #191c1e;
  font-size: 15px;
}

.card-details span {
  color: #74777d;
  font-size: 13px;
}

.add-payment-btn {
  padding-top: 8px;
}

@media (max-width: 768px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
  .address-grid {
    grid-template-columns: 1fr;
  }
}
</style>
