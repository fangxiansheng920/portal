<template>
    <el-container class="no-padding">
      <!--主页导航栏组件-->
      <el-header class="pageHeader">
        <NavigationBar />
      </el-header>
      <!--动态改变主题内容-->
      <main class="content-area no-padding">
        <slot></slot>
      </main>
    </el-container>
</template>


<script setup>
import NavigationBar from '@/components/NavigationBar.vue';
</script>



<style>

.pageHeader {
  width: 100%;
}
.content-area {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
