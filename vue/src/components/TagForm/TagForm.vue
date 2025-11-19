<template>
  <div>
    <!-- 标题和关闭按钮 -->
    <div class="form-title">
      {{ mode === 'edit' ? '编辑标签' : '新增标签' }}
      <el-icon @click="handleCancel" class="cancel-button">
        <CircleClose size="20" />
      </el-icon>
    </div>

    <!-- 表单滚动容器 -->
    <el-scrollbar class="tag-form-container">
      <!-- 表单核心 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        size="default"
      >
        <!-- 标签名称 -->
        <el-form-item label="标签名称" prop="tagName" required>
          <el-input 
            v-model="formData.tagName"
            placeholder="请输入标签名称"
            max-length="50"
          />
        </el-form-item>

        <!-- 标签颜色（颜色圆圈选择器，存储名称） -->
        <el-form-item label="标签颜色" prop="color" required>
          <div class="color-selection">
            <!-- 循环展示预设颜色 -->
            <div 
              v-for="color in predefinedColors" 
              :key="color.name"
              class="color-circle"
              :style="{ backgroundColor: color.name }"
              :class="{ 'selected': formData.color === color.name }"
              @click="formData.color = color.name"
              :title="color.name"
            >
              <!-- 选中状态指示器 -->
              <el-icon v-if="formData.color === color.name" class="check-icon">
                <Check />
              </el-icon>
            </div>
          </div>
          
          <!-- 显示当前选择的颜色名称
          <div class="selected-color-info" v-if="formData.color">
            已选择: {{ formData.color }}
          </div> -->
        </el-form-item>

        <!-- 图标选择器 -->
        <el-form-item label="标签图标" prop="icon">
          <IconSelector v-model="formData.icon" />
        </el-form-item>
      </el-form>
    </el-scrollbar>

    <!-- 底部按钮区 -->
    <div class="form-footer">
     <el-button type="primary" @click="handleSubmit">
        {{ mode === 'add' ? '创建' : '保存' }}
      </el-button>
      <el-button @click="handleCancel">取消</el-button>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, defineProps, defineEmits, watch } from 'vue';
import { CircleClose, Check } from '@element-plus/icons-vue';
import IconSelector from './IconSelector.vue';

// 接收父组件参数
const props = defineProps({
  mode: {
    type: String,
    default: '',
    validator: (value) => ['add', 'edit'].includes(value)
  },
  initialData: {
    type: Object,
    default: () => ({
      id: '',
      tagName: '',
      color: '',  // 存储颜色名称字符串（如 "red"、"blue"）
      icon: ''
    })
  }
});

// 向父组件传递事件
const emit = defineEmits(['cancel', 'submit']);

// 表单数据
const formData = reactive({ ...props.initialData });

// 预设10个常用颜色（仅存储名称，CSS支持的颜色名称）
const predefinedColors = [
  { name: 'black' },    // 黑色
  { name: 'white' },    // 白色
  { name: 'red' },      // 红色
  { name: 'green' },    // 绿色
  { name: 'blue' },     // 蓝色
  { name: 'yellow' },   // 黄色
  { name: 'purple' },   // 紫色
  { name: 'orange' },   // 橙色
  { name: 'gray' },     // 灰色
  { name: 'pink' }      // 粉色
];

// 表单验证规则
const formRules = {
  tagName: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { max: 50, message: '名称不能超过50个字符', trigger: 'blur' }
  ],
  color: [
    { required: true, message: '请选择标签颜色', trigger: 'change' }
  ],
  icon: [
    { required: true, message: '请选择标签图标', trigger: 'change' }
  ]
};

// 表单实例（用于校验）
const formRef = ref(null);

//重置表单
const resetForm = ()=>{
    formData.id='';
    formData.color='';
    formData.icon='';
    formData.tagName='';
}

// 监听初始数据变化（编辑时回显）
watch(
  () => props.initialData,
  (newData) => {
    if(newData){
        console.log('表单接收的initialData：', newData); 
        formData.tagName = newData.tagName;
        formData.id = newData.id;
        formData.color = newData.color;
        formData.icon = newData.icon;
    }
  },
  { immediate: true }
);

// 取消操作
const handleCancel = () => {
  emit('cancel');
  // 取消时重置表单（如果是新增模式）
  if (props.mode === 'add') {
    resetForm();
  }
};

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate();
  if (valid) {
    console.log('表单提交的数据（含id）：', formData); 
    emit('submit', { ...formData });
  }
};
</script>

<style scoped>
.form-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  font-size: 18px;
  font-weight: 500;
}

.cancel-button {
  cursor: pointer;
  color: #666;
  transition: color 0.2s;
}

.cancel-button:hover {
  color: #f53f3f;
}

.tag-form-container {
  height: 300px;
  padding: 16px 0;
}

/* 颜色选择器样式 */
.color-selection {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 8px 0;
  margin-bottom: 8px;
}

.color-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  border: 2px solid transparent;
}

/* 针对白色圆圈添加边框，避免在白色背景上看不见 */
.color-circle[style*="background-color: white"] {
  border: 1px solid #ddd;
}

.color-circle:hover {
  transform: scale(1.1);
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
}

.color-circle.selected {
  border-color: #333;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.2);
}

/* 选中状态的对勾图标（根据背景色自动调整颜色） */
.check-icon {
  position: absolute;
  font-weight: bold;
}

/* 白色背景上显示黑色对勾 */
.color-circle[style*="background-color: white"] .check-icon {
  color: black;
}

/* 深色背景上显示白色对勾 */
.color-circle:not([style*="background-color: white"]) .check-icon {
  color: white;
  text-shadow: 0 0 2px rgba(0, 0, 0, 0.5);
}

/* 选中颜色信息 */
.selected-color-info {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #eee;
  margin-top: 16px;
}
</style>
