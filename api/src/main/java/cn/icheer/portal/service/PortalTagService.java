package cn.icheer.portal.service;

import cn.icheer.portal.dto.TagDeleteByIdDTO;

import java.util.Set;

/**
 * @author fangweihao
 * @since 2025/8/11 15:18
 */
public interface PortalTagService {
    /**
     * 添加站点标签关联
     * @param portalId 站点ID
     * @param tagIdSet 标签ID集合
     */
    void addPortalTag(Long portalId, Set<Long> tagIdSet);

    /**
     * 删除站点标签关联
     * @param portalId 站点ID
     * @param tagIdSet 标签ID集合
     */
    void deletePortalTag(Long portalId, Set<Long> tagIdSet);

    /**
     * 获取该站点的标签id集合
     * @param portalId 站点ID
     * @return 标签ID集合
     */
    Set<Long> getPortalTagIdSet(Long portalId);

    /**
     * 通过站点id删除站点标签关联
     * @param portalId 站点ID
     */
    void deletePortalTagByPortalId(Long portalId);

    /**
     * 删除站点标签关联
     * @param tagId 标签ID
     */
    void deleteTagPortal(Long tagId);
}
