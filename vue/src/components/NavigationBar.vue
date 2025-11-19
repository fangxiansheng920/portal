<template>
  <el-menu
      :default-active="activeIndex"
      mode="horizontal"
      :ellipsis="false"
      @select="handleSelect"
  >
    <el-menu-item index="logo" @click="jumpToHomepage">
      <img
          src="@/assets/logo.png"
          alt="logo"
          :width="32"
          class="logo-img"

      />
      <h1 class="logo-title">Portal</h1>
    </el-menu-item>
    <!-- 2. 中间：搜索组件（真正的第 2 项） -->
<!--    <div class="center" >-->
<!--      <el-input-->
<!--          v-model="keyword"-->
<!--          clearable-->
<!--          placeholder="搜索"-->
<!--          class="search-input"-->
<!--          :prefix-icon="Search"-->
<!--          @keyup.enter="handleSearch"-->
<!--          @clear="handleSearch"-->
<!--      >-->
<!--      </el-input>-->
<!--    </div>-->
      <el-menu-item index="/index" >我的收藏</el-menu-item>
      <el-menu-item index="/all" >全部站点</el-menu-item>
      <el-menu-item index="/siteAdmin" >站点管理</el-menu-item>
<!--      <el-menu-item index="/allSite" >全部站点</el-menu-item>-->
<!--      <el-menu-item index="/siteManagement" >站点管理</el-menu-item>-->
    <!--用户图标&ndash;&gt;-->

    <el-menu-item  index="userInfo" @click="openUserInfo">
      <div class="user-icon ">
        <el-dropdown>
          <div  >
            <div class="icon-wrapper" trigger="hover">
              <el-avatar  v-if="userAvatar!=null && userAvatar !=='' " :src="userAvatar "></el-avatar>
              <el-avatar  v-else>{{ userUserName  ||'用户'}}</el-avatar>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu ref="userDropdownMenuRef">
              <el-dropdown-item @click.stop="goToUserProfile">{{ userUserName  }}</el-dropdown-item>
              <!--退出登录按钮-->
              <el-dropdown-item divided command="logout" class="logout-item" @click="handleCommand('')">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-menu-item>
  </el-menu>
   <UserInfoForm
   :visible="showUserProfile"
   @close="showUserProfile=false"
   @setPassword="handleSetPassword"
   @resetPassword="handleResetPassword"
   />
</template>

<script setup>
import { computed,onMounted,onUnmounted} from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/login.js'
import { ref } from 'vue';
import UserInfoForm from '@/components/SiteForm/UserInfoForm.vue'
import { useUserStore } from '@/store/user'
// import {eventBus } from '@/utils/EventBus'
// const keyword = ref('');
const router = useRouter()
const showUserProfile = ref(false);
const userDropdownMenuRef = ref();
const userStore = ref(null)
onMounted(() => {
userStore.value = useUserStore()
})
// 关键：用 computed 包装，确保响应式跟踪
const userAvatar = computed(() => userStore.value?.avatar || '')

const userUserName = computed(() => userStore.value?.userName || '')
const goToUserProfile=()=>{
  showUserProfile.value=true;
}

// 这里就是 activeIndex 应该放置的位置
const activeIndex = computed(() => {
  return router.currentRoute.value.path;
});


// function handleSearch() {
// //   根据目前的路由状态，使用不同的接口搜索
//   const params = {
//     keyword: keyword.value,
//     type: activeIndex.value ,
//     pageNum: 1,
//     pageSize: 10,
//     sort: 'createTime,desc'
//   };
//   console.log('搜索关键字:', params);
//   eventBus.emit('search', params);
// }
async function handleCommand() {
  try {
    // 1. 先调用API登出
    await logout();

    //清除pinia
    const userStore = useUserStore();
    userStore.updateInfo({});

    // 2. 清除本地数据
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');

    // 3. 显示成功消息
    ElMessage.success('退出登录成功');

    // 4. 跳转到登录页
    router.push('/login');

  } catch (error) {
    console.error('登出失败:', error);

    // 即使API登出失败，也强制本地清理
    localStorage.removeItem('userInfo');
    localStorage.removeItem('token');
    router.push('/login');
  }
}

// 新增：处理密码设置/重置事件
const handleSetPassword = () => {
  console.log('触发设置密码逻辑');
  // 可跳转到密码设置页面或打开密码弹窗
};

const handleResetPassword = () => {
  console.log('触发重置密码逻辑');
};


//点击logo跳转到首页
const jumpToHomepage= () =>{
  router.push('/index');
}

const handleSelect = (index) => {
  if (index === 'logo') {
    // 处理 logo 逻辑，例如跳转到首页
    router.push('/');
  } else if(index==='userInfo'){
    openUserInfo();
  }else{
    router.push(index);
  }
};
const openUserInfo=()=>{
  userDropdownMenuRef.value?.handleOpen?.()
}

onUnmounted(() => {
});

</script>



<style scoped>
*:focus {
  outline: none !important;
}
/*
.el-menu--horizontal > .el-menu-item:nth-child(1) {
  margin-right: auto;
}

*/
.el-menu--horizontal {
  display: flex;
  justify-content: space-between;   /* 两端对齐 */
}

/* 左边第一个元素 */
.el-menu--horizontal > .el-menu-item:nth-child(1) {
  margin-right: auto;   /* 把剩余空间全吸到左边 */
}

/* 右边最后一个元素 */
.el-menu--horizontal > .el-menu-item:last-child {
  margin-left: auto;    /* 把剩余空间全吸到右边 */
}

.center{
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.navbar-menu .el-menu {
  display: flex;
  flex-wrap: nowrap; /* 禁止换行 */
  gap: 20px;
}

.navbar-menu .el-menu-item {
  flex-shrink: 0;
  padding: 0 15px;
  font-size: 15px;
  white-space: nowrap;/* 防止菜单换行*/
}
.avatar-menu-item {
  padding: 0 !important; /* 覆盖默认 padding */
  border: none !important; /* 若有默认边框则清除 */
  margin: 0 !important;
}

/* 确保菜单item内部布局不影响头像 */
.avatar-menu-item .el-dropdown {
  display: inline-flex;
  align-items: center;
}
.search-bar-wrapper {
  flex: 1;
  display: flex;
  /* 让搜索框在容器内居中或靠左，按需调整 */
  justify-content: flex-start;
  margin: 0 20px;
  min-width: 0;
}

:deep(.el-input__wrapper) { border-radius: 20px; }
.search-input{
  width: 50%;
}
.search-input:hover{
  border: #409EFF;
}

.navbar-search  {
  width: 100% !important;
  max-width:none !important;
  height: 30px;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 0 10px;
  box-sizing: border-box;
}
.navbar-search >>> .search-btn {
  color: white;
  width: 80px; /* 目标宽度，可调整 */
  height: 100%;
  flex-shrink: 0; /* 禁止按钮被压缩 */
  background-color:#ffffff; /* 加个背景色更明显 */
}

.user-icon {
 width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 45px;
}
.icon-wrapper i {
  font-size: 24px;
  color: #333;
  cursor: pointer;
}

.icon-wrapper {
  height: 35px;
  display: flex;
  align-items: center;
}

.username-display {
  font-size: 16px;
  color: #333;
  margin-left: 10px;
  border-bottom: 1px solid #f0f0f0f0;
}

.dropdown-content {
  display: flex;
  flex-direction: column;  /* 垂直排列 */
  gap: 8px;               /* 增加间距 */
  padding: 10px 16px;     /* 调整内边距 */
}
.dropdown-content .username {
  font-weight: bold;
  color: #333;
  white-space: nowrap;    /* 防止换行 */
}
/* 退出登录样式 */
.dropdown-content .logout-text {
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 5px;              /* 图标和文字间距 */
}
.logo-img{
  margin-bottom: 5px;
  margin-top: 10px;
}
.logo-title{
  margin-left: 5px;
  padding-top: 8px;
}
</style>
