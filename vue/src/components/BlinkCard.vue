<template>
  <el-card 
    class="demo-card" 
    :class="{ 'blinking-border': isBlinking }" 
    @click="toggleBlink"
  >
    <div slot="header" class="card-header">
      <h2>点击卡片触发边框闪烁效果</h2>
    </div>
    <div class="card-content">
      <p>点击卡片任意位置，边框将开始闪烁。再次点击可停止闪烁。</p>
      <p class="status-text">{{ isBlinking ? '闪烁中...' : '点击开始闪烁' }}</p>
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue';

// 创建响应式变量控制闪烁状态
const isBlinking = ref(false);

// 切换闪烁状态的方法
const toggleBlink = () => {
  isBlinking.value = !isBlinking.value;
};
</script>

<style scoped>
.demo-card {
  max-width: 600px;
  margin: 20px auto;
  transition: all 0.3s ease;
  cursor: pointer;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  padding: 20px 0;
}

.status-text {
  color: #67c23a;
  margin-top: 15px;
  font-weight: 500;
}

/* 闪烁边框动画 */
@keyframes blink {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(64, 158, 255, 0);
    border-color: #e4e7ed;
  }
  50% {
    box-shadow: 0 0 0 5px rgba(64, 158, 255, 0.5);
    border-color: #409eff;
  }
}

/* 应用闪烁动画的类 */
.blinking-border {
  animation: blink 1.5s infinite;
  border: 2px solid #409eff;
}
</style>