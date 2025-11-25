<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <h1>{{ $t('nav.brand') }}</h1>
        <p>{{ $t('auth.registerTitle') }}</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('auth.firstName')" prop="firstName">
              <el-input v-model="registerForm.firstName" size="large" :placeholder="$t('auth.placeholderFirstName')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('auth.lastName')" prop="lastName">
              <el-input v-model="registerForm.lastName" size="large" :placeholder="$t('auth.placeholderLastName')" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item :label="$t('auth.email')" prop="email">
          <el-input v-model="registerForm.email" size="large" :placeholder="$t('auth.placeholderEmail')" />
        </el-form-item>

        <el-form-item :label="$t('auth.phone')" prop="phone">
          <el-input v-model="registerForm.phone" size="large" :placeholder="$t('auth.placeholderPhone')" />
        </el-form-item>

        <el-form-item :label="$t('auth.password')" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            size="large"
            show-password
            :placeholder="$t('auth.placeholderPasswordMin')"
          />
        </el-form-item>

        <el-form-item :label="$t('auth.confirmPassword')" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            size="large"
            show-password
            :placeholder="$t('auth.placeholderConfirmPassword')"
          />
        </el-form-item>

        <el-form-item prop="agreeTerms">
          <el-checkbox v-model="registerForm.agreeTerms">
            {{ $t('auth.agreeTerms') }}<a href="#" class="terms-link">{{ $t('auth.termsOfService') }}</a>{{ $t('auth.and') }}<a href="#" class="terms-link">{{ $t('auth.privacyPolicy') }}</a>
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            {{ $t('auth.registerButton') }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <p>
          {{ $t('auth.alreadyHaveAccount') }}
          <router-link to="/login">{{ $t('auth.loginButton') }}</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreeTerms: false
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

const registerRules = {
  firstName: [{ required: true, message: 'First name is required', trigger: 'blur' }],
  lastName: [{ required: true, message: 'Last name is required', trigger: 'blur' }],
  email: [
    { required: true, message: 'Email is required', trigger: 'blur' },
    { type: 'email', message: 'Valid email required', trigger: 'blur' }
  ],
  phone: [{ required: true, message: 'Phone number is required', trigger: 'blur' }],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { min: 8, message: 'Minimum 8 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm password', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreeTerms: [{
    validator: (rule, value, callback) => {
      if (!value) callback(new Error('You must agree to the terms'))
      else callback()
    },
    trigger: 'change'
  }]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const result = await authStore.register({
      firstName: registerForm.firstName,
      lastName: registerForm.lastName,
      email: registerForm.email,
      phone: registerForm.phone,
      password: registerForm.password
    })
    if (result.success) {
      ElMessage.success('Registration successful! Please login.')
      router.push('/login')
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    console.error('Register error:', error)
    ElMessage.error(error.response?.data?.message || 'Registration failed. Please try again.')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #041627 0%, #0058bc 100%);
  padding: 32px;
}

.register-container {
  width: 100%;
  max-width: 540px;
  padding: 48px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h1 {
  color: #0058bc;
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px 0;
}

.register-header p {
  color: #191c1e;
  font-size: 16px;
  margin: 0;
}

.register-form {
  margin-bottom: 24px;
}

.register-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #191c1e;
}

.register-button {
  width: 100%;
  background-color: #0058bc;
  border-color: #0058bc;
  font-weight: 700;
  padding: 14px 0;
  font-size: 16px;
}

.register-button:hover {
  background-color: #004493;
  border-color: #004493;
}

.register-footer {
  text-align: center;
  color: #74777d;
}

.register-footer a {
  color: #0058bc;
  text-decoration: none;
  font-weight: 600;
}

.register-footer a:hover {
  text-decoration: underline;
}

.terms-link {
  color: #0058bc;
  text-decoration: none;
  font-weight: 500;
}

.terms-link:hover {
  text-decoration: underline;
}
</style>
