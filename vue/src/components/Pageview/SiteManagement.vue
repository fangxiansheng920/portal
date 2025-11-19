<template>
  <div class="viewport-container">
    <div class="sitemanagemant-content">
      <!-- 我的站点 -->
      <div class="module">
        <h1 class="module-title">我的站点</h1>
        <SearchBox
            v-model="searchQueryMy"
            @search="onSearchMy"
            @clear="clearSearchMy"
            class="search-bar"
        />
        <div class="site-container">
          <template v-if="filteredMySites.length">
            <template v-for="site in myPaginatedData" :key="site.id">
              <SiteCard
                  v-if="!site.isAddCard"
                  :key="site.id"
                  :id="site.id"
                  :title="site.title"
                  :description="site.description"
                  :createBy="site.createBy"
                  :createTime="site.createTime"
                  :isAddCard="site.isAddCard"
                  :isFavorite="site.isFavorite"
                  :isVisible="site.isVisible"
                  :showFavorite="false"
                  :showGo="!site.isAddCard"
                  :showTime="!site.isAddCard"
                  :showEye="!site.isAddCard"
                  :showEdit="!site.isAddCard"
                  @notifyFavoriteToggle="handleFavoriteToggle"
                  @notifyEyeToggle="handleEyeToggle"
                  @click="handleClick"
                  @go="openGlobalPopup"
                  @eye="toggleEye"
                  @edit="openEditDrawer(site)"
              />
              <AddButton v-else @click ="openAddForm" />
            </template>
          </template>
          <!-- 搜索状态下无结果显示 -->
          <div v-else-if="isMySearching" class="no-results">
            没有找到匹配的我的站点
          </div>
          <!-- 非搜索状态下无结果显示 -->
          <div v-else class="no-results">
            还没有站点
          </div>
        </div>
        <div class="pagination" v-if="showMyPagination">
          <Pagination
              :pageCurrent="myCurrentPage"
              :totalPages="myTotalPages"
              @update:pageCurrent="handlePageUpdateMy"
          />
        </div>
      </div>

      <!-- 共享站点 -->
      <div class="module">
        <h1 class="module-title">共享站点</h1>
        <SearchBox
            v-model="searchQueryShared"
            @search="onSearchShared"
            @clear="clearSearchShared"
            class="search-bar"
        />
        <div class="site-container">
          <template v-if="filteredSharedSites.length">
            <SiteCard
                v-for="site in sharedPaginatedData"
                :key="site.id"
                :id="site.id"
                :title="site.title"
                :description="site.description"
                :createBy="site.createBy"
                :createTime="site.createTime"
                :isFavorite="site.isFavorite"
                :showFavorite="true"
                :showGo="true"
                :showTime="true"
                :showEdit="site.canEdit"
                @notifyFavoriteToggle="handleFavoriteToggle"
                @notifyEyeToggle="handleEyeToggle"
                @click="handleClick"
                @go="openGlobalPopup"
                @edit="openEditDrawer(site)"
            />
          </template>
          <!-- 搜索状态下无结果显示 -->
          <div v-else-if="isSharedSearching" class="no-results">
            没有找到匹配的共享站点
          </div>
          <!-- 非搜索状态下无结果显示 -->
          <div v-else class="no-results">
            还没有站点
          </div>
        </div>
        <div class="pagination" v-if="showSharedPagination">
          <Pagination
              :pageCurrent="sharedCurrentPage"
              :totalPages="sharedTotalPages"
              @update:pageCurrent="handlePageUpdateShared"
          />
        </div>
      </div>

      <div class="module">
        
      </div>
    </div>
    <!-- 使用VisitPopWindow组件 -->
    <VisitPopWindow ref="globalVisitPopup" />
    <!-- 使用抽屉组件实现表单填写 新建/编辑公用 -->
    <SiteDrawer
        :visible="drawerVisible"
        :mode="drawerMode"
        :isOwner="true"
        :siteData="currentSiteData"
        @close="closeDrawer"
        @submit="handleFormSubmit"
    />
    <!-- 二次确认弹窗（页面级） -->
    <SecondConfirmWindow
        :visible="isConfirmVisible"
        :message="confirmMessage"
        @confirm="confirmFavorite"
        @cancel="cancelFavorite"
    />
    <SecondVisibleWindow
        :visible="isEyeVisible"
        :message="eyeMessage"
        @confirm="confirmEyeToggle"
        @cancel="cancelEyeToggle"
    />
  </div>
</template>

<script setup>
import { watch, ref, computed, onMounted } from 'vue';
import SiteCard from '@/components/SiteCard/SiteCard.vue';
import VisitPopWindow from '../VisitPopWindow.vue';
import SearchBox from '@/components/SearchBar.vue';
import SiteDrawer from '@/components/SiteForm/SiteDrawer.vue';
import SecondConfirmWindow from '@/components/SecondConfirmWindow.vue';
import SecondVisibleWindow from '../SecondVisibleWindow.vue';
import Pagination from '@/components/PageButton.vue';
import AddButton from '@/components/AppButton/AddFormButton.vue';

// 收藏爱心弹窗状态管理（核心新增）
const isConfirmVisible = ref(false);  // 弹窗显示状态
const confirmMessage = ref('');       // 弹窗提示文本
const currentSiteId = ref(null);      // 当前操作的站点ID
const currentSiteSource = ref(null);  // 标记站点来源（mySites/sharedSites）

// 眼睛弹窗状态
const isEyeVisible = ref(false);  // 控制眼睛弹窗显示/隐藏
const eyeMessage = ref('');       // 眼睛弹窗提示文本

// 我的站点 - 分页参数
const myCurrentPage = ref(1);
const myItemsPerPage = 12; // 4列×3行=12个卡片/页
const myTotalPages = ref(0);
const showMyPagination = computed(() => myTotalPages.value > 1);

// 共享站点 - 分页参数
const sharedCurrentPage = ref(1);
const sharedItemsPerPage = 12;
const sharedTotalPages = ref(0);
const showSharedPagination = computed(() => sharedTotalPages.value > 1);

// 原始数据
const mySites = ref([
  { id: 1, title: '我的站点 1', isVisible: true,description: '这是我的第一个站点', createBy: '我', createTime: '2024-06-01', isFavorite: false, canEdit: true },
  { id: 2, title: '我的站点 2', description: '这是我的第二个站点', createBy: '我', createTime: '2024-06-02', isFavorite: false, canEdit: true },
  { id: 3, title: '我的站点 3', description: '这是我的第三个站点', createBy: '我', createTime: '2024-06-03', isFavorite: false, canEdit: true },
  { id: 4, title: '我的站点 4', description: '这是我的第四个站点', createBy: '我', createTime: '2024-06-04', isFavorite: false, canEdit: true },
  { id: 5, title: '我的站点 5', description: '这是我的第五个站点', createBy: '我', createTime: '2024-06-05', isFavorite: false, canEdit: true },
  { id: 6, title: '我的站点 6', description: '这是我的第六个站点', createBy: '我', createTime: '2024-06-06', isFavorite: false, canEdit: true },
  { id: 7, title: '我的站点 7', description: '这是我的第七个站点', createBy: '我', createTime: '2024-06-07', isFavorite: false, canEdit: true },
  { id: 8, title: '我的站点 8', description: '这是我的第八个站点', createBy: '我', createTime: '2024-06-08', isFavorite: false, canEdit: true },
  { id: 9, title: '我的站点 9', description: '这是我的第九个站点', createBy: '我', createTime: '2024-06-09', isFavorite: false, canEdit: true },
  { id: 10, title: '我的站点 10', description: '这是我的第十个站点', createBy: '我', createTime: '2024-06-10', isFavorite: false, canEdit: true },
  { id: 11, title: '我的站点 11', description: '这是我的第十一个站点', createBy: '我', createTime: '2024-06-11', isFavorite: false, canEdit: true },
  { id: 12, title: '我的站点 12', description: '这是我的第十二个站点', createBy: '我', createTime: '2024-06-12', isFavorite: false, canEdit: true },
  { id: 15, title: '共享站点 12', description: '这是共享的第十二个站点', createBy: '用户 L', createTime: '2024-06-15', isFavorite: false, canEdit: false },
]);

const sharedSites = ref([
  { id: 4, title: '共享站点 1', description: '这是共享的第一个站点', createBy: '用户 A', createTime: '2024-06-04', isFavorite: false, canEdit: false },
  { id: 5, title: '共享站点 2', description: '这是共享的第二个站点', createBy: '用户 B', createTime: '2024-06-05', isFavorite: true, canEdit: true },
  { id: 6, title: '共享站点 3', description: '这是共享的第三个站点', createBy: '用户 C', createTime: '2024-06-06', isFavorite: false, canEdit: false },
  { id: 7, title: '共享站点 4', description: '这是共享的第四个站点', createBy: '用户 D', createTime: '2024-06-07', isFavorite: false, canEdit: false },
  { id: 8, title: '共享站点 5', description: '这是共享的第五个站点', createBy: '用户 E', createTime: '2024-06-08', isFavorite: false, canEdit: false },
  { id: 9, title: '共享站点 6', description: '这是共享的第六个站点', createBy: '用户 F', createTime: '2024-06-09', isFavorite: false, canEdit: false },
  { id: 10, title: '共享站点 7', description: '这是共享的第七个站点', createBy: '用户 G', createTime: '2024-06-10', isFavorite: false, canEdit: false },
  { id: 11, title: '共享站点 8', description: '这是共享的第八个站点', createBy: '用户 H', createTime: '2024-06-11', isFavorite: false, canEdit: false },
  { id: 12, title: '共享站点 9', description: '这是共享的第九个站点', createBy: '用户 I', createTime: '2024-06-12', isFavorite: false, canEdit: false },
  { id: 13, title: '共享站点 10', description: '这是共享的第十个站点', createBy: '用户 J', createTime: '2024-06-13', isFavorite: false, canEdit: false },
  { id: 14, title: '共享站点 11', description: '这是共享的第十一个站点', createBy: '用户 K', createTime: '2024-06-14', isFavorite: false, canEdit: false },
  { id: 15, title: '共享站点 12', description: '这是共享的第十二个站点', createBy: '用户 L', createTime: '2024-06-15', isFavorite: false, canEdit: false },
  { id: 15, title: '共享站点 12', description: '这是共享的第十二个站点', createBy: '用户 L', createTime: '2024-06-15', isFavorite: false, canEdit: false },
  { id: 15, title: '共享站点 12', description: '这是共享的第十二个站点', createBy: '用户 L', createTime: '2024-06-15', isFavorite: false, canEdit: false },
]);

// 搜索相关状态
const searchQueryMy = ref('');
const searchResultMy = ref([]);
const isMySearching = ref(false);

const searchQueryShared = ref('');
const searchResultShared = ref([]);
const isSharedSearching = ref(false);

// 其他状态
const globalVisitPopup = ref(null);
const drawerVisible = ref(false);
const drawerMode = ref('');
const currentSiteData = ref({});

// 过滤结果计算
const filteredMySites = computed(() => {
  return isMySearching.value
      ? searchResultMy.value
      : mySites.value;
});

const filteredSharedSites = computed(() => {
  return isSharedSearching.value
      ? searchResultShared.value
      : sharedSites.value;
});

// 我的站点 - 分页数据
// 在计算分页数据时，动态添加AddButton
const myPaginatedData = computed(() => {
  const start = (myCurrentPage.value - 1) * myItemsPerPage;
  const end = start + myItemsPerPage;

  // 获取当前页的普通站点数据
  const currentPageSites = filteredMySites.value.slice(start, end);

  // 判断是否需要添加AddButton
  const shouldAddButton =
      // 条件1：当前是最后一页
      (myCurrentPage.value === myTotalPages.value) &&
      // 条件2：当前页卡片数量不足一页（非刚好12个）
      (currentPageSites.length < myItemsPerPage);

  // 如果需要添加AddButton，则加入数据中
  return shouldAddButton
      ? [...currentPageSites, { isAddCard: true }]
      : currentPageSites;
});
// 共享站点 - 分页数据
const sharedPaginatedData = computed(() => {
  const start = (sharedCurrentPage.value - 1) * sharedItemsPerPage;
  const end = start + sharedItemsPerPage;
  return filteredSharedSites.value.slice(start, end);
});

// 计算总页数
const calculateTotalPages = () => {
  // AddButton不占用数据位置，所以直接用原始数据长度计算
  myTotalPages.value = Math.max(1, Math.ceil(filteredMySites.value.length / myItemsPerPage));
  sharedTotalPages.value = Math.max(1, Math.ceil(filteredSharedSites.value.length / sharedItemsPerPage));

  // 边界检查（保持原有逻辑）
  if (myCurrentPage.value > myTotalPages.value) myCurrentPage.value = myTotalPages.value;
  if (sharedCurrentPage.value > sharedTotalPages.value) sharedCurrentPage.value = sharedTotalPages.value;
};

// 监听过滤结果变化，重新计算总页数
watch([filteredMySites, filteredSharedSites], () => {
  calculateTotalPages();
});

// 搜索逻辑
const onSearchMy = () => {
  const query = searchQueryMy.value.trim();
  isMySearching.value = true;
  searchResultMy.value = mySites.value.filter(site =>
      site.title.toLowerCase().includes(query.toLowerCase()) ||
      site.description.toLowerCase().includes(query.toLowerCase()) ||
      site.createBy.toLowerCase().includes(query.toLowerCase())
  );
  myCurrentPage.value = 1; // 重置当前页码为第一页
  calculateTotalPages();
};

const onSearchShared = () => {
  const query = searchQueryShared.value.trim();
  isSharedSearching.value = true;
  searchResultShared.value = sharedSites.value.filter(site =>
      site.title.toLowerCase().includes(query.toLowerCase()) ||
      site.description.toLowerCase().includes(query.toLowerCase()) ||
      site.createBy.toLowerCase().includes(query.toLowerCase())
  );
  sharedCurrentPage.value = 1; // 重置当前页码为第一页
  calculateTotalPages();
};

// 清空搜索
const clearSearchMy = () => {
  searchQueryMy.value = '';
  searchResultMy.value = [];
  isMySearching.value = false;
  myCurrentPage.value = 1; // 重置当前页码为第一页
  calculateTotalPages();
};

const clearSearchShared = () => {
  searchQueryShared.value = '';
  searchResultShared.value = [];
  isSharedSearching.value = false;
  sharedCurrentPage.value = 1; // 重置当前页码为第一页
  calculateTotalPages();
};

// 分页逻辑的处理函数
const handlePageUpdateMy = (newPage) => {
  myCurrentPage.value = newPage;
};

const handlePageUpdateShared = (newPage) => {
  sharedCurrentPage.value = newPage;
};

// 抽屉相关
const openAddForm = () => {
  drawerMode.value = 'add';
  currentSiteData.value = {};
  drawerVisible.value = true;
};

const openEditDrawer = (site) => {
  drawerMode.value = 'edit';
  currentSiteData.value = { ...site };
  drawerVisible.value = true;
};

const closeDrawer = () => {
  drawerVisible.value = false;
};

const handleFormSubmit = () => {
  // console.log(`${drawerMode.value === 'add' ? '新增' : '编辑'}表单提交数据:`, formData);
  closeDrawer();
  alert(`${drawerMode.value === 'add' ? '新增' : '编辑'}成功！`);
};

// 收藏弹窗触发（核心新增）
const handleFavoriteToggle = (siteId) => {
  // 查找站点并确定来源
  const siteInMy = mySites.value.find(s => s.id === siteId);
  const siteInShared = sharedSites.value.find(s => s.id === siteId);
  const site = siteInMy || siteInShared;

  if (site) {
    currentSiteId.value = siteId;
    currentSiteSource.value = siteInMy ? 'my' : 'shared'; // 标记来源
    // 根据当前状态设置提示文本
    confirmMessage.value = site.isFavorite
        ? '确定要取消收藏该站点吗？'
        : '确定要收藏该站点吗？';
    isConfirmVisible.value = true; // 显示弹窗
  }
};

// 弹窗确认操作（核心新增）
const confirmFavorite = () => {
  isConfirmVisible.value = false;
  // 根据来源更新对应数组的收藏状态
  if (currentSiteSource.value === 'my') {
    const site = mySites.value.find(s => s.id === currentSiteId.value);
    if (site) site.isFavorite = !site.isFavorite;
  } else {
    const site = sharedSites.value.find(s => s.id === currentSiteId.value);
    if (site) site.isFavorite = !site.isFavorite;
  }
  // console.log('收藏状态已更新:', currentSiteId.value);
  calculateTotalPages();
};

// 弹窗取消操作（核心新增）
const cancelFavorite = () => {
  isConfirmVisible.value = false;
};


const pendingEyeState = ref({
  siteId: null,
  newVisibility: null, // 待确认的可见性（true/false）
  originalVisibility: null // 原始状态，用于取消时恢复
});
// 处理眼睛图标点击，显示弹窗
const handleEyeToggle = (siteId) => {
  const site = mySites.value.find(s => s.id === siteId);
  if (site) {
    // 记录原始状态和新状态
    pendingEyeState.value = {
      siteId: siteId,
      newVisibility: !site.isVisible,
      originalVisibility: site.isVisible
    };

    eyeMessage.value = pendingEyeState.value.newVisibility
        ? '确定要显示该站点吗？显示后将在列表中可见。'
        : '确定要隐藏该站点吗？隐藏后将不在列表中显示。';

    isEyeVisible.value = true; // 显示弹窗
  }
};

// 确认时：应用新状态
const confirmEyeToggle = () => {
  isEyeVisible.value = false;
  const { siteId, newVisibility } = pendingEyeState.value;
  if (siteId) {
    const site = mySites.value.find(s => s.id === siteId);
    if (site) {
      site.isVisible = newVisibility; // 直接修改实际状态
      // console.log('站点可见性已更新:', site.isVisible);
      calculateTotalPages();
    }
  }
  // 重置 pending 状态
  pendingEyeState.value = { siteId: null, newVisibility: null, originalVisibility: null };
};

// 取消时：恢复原始状态
const cancelEyeToggle = () => {
  isEyeVisible.value = false;
  const { siteId, originalVisibility } = pendingEyeState.value;
  if (siteId) {
    const site = mySites.value.find(s => s.id === siteId);
    if (site) {
      site.isVisible = originalVisibility; // 恢复原始状态
    }
  }
  // 重置 pending 状态
  pendingEyeState.value = { siteId: null, newVisibility: null, originalVisibility: null };
};

// 其他事件处理
const handleClick = (id) => {
  console.log('点击卡片，跳转站点:', id);
};

const openGlobalPopup = (id) => {
  console.log('打开弹窗，站点 ID：', id);
  globalVisitPopup.value.showPopup(id);
};

const toggleEye = (id) => {
  console.log('切换眼睛状态:', id);
};

onMounted(() => {
  calculateTotalPages();
});

// 监听原始数据变化
watch([mySites, sharedSites], () => {
  calculateTotalPages();
}, { deep: true });

// 监听搜索状态变化
watch([isMySearching, isSharedSearching], () => {
  calculateTotalPages();
});
</script>

<style scoped>
.viewport-container {
  width: 100vw;
  min-height: 100vh;
  overflow-x: hidden;
  transform-origin: top left;
}

.sitemanagemant-content {
  padding: 5vh 5vw;
  margin-top: 60px;
  width: 100%;
  box-sizing: border-box;
}

.module {
  width: 100%;
  max-width: 1800px;
  margin: 0 auto 4vh;
  background-color: #fff;
  padding: 2vh 2vw;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  position: relative;
}

.module-title {
  text-align: left;
  margin: 0 0 1rem;
  font-size: 20px;
  color: #7d7b7b;
}

.search-bar {
  display: flex;
  margin-left: auto;
  margin-bottom: 1rem;
  transform: translateY(-50%);
}

.site-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  justify-items: stretch;
  min-height: 700px;
  align-content: start;

}

.add-btn {
  position: absolute;
  right: 1vw;
  bottom: 1vh;
  cursor: pointer;
}

.no-results {
  text-align: center;
  color: #999;
  grid-column: 1 / -1;
  padding: 40px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}

@media (max-width: 768px) {
  .site-container {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
