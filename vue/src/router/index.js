import{createRouter, createWebHistory} from 'vue-router'
import { ElMessage } from 'element-plus'
import { isTokenValid } from '@/utils/request.js'

const routes = [
    {path:'/',redirect:'/login'}, //根路径重新定向到登录页
    {path:'/login',component:()=>import('@/views/LoginPage.vue')}, //登录页
    {path:'/all',component:()=>import('@/views/HomePage.vue'),meta:{requiresAuth:true}}, //主页面
    {path:'/index',component:()=>import("@/views/MinePage.vue"), meta:{requiresAuth:true}}, //我的页面
    {path:'/siteAdmin',component:()=>import("@/views/SiteAdmin.vue"), meta:{requiresAuth:true}},
    {path:'/allSite',component:()=>import('@/views/AllSite.vue'), meta:{requiresAuth:true}},
    {path:'/siteManagement',component:()=>import('@/views/SiteManagement.vue'), meta:{requiresAuth:true}},
]


const router = createRouter({
    history:createWebHistory(),
    routes
})

//添加路由守卫  Navigation Guard
router.beforeEach((to, from, next) => {


  // 如果目标路由需要认证，并且没有有效的token
  if (to.meta.requiresAuth && !isTokenValid()) {
    ElMessage.error('请先登录');
    next('/login'); // 跳转到登录页
  } else {
    next(); // 继续路由跳转
  }
});


export default router
