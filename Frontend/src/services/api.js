import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// Create axios instance
const api = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Track if a redirect is already in progress to prevent loops
let isRedirecting = false

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status
    if ((status === 401 || status === 403) && !isRedirecting) {
      const path = window.location.pathname
      // Don't redirect if already on login or register page
      if (path === '/login' || path === '/register') {
        return Promise.reject(error)
      }

      isRedirecting = true
      const authStore = useAuthStore()
      authStore.logout()
      // Use replace to avoid back-button getting stuck in a loop
      window.location.replace('/login')
    }
    return Promise.reject(error)
  }
)

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  refreshToken: (refreshToken) => api.post('/auth/refresh', { refreshToken })
}

// Products API
export const productsAPI = {
  getProducts: (params) => api.get('/products', { params }),
  getProduct: (id) => api.get(`/products/${id}`),
  getCategories: () => api.get('/categories'),
  createProduct: (productData) => api.post('/products', productData),
  updateProduct: (id, productData) => api.put(`/products/${id}`, productData),
  deleteProduct: (id) => api.delete(`/products/${id}`)
}

// Cart API
export const cartAPI = {
  getCart: () => api.get('/cart'),
  addToCart: (item) => api.post('/cart/items', item),
  updateCartItem: (id, data) => api.put(`/cart/items/${id}`, data),
  removeCartItem: (id) => api.delete(`/cart/items/${id}`),
  clearCart: () => api.delete('/cart')
}

// Orders API
export const ordersAPI = {
  createOrder: (orderData) => api.post('/orders', orderData),
  getOrders: (params) => api.get('/orders', { params }),
  getOrder: (id) => api.get(`/orders/${id}`),
  cancelOrder: (id) => api.put(`/orders/${id}/cancel`),
  updateOrderStatus: (id, status) => api.put(`/seller/orders/${id}/status`, { status })
}

// User API
export const userAPI = {
  getProfile: () => api.get('/users/profile'),
  updateProfile: (data) => api.put('/users/profile', data),
  changePassword: (data) => api.put('/users/change-password', data),
  getAddresses: () => api.get('/users/addresses'),
  addAddress: (address) => api.post('/users/addresses', address),
  updateAddress: (id, address) => api.put(`/users/addresses/${id}`, address),
  deleteAddress: (id) => api.delete(`/users/addresses/${id}`)
}

// Seller API
export const sellerAPI = {
  getDashboard: () => api.get('/seller/dashboard'),
  getOrders: (params) => api.get('/seller/orders', { params }),
  getProducts: (params) => api.get('/seller/products', { params }),
  createProduct: (productData) => api.post('/seller/products', productData),
  updateProduct: (id, productData) => api.put(`/seller/products/${id}`, productData),
  deleteProduct: (id) => api.delete(`/seller/products/${id}`)
}

// Admin API
export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  getUsers: (params) => api.get('/admin/users', { params }),
  updateUser: (id, data) => api.put(`/admin/users/${id}`, data),
  getSellers: (params) => api.get('/admin/sellers', { params }),
  approveSeller: (id) => api.put(`/admin/sellers/${id}/approve`),
  rejectSeller: (id) => api.put(`/admin/sellers/${id}/reject`)
}

export default api
