import service from '@/utils/request';

// 展示全部站点
export const getAllsites = (pageCurrent) => {
  return service.post('/portal/homePage', {
    pageCurrent: pageCurrent
  });
};


//展示 “我的创建” 站点
export const getMyCreateSites =(params) =>{
  return service.post('/portal/myCreatePage',params);
}


//展示 “共享站点” 站点
export const getSharedSites =(params) =>{
  return service.post('/portal/sharedPage',params);
}

//展示 “我的收藏” 站点
export const getMyCollectSites =(params) =>{
  return service.post('/portal/myCollectPage', params);
}


//展示 “最近访问” 站点
export const getRecentAccessSite =() =>{
  return service.post('/portal/recentAccessPortal', {
  });
}




//获取所有标签
export const getAllTag = ()=>{
  return service.post('/portal/getAllTag');
}

//创建站点标签
export const createTag = (params)=>{
  return service.post('/portal/createTag',params)
}

//修改站点标签
export const modifyTag = (params) =>{
  return service.post('/portal/modifyTag',params);
}
//删除标签
export const deleteTag = (params)=>{
  return service.post('/portal/deleteTag',params);
}

// 点击反馈
export const clickPortal =(params) =>{
  return service.post('/portal/clickPortalUrl', params);
}

//展示 “回收站”  站点
export const getDeletedPortal = (params) => {
  return service.post('/portal/getDeletedPortal', params);
};
//根据标签，获取分类站点列表（单个）
export const getPortalsByTag = (params)=>{
  return service.post('/portal/getPortalByOneTag',params);
}
//展示 根据标签获取站点列表
export const getAllPortalByTags =(params) =>{
  return service.post('/portal/getAllPortalByTags',params);
}
//我的收藏备注
export const modifyCollectRemark= (params)=>{
  return service.post('/portal/modifyCollectRemark',params);
}

//获取收藏站点我的备注
export const getCollectRemark= (params)=>{
  return service.post('/portal/getCollectRemark',params);
}
