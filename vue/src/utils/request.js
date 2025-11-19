import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router/index'

const VITE_GLOB_API_URL = import.meta.env.VITE_API_BASE_API || '';
const VITE_GLOB_API_URL_PREFIX = import.meta.env.VITE_GLOB_API_URL_PREFIX || '';
// 创建 axios 实例
const service = axios.create({
  baseURL: VITE_GLOB_API_URL + VITE_GLOB_API_URL_PREFIX,// 基础路径
  timeout: 5000,// 超时时间
   headers: {
    'Content-Type': 'application/json' // 关键：设置为 JSON
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);


// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data; // 直接返回响应数据
  if(res.code===200 ||res.code===204 || res.code===201&& res.status ===true){
    return response.data;
  }else{
    return Promise.reject(new Error(res.message || '操作失败，请稍后再试'));
  }
},
  error => {
     let is401Handled = false;
    if (error.response) {
      const { status, data } = error.response;
      const errorMessage = data.message || '操作失败，请稍后再试';

      switch (status) {
        case 401:
        // Token 过期或无效，清除本地 Token 并跳转到登录页
        if(!is401Handled){
          is401Handled = true;
        ElMessage.error('登录已过期，请重新登录');
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        router.push('/login')
        }
        break;
        case 403:
          ElMessage.error(errorMessage || '无权限访问');
          break;
        case 404:
          ElMessage.error(errorMessage || '资源不存在');
          break;
        case 500:
          ElMessage.error(errorMessage || '服务器错误');
          localStorage.removeItem('token');
          localStorage.removeItem('userInfo');
          router.push('/login');
          break;
        default:
          ElMessage.error(errorMessage);
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时');
    } else {
      ElMessage.error('网络错误');
    }
    return Promise.reject(error);
  }
);

export function isTokenValid(){
  const token = localStorage.getItem('token');
  return !!token;
}

export default service;
