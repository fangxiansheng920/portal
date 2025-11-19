<template>
  <div class="site-card" >
    <div class="title-section">
      <h3 class="site-title" @click="handleView" >{{ title }}</h3>

      <template v-if="publicFlag ===1">
        <el-tag type="primary" round>Public</el-tag>
      </template>

      <template v-else-if="publicFlag === 0">
        <el-tag type="warning" round>Private</el-tag>
      </template>
      <span class="creator">创建人：{{ createBy }}</span>
    </div>
    <p class="site-description">{{ truncatedDescription }}</p>
    <div class="site-meta">

      <template v-if="showTime">
        <FormattedDate  :time="createTime" />
      </template>

      <template v-if="showGo">
        <Golink @click="onGoClick" />
      </template>



    </div>
    <div class="site-icons">
      <div class="overlay-container" v-if="showMenu">
        <el-dropdown placement="bottom" >
          <el-button link > <el-icon><MoreFilled /></el-icon></el-button>
          <template #dropdown >
            <el-dropdown-menu>
              <el-dropdown-item @click="editSite">编辑</el-dropdown-item>
              <el-dropdown-item v-if="canShowDelete"  @click="handleDelete">删除</el-dropdown-item>
              <el-dropdown-item @click="handleCollectToggle">{{ collectFlag? '取消收藏':'收藏' }}</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

        <FavoriteButton v-if="showFavorite"
        :collectFlag="collectFlag"
        :portalId="id"
        @collectChange="handleCollectChange"/>
        <DeleteIcon v-if="showDelete" :isVisible="isVisible" @click="handleDelete"/>
      </div>
      <template v-if="showEdit">
        <EditButton @click="editSite" class="edit-button" />
      </template>
    </div>
</template>

<script setup>
import {computed, defineProps, defineEmits} from 'vue'
import FavoriteButton from '@/components/IconButton/FavoriteButton.vue'
import Golink from '@/components/IconButton/GoLink.vue'
import FormattedDate  from '@/components/IconButton/FormattedDate.vue'
import EditButton from '@/components/IconButton/EditPen.vue'
import  DeleteIcon  from '@/components/IconButton/DeleteIcon.vue'
import { MoreFilled } from '@element-plus/icons-vue'
// import { switchCollectStatus } from '@/api/siteManagement.js'

const props = defineProps({
  id: [String, Number],
  title: String,
  description: String,
  createBy: String,
  createTime: String,
  status: {type:Number,default:1},  //1为public，0为privata
  publicFlag:{type:Number,default:1}, // 1表示公开修改权，0表示私有修改权
  collectFlag: Boolean,
  isVisible: Boolean, // 是否可见
  showFavorite: Boolean, // 是否显示爱心收藏组件
  showGo: Boolean, // 是否显示go符号组件
  showTime: Boolean, // 是否显示时间组件
  showEdit: Boolean, // 是否显示编辑组件
  showDelete: Boolean, // 是否显示删除组件
  showMenu:Boolean,
  isSharedSite: { type: Boolean, default: false }
})

const emit = defineEmits([ 'click', 'go', 'edit','delete','view','collectChange','notifyFavoriteToggle'])

const truncatedDescription = computed(() => {
  return props.description.length > 30
    ? props.description.slice(0, 30) + '...'
    : props.description
})

function onGoClick(event){
  event.stopPropagation();
  emit('go',props.id)
}

function editSite() {
  emit('edit', props.id)
}

function handleDelete() {
  emit('delete', props.id);
}
function handleView() {
  emit('view', props.id);
}

// 获取当前登录用户的 userName（从 localStorage 的 userInfo 中读取）
const currentUserName = computed(() => {
  const userInfoStr = localStorage.getItem('userInfo');
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr);
      return userInfo.userName || ''; // 匹配 userName 字段
    } catch (error) {
      console.error('解析用户信息失败:', error);
      return '';
    }
  }
  return '';
});

// 判断当前用户是否为站点作者
const isAuthor = computed(() => {
  return currentUserName.value === props.createBy;
});

// 删除选项是否显示的逻辑
const canShowDelete = computed(() => {
  if (props.isSharedSite) {
    // 共享站点中：删除选项不显示
    return false;
  } else {
    // 我的站点中：作者且公开站点显示删除
    return isAuthor.value;
  }
});


const handleCollectChange = (payload)=>{
  emit('collectChange',payload)
}

const handleCollectToggle = () => {
  // 触发父组件的弹窗逻辑（与原来爱心按钮的逻辑一致）
  emit('notifyFavoriteToggle', props.id);
};

</script>

<style scoped>
.site-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  cursor: pointer;
  transition: transform 0.2s;
  padding-right: 2.5rem; /* 为收藏按钮预留空间 */
  width:70%;
  height: 180px;
}

.site-card:hover {
  transform: translateY(-2px);
}

.title-section {
  margin-bottom: 0.5rem;
  text-align: center;
}

.site-title {
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 0.2rem 0;
  padding-right: 1rem;
}

.creator {
  display: block;
  font-size: 12px;
  color: #999;
}

.site-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 0.5rem;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
}

.site-meta {
  position: absolute;
  bottom: 1rem;
  left: 1rem;
  right: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.go-link{
  transition: all 0.2s ease;
}

.go-link:hover {
  color: #1890ff;
  transform: translateX(2px);
}

.site-icons {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  z-index:1;
}

.overlay-container {
  position: relative;
  width: 24px; /* 调整为图标的宽度 */
  height: 24px; /* 调整为图标的高度 */
}

.overlay-container > * {
  position: absolute;
  top: 0;
  left: 0;
}

.edit-button {
  margin-top: 0.1rem;
}
</style>
