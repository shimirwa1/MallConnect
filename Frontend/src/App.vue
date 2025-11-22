<template>
  <div id="app">
    <!-- Navigation Header -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h1>MallConnect</h1>
        </div>
        <div class="nav-links">
          <router-link to="/">{{ $t('common.home') }}</router-link>
          <router-link to="/products">{{ $t('common.products') }}</router-link>
          <router-link to="/cart">
            <el-badge :value="cartCount" class="cart-badge">
              {{ $t('common.cart') }}
            </el-badge>
          </router-link>
        </div>
        <div class="user-actions">
          <el-button v-if="!isLoggedIn" @click="$router.push('/login')">
            {{ $t('common.login') }}
          </el-button>
          <el-button v-if="!isLoggedIn" @click="$router.push('/register')">
            {{ $t('common.register') }}
          </el-button>
          <el-dropdown v-if="isLoggedIn" @command="handleUserCommand">
            <span class="el-dropdown-link">
              {{ userEmail }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">{{ $t('common.profile') }}</el-dropdown-item>
                <el-dropdown-item command="orders">{{ $t('common.orders') }}</el-dropdown-item>
                <el-dropdown-item command="logout">{{ $t('common.logout') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button @click="toggleLanguage">
            {{ $t('common.language') }}
          </el-button>
        </div>
      </div>
    </el-header>

    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 MallConnect Global. All rights reserved.</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const { locale, t } = useI18n()
const authStore = useAuthStore()

const isLoggedIn = computed(() => authStore.isLoggedIn)
const userEmail = computed(() => authStore.userEmail)
const cartCount = computed(() => authStore.cartCount)

const toggleLanguage = () => {
  locale.value = locale.value === 'en' ? 'zh' : 'en'
  localStorage.setItem('locale', locale.value)
}

const handleUserCommand = (command) => {
  if (command === 'logout') {
    authStore.logout()
    router.push('/')
  } else {
    router.push(`/${command}`)
  }
}

onMounted(() => {
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale) {
    locale.value = savedLocale
  }
})
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #ffffff;
  border-bottom: 1px solid #e0e3e6;
  padding: 0 32px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.logo h1 {
  color: #0058bc;
  font-size: 24px;
  font-weight: 700;
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 32px;
}

.nav-links a {
  text-decoration: none;
  color: #191c1e;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-links a:hover {
  color: #0058bc;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.main-content {
  flex: 1;
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px;
  width: 100%;
}

.footer {
  background-color: #e0e3e6;
  padding: 32px;
  text-align: center;
}

.footer-content {
  max-width: 1280px;
  margin: 0 auto;
}

.cart-badge {
  margin-top: -4px;
}
</style>
