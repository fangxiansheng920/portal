<template>
  <!-- 弹窗阶段1：选择访问方式 -->
  <div v-if="popupStage === 1" class="modal-overlay" @click.self="hidePopup">
    <div class="modal">
      <button class="close-button" @click="hidePopup">X</button>
      <p>当前访问的站点为：</p>
      <p>请选择访问方式</p>
      <div class="button-group">
      <button class="pcbutton" @click="tryPCAccess">PC(还没做)</button>
      <button class="wxbutton" @click="openWechatView">企业微信</button>
      </div>
    </div>
  </div>

  <!-- 弹窗阶段2：企业微信展示 -->
  <div v-if="popupStage === 2" class="modal-overlay" @click.self="hidePopup">
    <div class="modal">
      <p>企业微信访问</p>
      <div class="image-placeholder">等数据库上传图片图片展示区域</div>
      <button class="return-button" @click="backToChoose">返回</button>
    </div>
  </div>
</template>

<script setup>
import { ref,defineExpose} from 'vue'

const popupStage = ref(0) // 0: 关闭, 1: 选择方式, 2: 企业微信

function showPopup() {
  popupStage.value = 1
}

function hidePopup() {
  popupStage.value = 0
}

function backToChoose() {
  popupStage.value = 1
}

function openWechatView() {
  popupStage.value = 2
}

function tryPCAccess() {
  console.log('PC 访问（暂无效果）')
}

defineExpose({
  showPopup,
  hidePopup
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal {
  position: relative; /* 确保关闭按钮的定位上下文 */
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  min-width: 300px; /* 确保弹窗有足够的宽度 */
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.close-button:hover {
  color: #333;
}

.image-placeholder {
  width: 300px;
  height: 200px;
  background-color: #f0f0f0;
  border: 1px dashed #ccc;
  margin: 10px auto;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}
.button-group{
  display:flex;
  flex-direction: colum;
  gap:10px;
  margin:15px 0;
}

.pcbutton{
  background-color:#007bff;
  color:white;
  border:none;
  padding:10px 20px;
  border-radius:4px;
  cursor:pointer;
}

.wxbutton {
  background-color: #07c160; /* 企业微信绿色 */
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}
</style>