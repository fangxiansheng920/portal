<template>
  <div class="login-account">
    <div class="login-tip"></div>

    <div class="input-group">
      <input
        v-model="phoneNumber"
        type="text"
        placeholder="请输入手机号以登录"
        maxlength="12"
        class="account-input"
        @keyup.enter="handleLogin"
      >
    </div>

    <div class="input-group">
      <input
        v-model="password"
        type="password"
        placeholder="请输入密码"
        maxlength="50"
        class="password-input"
        @keyup.enter="handleLogin"
      >
    </div>

    <div class="login-options">
      <label class="auto-login">
        <input type="checkbox"> 下次自动登录
      </label>
    </div>

    <div class="login-btn-container">
      <button class="login-btn" @click="handleLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </div>

    <div class="other-options">
      <!-- 预留其他选项 -->
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits} from 'vue';
import { ElMessage } from 'element-plus';
import { authLogin } from '@/api/login';
import { mockLogin } from '@/mocks/logintest.js';
import { clearUserInfo } from '@/utils/auth.js';
import { useUserStore } from '@/store/user';
import { getCurrentUserInfo } from '@/api/UserData'

const emit = defineEmits(['login-success']);

const phoneNumber = ref('');
const password = ref('');
const loading = ref(false);



const handleLogin = async () => {
  if (!phoneNumber.value || !password.value) {
    ElMessage.error('账号和密码不能为空');
    return;
  }

  loading.value = true;
  try {
    // 1. 登出前先清除现有用户信息（本地存储 + Pinia）
    clearUserInfo();
    const userStore = useUserStore(); // 提前实例化 Pinia
    userStore.updateInfo({}); // 清空旧状态
    localStorage.removeItem('userInfo');

    // 2. 发起登录请求（兼容mock和真实接口）
    const loginRes = import.meta.env.VITE_APP_USE_MOCK_AUTH === 'true'
      ? await mockLogin({ phoneNumber: phoneNumber.value, password: password.value })
      : await authLogin({ phoneNumber: phoneNumber.value, password: password.value });

    // 3. 基础响应格式校验
    if (!loginRes || typeof loginRes !== 'object') {
      throw new Error('服务器返回格式异常');
    }

    // 4. 处理登录成功响应
    if (loginRes.code === 200 && loginRes.status === true&&loginRes.data) {
      // 校验登录成功后的数据结构
      if (!loginRes.data || typeof loginRes.data !== 'object') {
        throw new Error('登录成功，但数据格式错误');
      }
      if (!loginRes.data.token) {
        throw new Error('登录成功，但未返回token');
      }

      // 5. 保存token到本地存储
      localStorage.setItem('token', loginRes.data.token);
      await new Promise(resolve => setTimeout(resolve, 100)); 

      // 6. 同步获取完整用户信息（关键：用await确保顺序执行）
      try {
        const userInfoRes = await getCurrentUserInfo(); // 同步等待结果
        if (!userInfoRes.data.user) {
          throw new Error('获取用户信息为空');
        }

        // 7. 同时更新本地存储和Pinia（确保数据一致）
        const userData = userInfoRes.data.user;
        localStorage.setItem('userInfo', JSON.stringify(userData));
        userStore.updateInfo(userData); // 实时更新Pinia状态

        ElMessage.success({
          message:'登录成功',
          duration:700
        })
        emit('login-success'); // 通知父组件登录成功
      } catch (error) {
        // console.error('获取用户信息失败:', error);
        throw new Error('登录成功，但获取用户信息失败'); // 终止流程并提示
      }
    }

    // 8. 处理登录失败响应（业务状态码非成功）
    else {
      const errorMsg = loginRes.message || `登录失败（状态码：${loginRes.code || '未知'}）`;
      throw new Error(errorMsg);
    }
  } catch (error) {
    // console.error('登录错误:', error);
    // ElMessage.error(error.message); // 统一错误提示
  } finally {
    loading.value = false; // 无论成功失败都关闭加载状态
  }
};


</script>

<style scoped>
.login-account {
  width: 100%;
  max-width: 320px;
  margin: 0 auto;
  margin-top: -10px;
}

.input-group {
  margin-bottom: 20px;
}

.input-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.input-group input:focus {
  border-color: #1890ff;
  outline: none;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 14px;
}

.auto-login {
  color: #666;
  display: flex;
  align-items: center;
}

.auto-login input {
  margin-right: 5px;
}

.login-btn-container {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  padding: 15px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #40a9ff;
}

.login-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.other-options {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #999;
  font-size: 14px;
}
</style>
