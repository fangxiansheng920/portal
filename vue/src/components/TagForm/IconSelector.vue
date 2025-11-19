<template>
  <div class="icon-selector">
    <!-- 触发选择器的按钮 -->
    <el-popover
      v-model:visible="showSelector"
      placement="bottom-start"
      width="400"
      trigger="click"
    >
      <template #reference>
        <el-button 
          size="small" 
          class="select-button"
          :icon="getSelectedIconComponent"
        >
          {{ selectedIcon ? '更换图标' : '选择图标' }}
        </el-button>
      </template>
      
      <!-- 图标选择面板 -->
      <div class="icon-selector-panel">
        <el-input 
          v-model="searchIcon" 
          placeholder="搜索图标..." 
          size="small"
          class="icon-search"
        />
        
        <div class="icon-grid">
          <div 
            v-for="(icon, key) in filteredIcons" 
            :key="key"
            class="icon-item"
            @click="selectIcon(key)"
          >
            <component :is="icon" class="icon" />
            <span class="icon-name">{{ key }}</span>
          </div>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup>
import { ref, computed, watch,defineEmits,defineProps} from 'vue';
import { ElPopover, ElButton, ElInput } from 'element-plus';
import * as Icons from '@element-plus/icons-vue';

// 接收外部传入的已选图标
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

// 向父组件传递选中的图标
const emit = defineEmits(['update:modelValue']);

// 状态管理
const showSelector = ref(false);
const searchIcon = ref('');
const selectedIcon = ref(props.modelValue);

// 过滤图标列表
const filteredIcons = computed(() => {
  if (!searchIcon.value) return Icons;
  
  return Object.fromEntries(
    Object.entries(Icons).filter(([key]) => 
      key.toLowerCase().includes(searchIcon.value.toLowerCase())
    )
  );
});

// 获取选中的图标组件
const getSelectedIconComponent = computed(() => {
  return selectedIcon.value && Icons[selectedIcon.value] 
    ? Icons[selectedIcon.value] 
    : null;
});

// 选择图标
const selectIcon = (iconName) => {
  selectedIcon.value = iconName;
  emit('update:modelValue', iconName);
  showSelector.value = false;
};

// 监听外部传入的图标变化
watch(
  () => props.modelValue,
  (newVal) => {
    selectedIcon.value = newVal;
  }
);
</script>

<style scoped>
.icon-selector-panel {
  padding: 16px;
}

.icon-search {
  margin-bottom: 16px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 12px;
}

.icon-item:hover {
  background-color: #f5f7fa;
}

.icon {
  font-size: 20px;
  margin-bottom: 4px;
  color: #606266;
}

.icon-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  text-align: center;
}

.select-button {
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
