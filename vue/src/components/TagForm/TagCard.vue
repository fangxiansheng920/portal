<template>
    <div class="tag-card" @click="handleClick">
        <!-- 左边，icon -->
        <div class="card-left">
            <div class="icon-container">

                <component
                    v-if="tag.icon"
                    :is="iconComponent"
                    :style="'color: ' + (tag.color || '#333')"
                    class="tag-icon"
                /><div v-else class="icon-placeholder"></div>


            </div>
        </div>

        <!-- 右边，name -->
      <div  :style="'background-color: ' + (tag.color || '#333')" class="color-dot"></div>
        <div class="card-right">

            <div class="tag-name">{{ tag.tagName || '未命名标签' }}</div>
        </div>

        <!-- 操作按钮（hover时显示） -->
        <div class="tag-actions">
            <el-icon @click.stop="handleEdit" class="action-icon">
                <Edit />
            </el-icon>
            <el-icon @click.stop="handleDelete" class="action-icon">
                <Delete />
            </el-icon>
        </div>
    </div>
</template>


<script setup>
import { defineProps, defineEmits,computed } from 'vue';
import { Edit, Delete } from '@element-plus/icons-vue';
import * as Icons from '@element-plus/icons-vue';

// 定义组件接收的参数
const props = defineProps({
    // 标签对象，包含id、name、icon等属性
    tag: {
        type: Object,
        required: true,
        default: () => ({})
    }
});

// 定义组件发出的事件
const emit = defineEmits(['click', 'edit', 'delete']);

// 获取图标组件
const iconComponent = computed(() => {
    // 确保标签有图标名称且该图标存在于Element Plus图标集中
    if (props.tag.icon && Icons[props.tag.icon]) {
        return Icons[props.tag.icon];
    }
    return null;
});

// 卡片点击事件
const handleClick = () => {
    emit('click', props.tag);
};

// 编辑按钮点击事件
const handleEdit = () => {
    emit('edit', props.tag);
};

// 删除按钮点击事件
const handleDelete = () => {
    emit('delete', props.tag);
};
</script>

<style scoped>
.tag-card {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background-color: #ffffff;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    min-width: 100px;
    max-width: 200px;
    position: relative;
    cursor: pointer;
    transition: all 0.2s ease;
}

.tag-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    border-color: #d1d5db;
}

.card-left {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
}

.icon-container {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
}

.tag-icon {
    width: 20px;
    height: 20px;
}

.icon-placeholder {
    width: 16px;
    height: 16px;
    background-color: #d1d5db;
    border-radius: 4px;
}

.card-right {
    flex: 1;
    min-width: 0; /* 解决内容过长时的溢出问题 */
}

.tag-name {
    font-size: 14px;
    font-weight: 500;
    color: #111827;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.tag-actions {
    display: flex;
    gap: 8px;
    margin-left: 2px;
    opacity: 0;
    transition: opacity 0.2s ease;
}

.tag-card:hover .tag-actions {
    opacity: 1;
}

.action-icon {
    width: 18px;
    height: 18px;
    color: #6b7280;
    cursor: pointer;
    transition: color 0.2s ease;
}

.action-icon:hover {
    color: #111827;
}

.action-icon:first-child:hover {
    color: #3b82f6; /* 编辑图标 hover 颜色 */
}

.action-icon:last-child:hover {
    color: #ef4444; /* 删除图标 hover 颜色 */
}
.color-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  margin-left: -8px;
  margin-right: 8px;
}
</style>
