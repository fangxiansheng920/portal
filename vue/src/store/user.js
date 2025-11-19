// src/store/user.js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  // 1. 状态：存储用户信息
  state: () => ({
    info: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),

  // 2. 计算属性：获取头像、用户名等
  getters: {
    avatar: (state) => state.info.avatar || '',
    userName: (state) => state.info.userName || '',
    role: (state) =>state.info.role ||''
  },

  // 3. 方法：更新用户信息

actions: {
  updateInfo(newInfo) {
    // 关键：用新对象替换，强制触发响应式更新
    this.info = { ...newInfo }; 
    localStorage.setItem('userInfo', JSON.stringify(newInfo));
  }
}
})