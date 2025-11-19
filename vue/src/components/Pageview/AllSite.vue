<template>
  <div class="viewport-container">
    <div class="homepage-content">
      <!-- 我的收藏 -->
      <div class="module">
        <div class="module-header">
          <SearchBox
            v-model="searchQuery"
            @search="onSearch"
            @clear="clearSearch"
            class="search-bar"
          />
        </div>

        <!-- 新增：加载状态 -->
        <div v-if="loading" class="loading">
          <el-loading-spinner></el-loading-spinner>
          <p>正在加载数据...</p>
        </div>
        <div class="site-container">
          <template v-if="paginatedFavorites.length > 0">
            <SiteCard
              v-for="site in paginatedFavorites"
              :key="site.id"
              :id="site.id"
              :title="site.title"
              :status = "false"
              :publicFlag="false"
              :description="site.description"
              :createBy="site.createBy"
              :createTime="site.createTime"
              :collectFlag="site.collectFlag"
              :showGo="true"
              :showTime="true"
              :showFavorite="true"
              :showMenu="false"
              @notifyFavoriteToggle="handleFavoriteToggle"
              @click="handleClick"
              @go="openGlobalPopup"
              @collectChange="updateCollectStatus"
              @view="handleView(site.id)"
            />
          </template>
          <div v-else-if="isSearching" class="no-results">
            没有找到匹配的站点
          </div>
          <div v-else class="no-results">
          <el-empty description="暂时没有数据" />
          </div>
        </div>
        <!-- 分页控件 -->
        <div class="pagination" v-if="showPagination">
          <Pagination
            :pageCurrent="pageCurrent"
            :pages="pages"
            @update:pageCurrent="handlePageUpdate"
          />
        </div>
      </div>
    </div>
  </div>
  <!-- 使用VisitPopWindow组件 -->
  <VisitPopWindow ref="globalVisitPopup" />
  <!-- 新增：二次确认弹窗 -->
  <SecondConfirmWindow
    :visible="isConfirmVisible"
    :message="confirmMessage"
    @confirm="confirmFavorite"
    @cancel="cancelFavorite"
  />
  <div v-if="showViewDialog" class="custom-dialog">
          <div class="dialog-content">
            <CardDetails
              v-if="selectedSite"
              :site="selectedSite"
              @close="showViewDialog = false"
            />
          </div>
        </div>
  <div v-if="showViewDialog" class="dialog-overlay" @click="showViewDialog = false"></div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import SearchBox from '@/components/SearchBar.vue'
import SiteCard from '@/components/SiteCard/SiteCard.vue'
import VisitPopWindow from '../VisitPopWindow.vue'
import SecondConfirmWindow from '@/components/SecondConfirmWindow.vue'
import Pagination from '@/components/PageButton.vue'
import CardDetails from '@/components/SiteForm/SiteInfo.vue';
import { getAllsites } from '@/api/siteData.js';
import { getCurrentUserInfo } from '@/api/UserData.js';
import { switchCollectStatus } from '@/api/siteManagement.js'
import {getSiteInfo} from '@/api/siteManagement.js';



// 原有变量保留（仅删除旧弹窗相关）
const searchQuery = ref('')
const searchResult = ref([])
const isSearching = ref(false)
const globalVisitPopup = ref(null)

// 新增：弹窗状态管理（核心）
const isConfirmVisible = ref(false)  // 弹窗显示状态
const confirmMessage = ref('')       // 弹窗提示文本
const currentSiteId = ref(null)      // 当前操作的站点ID

const pageNum = ref(1) //当前页码
const pageSize = 12
const allSites = ref([]) // 用于存储所有站点数据
const pages = ref(0) // 总页数


// 获取全部站点数据
const fetchAllSites = async () => {
  try {
    const res = await getAllsites(pageNum.value);
    // console.log('接口返回：',res);
    // console.log('res.code的值',res.code,'类型：',typeof res.code);
    // console.log('res.status的值',res.status,'类型：',typeof res.status);
    // console.log('res.data的值',res.data,'类型：',typeof res.data);
    if (!res || typeof res !== 'object') {
      throw new Error('接口返回格式异常，未获取到数据');
    }
    if (res.code === 200 && res.status === true && res.data) {
      allSites.value = res.data.list.map(site => ({
        ...site,
        collectFlag: site.collectFlag===1// 假设后端返回的 collectFlag 为 1 表示收藏，0 表示未收藏
      }));

      // 打印每个站点的 收藏状态
      allSites.value.forEach(site => {
        console.log(`站点 ${site.id} 的收藏状态: ${site.collectFlag}`);
      });

      pages.value = res.data.pages || 0;
      pageNum.value = res.data.pageNum || 1;
    } else {
      // console.erroe('获取全部站点失败')
    }
  } catch (error) {
    console.error('加载站点错误:', error);
  }
};

// 分页数据
const paginatedFavorites = computed(() => {
  if (isSearching.value) {
    return searchResult.value.slice(
      (pageNum.value - 1) * pageSize,
      pageNum.value * pageSize
    )
  } else {
    return allSites.value.slice(
      (pageNum.value - 1) * pageSize,
      pageNum.value * pageSize
    )
  }
})

// 搜索相关函数
function onSearch(query) {
  isSearching.value = true
  searchResult.value = performSearch(query)
  pageNum.value = 1
}

function clearSearch() {
  searchQuery.value = ''
  isSearching.value = false
  searchResult.value = []
  pageNum.value = 1
}

const confirmFavorite = async () => {
  isConfirmVisible.value = false;
  if (!currentSiteId.value) return;

  const site = allSites.value.find(s => s.id === currentSiteId.value);
  if (!site) return;

  const newState = !site.collectFlag;

  try {
    const res = await switchCollectStatus(newState, currentSiteId.value);
    // console.log('接口返回结果：', res); // 检查接口返回是否正常
    if (res.code !== 200 || !res.status) {
      throw new Error(res.message || '收藏操作失败');
    }

    // 更新前端状态
    site.collectFlag = newState; // 关键：更新站点的收藏状态
    ElMessage.success({
      message:newState ? '收藏成功':'取消收藏成功',
      duration:500//毫秒
    })
  } catch (error) {
    console.error('收藏接口失败：', error); // 检查是否有错误抛出
    ElMessage.error(error.message || '操作失败，请重试');
  }
};
// 新增：弹窗触发逻辑
const handleFavoriteToggle = async (siteId) => {
  currentSiteId.value = siteId;
  const collectFlag = allSites.value.find(s => s.id === siteId).collectFlag;
  const newStatus = !collectFlag;
  confirmMessage.value = newStatus
    ? '确定要收藏此站点吗？'
    : '确定要取消收藏此站点吗？';
  isConfirmVisible.value = true;
};

//弹窗取消操作
const cancelFavorite = () => {
  isConfirmVisible.value = false
}
// 处理点击事件
const showViewDialog=ref(false);
const loading = ref(false);
const selectedSite =ref([])

const handleView =async(siteId) => {
  loading.value =true;
  try{
    const response = await getSiteInfo(siteId);
    // console.log('获取站点详情响应:', response);

    if (response.code === 200 && response.status === true && response.data) {
       selectedSite.value = response.data;
       showViewDialog.value = true;
      //  console.log('显示站点详情弹窗',showViewDialog.value);
       ElMessage.success('获取站点详情成功');
    } else {
      throw new Error(response.message || '获取站点信息失败');
    }
  }catch (error) {
    // 统一错误处理
    console.error('获取站点详情失败:', error);
    ElMessage.error(error.message || '获取站点详情失败，请重试');
    showViewDialog.value = false; // 关闭弹窗
  } finally {
    loading.value = false; // 结束加载状态（无论成功或失败）
  }
};

// 分页更新逻辑
// 修改分页更新函数：切换页码后同步收藏状态
const handlePageUpdate = async (newPage) => {
  pageNum.value = newPage;
  loading.value = true; // 显示加载状态
  try {
    if (isSearching.value) {
      // 搜索状态下的分页（如果需要同步收藏状态，同样适用）
      paginatedFavorites.value = searchResult.value.slice(
        (newPage - 1) * pageSize,
        newPage * pageSize
      );
    } else {
      // 非搜索状态：重新获取站点数据
      await fetchAllSites();
    }
  } catch (error) {
    console.error('分页更新失败:', error);
  } finally {
    loading.value = false;
  }
};

// 模拟搜索函数
function performSearch(query) {
  // 示例：简单匹配标题或描述
  return allSites.value.filter(site =>
    site.title.includes(query) || site.description.includes(query)
  )
}

// 响应式缩放（保留原始逻辑）
const scaleFactor = ref(1)
function updateScale() {
  const windowWidth = window.innerWidth
  const baseWidth = 1920
  scaleFactor.value = Math.max(windowWidth / baseWidth, 0.7)
}
const updateCollectStatus = (payload) => {
  const { portalId, collectFlag } = payload;

  // 使用 map 创建新数组，确保Vue能检测到变化
  allSites.value = allSites.value.map(site => {
    if (site.id === portalId) {
      return { ...site, collectFlag }; // 返回新对象
    }
    return site;
  });
};
const currentUserName = ref('');
// console.log('当前用户名称:', currentUserName.value);

onMounted(async () => {
  try {
    // 先获取当前用户信息（含名称）
    const userInfo = await getCurrentUserInfo();
    currentUserName.value = userInfo.userName || ''; // 存储当前用户名称
    // console.log('当前登录用户名称:', currentUserName.value);

    // 再获取站点数据（确保用户信息先加载）
    await fetchAllSites();
    loading.value = false;
  } catch (error) {
    console.error('初始化数据失败:', error);
    ElMessage.error('加载失败，请刷新重试');
  }
});

onUnmounted(() => {
  window.removeEventListener('resize', updateScale)
})
</script>

<style>
/* 完全保留你的原始样式，未做任何修改 */
html, body {
   overflow: hidden !important;
  margin: 0; /* 去掉默认的边距 */
  padding: 0; /* 去掉默认的内边距 */
}
.viewport-container {
  width: 100vw;
  min-height: 100vh;
  transform-origin: top left;
}

.homepage-content {
  padding: 10vh 5vw 5vh;
  padding-top: 50px;
  width: 100%;
  box-sizing: border-box;
}

.module {
  dispLay:grid;
  width: 100%;
  max-width: 1800px;
  margin: 0 auto 4vh;
  background-color: #fff;
  padding: 2vh 2vw;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  margin-top: 100px;
  max-height: calc(100vh - 200px);
  overflow-y:auto;
}

.module-title {
  margin-bottom: 2vh;
  font-size: clamp(24px, 3vw, 48px);
  color: #333;
  text-align: center;
}

.module-title-favorites {
  margin-top: -30px;
}

.module-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 2vh;
  position: relative;
}

.site-container {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));  /*列数，宽度在（0-1fr之间）*/
  gap: 100px;
  justify-items: center;
  margin-bottom: 2vh;
  min-height: 850px;
}

.search-bar {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.pagination button {
  padding: 5px 12px;
  border: 1px solid #ddd;
  background: #f5f5f5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination button:hover {
  background: #e9e9e9;
}

.pagination button.active {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.no-results {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 18px;
}

.custom-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);

}
@media (max-width: 768px) {
  .site-container {
    grid-template-columns: repeat(2, 1fr);
  }

  .pagination {
    flex-wrap: wrap;
  }
}
</style>
