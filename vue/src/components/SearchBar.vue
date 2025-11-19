<template>
  <div class="search-box-wrapper" :class="externalClass">
    <input
      type="text"
      :placeholder="placeholder"
      v-model="inputValue"
      @input="handleInput"
      @keyup.enter="handleSearch"
    />
    <button v-if="inputValue" @click="clearSearch" class="clear-btn">
      <i class="fas fa-times"></i>
    </button>
    <button @click="handleSearch" :class="['search-btn', buttonClass]">
      <i class="fas fa-search"></i>
    </button>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({
   // 接收外部类名
  externalClass: {
    type: String,
    default: ''
  },
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入关键词'
  }
})

const emit = defineEmits(['update:modelValue', 'search', 'clear'])
const inputValue = ref(props.modelValue); // 初始化时同步父组件的 modelValue

// 监听父组件 modelValue 变化，同步到 inputValue（比如父组件清空搜索时）
watch(
  () => props.modelValue,
  (newVal) => {
    inputValue.value = newVal;
  }
)

function handleInput(e) {
  inputValue.value = e.target.value;
  emit('update:modelValue', inputValue.value); // 实时同步给父组件
}

function handleSearch() {
  const value = inputValue.value.trim();
  emit('search', value); // 触发搜索事件，传搜索值
  emit('update:modelValue', value); // 确保父组件同步最新值
}

function clearSearch() {
  inputValue.value = '';
  emit('update:modelValue', ''); // 同步清空给父组件
  emit('clear'); // 触发清空事件
}
</script>

<style scoped>
.search-box-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 6px;
  overflow: hidden;
  width: 100%; 
  max-width: 300px; 
  background: #fff;
}

.search-box-wrapper input {
  flex: 1!important;
  padding: 8px 12px;
  border: none;
  outline: none;
  font-size: 14px;
  
}

.search-btn, .clear-btn {
  background-color: #f5f5f5;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.search-btn:hover, .clear-btn:hover {
  background-color: #e0e0e0;
}

.search-btn i, .clear-btn i {
  color: #666;
  font-size: 16px;
}

.clear-btn {
  background: transparent;
}
</style>