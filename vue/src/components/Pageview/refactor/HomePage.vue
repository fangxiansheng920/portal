<template>
  <!-- 模板内容保持不变 -->
  <el-container>
    <el-header class="row-flex-center no-padding ">
      <el-col :span="23" class=" search-banner">
        <el-row :gutter="20" class=" row-flex-center">
          <el-col :span="12" style="width: auto">
            <div >
              <TagsTab :items="classList"  @active-changed="handleClassChange" :active-index="activeIndex"/>
            </div>

          </el-col>
          <el-col :span="3">
            <el-select v-model="sortOptions.isCollect"
                       placeholder="Select"
                       @change = "handleCollectFilterChange"
            >
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-col>
          <el-col :span="9"  >
            <SearchInput   @search="handleSearch" @clear="handleClear" @refresh="handleReset" />
          </el-col>

        </el-row>
      </el-col>
    </el-header>
    <el-main   class="no-padding no-overflow ">
      <el-scrollbar class="content-scrollbar ">
        <el-row class="row-flex-center" v-loading="isLoading">
          <el-col :span="23" >
            <div v-if="!containerPagerShow">
              <!-- 分类站点 -->
              <SiteListModule
              v-for="(items) in categorizedSiteLists "
              :moduleName=" items.tag.name"
              :siteList="items.portals"
              @update="refreshCurrentData"
              @onload="getAllTypeSiteList"
              :key="items.tag.id" />
              <el-empty
                v-if="searchKeyword && categorizedSiteLists.length === 0 && !isLoading"
                description="没有找到匹配的站点"
              >
                <el-button type="primary" @click="handleClear">
                  <el-icon><Refresh /></el-icon>清除搜索条件
                </el-button>
              </el-empty>
            </div>
            <div v-else>
              <SiteListModule
              :site-list="siteList"
              :module-name="siteListModuleTitle"
              @update="refreshCurrentData"
              @onload="getSiteListByOneTag"
               :key="activeIndex"
              />
            </div>

          </el-col>
        </el-row>
      </el-scrollbar>
    </el-main>
    <el-footer class="row-flex-center  " v-if="containerPagerShow">
      <el-row  class="row-flex-center " >
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
    </el-footer>

  </el-container>
</template>

<script setup>
import {onMounted, ref, watch} from "vue";
import {
  getAllPortalByTags,
  getPortalsByTag,  // 改为使用单个标签接口
} from "@/api/siteData";
import SiteListModule from "@/components/Pageview/refactor/SiteListModule.vue";
import {getAllTag} from "@/api/siteManagement";
import TagsTab from "@/components/AppButton/TagsTab.vue";
import SearchInput from "@/components/AppButton/SearchInput.vue";
import {Refresh} from "@element-plus/icons-vue"

const activeIndex = ref(0);
const pagination = ref({
  currentPage: 1,
  pageSize: 20,
  total: 0,
});
const siteList = ref([]);
const categorizedSiteLists = ref([]);
const classList = ref([{name:"全部", id: null}]);
const containerPagerShow = ref(false);
const siteListModuleTitle = ref('');
const searchKeyword = ref('');
const options = [
  {value: '2', label: '全部'},
  {value: '1', label: '已收藏'},
  {value: '0', label: '未收藏'},
];
const sortOptions = ref({isCollect: '2'});
const isLoading = ref(false);
// 初始化分页
function initPagination() {
  pagination.value = {
    currentPage: 1,
    pageSize: 20,
    total: 0,
  };
}

// 获取所有分类
function getAllClassList() {
 getAllTag().then(res => {
   classList.value.push(...res.data);
 })
}

// 获取所有分类站点列表
function getAllTypeSiteList() {
  isLoading.value = true;
  const params = {
    num: pagination.value.pageSize,
    keyword: searchKeyword.value || '',
    isCollect: Number(sortOptions.value.isCollect)
  };

  getAllPortalByTags(params).then(res => {
    if (res.code === 200 && res.status) {
      categorizedSiteLists.value = res.data || [];
      pagination.value.total = categorizedSiteLists.value.reduce(
        (total, item) => total + (item.portalTotal || 0), 0
      );
    } else {
      categorizedSiteLists.value = [];
    }
  }).catch(err => {
    console.error('获取站点列表失败:', err);
    categorizedSiteLists.value = [];
  }).finally(() => {
    isLoading.value = false;
  });
}

// 修改：使用单个标签接口并修正参数
function getSiteListByOneTag() {
  const currentTag = classList.value[activeIndex.value];
  if (!currentTag?.id) return;

  isLoading.value = true;
  const params = {
    tagId: Number(currentTag.id),
    pageNum: pagination.value.currentPage,
    pageSize: pagination.value.pageSize,
    keyword: searchKeyword.value || '',
    isCollect: Number(sortOptions.value.isCollect)
  };

  getPortalsByTag(params).then(res => {
    if (res.code === 200 && res.status) {
      siteList.value = res.data.list || [];
      pagination.value.total = res.data.total || 0;
      pagination.value.currentPage = res.data.pageNum || 1;
      pagination.value.pageSize = res.data.pageSize || 20;

      siteListModuleTitle.value = searchKeyword.value
        ? `🔍${classList.value[activeIndex.value].name} - 搜索"${searchKeyword.value}"`
        : classList.value[activeIndex.value].name;
    } else {
      siteList.value = [];
      pagination.value.total = 0;
    }
  }).catch(err => {
    console.error('获取分类站点失败:', err);
    siteList.value = [];
    pagination.value.total = 0;
  }).finally(() => {
    isLoading.value = false;
  });
}

function refreshCurrentData() {
  if (searchKeyword.value) {
    // 场景1：有搜索关键词（无论是全部标签还是单个标签下的搜索）
    if (activeIndex.value === 0) {
      // 全部标签下的搜索结果
      getAllTypeSiteList();
    } else {
      // 单个标签下的搜索结果
      getSiteListByOneTag();
    }
  } else {
    // 场景2：无搜索关键词（纯标签筛选）
    if (containerPagerShow.value) {
      // 单个标签筛选结果
      getSiteListByOneTag();
    } else {
      // 全部标签结果
      getAllTypeSiteList();
    }
  }
}

// 收藏筛选变化处理
function handleCollectFilterChange() {
  if (containerPagerShow.value) {
    getSiteListByOneTag();
  } else {
    getAllTypeSiteList();
  }
}

// 分类切换事件处理
const handleClassChange = (index) => {
  activeIndex.value = index;

  if(index === 0){
    getAllTypeSiteList();
    containerPagerShow.value = false;
  } else{
    initPagination();
    getSiteListByOneTag();
    containerPagerShow.value = true;
  }
};

// 关键词搜索处理
function handleSearch(keyword) {
  const trimmedKeyword = keyword.trim();
  if (trimmedKeyword === '') {
    return handleClear();
  }

  searchKeyword.value = trimmedKeyword;
  initPagination();

  if (activeIndex.value === 0) {
    containerPagerShow.value = false;
    getAllTypeSiteList();
  } else {
    containerPagerShow.value = true;
    getSiteListByOneTag();
  }
}
// 清除搜索结果
function handleClear() {
  searchKeyword.value = '';
  initPagination();
  // activeIndex.value = 0;
  // containerPagerShow.value = false;
  // siteListModuleTitle.value = '';
  // getAllTypeSiteList();
 handleCollectFilterChange() ;

}
function handleReset() {
  searchKeyword.value = '';
  initPagination();
  activeIndex.value = 0;
  containerPagerShow.value = false;
  siteListModuleTitle.value = '';
  sortOptions.value.isCollect = '2';
  getAllTypeSiteList();
}
function handlePageChange(page) {
  pagination.value.currentPage = page;
  getSiteListByOneTag();
}

function handleSizeChange(size) {
  pagination.value.pageSize = size;
  getSiteListByOneTag();
}

// 新增：监听搜索关键词变化
watch(searchKeyword, (newVal, oldVal) => {
  if (newVal !== oldVal && newVal !== '' && activeIndex.value === 0) {
    getAllTypeSiteList();
  }
});

onMounted(() => {
  getAllClassList();
  getAllTypeSiteList();
});
</script>

<style scoped>
@import '@/assets/styles/common.css';
.search-banner{
  margin-bottom: 10px;
  margin-top: 10px;
  margin-left: 15px;
}
.content-scrollbar{
  height: calc(100vh - 200px);
  min-width: 80vw;
  width: calc(99vw);
  margin-left: 10px;
}

</style>
