<template>
  <div class="cart-page">
    <div class="cart-header">
      <h1 class="cart-title">{{ $t('cart.title') }}</h1>
      <router-link to="/products" class="continue-shopping">
        <el-icon><arrow-left /></el-icon>
        {{ $t('cart.continueShopping') }}
      </router-link>
    </div>

    <!-- Empty State -->
    <div v-if="authStore.cartItems.length === 0" class="empty-cart">
      <el-icon :size="80" color="#c4c6cd"><shopping-bag /></el-icon>
      <h2>{{ $t('cart.emptyCart') }}</h2>
      <p>Looks like you haven't added anything to your cart yet.</p>
      <router-link to="/products">
        <el-button type="primary" size="large">{{ $t('cart.startShopping') }}</el-button>
      </router-link>
    </div>

    <!-- Cart Content -->
    <div v-else class="cart-layout">
      <div class="cart-items-section">
        <div v-for="item in authStore.cartItems" :key="item.id" class="cart-item">
          <div class="item-image">
            <img :src="item.image" :alt="item.name" />
          </div>
          <div class="item-details">
            <div class="item-info">
              <h3 class="item-name">{{ item.name }}</h3>
              <p class="item-meta">Color: {{ item.variant || 'Default' }} | {{ item.category }}</p>
              <span class="item-price">${{ item.price.toFixed(2) }}</span>
            </div>
            <div class="item-actions">
              <el-input-number
                v-model="item.quantity"
                :min="1"
                :max="99"
                size="small"
                @change="handleQtyChange(item)"
              />
              <button class="remove-btn" @click="handleRemove(item.id)">
                <el-icon><delete /></el-icon>
                {{ $t('cart.remove') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Order Summary Sidebar -->
      <aside class="summary-sidebar">
        <div class="summary-card">
          <h2 class="summary-title">Order Summary</h2>

          <div class="summary-rows">
            <div class="summary-row">
              <span>{{ $t('cart.subtotal') }}</span>
              <span class="summary-value">${{ subtotal.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>{{ $t('cart.shipping') }}</span>
              <span class="summary-value">{{ shipping === 0 ? 'Free' : '$' + shipping.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>{{ $t('cart.tax') }}</span>
              <span class="summary-value">${{ tax.toFixed(2) }}</span>
            </div>
            <el-divider />
            <div class="summary-row total-row">
              <span>{{ $t('cart.total') }}</span>
              <span class="total-value">${{ total.toFixed(2) }}</span>
            </div>
          </div>

          <el-button
            type="primary"
            size="large"
            class="checkout-btn"
            @click="$router.push('/checkout')"
          >
            {{ $t('cart.proceedToCheckout') }}
            <el-icon><arrow-right /></el-icon>
          </el-button>

          <p class="checkout-note">Taxes and shipping calculated at checkout.</p>

          <!-- Payment Icons -->
          <div class="payment-icons">
            <el-icon><credit-card /></el-icon>
            <el-icon><wallet /></el-icon>
            <el-icon><money /></el-icon>
            <el-icon><bank-card /></el-icon>
          </div>
        </div>

        <!-- Promo Code -->
        <div class="promo-card">
          <el-input
            v-model="promoCode"
            placeholder="Promo code"
            size="large"
          >
            <template #append>
              <el-button type="primary" @click="applyPromo">Apply</el-button>
            </template>
          </el-input>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { cartAPI } from '@/services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const authStore = useAuthStore()
const promoCode = ref('')

// Fetch cart from backend when logged in
const fetchCart = async () => {
  if (!authStore.isLoggedIn) return
  try {
    const response = await cartAPI.getCart()
    if (response && response.items) {
      const items = response.items.map(item => ({
        id: item.productId,
        cartItemId: item.id,
        name: item.productName,
        price: Number(item.price || 0),
        quantity: Number(item.quantity || 1),
        image: item.productImageUrl || 'https://via.placeholder.com/300',
        subtotal: Number(item.subtotal || 0)
      }))
      authStore.cartItems = items
      localStorage.setItem('cart', JSON.stringify(items))
    }
  } catch (err) {
    console.error('Error fetching cart:', err)
  }
}

onMounted(() => {
  if (authStore.isLoggedIn) {
    fetchCart()
  }
})

const subtotal = computed(() =>
  authStore.cartItems.reduce((sum, item) => sum + (Number(item.subtotal) || Number(item.price || 0) * Number(item.quantity || 0)), 0)
)

const shipping = computed(() => subtotal.value > 99 ? 0 : 15.00)
const tax = computed(() => subtotal.value * 0.08)
const total = computed(() => subtotal.value + shipping.value + tax.value)

const handleQtyChange = async (item) => {
  authStore.updateCartQuantity(item.id, item.quantity)
  if (authStore.isLoggedIn && item.cartItemId) {
    try {
      await cartAPI.updateCartItem(item.cartItemId, { quantity: item.quantity })
    } catch (err) {
      console.error('Error updating cart item:', err)
    }
  }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('Remove this item from your cart?', 'Confirm', {
      confirmButtonText: 'Yes, remove',
      cancelButtonText: 'Cancel',
      type: 'warning'
    })
    const item = authStore.cartItems.find(i => i.id === id)
    if (authStore.isLoggedIn && item?.cartItemId) {
      try {
        await cartAPI.removeCartItem(item.cartItemId)
      } catch (err) {
        console.error('Error removing from backend cart:', err)
      }
    }
    authStore.removeFromCart(id)
    ElMessage.success('Item removed from cart')
  } catch {}
}

const applyPromo = () => {
  if (promoCode.value) {
    ElMessage.success('Promo code applied!')
  }
}
</script>

<style scoped>
.cart-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px 64px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.cart-title {
  font-size: 36px;
  font-weight: 700;
  color: #041627;
  margin: 0;
}

.continue-shopping {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #0058bc;
  text-decoration: none;
  font-weight: 600;
  font-size: 16px;
}

.continue-shopping:hover {
  text-decoration: underline;
}

/* Empty State */
.empty-cart {
  text-align: center;
  padding: 96px 0;
  background: white;
  border-radius: 16px;
  border: 1px solid #eceef1;
}

.empty-cart h2 {
  color: #041627;
  margin: 24px 0 8px;
  font-size: 24px;
}

.empty-cart p {
  color: #74777d;
  margin: 0 0 24px;
}

/* Cart Layout */
.cart-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 32px;
  align-items: start;
}

.cart-items-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cart-item {
  display: flex;
  gap: 24px;
  padding: 24px;
  background: white;
  border: 1px solid #eceef1;
  border-radius: 16px;
  transition: box-shadow 0.2s;
}

.cart-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.item-image {
  width: 128px;
  height: 128px;
  border-radius: 12px;
  overflow: hidden;
  background: #f2f4f7;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-name {
  font-size: 20px;
  font-weight: 600;
  color: #191c1e;
  margin: 0 0 4px 0;
}

.item-meta {
  font-size: 13px;
  color: #74777d;
  margin: 0 0 8px 0;
}

.item-price {
  font-size: 20px;
  font-weight: 600;
  color: #0058bc;
}

.item-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
}

.remove-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: #ba1a1a;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.remove-btn:hover {
  background: #ffdad6;
}

/* Summary Sidebar */
.summary-sidebar {
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

.summary-title {
  font-size: 22px;
  font-weight: 600;
  color: #041627;
  margin: 0 0 24px 0;
}

.summary-rows {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #44474c;
  font-size: 15px;
}

.summary-value {
  font-weight: 600;
  color: #191c1e;
}

.total-row {
  font-size: 18px;
}

.total-value {
  font-size: 24px;
  font-weight: 700;
  color: #0058bc;
}

.checkout-btn {
  width: 100%;
  padding: 16px;
  background: #0058bc;
  border-color: #0058bc;
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 16px;
}

.checkout-btn:hover {
  background: #004493;
  border-color: #004493;
}

.checkout-note {
  text-align: center;
  color: #74777d;
  font-size: 13px;
  margin: 0 0 24px 0;
}

.payment-icons {
  display: flex;
  justify-content: center;
  gap: 16px;
  color: #c4c6cd;
  font-size: 24px;
  padding-top: 20px;
  border-top: 1px solid #eceef1;
}

.promo-card {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }
  .cart-item {
    flex-direction: column;
  }
  .item-image {
    width: 100%;
    height: 200px;
  }
}
</style>
