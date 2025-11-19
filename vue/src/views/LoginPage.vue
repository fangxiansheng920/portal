<template>
  <div class="page">
    <div class="login-container">
      <!-- 登录标题 -->
      <div class="title-display">
        <h1 class="login-title">Portal</h1>
      </div>

      <!-- 文字切换导航 -->
      <div class="login-tabs">
        <span :class="{active: loginType ==='account'}" @click="loginType='account'">账号密码登录</span>
<!--        <span>|</span>-->
      </div>

      <div class="login-content">
        <!-- 动态切换登录方式 -->
        <AccountLoginForm
            v-if="loginType === 'account'"
            @login-success="handleLoginSuccess"
        />
<!--        <QrcodeLoginForm-->
<!--            v-else-->
<!--            @login-success="handleLoginSuccess"-->
<!--        />-->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref ,onMounted} from 'vue'
import { useRouter } from 'vue-router'
import AccountLoginForm from '@/components/LoginForm/AccountLoginForm.vue'
import { isTokenValid } from '@/utils/request';

const token = localStorage.getItem('token');
const router = useRouter();
//页面加载时检查token
onMounted(()=>{
  if(isTokenValid(token) ){
    router.push('/index');
  }
})

const loginType = ref('account') // 默认显示账号登录
const handleLoginSuccess = () => {
  // console.log('用户登录成功：', userInfo)
  router.push('/index')
}
</script>

<style scoped>
.page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}
.login-container {
  width: 600px;
  height: 500px;
  padding: 30px;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.title-display {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-title {
  //margin-left: 10px;
  margin-bottom: 16px;
  color: #333;
  font-size: 24px;
}

.login-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 25px;
  font-size: 16px;
}

.login-tabs span {
  margin: 0 15px;
  cursor: pointer;
  color: #666;
}

.login-tabs span.active {
  color: #1890ff;
  font-weight: bold;
}

.login-content {
  padding: 20px 40px;
  min-height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>
