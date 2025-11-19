package cn.icheer.portal.service.impl;

import cn.icheer.portal.mapper.PortalTagMapper;
import cn.icheer.portal.service.PortalTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author fangweihao
 * @since 2025/8/11 15:19
 */
@Slf4j
@Service
public class PortalTagServiceImpl implements PortalTagService {
    @Resource
    private PortalTagMapper portalTagMapper;

    /**
     * 添加站点标签关联
     * @param portalId 站点ID
     * @param tagIdSet 标签ID集合
     */
    @Override
    public void addPortalTag(Long portalId, Set<Long> tagIdSet) {
        try {
            portalTagMapper.addPortalTag(portalId, tagIdSet);
            log.debug("Add portal tag success, portalId = {}.", portalId);
        } catch (Exception e) {
            log.debug("Add portal tag failed, portalId = {}.", portalId);
            throw e;
        }
    }

    /**
     * 删除站点标签关联
     * @param portalId 站点ID
     * @param tagIdSet 标签ID集合
     */
    @Override
    public void deletePortalTag(Long portalId, Set<Long> tagIdSet) {
        try {
            portalTagMapper.deletePortalTag(portalId, tagIdSet);
            log.debug("Delete portal tag success, portalId = {}.", portalId);
        } catch (Exception e) {
            log.debug("Delete portal tag failed, portalId = {}.", portalId);
            throw e;
        }
    }

    /**
     * 获取站点标签ID集合
     * @param portalId 站点ID
     * @return 标签ID集合
     */
    @Override
    public Set<Long> getPortalTagIdSet(Long portalId) {
        return portalTagMapper.getPortalTagIdSet(portalId);
    }

    /**
     * 通过站点ID删除站点标签关联
     * @param portalId 站点ID
     */
    @Override
    public void deletePortalTagByPortalId(Long portalId) {
        portalTagMapper.deletePortalTagByPortalId(portalId);
    }

    /**
     * 删除站点标签关联
     * @param tagId 标签ID
     */
    @Override
    public void deleteTagPortal(Long tagId) {
        try {
            portalTagMapper.deleteTagPortal(tagId);
            log.debug("Delete tag portal success, tagId = {}.", tagId);
        } catch (Exception e) {
            log.debug("Delete tag portal failed, tagId = {}.", tagId);
            throw e;
        }
    }
}
