<template>
  <div>
    <el-card  shadow="hover"  class="info_card" @click="addSite">
      <el-row class="card_row">
        <!--网站logo       -->
        <div >
          <el-avatar :icon="Plus" class="add_icon"/>
        </div>
        <!--网站简要信息       -->
        <div class="info_center ">
          <div class="info_title">
            新建站点
          </div>
        </div>
      </el-row>
    </el-card>
  </div>
</template>
<script setup>
import {defineEmits} from 'vue'
import {Plus} from '@element-plus/icons-vue'
const emit = defineEmits(['addSite']);
function addSite(){
  emit('addSite');
}
</script>
<style scoped>


.info_card{
  margin: 10px;
  cursor: pointer;
  position: relative;
  border-radius: 10px;
}
.add_icon{
  width: 60px;
  height: 60px;
  font-size: 30px;
  font-weight: bolder;
}

.el-card{
  border: rgb(198, 226, 255) 2px  dashed !important;
}
.el-card:hover{
  border: #409EFF 2px  dashed !important;
  background-color: rgb(236, 245, 255);
  .add_icon{
    background-color: #409EFF;
  }
  .info_title{
    color: #409EFF;
  }
}


.card_row{
  height: 60px;
  min-width: 260px;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.info_center{
  width: 200px;
  overflow: hidden;
  display: flex;
  margin-left: 30px;
  border-radius: 10px;
}
.info_title:hover{
  color: #409EFF;
}
.info_title{
  font-size: 16px;
  font-weight: bolder;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}





</style>
