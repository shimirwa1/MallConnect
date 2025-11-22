<template>
  <div class="checkout-page">
    <h1 class="page-title">{{ $t('common.checkout') }}</h1>

    <div class="checkout-layout">
      <!-- Main Checkout Form -->
      <div class="checkout-main">
        <!-- Shipping Address -->
        <div class="checkout-section">
          <div class="section-header">
            <span class="section-number">1</span>
            <h2>Shipping Address</h2>
          </div>

          <el-form :model="shipping" label-position="top" class="checkout-form">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="First Name" required>
                  <el-input v-model="shipping.firstName" size="large" placeholder="John" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Last Name" required>
                  <el-input v-model="shipping.lastName" size="large" placeholder="Doe" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="Address" required>
              <el-input v-model="shipping.address" size="large" placeholder="123 Main Street" />
            </el-form-item>
            <el-form-item label="Apartment, suite, etc. (optional)">
              <el-input v-model="shipping.apt" size="large" placeholder="Apt 4B" />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="City" required>
                  <el-input v-model="shipping.city" size="large" placeholder="New York" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="State" required>
                  <el-select v-model="shipping.state" size="large" placeholder="Select">
                    <el-option label="New York" value="NY" />
                    <el-option label="California" value="CA" />
                    <el-option label="Texas" value="TX" />
                    <el-option label="Florida" value="FL" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="ZIP Code" required>
                  <el-input v-model="shipping.zip" size="large" placeholder="10001" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="Phone">
              <el-input v-model="shipping.phone" size="large" placeholder="+1 (555) 000-0000" />
            </el-form-item>
          </el-form>
        </div>

        <!-- Payment Method -->
        <div class="checkout-section">
          <div class="section-header">
            <span class="section-number">2</span>
            <h2>Payment Method</h2>
            <span class="simulation-badge">SIMULATION MODE</span>
          </div>

          <div class="payment-options">
            <div
              v-for="method in paymentMethods"
              :key="method.id"
              :class="['payment-method', { active: selectedPayment === method.id }]"
              @click="selectedPayment = method.id"
            >
              <el-icon :size="24"><credit-card /></el-icon>
              <div class="method-info">
                <strong>{{ method.name }}</strong>
                <span>{{ method.description }}</span>
              </div>
              <el-icon v-if="selectedPayment === method.id" color="#0058bc"><check-filled /></el-icon>
            </div>
          </div>

          <!-- Simulated Card Fields -->
          <div v-if="selectedPayment === 'card'" class="card-fields">
            <el-form-item label="Card Number">
              <el-input size="large" placeholder="4242 4242 4242 4242" maxlength="19">
                <template #suffix>
                  <el-icon><credit-card /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="Expiry Date">
                  <el-input size="large" placeholder="MM / YY" maxlength="5" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="CVC">
                  <el-input size="large" placeholder="123" maxlength="4" type="password" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="Cardholder Name">
              <el-input size="large" placeholder="John Doe" />
            </el-form-item>
          </div>
        </div>

        <!-- Order Review -->
        <div class="checkout-section">
          <div class="section-header">
            <span class="section-number">3</span>
            <h2>Review Your Order</h2>
          </div>

          <div class="order-items">
            <div v-for="item in authStore.cartItems" :key="item.id" class="order-item">
              <div class="order-item-image">
                <img :src="item.image" :alt="item.name" />
                <span class="item-qty">{{ item.quantity }}</span>
              </div>
              <div class="order-item-info">
                <h4>{{ item.name }}</h4>
                <p>{{ item.category }}</p>
              </div>
              <span class="order-item-price">${{ (Number(item.price || 0) * Number(item.quantity || 0)).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Order Summary Sidebar -->
      <aside class="checkout-sidebar">
        <div class="summary-card">
          <h3>Order Summary</h3>

          <div class="summary-rows">
            <div class="summary-row">
              <span>Subtotal ({{ authStore.cartCount }} items)</span>
              <span>${{ subtotal.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>Shipping</span>
              <span>{{ shippingCost === 0 ? 'Free' : '$' + shippingCost.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>Tax</span>
              <span>${{ tax.toFixed(2) }}</span>
            </div>
            <el-divider />
            <div class="summary-row total-row">
              <span>Total</span>
              <span class="total-price">${{ total.toFixed(2) }}</span>
            </div>
          </div>

          <el-button
            type="primary"
            size="large"
            class="place-order-btn"
            :loading="placing"
            @click="handlePlaceOrder"
          >
            Place Order — ${{ total.toFixed(2) }}
          </el-button>

          <p class="secure-note">
            <el-icon><shield /></el-icon>
            Secure checkout. Your data is encrypted.
          </p>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const placing = ref(false)
const selectedPayment = ref('card')

const shipping = ref({
  firstName: '',
  lastName: '',
  address: '',
  apt: '',
  city: '',
  state: '',
  zip: '',
  phone: ''
})

const paymentMethods = [
  { id: 'card', name: 'Credit / Debit Card', description: 'Visa, Mastercard, Amex' },
  { id: 'paypal', name: 'PayPal', description: 'Pay with your PayPal account' },
  { id: 'cod', name: 'Cash on Delivery', description: 'Pay when you receive' }
]

const subtotal = computed(() =>
  authStore.cartItems.reduce((sum, item) => sum + (Number(item.price || 0) * Number(item.quantity || 0)), 0)
)
const shippingCost = computed(() => subtotal.value > 99 ? 0 : 15.00)
const tax = computed(() => subtotal.value * 0.08)
const total = computed(() => subtotal.value + shippingCost.value + tax.value)

const handlePlaceOrder = async () => {
  if (!shipping.value.firstName || !shipping.value.lastName || !shipping.value.address) {
    ElMessage.warning('Please fill in all required shipping fields')
    return
  }
  placing.value = true
  // Simulate order placement
  await new Promise(resolve => setTimeout(resolve, 1500))
  authStore.clearCart()
  ElMessage.success('Order placed successfully! (Simulation)')
  router.push('/orders')
  placing.value = false
}
</script>

<style scoped>
.checkout-page {
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

.checkout-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 32px;
  align-items: start;
}

.checkout-main {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.checkout-section {
  padding: 32px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-number {
  width: 32px;
  height: 32px;
  background: #0058bc;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #041627;
  margin: 0;
}

.simulation-badge {
  margin-left: auto;
  padding: 4px 12px;
  background: #d2e4fb;
  color: #004493;
  border-radius: 9999px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.05em;
}

.checkout-form {
  margin-top: 8px;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border: 2px solid #eceef1;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-method:hover {
  border-color: #c4c6cd;
}

.payment-method.active {
  border-color: #0058bc;
  background: #f7f9fc;
}

.method-info strong {
  display: block;
  color: #191c1e;
  font-size: 15px;
}

.method-info span {
  color: #74777d;
  font-size: 13px;
}

.card-fields {
  padding: 20px;
  background: #f7f9fc;
  border-radius: 12px;
}

/* Order Items */
.order-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f7f9fc;
  border-radius: 12px;
}

.order-item-image {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  background: #e0e3e6;
  flex-shrink: 0;
}

.order-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-qty {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 22px;
  height: 22px;
  background: #041627;
  color: white;
  border-radius: 50%;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.order-item-info {
  flex: 1;
}

.order-item-info h4 {
  margin: 0 0 2px;
  font-size: 15px;
  color: #191c1e;
}

.order-item-info p {
  margin: 0;
  font-size: 13px;
  color: #74777d;
}

.order-item-price {
  font-weight: 700;
  color: #0058bc;
}

/* Sidebar */
.checkout-sidebar {
  position: sticky;
  top: 100px;
}

.summary-card {
  padding: 32px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.summary-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 20px 0;
}

.summary-rows {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  color: #44474c;
  font-size: 15px;
}

.total-row {
  font-size: 18px;
  font-weight: 600;
  color: #191c1e;
}

.total-price {
  font-size: 22px;
  color: #0058bc;
}

.place-order-btn {
  width: 100%;
  padding: 16px;
  background: #0058bc;
  border-color: #0058bc;
  font-weight: 700;
  font-size: 16px;
}

.place-order-btn:hover {
  background: #004493;
  border-color: #004493;
}

.secure-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #74777d;
  font-size: 13px;
  margin: 16px 0 0 0;
}

@media (max-width: 768px) {
  .checkout-layout {
    grid-template-columns: 1fr;
  }
}
</style>
