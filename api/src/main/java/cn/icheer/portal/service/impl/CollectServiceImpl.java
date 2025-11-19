package cn.icheer.portal.service.impl;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.dto.CollectPortalDTO;
import cn.icheer.portal.dto.CollectRemarkDTO;
import cn.icheer.portal.dto.CollectTopDTO;
import cn.icheer.portal.entity.User;
import cn.icheer.portal.mapper.CollectMapper;
import cn.icheer.portal.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author fangweihao
 * @since 2025/7/8 8:54
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {
    @Resource
    private CollectMapper collectMapper;

    /**
     * 获取用户当前信息
     * @return JWTUserDetails
     */
    private static JWTUserDetails getUserInfo() {
        // 获取用户当前信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (JWTUserDetails) authentication.getPrincipal();
    }

    /**
     * 获取站点是否被收藏 1：收藏 0：未收藏
     * @param portalId Long
     * @return Short
     */
    @Override
    public Short getPortalDoCollect(Long portalId) {
        JWTUserDetails user = getUserInfo();
        log.debug("get JWTUserDetails id: {} ", user.getId());

        // 判断是否有收藏记录，如果没有则直接返回0，如果有则返回查询结果
        Short doCollect = collectMapper.getPortalDoCollect(portalId, user.getId());
        if (doCollect == null) {
            return 0;
        }
        return doCollect;
    }

    /**
     * 收藏站点
     * @param collectPortalDTO CollectPortalDTO
     * @param userId Long
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer collectPortal(CollectPortalDTO collectPortalDTO, Long userId) {
        // 判断是否有收藏记录，如果没有则创建，如果有则更新
        if (collectMapper.getCollect(userId, collectPortalDTO.getPortalId()) == null) {
            int flag = collectMapper.collectPortal(collectPortalDTO, userId);
            if (flag == 1) {
                log.debug("create collectPortal success, userId: {}, portalId: {} .", userId, collectPortalDTO.getPortalId());
                collectMapper.collectTop(new CollectTopDTO(collectPortalDTO.getPortalId(), (short) 0), userId);
                return Constants.OPERATION_SUCCESS;
            } else {
                log.debug("create collectPortal failed, userId: {}, portalId: {} .", userId, collectPortalDTO.getPortalId());
                return Constants.OPERATION_FAILED;
            }
        }
        int flag = collectMapper.updateDoCollect(collectPortalDTO, userId);
        // 取消收藏时需要将置顶字段设置为0，取消置顶状态
        if (collectPortalDTO.getDoCollect() == 0) {
            collectMapper.collectTop(new CollectTopDTO(collectPortalDTO.getPortalId(), (short) 0), userId);
        }
        if (flag == 1) {
            log.debug("update collectPortal success, userId: {}, portalId: {} .", userId, collectPortalDTO.getPortalId());
            return Constants.OPERATION_SUCCESS;
        } else {
            log.debug("update collectPortal failed, userId: {}, portalId: {} .", userId, collectPortalDTO.getPortalId());
            return Constants.OPERATION_FAILED;
        }
    }

    /**
     * 根据站点id删除收藏记录
     * @param id Long
     */
    @Override
    public void deleteCollectByPortalId(Long id) {
        collectMapper.deleteCollectByPortalId(id);
        log.debug("delete collect success, portalId: {} .", id);
    }

    /**
     * 我的收藏里置顶站点（可置顶也可取消）
     * @param collectTopDTO CollectTopDTO
     * @param userId Long
     * @return Integer
     */
    @Override
    public Integer collectTop(CollectTopDTO collectTopDTO, Long userId) {
        int flag = collectMapper.collectTop(collectTopDTO, userId);
        // 置顶操作失败
        if (flag == 0) {
            log.debug("Top portal in collect failed, portalId: {} .", collectTopDTO.getPortalId());
            return Constants.OPERATION_FAILED;
        }
        // 置顶操作成功，判断置顶参数
        if (collectTopDTO.getIsTop() == 1) {
            log.debug("Top portal in collect success, portalId: {} .", collectTopDTO.getPortalId());
        } else {
            log.debug("Cancel top portal in collect success, portalId: {} .", collectTopDTO.getPortalId());
        }
        return Constants.OPERATION_SUCCESS;
    }

    /**
     * 获取站点是否置顶 1：是 0：否
     * @param portalId Long
     * @return Short
     */
    @Override
    public Short getCollectPortalIsTop(Long portalId) {
        JWTUserDetails user = getUserInfo();
        log.debug("get JWTUserDetails id: {} ", user.getId());

        return collectMapper.getCollectPortalIsTop(portalId, user.getId());
    }

    /**
     * 判断站点是否被任何人收藏
     * @param portalId Long
     * @return Integer
     */
    @Override
    public Integer isCollectByAnyone(Long portalId) {
        if (collectMapper.isCollectByAnyone(portalId) != null) {
            // 有人收藏
            log.debug("Portal is collected by anyone, portalId: {} .", portalId);
            return Constants.IS_COLLECT_BY_ANYONE;
        } else {
            // 没人收藏
            log.debug("Portal is not collected by anyone, portalId: {} .", portalId);
            return Constants.IS_COLLECT_NO_ONE;
        }
    }

    /**
     * 获取收藏的备注
     * @param portalId Long
     * @return String
     */
    @Override
    public String getCollectRemark(Long portalId) {
        // 获取用户当前信息
        JWTUserDetails user = getUserInfo();
        // 获取备注
        String remark = collectMapper.getCollectRemark(user.getId(), portalId);
        log.debug("Get collect remark: portalId: {} .", portalId);
        return remark;
    }

    /**
     * 修改收藏备注
     * @param collectRemarkDTO CollectRemarkDTO
     * @return Integer
     */
    @Override
    public Integer modifyCollectRemark(CollectRemarkDTO collectRemarkDTO) {
        // 获取用户当前信息
        JWTUserDetails user = getUserInfo();
        // 修改备注
        int flag = collectMapper.modifyCollectRemark(user.getId(), collectRemarkDTO.getPortalId(), collectRemarkDTO.getRemark());
        if (flag == 0) {
            return Constants.OPERATION_FAILED;
        } else {
            return Constants.OPERATION_SUCCESS;
        }
    }

    /**
     * 修改更新时间
     * @param portalId Long
     */
    @Override
    public void modifyUpdateTime(Long portalId) {
        // 获取用户当前信息
        JWTUserDetails user = getUserInfo();
        // 修改更新时间
        collectMapper.modifyUpdateTime(user.getId(), portalId);
        log.debug("Modify update time in collect success, portalId: {} .", portalId);
    }


}
