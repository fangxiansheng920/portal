<template>
  <div
      class="demo-rich-conent"
      style="display: flex; gap: 16px; flex-direction: column"
  >
    <el-row :gutter="20" class=" row-flex-start">
      <el-avatar
          :size="60"
          :src="site.portalLogo"
          style="margin-bottom: 8px"
      />
      <el-space class="note-container">

      </el-space>
    </el-row>
    <div >
      <p
          class="demo-rich-content__name"
          style="margin: 0; font-weight: 500"
      >
       {{ site.title}}
      </p>
      <p
          class="demo-rich-content__mention"
          style="margin: 0; font-size: 12px; color: var(--el-color-info)"
      >
        @{{site.createBy }}
      </p>
    </div>

    <p class="demo-rich-content__desc" style="margin: 0">
     {{site.description }}
    </p>
    <div class="tip-color tip">
      <p class="demo-rich-content__desc info-description " >
        {{ noteContent }}
      </p>
    </div>

  </div>
</template>

<script setup>
import {ref, watch, defineProps, onMounted} from 'vue';
import {getCollectRemark,} from "@/api/siteData";
import {ElMessage} from "element-plus";

const props = defineProps({
  site: {
    type: Object,
    required: true
  },

});



const noteContent = ref("");
const getUserNote = () => {
  getCollectRemark({portalId: props.site.id}).then(res => {
    if(res.code === 200) {
      noteContent.value = res.data;
    }else {
      noteContent.value = '';
      ElMessage.error(res.message);
    }
  })
};
// 监听visible prop变化
watch(() => props.visible, () => {
  getUserNote();
});
onMounted(() => {

});





</script>

<style scoped>
.note-container{
  border: #e74c3c solid 1px;
  margin-left: 10px;
  width: 200px;
}
.info-description{
  margin: 0;
  font-size: 12px;
  color: var(--el-color-info)
}

.tip-color{
  background-color: #ecf5ff;
}

</style>
