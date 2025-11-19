<template>
  <div >
    <el-container >
      <!--左边侧边栏-->
      <el-aside class="aside"  >
        <el-menu
            :default-active="activeIndex"
            class="el-menu-vertical"
            @select="handleSelect"
        >
          <el-menu-item index="我的站点">
            <el-icon><Location /></el-icon>
            <template #title>我的站点</template>
          </el-menu-item>
          <el-menu-item index="回收站">
            <el-icon><Box /></el-icon>
            <template #title>回收站</template>
          </el-menu-item>

          <el-menu-item v-if="role===1" index="标签管理">
            <el-icon><PriceTag /></el-icon>
            <template #title>标签管理</template>
          </el-menu-item>

        </el-menu>
      </el-aside>

      <!--右边主内容-->
      <el-main class="no-padding no-overflow">
        <el-scrollbar class="admin-scrollbar">
          <!-- <el-row   justify="start" style="opacity: 0">
            <el-button type="primary" @click="openAddForm"><el-icon><Plus /></el-icon>添加站点</el-button>
          </el-row> -->
          <el-space v-if="pagination.total>0" wrap class="center-space">
            <AddCard  v-if="activeIndex==='我的站点'" @add-site="openAddForm"/>
            <template v-if="activeIndex==='回收站'">

            <InfoCard
            v-for="item in siteList"
            :site="item"
            :key="item.id"
            @update="onLoad"
            @edit="editSite"
            :is-admin="true"
            :is-in-recycle="true"
            />
            </template>
            <template v-else>
              <InfoCard
            v-for="item in siteList"
            :site="item"
            :key="item.id"
            @update="onLoad"
            @edit="editSite"
            :is-admin="true"
            />
            </template>

          </el-space>

          <template v-if="activeIndex==='标签管理'" >
              <template v-if="tags.length>0" >
                <div class="tag-container " >
                  <el-row>
                    <el-button class="tag-add" type="primary" @click="openAddTagForm('add')">
                      <el-icon ><Plus  /></el-icon>
                    </el-button>
                  </el-row>
                  <el-space wrap class="center-space">
                    <TagCard
                        v-for="tag in tags"
                        :key="tag.id"
                        :tag="tag"
                        @edit="openEditTagForm('edit',tag)"
                        @delete="handleDeleteTag(tag.id)"
                    />
                  </el-space>

                </div>
              </template>
              <el-empty v-else description="还没有标签"  class=" admin-scrollbar">
                <el-button type="primary" @click="openAddTagForm('add',{})">
                <el-icon><Plus/></el-icon>添加第一个标签
                </el-button>
              </el-empty>

            </template>

          <el-row justify="center" class="pagination_row" v-if="pagination.total>0">
            <el-pagination
                background
                layout="total,prev, pager, next, sizes"
                :total="pagination.total"
                :page-size="pagination.pageSize"
                v-model:current-page="pagination.currentPage"
                @current-change="handlePageChange"
                @size-change="handleSizeChange"
              />
          </el-row>
          <el-empty v-if="activeIndex==='我的站点'&&pagination.total===0" description="还没有站点"  >
            <el-button type="primary" @click="openAddForm"><el-icon><Plus /></el-icon>添加第一个站点</el-button>
          </el-empty>
          <el-empty v-if="pagination.total===0 && activeIndex==='回收站'" description="回收站空空的"></el-empty>
          <!-- <el-empty v-if="activeIndex!=='我的站点' && pagination.total===0 " /> -->

        </el-scrollbar>
      </el-main>
    </el-container>
    <!-- 使用抽屉组件实现表单填写 新建/编辑公用 -->
    <SiteDrawer
        :visible="drawerVisible"
        :mode="drawerMode"
        :isOwner="user.id===currentSiteData.userId"
        :siteData="currentSiteData"
        @close="closeDrawer"
        @submit="onLoad"
    />

    <TagDrawer
    :visible="TagDrawerVisible"
    :mode="TagDrawerMode"
    :tag-data="currentTagData"
    @submit="handleSubmit"
    @cancel="closeTagDrawer"
    @close="TagDrawerVisible = false"
    />

  </div>

</template>
<script setup>
import {
  Plus,
  Location,
  Box,
  PriceTag,
} from '@element-plus/icons-vue'
import {onMounted, ref, computed, onUnmounted} from "vue";
import InfoCard from "@/components/SiteCard/InfoCard.vue";
import
{
    getMyCreateSites,
    getSharedSites,
    getDeletedPortal,
    getAllTag,
    createTag,
    modifyTag,
    deleteTag,
} from "@/api/siteData";
import TagCard from "@/components/TagForm/TagCard.vue"
import SiteDrawer from "@/components/SiteForm/SiteDrawer.vue";
import TagDrawer from '@/components/TagForm/TagDrawer.vue';
import {getUserInfo} from "@/utils/auth";
import AddCard from "@/components/SiteForm/AddCard.vue";
import { ElMessage,ElMessageBox } from 'element-plus';
import { useUserStore } from '@/store/user.js'
const activeIndex = ref("我的站点");
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
});
const siteList = ref([]);
const user =getUserInfo();
const userStore = ref(null)
const role = computed(() => userStore.value?.info?.role || 0);
onMounted(() => {
  userStore.value = useUserStore() // 确保 Pinia 已激活
  // console.log('userStore 完整数据:', userStore.value.info)
  // console.log('当前 role 值:', userStore.value.info.role)
})

// 站点抽屉
const drawerVisible = ref(false);
const drawerMode = ref('');
const currentSiteData = ref({});


//标签抽屉
// 在脚本部分添加标签抽屉相关状态
const TagDrawerVisible = ref(false); // 控制标签抽屉显示/隐藏
const TagDrawerMode = ref('add'); // 标签抽屉模式：'add' 或 'edit'
const currentTagData = ref({}); // 当前编辑的标签数据

// 打开标签抽屉（新增/编辑）
const openAddTagForm = (mode, tag = {}) => {
  TagDrawerMode.value = mode;
  currentTagData.value = mode === 'edit' ? { ...tag } : {}; // 编辑时传递数据
  TagDrawerVisible.value = true;
};

// 修复编辑标签的方法名（与模板保持一致）
const openEditTagForm = (mode, tag) => {
  // console.log('编辑的标签完整数据：', tag);
  TagDrawerMode.value = 'edit'; // 显式设置为edit
  currentTagData.value = { ...tag }; // 深拷贝，避免引用问题
  TagDrawerVisible.value = true;
};


// 父组件中，编辑提交前添加校验
const handleSubmit = async (formData) => {
  try {
    // 编辑模式下，检查名称是否与其他标签冲突
    if (TagDrawerMode.value === 'edit') {
      // 过滤掉当前标签，检查剩余标签中是否有同名
      const hasDuplicate = tags.value.some(tag => {
        return tag.id !== formData.id && tag.tagName === formData.tagName;
      });

      if (hasDuplicate) {
        // 提示用户名称已存在
      ElMessage.error({
      message:'标签名已存在，请修改',
      duration:700
    })
        return; // 阻止提交
      }
    }

    // 继续提交请求...
    if (TagDrawerMode.value === 'add') {
      await createTag(formData);
      ElMessage.success({
      message:'新增标签成功！',
      duration:200
    })
    } else {
      await modifyTag(formData);
      ElMessage.success({
      message:'修改标签成功',
      duration:700
    })
    }
    fetchAllTags();
    closeTagDrawer();
  } catch (error) {
    // console.error('标签操作失败：', error);
  }
};

// 关闭标签抽屉
const closeTagDrawer = () => {
  TagDrawerVisible.value = false;
  currentTagData.value = {}; // 重置数据，避免下次打开残留
};

// 处理标签删除
const handleDeleteTag = async (tagId) => {
  try {
    // 先显示二次确认弹窗
    await ElMessageBox.confirm(
      '确定要删除这个标签吗？',
      '删除确认',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning' // 显示警告图标，增强提示性
      }
    );

    // 用户点击确认后，才执行删除接口调用
    await deleteTag({ tagId }); // 注意参数格式与后端DTO匹配（如{tagId}或{id}）

    // 删除成功提示
    ElMessage.success({
      message: '删除标签成功！',
      duration: 700
    });

    // 刷新标签列表
    fetchAllTags();

  } catch (error) {
    // console.log
  }
};



//标签相关
const tags=ref([]);

//获取所有的标签
function fetchAllTags(){
  getAllTag().then(res=>{
    // 将 name 转换为 tagName，统一字段名
    tags.value = (res.data || []).map(tag => ({
      ...tag,
      tagName: tag.name, // 关键：新增 tagName 字段，值为 name
    }));
  }).catch(error=>{
    console.error('加载标签失败',error);
  });
}
//新增标签

const handlePageChange = (newPage) => {
  // console.log('分页事件触发！新页码:', newPage);
  pagination.value.currentPage = newPage; // 确保双向绑定生效
  onLoad(); // 重新加载数据
};

// 分页初始化
function initPagination(){
      pagination.value.currentPage = 1;
      pagination.value.pageSize = 10;
      pagination.value.total = 0;
}
// 菜单选择回调
function handleSelect(selectIndex){
  closeDrawer();
  closeTagDrawer();
  activeIndex.value = selectIndex;
  initPagination();
  onLoad();
}
const handleSizeChange = (newSize) => {
  // console.log('切换每页条数:', newSize);
  pagination.value.pageSize = newSize;
  pagination.value.currentPage = 1; // 重置为第1页
  onLoad(); // 重新请求数据
};
function openAddForm(){
  drawerMode.value = 'add';
  currentSiteData.value = {};
  drawerVisible.value = true;
}
function editSite(site){
  drawerMode.value = 'edit';
  currentSiteData.value = site;
  drawerVisible.value = true;
}
//
function getMySites(){
  const params = {
    pageCurrent : pagination.value.currentPage,
    pageSize : pagination.value.pageSize
  }
  getMyCreateSites(params).then(res => {
    siteList.value = res.data.list||[];
    pagination.value.total = res.data.total;
    pagination.value.currentPage = res.data.pageNum;
    pagination.value.pageSize = res.data.pageSize;
  });
}
function getPublicSites(){
  const params = {
    pageCurrent : pagination.value.currentPage,
    pageSize : pagination.value.pageSize
  }
  getSharedSites(params).then(res => {
    siteList.value = res.data.list||[];
    pagination.value.total = res.data.total;
    pagination.value.currentPage = res.data.pageNum;
    pagination.value.pageSize = res.data.pageSize;
  });
}
function getDeletedSites(){
    const params = {
    pageCurrent : pagination.value.currentPage,
    pageSize : pagination.value.pageSize
  }
    getDeletedPortal(params).then(res => {
    siteList.value = res.data.list||[];
    pagination.value.total = res.data.total;
    pagination.value.currentPage = res.data.pageNum;
    pagination.value.pageSize = res.data.pageSize;
  });
}
function onLoad(){
  switch (activeIndex.value){
    case "我的站点":
      getMySites();
      break;
    case "共享站点":
      getPublicSites();
      break;
    case "回收站":
       getDeletedSites();
       break;
    case '标签管理':
      fetchAllTags();
      break;
    default:
      break;
  }
}
const closeDrawer = () => {
  drawerVisible.value = false;
  // console.log('抽屉关闭后状态:', drawerVisible.value);
};


onMounted(() => {
  onLoad();
});
onUnmounted(() => {
  // 组件销毁时执行清理操作
  closeDrawer();
});
</script>
<style scoped>
@import "@/assets/styles/common.css";
 .admin-scrollbar{
   height: calc(100vh - 120px);
   min-width: 100%;
   width: calc(100vw - 258px);

 }
 .center-space{
   display: flex;
   flex-wrap: wrap;
   justify-content: start;
   align-content: start;
   min-height: 80vh;
   align-items: start;
 }
 .el-menu-vertical{
   height: 99%;
   border-right: 1px solid #d9d9d9;
 }
 .aside{
   max-width: 200px;
   padding-top: 10px;
 }

 .tag-container{
   display: flex;
   flex-wrap: wrap;
   justify-content: start;
   align-content: start;
   gap:20px;
   vertical-align:baseline;
   padding: 20px;
 }
.tag-add{
  min-height:54px
}
</style>
