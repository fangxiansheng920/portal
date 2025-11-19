<template>
  <button class="add-button" @click="handleClick">
    <div class="icon">+</div>
    <div class="text">创建新项目</div>
  </button>
</template>

<script setup>
import { defineEmits } from 'vue';

const emit = defineEmits(['click']);

const handleClick = () => {
  emit('click');
};
</script>

<style scoped>
.add-button {
  /* 关键：与普通卡片完全相同的样式 */
  background-color: #fff;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  
  /* 关键：固定高度 + 宽度占满网格单元格 */
  height: 212px;  /* 与普通卡片高度一致 */
  width: 83%;    /* 占满网格单元格 */
  
  /* 重置按钮默认样式 */
  border: none;
  cursor: pointer;
  transition: transform 0.2s;
  
  /* 内容居中 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.add-button:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.icon {
  font-size: 28px;
  margin-bottom: 8px;
  color: #409eff;
}

.text {
  font-size: 14px;
  color: #409eff;
}
</style>