<!-- GoLink.vue -->
<template>
  <span class="go-link" @click="handleCLick">
    Go →
  </span>
</template>

<script setup>
import { defineEmits } from 'vue'; 

const emit = defineEmits(['click'])

function handleCLick(event) {
  event.stopPropagation(); // 阻止事件冒泡
  emit('click', event) // 确保事件对象被传递
}
</script>

<style>
.go-link {
  transition: all 0.2s ease;
  cursor: pointer;
}
.go-link:hover {
  color: #1890ff;
  transform: translateX(2px);
}
</style>