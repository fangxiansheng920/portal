<template>
    <el-container >
      <el-header class="row-flex-center">
        <el-row  class="search-banner row-flex-center">
          <el-col :span="20" >
            <SearchInput  @search="handleSearch" @clear="handleRefresh" @refresh="handleRefresh"/>
          </el-col>
        </el-row>
      </el-header>
      <el-main class="no-padding no-overflow">
        <el-scrollbar class="scrollbar ">
          <el-row class="row-flex-center" v-loading="pageLoading">
            <el-col :span="23">
              <!-- 收藏站点 -->
              <div>
                <div  v-if="siteList.length >0" class="site-list">
                  <InfoCard v-for="(site,index) in siteList"
                            :key="index"
                            :site="site"
                            :top-action="true"
                            @update="getCollectSiteList"
                            @editNote="editNode(site)"
                            @topUpdate="topUpdate"
                  />
                </div>
                <el-card v-else class="empty-card">
                  <el-empty description="暂无收藏站点" >
                    <h3>收藏您常用的站点，方便快速访问</h3>
                    <el-button type="primary" @click="toAllSite"><el-icon><Plus /></el-icon>添加收藏站点</el-button>
                  </el-empty>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </el-scrollbar>
      </el-main>
      <el-footer class="row-flex-center no-padding">
        <el-row justify="center" class="row-flex-center">
        <el-pagination
            background
            layout="total,prev, pager, next, sizes"
            :total="pagination.total"
            :page-size="pageSize"
            v-model:current-page="pagination.currentPage"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
        />
      </el-row>
      </el-footer>
      <NoteEditModal  :visible="noteEditVisible" :site="noteEdit"   @close="noteModalClose"    />

    </el-container>
</template>

<script setup>
import {onMounted, ref} from "vue";
import { getMyCollectSites} from "@/api/siteData";
import {getAllTag, searchByKeyword} from "@/api/siteManagement";
import SearchInput from "@/components/AppButton/SearchInput.vue";
import InfoCard from "@/components/SiteCard/InfoCard.vue";
import {useRouter} from "vue-router";
import {Plus} from "@element-plus/icons-vue";
import {PAGE_MY_FAVORITES} from "@/constants/constant";
import NoteEditModal from "@/components/SiteCard/NoteEditModal.vue";
const  router = useRouter();
const  pageSize= ref(20);
const  pagination = ref({
  pageCurrent: 1,
  pageSize: pageSize,
  total: 0,
});
const  siteList = ref([]);   // 站点列表
const  classList = ref([]); // 所有分类
const  isBlinking = ref(false); // 闪烁边框flag
const  noteEditVisible = ref(false);
const  pageLoading = ref(false);
const  noteEdit=ref({
  portalId: 0,
  collectRemark: "",
});
// 初始化分页参数
function initPagination() {
  pagination.value = {
    pageCurrent: 1,
    pageSize: pageSize,
    total: 0,
  };
}
// 获取收藏站点列表
async function getCollectSiteList() {
  pageLoading.value = true;
  await getMyCollectSites(pagination.value).then(res => {
    pageLoading.value = false;
    siteList.value = res.data.list;
    pagination.value.currentPage = res.data.pageNum;
    pagination.value.total = res.data.total;
    pagination.value.pageSize = res.data.pageSize;
  });
}
// 跳转到所有站点页面
function toAllSite(){
  router.push("/all")
}
function handleRefresh() {
  getCollectSiteList();
}
// 关键字搜索站点
function handleSearch(params) {
  initPagination();
  var searchParams = {
    keyword: params,
    pageCurrent: pagination.value.pageCurrent,
    pageSize: pagination.value.pageSize,
    pageIndex: PAGE_MY_FAVORITES,
    // sort: params.sort,
  };
  searchByKeyword(searchParams).then(res => {

    siteList.value = res.data.list;
    pagination.value.total = res.data.total;
    pagination.value.currentPage = res.data.pageNum;
    pagination.value.pageSize = res.data.pageSize;
  });

}
// 分页切换
function handlePageChange(val) {
  pagination.value.pageCurrent=val;
  getCollectSiteList();
}
// 切换每页显示条数
function handleSizeChange(val) {
  pageSize.value = val;
  getCollectSiteList();
}
// 获取所有分类列表
function getAllClassList() {
  getAllTag().then(res => {
    classList.value = res.data;
  })
}
// 置顶闪烁
function topBlinking(){
  isBlinking.value = true;
  setTimeout(() => {
    isBlinking.value = false;
  }, 2000);
}
function topUpdate(site){
  getCollectSiteList().then(() => {
    if(site.isTop){
      topBlinking();
    }
  })
}
function editNode(site){
  noteEdit.value = site;
  noteEditVisible.value = true;
}
function noteModalClose(){
  getCollectSiteList();
  noteEditVisible.value = false;
  noteEdit.value = {
    portalId: 0,
  }

}

onMounted(() => {
  getCollectSiteList();
  getAllClassList();
});



</script>

<style scoped>
@import '@/assets/styles/common.css';

.scrollbar{
  height: calc(100vh - 200px);
  min-width: 80vw;
  width: calc(98vw);
}
.search-banner{
  margin-bottom: 10px;
  margin-top: 10px;
  width: 100%;
}

.site-list{
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
}
.empty-card{
  width: 100%;
  height: 70vh;
}

</style>
