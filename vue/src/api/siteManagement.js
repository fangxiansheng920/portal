import service from '@/utils/request';

//站点参数结构（复用）
/**
 *
 * @param {string} title - 站点名称
 * @param {string} portalUrl - 站点描述
 *  @param {short} publicFlag - 站点图标
 * @param {string} portalQRcode - 站点链接
 * @param {short} status - 站点状态公开 1/0
 * @param {string} description - 站点分类
 * @param {Long} tagId - 站点标签
 *  @param {string} accessType - 站点所有者
 */


//站点   增
export const createSite = async(param) => {
    return service.post('/portal/createPortal', {
        ...param
    });
}

//站点  软删除
export const deleteSite = async (portalId) => {
    return service.post('/portal/deletePortal', {
        portalId: portalId
    });
}

//回收站  硬删除
export const deletePortalForever = async(portalId)=>{
    return service.post('/portal/deletePortalForever',{
        portalId: portalId
    })
}

//站点  改
export const updateSite = async (param,portalId) => {
    return service.post('/portal/updatePortalInfo', {
        ...param,
        portalId: portalId
    });
}

//站点  查
export const getSiteInfo = async (portalId) => {
    return service.post('/portal/getPortalInfoById', {
        portalId: portalId
    });
}

//站点 上传图片
export const uploadImage = async (file) => {
  const formData = new FormData();
  formData.append('file', file); // 'file' 是后端接收文件的字段名
  return service.post('/uploadImage', formData, {
  });
};

//站点  收藏
export const collectSite = async (params) => {
    return service.post('/portal/collectPortal', params);
}

//切换站点收藏状态api
export const switchCollectStatus =async(doCollect,portalId)=>{
    return service.post('/portal/collectPortal',{
        doCollect:doCollect ? 1:0,
        portalId : portalId
    })
}
// 站点搜索
export const searchByKeyword = async (params) => {
    return service.post('/portal/searchPortalByKeyword', params);
}

//获取所有标签
export const getAllTag = async ()=>{
    return service.post('/portal/getAllTag')
}


//回收站恢复功能
export const restoreDeletedPortal = async(portalId)=>{
    return service.post('/portal/restoreDeletedPortal',{
        portalId:portalId
});
}


//置顶站点（我的收藏）
export const collectTop = async(parmas) =>{
    return service.post('/portal/collectTop',parmas)
}