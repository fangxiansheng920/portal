import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@fortawesome/fontawesome-free/css/all.css'
import { startTokenWatch} from './utils/token_expire'
import {createPinia} from 'pinia'
import UserInfoForm from '@/components/SiteForm/UserInfoForm.vue';
import './registerServiceWorker'



const app = createApp(App)
const pinia  = createPinia()

app.use(ElementPlus)
app.use(pinia)
app.use(router)
app.component('UserInfoForm', UserInfoForm);
app.mount('#app')
startTokenWatch(router);