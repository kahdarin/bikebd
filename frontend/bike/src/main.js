import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
//import axios from 'axios'
import axios from '@/api/request'
axios.defaults.baseURL = 'http://192.168.1.102:8089/'
app.config.globalProperties.$axios = axios
app.use(router)
app.use(ElementPlus)
app.use(store)

app.mount('#app')
