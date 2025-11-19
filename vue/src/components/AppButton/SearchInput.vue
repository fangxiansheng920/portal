<template>
  <div class="container" >
    <el-input
        v-model="keyword"
        clearable
        placeholder="搜索"
        class="search-input"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
        @clear="handleClear"
    >
    </el-input>
    <el-button type="primary" @click="handleSearch"  :icon="Search"  round class="search_button">搜索</el-button>
    <el-tooltip>
      <template #content>
        重置
      </template>
      <el-button type="primary" @click="handleRefresh"  :icon="Refresh"  plain circle class="search_button"></el-button>
    </el-tooltip>

  </div>

</template>
<script setup >
import {ref, defineEmits} from "vue";
import {ElInput} from "element-plus";
import {Search,Refresh} from "@element-plus/icons-vue";
const keyword = ref('');
const emit = defineEmits(['search', 'clear','refresh']);
function handleSearch() {
  emit('search', keyword.value);
}
function handleRefresh() {
  keyword.value = '';
  emit('refresh');
}
function handleClear() {
  emit('clear');
}
</script>
<style scoped>

:deep(.el-input__wrapper) { border-radius: 20px; }
.search-input{
  width: 80%;
  height: 100%;
}
.search-input:hover{
  border: #409EFF;
}

.container{
  display: flex;
  justify-content: center;
  align-items: center;

}
.search_button{
  margin-left: 10px;

}

</style>
