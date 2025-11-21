<template>
  <div>
    <el-tooltip
        class="tooltip"
        effect="dark"
        :disabled="!props.site.collectRemark"
        placement="bottom"
    >
      <template #content>
        <div class="tooltip-text">
          {{ props.site.collectRemark }}
        </div>
      </template>
      <el-card shadow="hover" class="info-card"    @mouseleave="detailVisible=false" >
        <!-- 置顶图标 -->
        <div class="toTop-position" v-show="props.site.isTop === IS_TOP">
          <el-tag type="warning" effect="dark" size="small">Top</el-tag>
        </div>
        <div class="toTop-position" v-show="props.isAdmin">
          <el-tag size="small" class="info-tag" type="success" v-if="props.site.publicFlag && props.isAdmin">public</el-tag>
          <el-tag size="small" class="info-tag" v-if="props.isAdmin && props.site.publicFlag === 0" type="warning">private</el-tag>
        </div>
        <el-row class="action-row">
          <template v-if="!props.isInRecycle">
            <el-icon
                v-show="topAction"
                class="action-icon top-icon action-icon_passive"
                :class="{ 'Toped': props.site.isTop === IS_TOP,  }"
                @click="topHandle(props.site.isTop === IS_TOP ? NOT_TOP : IS_TOP)"
                :title="props.site.isTop === IS_TOP ? '取消置顶' : '置顶'"
            >
              <ArrowUp />
            </el-icon>
            <el-icon
                @mouseover="props.site.collectFlag === IS_COLLECTED ? hoverAction = 'collected' : hoverAction = ''"
                @mouseleave="hoverAction = ''"
                class="action-icon star-icon "
                :class="{ 'collection': props.site.collectFlag === IS_COLLECTED, 'action-icon_passive': props.site.collectFlag !== IS_COLLECTED }"
                @click="collectHandle(props.site.collectFlag === IS_COLLECTED ? NOT_COLLECTED : IS_COLLECTED)"
                :title="props.site.collectFlag === IS_COLLECTED ? '取消收藏' : '收藏'"
            >
              <HeartDislike v-if="props.site.collectFlag === IS_COLLECTED && hoverAction === 'collected'" />
              <HeartSharp v-else />
            </el-icon>
            <el-icon
                v-show="topAction"
                title="备注"
                class="action-icon edit-icon action-icon_passive"
                @click="editNote"
            >
              <List />
            </el-icon>
            <el-icon
                title="编辑"
                class="action-icon edit-icon action-icon_passive"
                @click="onEdit"
                v-if="props.isAdmin"
            >
              <Edit />
            </el-icon>
            <el-icon
                title="删除"
                class="action-icon delete-icon action-icon_passive"
                @click="onDel"
                v-if="props.isAdmin"
            >
              <Delete />
            </el-icon>
          </template>
          <template v-else>
            <el-icon
                title="恢复删除"
                class="action-icon undo-icon action-icon_passive"
                @click="onUndo"
            >
              <ArrowUndoOutline />
            </el-icon>
            <el-icon
                title="彻底删除"
                class="action-icon delete-icon action-icon_passive"
                @click="realDel"
                v-if="props.isAdmin"
            >
              <Delete />
            </el-icon>
          </template>
        </el-row>
        <el-row class="card-row row-flex-center ">
          <!-- 网站logo -->
          <!-- 站点详情 -->
          <el-popover
              :visible="detailVisible"
              :width="300"
              trigger="click"
              popper-style="box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 20px;"
          >
            <template #reference>
              <div  ref="logoRef">
                <el-avatar
                    @click="detailVisible=!detailVisible"
                    class="site-logo"
                    v-if="props.site.portalLogo"
                    :src="getFullPortalLogoUrl(props.site.portalLogo)"
                    size="large"
                />
                <el-avatar v-else size="large">404</el-avatar>
              </div>
            </template>
            <template #default>
              <SiteDetailModal :site="props.site"  />
            </template>
          </el-popover>

          <!-- 网站简要信息 -->
          <div class="info-center">
            <div class="info_text">
              <!-- 网站标题 -->
              <div class="info-title"   @click="openSiteGo">{{ props.site.title }}</div>
              <!-- 简介 -->
              <div class="info-description"   >
                {{ props.site.description }}
              </div>
              <div >
                <el-tag size="small" class="info-tag accessType-tag" type="primary" effect="plain"
                        :class="{ 'blinking-border': isBlinking }"
                        v-for="item in accessTypeList" :key="item" @click="openByType(item,$event)">{{ item }}</el-tag>
              </div>
            </div>
          </div>
          <!-- 跳转详情 -->
          <div >
            <el-icon class="go-button" @click="openSiteGo">
              <ArrowRightBold />
            </el-icon>
          </div>
        </el-row>
        <!-- 备注编辑-->
        <el-tooltip
            v-model:visible="qrVisible"
            content="Bottom center"
            placement="bottom"
            trigger="click"
            virtual-triggering
            :virtual-ref="triggerRef"
        >
          <template #content>
            <img :src="props.site.portalQRcode"  width="200" v-if="currentType===ACCESS_TYPE.MINI"/>
            <el-row class="col-flex-start qr-popup" v-else>
              <p class="qr-url-desc">{{codeText}}</p>
              <vue-qr
                  :text="codeText"
                  :size="200"
                  colorDark="#5559FF"
                  colorLight="#ffffff"
              >

              </vue-qr>
              <el-row class="margin-top-15">
                <el-button type="primary" @click="windowOpenAndReload(codeText)"  link >打开网站  <el-icon ><OpenOutline  /></el-icon> </el-button>
              </el-row>

            </el-row>


          </template>
        </el-tooltip>
      </el-card>
    </el-tooltip>
  </div>
</template>
<script setup>
import {defineProps, defineEmits, ref,watch} from 'vue';

const getFullPortalLogoUrl = (portalLogo) => {
  return portalLogo.startsWith('http') ? portalLogo : import.meta.env.VITE_API_BASE_API + 'website/' + portalLogo;
};
import { ArrowRightBold, Delete, Edit,List} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowUndoOutline,ArrowUp,HeartSharp,OpenOutline,HeartDislike } from '@vicons/ionicons5';
import {collectSite, deleteSite, restoreDeletedPortal, collectTop, deletePortalForever} from "@/api/siteManagement";
import { clickPortal } from "@/api/siteData";
import {ACCESS_TYPE } from "@/constants/constant";
import vueQr from 'vue-qr/src/packages/vue-qr.vue' ;
import SiteDetailModal from "@/components/SiteCard/SiteDetailModal.vue";
const codeText = ref('');
const currentType = ref();
const emit = defineEmits(['update', "edit","editNote","topUpdate"]);
const props = defineProps({
  site: Object, // 站点信息
  isAdmin: Boolean, // 是否为管理员
  isInRecycle: Boolean, // 是否在回收站
  topAction: Boolean, // 是否显示置顶操作
});
const hoverAction=ref('');
const IS_TOP = 1;
const NOT_TOP = 0;
const IS_COLLECTED = 1;
const NOT_COLLECTED = 0;
const accessTypeList = ref([]);
const accessTypeUrlList = ref([]);
const pcAccess =ref({});
const qwAccess =ref({});
const wxAccess =ref({});
const logoRef=ref();
const qrVisible = ref(false);
const detailVisible = ref(false);
const position = ref({
  top: 0,
  left: 0,
  bottom: 0,
  right: 0,
});
const triggerRef = ref({
  getBoundingClientRect: () => position.value,
})
// 创建响应式变量控制闪烁状态
const isBlinking = ref(false);

// 切换闪烁状态的方法
const toggleBlink = () => {
  isBlinking.value = !isBlinking.value;
};
function accessTypeLoad(site) {
  if (!site) return; // 若 site 为 null/undefined，直接返回

  // 使用可选链操作符 ?. 避免访问不存在的属性
  accessTypeList.value = site.accessType ? site.accessType.split(',') : [];

  if (site.accessType !== ACCESS_TYPE.MINI) {
    // 处理 portalUrl 可能为 null 的情况
    if (site.portalUrl && !site.portalUrl.includes("toQrCode")) {
      pcAccess.value = {
        accessType: ACCESS_TYPE.PC,
        url: site.portalUrl,
        toQrCode: false
      };
      accessTypeUrlList.value = [pcAccess.value];
    } else {
      // 安全解析 JSON，避免解析失败报错
      try {
        accessTypeUrlList.value = site.portalUrl ? JSON.parse(site.portalUrl) : [];
      } catch (e) {
        console.error('解析 portalUrl 失败:', e);
        accessTypeUrlList.value = []; // 解析失败时重置
      }
      // 遍历前先判断数组有效
      if (Array.isArray(accessTypeUrlList.value)) {
        accessTypeUrlList.value.forEach(item => {
          switch (item.accessType) {
            case ACCESS_TYPE.PC:
              pcAccess.value = item;
              break;
            case ACCESS_TYPE.QIWEI:
              qwAccess.value = item;
              break;
            case ACCESS_TYPE.WECHAT:
              wxAccess.value = item;
              break;
          }
        });
      }
    }
  }
}
// 打开站点
function openSite() {
  clickPortal({ portalId: props.site.id });
  if (props.site.accessType === ACCESS_TYPE.MINI) {
    // 只有小程序一种访问方式->直接展示二维码
    // 展开二维码面板
    const dom = logoRef.value.getBoundingClientRect();
    position.value = DOMRect.fromRect({
      x: dom.x + 30,
      y: dom.y + 60,
    });
    qrVisible.value = true;
  }else if (props.site.accessType === ACCESS_TYPE.PC) {
    windowOpenAndReload(pcAccess.value.url);
    // window.open(pcAccess.value.url);

  }else {
    ElMessage.success('请选择访问方式');
    isBlinking.value = true;
    setTimeout(() => {
      toggleBlink();  // 切换闪烁状态
    }, 700);
  }
}


//直接打开站点
function openSiteGo() {
  clickPortal({ portalId: props.site?.id }); // 用可选链避免 id 不存在报错

  if (!props.site) return; // 若 site 不存在，直接返回

  if (props.site.accessType === ACCESS_TYPE.MINI) {
    // 小程序逻辑...
    openSite();
  } else if (props.site.accessType?.includes(ACCESS_TYPE.PC)) { // 用可选链判断
    // window.open(pcAccess.value?.url);
    windowOpenAndReload(pcAccess.value?.url);

  } else {
    accessTypeUrlList.value.forEach(item => {
      if (item.accessType !== ACCESS_TYPE.MINI) {
        // window.open(item.url);
        windowOpenAndReload(item.url);
      }
    });
  }
  emit('update'); //刷新排序
}
function windowOpenAndReload(url){
  window.open(url);
  setTimeout(() => {
    emit('update')  // 刷新排序，延迟500毫秒执行
  }, 500);
}
//不同的打开方式
function openByType(type, event) {
  currentType.value = type;
  clickPortal({ portalId: props.site?.id }); // 确保 site.id 存在

  if (type === ACCESS_TYPE.MINI) {
    // 计算显示位置
    position.value = DOMRect.fromRect({
      x: event.target.getBoundingClientRect().x + 5,
      y: event.target.getBoundingClientRect().y + 10,
    });
    // 显示二维码（模板中会根据currentType渲染portalQRcode）
    qrVisible.value = true;
    // 可选：如果没有上传二维码，提示用户
    if (!props.site?.portalQRcode) {
      ElMessage.warning('未配置小程序二维码');
    }
    return; // 单独处理后直接返回，不执行其他逻辑
  }

  // 先获取对应的访问方式配置，增加存在性判断
  const accessConfig = {
    [ACCESS_TYPE.PC]: pcAccess.value,
    [ACCESS_TYPE.WECHAT]: wxAccess.value,
    [ACCESS_TYPE.QIWEI]: qwAccess.value,
  }[type];

  // 检查配置是否存在
  if (!accessConfig) {
    ElMessage.warning({
      message: '未配置访问方式',
      duration: 700
    })
    return;
  }

  // 使用可选链操作符访问 url
  codeText.value = accessConfig?.url || '';

  // 处理位置和显示逻辑
  position.value = DOMRect.fromRect({
    x: event.target.getBoundingClientRect().x + 5,
    y: event.target.getBoundingClientRect().y + 10,
  });
  qrVisible.value = true;
}
// 置顶处理
function topHandle(isTop) {
  const params = {
    portalId: props.site.id,
    isTop: isTop ? IS_TOP : NOT_TOP,
  };
  collectTop(params).then((res) => {
    ElMessage.success({
      message:isTop?'置顶成功':res.message,
      duration:500//毫秒
    });
    emit('topUpdate',props.site);

  }).catch((error) => {
    ElMessage.error('操作失败: ' + error.message);
  });
}
function collectSiteHandle(collectFlag) {
  const params = {
    doCollect: collectFlag ? '1' : '0',
    portalId: props.site.id,
  };
  collectSite(params).then(() => {
    ElMessage.success({
      message:collectFlag ? '收藏成功':'取消收藏成功',
      duration:500//毫秒
    })
    emit('update');
  }).catch((error) => {
    ElMessage.error('操作失败: ' + error.message);
  });
}

// 收藏处理
function collectHandle(collectFlag) {
  if(collectFlag=== NOT_COLLECTED)
  {
   confirmAction('确定取消收藏此站点?', () => {
     collectSiteHandle(collectFlag);
   });
  }else{
    collectSiteHandle(collectFlag);
  }

}
//编辑备注
function editNote() {
  emit('editNote');
}
// 编辑站点信息
function onEdit() {
  emit('edit', props.site);
}

// 软删除站点信息
function onDel() {
  confirmAction('确定删除此站点?', () => {
    deleteSite(props.site.id).then(() => {
      ElMessage.success({
      message:'删除成功',
      duration:500//毫秒
    })
      emit('update');
    }).catch((error)=>{
      ElMessage.error({
        message:`删除失败：${error.message || '未知错误'}`,
        duration:700
      })
    });
  });
}

//真删除
function realDel(){
  confirmAction('确定彻底删除此站点?',()=>{
    deletePortalForever(props.site.id).then(()=>{
      ElMessage.success({
        message:'彻底删除成功！',
        duration:700
      })
      emit('update');
    }).catch((error)=>{
      ElMessage.error({
        message:`删除失败：${error.message || '未知错误'}`,
        duration:500
      })
    });
  });
}

// 恢复站点信息
function onUndo() {
  confirmAction('确定恢复站点?', () => {
    restoreDeletedPortal(props.site.id).then(() => {
      ElMessage.success({
      message:'恢复成功',
      duration:500//毫秒
    })
      emit('update');
    }).catch((error) => {
      ElMessage.error('恢复失败: ' + error.message);
    });
  });
}
// 通用确认操作
function confirmAction(message, action) {
  return ElMessageBox
      .confirm(message, { textColor: '#c0c4cc', confirmButtonText: '确定', cancelButtonText: '取消' })
      .then(action)
      .catch(() => {
        // catch error
      });
}

watch(
  () => props.site,
  (newVal) => {
    if (newVal) { // 确保 newVal 存在才执行
      accessTypeLoad(newVal);
    } else {
      // 当 site 为 null 时重置数据，避免残留旧值
      accessTypeList.value = [];
      accessTypeUrlList.value = [];
      pcAccess.value = null;
      qwAccess.value = null;
      wxAccess.value = null;
    }
  },
  { immediate: true }
);

</script>
<style scoped>
@import "@/assets/styles/common.css";
.toTop-position{
  margin-top: -25px;
  margin-left: -20px;
}
.collection{
  color: pink;
}
.Toped{
  color:orange;
}
.tip-description{
  max-width: 250px !important;
  overflow-wrap: break-word;/* 同上，兼容性更好 */
}
.info-card{
  transition: margin-top 0.3s ease; /* 平滑过渡效果 */
  margin: 10px;
  cursor: pointer;
  position: relative;
  padding: 0;
  border-radius: 10px;
  background-color: #F7F7F7 !important;
}
.site-logo{
  box-shadow: 2px 5px 5px -2px rgba(64, 158, 255, .6) !important;
}
.info-card:hover{
  box-shadow: 0 8px 8px -2px rgba(64, 158, 255, .6) !important;
  /*   margin-top: -1px; //使用这个浮动效果，鼠标边缘会反复抖动     */
  transform: translateY(-2px);
  .info-title{
    color: #409EFF;
  }
  .go-button{
    color: #409EFF;
  }
  .action-icon{
    opacity: 1;
    z-index: 10;
  }
}
.action-row{
  display: flex;
  justify-content: end;
  align-items: center;
}
.card-row{
  margin-top: -5px;
  height: 60px;
  min-width: 260px;
  width: 100%;
}
.info-center{
  width: 200px;
  display: flex;
  height: 60px;
  margin-left: 10px;
}
.info-title:hover{
  color: #409EFF;
}
.info-title{
  width: 200px;
  overflow: hidden;         /* 超出隐藏 */
  white-space: nowrap;      /* 强制不换行 */
  text-overflow: ellipsis;  /* 结尾用 … 表示省略 */
  font-size: 16px;
  font-weight: bolder;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  outline: none;
}
.info-description{
  width: 200px;
  overflow: hidden;         /* 超出隐藏 */
  white-space: nowrap;      /* 强制不换行 */
  text-overflow: ellipsis;  /* 结尾用 … 表示省略 */
  font-size: 12px;
  color: var(--el-color-info) !important;
}

.go-button{
  margin-left: 5px;
  cursor: pointer;
  color: #cccccc;
}
.go-button:hover{
  color: #409EFF;
}
.info-tag{
  z-index: 10;
  font-size: 12px;
  margin-right: 5px;
}
.accessType-tag:hover{
  background-color: #409EFF;
  color: #fff;
  transition: all 0.3s ease;
}
.accessType-tag{
  cursor: pointer;
  font-size: 12px;
  margin-right: 5px;
}
.qr-popup {
  width: 220px; /* 二维码+文本的总宽度，按需调整 */
  margin: 0 auto; /* 居中，消除左侧空白 */
  padding: 10px;
  box-sizing: border-box;
}
.vue-qr {
  margin: 0 auto;
  display: block;
}
.margin-top-10{
  margin: 0 auto;
}

.action-icon{
  transition: margin-top 0.1s ease; /* 平滑过渡效果 */
  border:  rgba(255, 255, 255, 0.6) 2px solid;
  background-color: rgba(255, 255, 255, 0.6);
  margin: -12px 2px 0 2px;
  border-radius: 20%;
  padding: 2px;
  width: 22px;
  font-size: 18px;
  font-weight: bolder;

}
.action-icon_passive{
  opacity: 0;
  color: rgb(198, 226, 255);
}
.action-icon:hover{
  cursor: pointer;
  font-size: 22px;
  font-weight: 800;

}

.top-icon:hover{
  color: orange;
  border-color: orange;
}
.star-icon:hover{
  color: lightpink;
  border-color: lightpink;
}
.edit-icon:hover{
  color:  #165dff;
  border-color: #165dff;
}
.delete-icon:hover{
  color: red;
  border-color: red;
}
.undo-icon:hover{
  color:gray;
}
/* 闪烁边框动画 */
@keyframes blink {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(64, 158, 255, 0);
    border-color: #e4e7ed;
  }
  50% {
    box-shadow: 0 0 0 5px rgba(255, 215, 0, 0.5);
    border-color: gold;
  }
}
.tip-QrCodeImg{
  min-width: 220px;
}

/* 应用闪烁动画的类 */
.blinking-border {
  animation: blink 1.5s infinite ;
  border: 1px solid #409eff;
}
.site-qrCode-show{
  width: 100px;
  height: 100px;
}
.qr-url-desc{
  font-size: 12px;
  color: var(--el-color-info);
  margin: 0;
  padding: 0;
  width: 200px;
  overflow: hidden;         /* 超出隐藏 */
  white-space: nowrap;      /* 强制不换行 */
  text-overflow: ellipsis;  /* 结尾用 … 表示省略 */
}
</style>
