<template>
  <div v-show="props.visible" class="drawer">
    <SiteForm
      :mode="props.mode"
      :isOwner="props.isOwner"
      :initialData="props.siteData"
      @submit="handleSubmit"
      @cancel="closeDrawer"
    />
  </div>     
</template>

<script setup>
import { defineProps, defineEmits,watch} from 'vue';
import SiteForm from './SiteForm.vue';

// 定义 props
const props = defineProps({
  visible: Boolean,
  mode: String,
  isOwner: Boolean,
  siteData: Object
});



// 定义 emits
const emit = defineEmits(['close', 'submit']);

watch(
  () => props.siteData,
  () => {
    // console.log('siteData changed:', newData);
  },
  { immediate: true }
)


// 提交表单处理函数
function handleSubmit(data) {
  emit('submit', data);
  closeDrawer();
}
// console.log('抽屉组件接收到的props:', props.siteData);
// 关闭抽屉处理函数
function closeDrawer() {
  emit('close');
  // console.log('提交后抽屉状态:');
}
</script>

<style scoped>
.drawer {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 10;
}
</style>