import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import { createI18n } from 'vue-i18n'
import en from './locales/en.json'
import zh from './locales/zh.json'

const app = createApp(App)

// Pinia for state management
const pinia = createPinia()
app.use(pinia)

// Vue Router
app.use(router)

// Element Plus
app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Vue I18n
const i18n = createI18n({
  legacy: false,
  local: 'en',
  fallbackLocale: 'en',
  messages: {
    en,
    zh
  }
})
app.use(i18n)

app.mount('#app')
