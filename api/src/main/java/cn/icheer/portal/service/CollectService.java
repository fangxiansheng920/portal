package cn.icheer.portal.service;

import cn.icheer.portal.dto.CollectPortalDTO;
import cn.icheer.portal.dto.CollectRemarkDTO;
import cn.icheer.portal.dto.CollectTopDTO;

import javax.servlet.http.HttpSession;

/**
 * @author fangweihao
 * @since 2025/7/8 8:54
 */
public interface CollectService {

    /**
     * 获取站点是否被收藏 1：收藏 0：未收藏
     * @param portalId Long
     * @return Short
     */
    Short getPortalDoCollect(Long portalId);

    /**
     * 收藏站点
     * @param collectPortalDTO CollectPortalDTO
     * @param userId Long
     * @return Integer
     */
    Integer collectPortal(CollectPortalDTO collectPortalDTO, Long userId);

    /**
     * 根据站点id删除收藏记录
     * @param id Long
     */
    void deleteCollectByPortalId(Long id);

    /**
     * 置顶站点（可置顶也可取消）（我的收藏里）
     * @param collectTopDTO CollectTopDTO
     * @return Integer
     */
    Integer collectTop(CollectTopDTO collectTopDTO, Long userId);

    /**
     * 获取站点是否置顶 1：是 0：否
     * @param portalId Long
     * @return Short
     */
    Short getCollectPortalIsTop(Long portalId);

    /**
     * 判断站点是否被任何人收藏
     * @param portalId Long
     * @return Integer
     */
    Integer isCollectByAnyone(Long portalId);

    /**
     * 获取收藏的备注
     * @param portalId Long
     * @return String
     */
    String getCollectRemark(Long portalId);

    /**
     * 修改收藏备注
     * @param collectRemarkDTO CollectRemarkDTO
     * @return Integer
     */
    Integer modifyCollectRemark(CollectRemarkDTO collectRemarkDTO);

    /**
     * 修改收藏更新时间
     * @param portalId Long
     */
    void modifyUpdateTime(Long portalId);
}
