<template>
  <div>
    <div class="module-name" v-if="props.moduleName">
      <el-icon class="title-icon" size="22"><ExtensionPuzzle /></el-icon>{{ props.moduleName }}
      <!-- <el-button link  @click="reloadSiteList">
        <Refresh />
      </el-button> -->
    </div>
    <div class="site-list" v-if="props.siteList.length > 0">
      <InfoCard v-for="site in props.siteList" :key="site.id" :site="site"  @update="handleUpdate"  @onload="reloadSiteList" />
    </div>
    <el-card v-else>
      <el-empty description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup>
import { defineEmits, defineProps } from "vue";
// import { Refresh } from "@element-plus/icons-vue";
import { ExtensionPuzzle } from "@vicons/ionicons5";
import InfoCard from "@/components/SiteCard/InfoCard.vue";
const emit = defineEmits(['onload','update']);
const props = defineProps({
  moduleName: String,
  siteList: Array,
});

function reloadSiteList() {
  emit('onload');
}

function handleUpdate() {
  emit('update');
}
</script>

<style scoped>
.title-icon{
  font-weight: bolder;
  color: #409EFF;
  margin-right: 10px;
}
.module-name {
  display: flex;
  justify-content: start;
  align-items: center;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  padding: 10px 0;
}

.site-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
}
</style>
