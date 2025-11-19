<template>
  <button
    class="favorite-btn"
    :class="{ 'is-collected': collectFlag, 'loading': isLoading }"
    @click="handleToggle"
    :disabled="isLoading"
  >
    <!-- 加载状态 -->
    <el-loading v-if="isLoading" size="14px" />
    <!-- 收藏图标 -->
    <i class="fas fa-heart"  :class="{'collected':collectFlag}"></i>
  </button>
</template>

<script setup>
import { ref,defineEmits,defineProps} from 'vue';
import { ElMessage } from 'element-plus';
import { switchCollectStatus } from '@/api/siteManagement';
import router from "@/router"; // 导入你的接口

const props = defineProps({
  collectFlag: Boolean, // 初始收藏状态
  portalId: [String, Number] // 站点ID（portalId）
});

const emit = defineEmits(['collectChange']); // 触发状态变化事件
const isLoading = ref(false); // 加载状态

// 点击切换收藏状态
const handleToggle = async (event) => {
  event.stopPropagation();
  if (!props.portalId) {
    ElMessage.warning('站点ID不存在');
    return;
  }

  isLoading.value = true;
  try {
    // 调用接口：switchCollectStatus(是否收藏, 站点ID)
    const res = await switchCollectStatus(!props.collectFlag, props.portalId);
    if(res.code===200&& res.status===true){
      emit('collectChange', {
      portalId: props.portalId,
      collectFlag: !props.collectFlag // 切换状态
    });
    // ElMessage.success({
    //   message:!props.collectFlag? '收藏成功':'取消收藏成功',
    //   duration:1000//毫秒
    // })
    }
    else if(res.code===401 || res.status===false){
      ElMessage.error(res.message||'未登录或Token已失效，请重新登录');
      router.push('/login');
    }
  } catch (error) {
    console.error('收藏操作失败:', error);
    ElMessage.error(error.message || '操作失败，请重试');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.favorite-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  color: #999;
  font-size: 18px;
  padding: 4px;
}


.favorite-btn.loading {
  cursor: not-allowed;
  color: #ccc;
}
</style>
