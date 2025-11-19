<template>
  <el-dialog
    v-model="localVisible"
    title="修改密码"
    width="400px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form 
      ref="passwordFormRef" 
      :model="passwordForm" 
      :rules="passwordRules" 
      label-width="80px"
      class="password-form"
    >
      <!-- 旧密码 -->
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          placeholder="请输入旧密码"
          clearable
          :disabled="loading"
        />
      </el-form-item>

      <!-- 新密码 -->
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          placeholder="请输入新密码（至少6位）"
          clearable
          :disabled="loading"
        />
      </el-form-item>

      <!-- 重复新密码 -->
      <el-form-item label="确认密码" prop="repeatPassword">
        <el-input
          v-model="passwordForm.repeatPassword"
          type="password"
          placeholder="请重复新密码"
          clearable
          :disabled="loading"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button 
        @click="handleClose"
        :disabled="loading"
      >
        取消
      </el-button>
      <el-button 
        type="primary" 
        @click="handleSubmit"
        :loading="loading"
      >
        确认修改
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, computed,onMounted,watch} from 'vue';
import { ElMessage} from 'element-plus';
import { changePassword } from '@/api/UserData'; // 导入修改密码接口

// 定义 props
const props = defineProps({
  // 控制弹窗显示/隐藏
  modelValue: {
    type: Boolean,
    default: false
  }
});

// 定义事件（关闭弹窗、修改成功）
const emit = defineEmits(['update:modelValue','close', 'success']);

// 创建一个计算属性来代理 visible
const localVisible = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit('update:modelValue', value);  // 通过事件通知父组件
  }
});

// 响应式数据
const passwordForm = ref({
  oldPassword: '', // 旧密码
  newPassword: '', // 新密码
  repeatPassword: '' // 重复新密码
});
const loading = ref(false); // 加载状态
const passwordFormRef = ref(null); // 表单引用

// 表单验证规则
const passwordRules = ref({
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 1, message: '新密码长度至少6位', trigger: 'blur' }
  ],
  repeatPassword: [
    { required: true, message: '请重复新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

// 关闭弹窗（重置表单）
const handleClose = () => {
  passwordForm.value = { oldPassword: '', newPassword: '', repeatPassword: '' };
  passwordFormRef.value?.resetFields(); // 重置验证状态
  emit('update:modelValue', false);
};

// 提交密码修改
const handleSubmit = async () => {
  // 前端表单验证
  try {
    await passwordFormRef.value.validate();
  } catch (error) {
    return; // 验证失败则终止
  }

  loading.value = true;
  try {
        // 调用后端修改密码接口
        const res = await changePassword({
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword,
        repeatPassword:passwordForm.value.repeatPassword
        });

    if (res.code === 200 && res.status===true) {
      ElMessage.success('密码修改成功');
      emit('success'); // 通知父组件修改成功
      handleClose(); // 关闭弹窗并重置表单
    } else {
      ElMessage.error(res.message || '密码修改失败，请检查旧密码是否正确');
    }
  } catch (error) {
    console.error('密码修改接口异常:', error);
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 监听 visible 变化，重置表单状态
onMounted(() => {
  watch(
    () => props.visible,
    (newVal) => {
      if (!newVal) {
        passwordForm.value = { oldPassword: '', newPassword: '', repeatPassword: '' };
        passwordFormRef.value?.resetFields();
      }
    }
  );
});
</script>

<style scoped>
/* .password-form {
  margin-top: 16px;
  margin:0 auto;
  
} */

</style>