import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const cartItems = ref(JSON.parse(localStorage.getItem('cart') || '[]'))

  const isLoggedIn = computed(() => !!token.value)
  const userEmail = computed(() => user.value?.email || '')
  const userRole = computed(() => user.value?.role || '')
  const cartCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))

  const login = async (credentials) => {
    try {
      const response = await axios.post('/api/v1/auth/login', credentials)
      token.value = response.data.token
      user.value = response.data.user
      localStorage.setItem('token', token.value)
      localStorage.setItem('user', JSON.stringify(user.value))
      axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Login failed' }
    }
  }

  const register = async (userData) => {
    try {
      const response = await axios.post('/api/v1/auth/register', userData)
      return { success: true, message: response.data.message }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Registration failed' }
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    cartItems.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('cart')
    delete axios.defaults.headers.common['Authorization']
  }

  const addToCart = async (product, quantity = 1) => {
    // Try backend first if logged in
    if (token.value) {
      try {
        const { default: api } = await import('@/services/api')
        const { cartAPI } = await import('@/services/api')
        await cartAPI.addToCart({
          productId: product.id,
          quantity: quantity
        })
        // Refresh cart from backend
        const response = await cartAPI.getCart()
        if (response && response.items) {
          cartItems.value = response.items.map(item => ({
            id: item.productId,
            cartItemId: item.id,
            name: item.productName,
            price: Number(item.price || 0),
            quantity: Number(item.quantity || 1),
            image: item.productImageUrl || product.image || 'https://via.placeholder.com/300',
            subtotal: Number(item.subtotal || 0)
          }))
          localStorage.setItem('cart', JSON.stringify(cartItems.value))
          return
        }
      } catch (err) {
        console.error('Error syncing cart to backend:', err)
        // Fall back to local cart
      }
    }
    // Local cart fallback
    const existingItem = cartItems.value.find(item => item.id === product.id)
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      cartItems.value.push({ ...product, quantity })
    }
    localStorage.setItem('cart', JSON.stringify(cartItems.value))
  }

  const removeFromCart = (productId) => {
    cartItems.value = cartItems.value.filter(item => item.id !== productId)
    localStorage.setItem('cart', JSON.stringify(cartItems.value))
  }

  const updateCartQuantity = (productId, quantity) => {
    const item = cartItems.value.find(item => item.id === productId)
    if (item) {
      if (quantity <= 0) {
        removeFromCart(productId)
      } else {
        item.quantity = quantity
        localStorage.setItem('cart', JSON.stringify(cartItems.value))
      }
    }
  }

  const clearCart = () => {
    cartItems.value = []
    localStorage.setItem('cart', JSON.stringify(cartItems.value))
  }

  return {
    token,
    user,
    cartItems,
    isLoggedIn,
    userEmail,
    userRole,
    cartCount,
    login,
    register,
    logout,
    addToCart,
    removeFromCart,
    updateCartQuantity,
    clearCart
  }
})
