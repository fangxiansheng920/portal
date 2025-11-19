<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleCancel">
    <div class="modal-content">
      <h3 class="modal-title">站点可见性设置</h3>
      <p class="modal-message">{{ message }}</p>
      <div class="modal-buttons">
        <button class="cancel-btn" @click="handleCancel">取消</button>
        <button class="confirm-btn" @click="handleConfirm">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

// 接收父组件传递的属性
// eslint-disable-next-line no-unused-vars
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: '确定要执行此操作吗？'
  }
})

// 使用 defineEmits 定义要触发的事件
const emit = defineEmits(['confirm', 'cancel'])

// 处理确认按钮点击
const handleConfirm = () => {
  emit('confirm')
}

// 处理取消按钮点击
const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
/* 样式保持不变 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 确保在其他内容之上 */
}

.modal-content {
  background-color: white;
  padding: 24px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.modal-title {
  margin: 0 0 16px;
  font-size: 18px;
  color: #333;
  text-align: center;
}

.modal-message {
  margin: 0 0 24px;
  font-size: 16px;
  color: #666;
  text-align: center;
  line-height: 1.5;
}

.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.cancel-btn, .confirm-btn {
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #ddd;
}

.cancel-btn {
  background-color: #fff;
  color: #666;
}

.cancel-btn:hover {
  background-color: #f5f5f5;
}

.confirm-btn {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.confirm-btn:hover {
  background-color: #0984e3;
}
</style>