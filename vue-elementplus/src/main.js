import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
//引入公共样式

import 'element-plus/dist/index.css'
import "@/assets/styles/default.css"
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import {hasPermission} from "./common/directives/index"
//引入pinia
import { createPinia } from 'pinia'



const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
//全局注册elementplus
app.use(ElementPlus, {
	locale: zhCn,
});

app.directive('permission',hasPermission)
app.use(router)
app.use(pinia)
app.mount('#app')
