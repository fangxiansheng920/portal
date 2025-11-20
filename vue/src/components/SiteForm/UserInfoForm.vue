<template>
  <!-- 遮罩层 -->
  <div v-if="visible" class="modal-overlay" @click="handleClose" />

  <!-- 弹窗主体 -->
  <div class="User-Profile-modal" v-if="visible">

    <div class="modal-close" @click="handleClose">
    <el-icon><Close size="20" /></el-icon>
  </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <!-- <el-spinner size="40"></el-spinner> -->
      <p>用户信息加载中</p>
    </div>

    <!-- 内容区域 -->
    <div v-else class="form-inner">
      <!-- 头像上传区域 -->
      <div class="avatar-container">
        <el-upload
          class="avatar-upload"
          :show-file-list="false"
          :on-success="handleAvatarUploadSuccess"
          :before-upload="beforeAvatarUpload"
          :disabled="uploadDisabled || saving"
          :action="UploadURL"
          :headers="{ Authorization: 'Bearer ' + token }"
        >
          <!-- 已上传头像显示 -->
          <img
            v-if="avatar"
            :src="avatar"
            class="user-avatar"
            alt="用户头像"

          />

          <!-- 未上传头像时显示上传按钮 -->
          <div v-else class="avatar-uploader-icon">
            <el-icon><Plus size="24" /></el-icon>
          </div>

          <!-- 上传中显示加载动画 -->
          <template #loading>
            <div class="avatar-loading">
              <!-- <el-spinner size="20" ></el-spinner> -->
            </div>
          </template>
        </el-upload>
        <p class="upload-hint">点击头像可更换图片</p>
      </div>

      <!-- 用户信息 -->
      <div class="user-info">
        <div v-if="tempUserName" class="info-item">
          <p><span class="label">用户名称:</span></p>
          <el-input
            v-model="tempUserName"
            size="small"
            :maxlength="20"
            show-word-limit
            class="name-input"
            placeholder="请输入用户名"
            :disabled="saving"
          ></el-input>
        </div>
        <p v-if="epWxId">微信号：{{ epWxId }}</p>
        <p v-else>微信号：Null</p>
        <p v-if="createTime">创建时间：{{ formatTime(createTime) }}</p>
        <p v-else>创建时间：Null</p>
        <p v-if="phoneNumber">电话：{{ phoneNumber }}</p>
        <p v-else>电话号：未绑定</p>
        <p>密码：</p>
      </div>

      <!-- 保存按钮 -->
      <div class="action-buttons">
        <el-button
          type="primary"
          @click="saveProfile"
          :loading="saving"
          :disabled="!hasChanges"
        >
          保存修改
        </el-button>
      </div>

      <!-- 密码更改区域 -->
      <div class="password-action">
        <div v-if="hasPassword !== 1">
          <div class="set-password">设置密码</div>
        </div>
        <div v-else>
          <div class="reset-password" @click="() => {
                // console.log('点击重新设置密码，showPasswordReset 变为 true');
                showPasswordReset = true;
              }">修改密码</div>
        </div>
      </div>
    </div>
  </div>
  <UserChangePW
  v-model="showPasswordReset"
  @success="onPasswordResetSuccess"
  class="ChangePW-modal"
  />

</template>

<script setup>
import { ref, defineProps, defineEmits, watch, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getCurrentUserInfo, modifyUserInfo } from '@/api/UserData';
import {useUserStore} from '@/store/user'
import { Close,Plus } from '@element-plus/icons-vue';
import UserChangePW from '@/components/SiteForm/UserChangePW.vue';


// 定义 props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

// 定义事件
const emit = defineEmits(['close', 'setPassword', 'resetPassword']);

const showPasswordReset =ref(false);

// 头像上传接口地址（与站点图标上传接口一致）
const UploadURL = import.meta.env.VITE_API_BASE_API + 'uploadAvatarImage';  // 确保地址正确

// 响应式数据
const avatar = ref('');           // 当前显示的头像URL
const originalAvatar = ref('');   // 原始头像URL（未修改时）
const originalUserName = ref(''); // 原始用户名（未修改时）
const tempUserName = ref('')  //临时值
const tempAvatarUrl = ref('')//临时值

const epWxId = ref('');
const createTime = ref('');
const phoneNumber = ref('');
const password = ref(null);
const loading = ref(false);       // 初始化加载状态
const saving = ref(false);        // 保存中状态
const uploadDisabled = ref(false);
const token = ref(localStorage.getItem('token') || '');  // 认证token
const hasPassword = ref('')

// 计算属性：是否有修改（用于禁用保存按钮）
const hasChanges = computed(() => {
  return (
    tempUserName.value !== originalUserName.value ||
    tempAvatarUrl.value !== originalAvatar.value
  );
});

// 时间格式化工具函数
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  try {
    return new Date(timeStr).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return timeStr;
  }
};

// 初始化/刷新用户信息
const getUserInfo = async () => {
  loading.value = true;
  try {
    const res = await getCurrentUserInfo();
    // console.log("用户信息接口返回：", res);
    if (res.code === 200 && res.status===true && res.data&& res.data.user) {
      const userData = res.data.user;
      // 更新原始值（用于判断是否修改）
      originalAvatar.value = userData.avatar || '';
      originalUserName.value = userData.userName || '';

      // 临时值初始化为原始值（未修改时保持一致）
      tempAvatarUrl.value = originalAvatar.value;
      tempUserName.value = originalUserName.value;

      // 更新显示值
      avatar.value = originalAvatar.value;
      tempUserName.value = originalUserName.value;
      // 其他用户信息
      epWxId.value = userData.epWxId || '';
      createTime.value = userData.createTime || '';
      phoneNumber.value = userData.phoneNumber || '';
      password.value = userData.password ?? null;
      hasPassword.value = res.data.hasPassword ?? '';
    } else {
      ElMessage.error(res.message || '获取用户信息失败');
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息异常，请重试');
  } finally {
    loading.value = false;
  }
};

// 头像上传成功回调（获取URL）
const handleAvatarUploadSuccess = (response) => {
  if (response.code === 200) {
    // 更新临时头像
    tempAvatarUrl.value = "/avatar/" + response.message;
    // 更新预览图为本地URL
    avatar.value = "/avatar/" + response.message;
    ElMessage.success('头像上传成功');
  } else {
    ElMessage.error(response.message || '头像上传失败，请重试');
    tempAvatarUrl.value = ''; // 上传失败清空URL
  }
};

// 上传前校验（暂时不限制类型和大小）
const beforeAvatarUpload = () => {
  return true;
};

// 保存用户信息（核心方法）
const saveProfile = async () => {
  // 校验用户名必填
  if (!tempUserName.value.trim()) {
    ElMessage.warning('请输入用户名');
    return;
  }

  // 校验头像URL（如果是新上传的）
  if (tempAvatarUrl.value === '' && originalAvatar.value === '') {
    ElMessage.warning('请上传头像');
    return;
  }

  saving.value = true;
  try {
    // 确定最终提交的头像URL（新上传的优先，否则用原始URL）
    const finalAvatarUrl = tempAvatarUrl.value || originalAvatar.value;

    // 调用修改接口：传递字符串类型的userName和avatar
    const res = await modifyUserInfo(tempUserName.value.trim(), finalAvatarUrl);

    if (res && res.code === 200 && res.status) {
      // 更新token
      localStorage.setItem('token', res.data);
      token.value = res.data;
      // 刷新用户信息（确保显示最新头像和用户名）
       const userInfoRes = await getCurrentUserInfo();
      if (userInfoRes.code === 200 && userInfoRes.data && userInfoRes.data.user) {
        // 3. 更新本地存储的 userInfo
        const userData = userInfoRes.data.user;
        localStorage.setItem('userInfo', JSON.stringify(userData));
        const userStore = useUserStore()
        userStore.updateInfo(userData)


        // 4. 同步更新界面显示
        avatar.value = userData.avatar || '';
        tempUserName.value = userData.userName || '';
        originalAvatar.value = userData.avatar || '';
        originalUserName.value = userData.userName || '';

        localStorage.setItem('userInfo', JSON.stringify(userData));

      }

      ElMessage.success('修改成功！');
    } else {
      ElMessage.error(res.message || '修改失败，请重试');
    }
  } catch (error) {
    console.error('保存用户信息失败:', error);
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    saving.value = false;
  }
};


// 关闭弹窗
const handleClose = () => {
  if (hasChanges.value) {
    ElMessageBox.confirm('有未保存的修改，是否放弃？', '提示', {
      confirmButtonText: '放弃',
      cancelButtonText: '继续编辑',
      type: 'warning'
    }).then(() => {
      // 放弃修改：临时值重置为原始值
      tempUserName.value = originalUserName.value;
      tempAvatarUrl.value = originalAvatar.value;
      // 同步界面显示
      avatar.value = tempAvatarUrl.value;
      emit('close');
    });
  } else {
    emit('close');
  }
};

// 定义密码重置成功的回调方法
const onPasswordResetSuccess = () => {
  showPasswordReset.value = false; // 关闭密码重置弹窗
};

// 监听弹窗显示状态：显示时加载数据
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      getUserInfo();
    }
  },
  { immediate: true }
);
</script>

<style scoped>
/* 基础样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.User-Profile-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 500px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 24px;
  z-index: 999;
}
/* 新增关闭按钮样式 */
.modal-close {
  position: absolute; /* 绝对定位，相对于弹窗容器 */
  top: 16px; /* 距离顶部 16px */
  right: 16px; /* 距离右侧 16px */
  cursor: pointer; /* 鼠标悬停显示手型 */
  color: #999; /* 默认灰色 */
  transition: color 0.2s; /* 颜色过渡动画 */
  z-index: 10; /* 确保在其他内容上方 */
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 40px 0;
}

/* 头像区域 */
.avatar-container {
  text-align: center;
  margin-bottom: 30px;
}

.avatar-upload {
  display: inline-block;
  position: relative;
}

.user-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s;
  border: none;
}

.avatar-uploader-icon {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: #f5f7fa;
  border: 2px dashed #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8c8c8c;
  cursor: pointer;
  transition: all 0.3s;
}
.avatar-uploader-icon:hover {
  border-color: #165dff;
  color: #165dff;
}
img:focus {
  outline: none;
}

.avatar-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(255, 255, 255, 0.8);
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.ChangePW-modal{
  position:relative;
}

.upload-hint {
  margin-top: 12px;
  font-size: 12px;
  color: #666;
}

/* 用户信息样式 */
.user-info {
  margin: 20px 0;
  line-height: 1.8;
}

.info-item {
  display: flex;
  align-items: center;
  margin: 12px 0;
}

.info-item .label {
  width: 80px;
  color: #666;
  margin-right: 10px;
}

.name-input {
  flex: 1;
  min-width: 0;
}

.user-info p {
  margin: 8px 0;
  color: #333;
}
.user-info p:before {
  content: "•";
  color: #165dff;
  margin-right: 8px;
  font-size: 16px;
}

/* 按钮样式 */
.action-buttons {
  text-align: center;
  margin: 20px 0;
}

/* 密码操作样式 */
.password-action {
  text-align: center;
  margin-top: 20px;
}
.set-password, .reset-password {
  color: #165DFF;
  cursor: pointer;
  padding: 8px 20px;
  border-radius: 4px;
  display: inline-block;
  transition: all 0.2s;
}
.set-password:hover, .reset-password:hover {
  background-color: #f0f5ff;
}
</style>
