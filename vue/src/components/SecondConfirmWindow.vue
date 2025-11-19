<!-- components/SecondConfirmWindow.vue -->
<template>
  <div v-if="visible" class="confirm-overlay">
    <div class="confirm-modal">
      <h3 class="modal-title">{{ title }}</h3>
      <p class="modal-message">{{ message }}</p>
      <div class="modal-buttons">
        <button @click="handleCancel" class="cancel-btn">取消</button>
        <button @click="handleConfirm" class="confirm-btn">确认</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {defineProps,defineEmits } from 'vue'

// 接收弹窗配置
// eslint-disable-next-line no-unused-vars
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '操作确认'
  },
  message: {
    type: String,
    required: true
  }
})

// 触发确认/取消事件
const emit = defineEmits(['confirm', 'cancel'])

const handleConfirm = () => {
  emit('confirm') // 通知父组件用户确认
}

const handleCancel = () => {
  emit('cancel') // 通知父组件用户取消
}
</script>

<style scoped>
/* 遮罩层 */
.confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999; /* 确保在最上层 */
}

/* 弹窗容器 */
.confirm-modal {
  background: white;
  border-radius: 8px;
  padding: 20px;
  width: 300px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-title {
  margin: 0 0 16px;
  font-size: 18px;
  color: #333;
}

.modal-message {
  margin: 0 0 20px;
  color: #666;
  font-size: 14px;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

button {
  padding: 6px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  border: 1px solid #ddd;
}

.cancel-btn {
  background: #f5f5f5;
  color: #333;
}

.confirm-btn {
  background: #e74c3c; /* 与爱心颜色呼应 */
  color: white;
  border-color: #e74c3c;
}

/* 按钮 hover 效果 */
.cancel-btn:hover {
  background: #eee;
}

.confirm-btn:hover {
  background: #c0392b;
}
</style>