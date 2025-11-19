package cn.icheer.portal.service.impl;

import cn.icheer.portal.entity.UserPortal;
import cn.icheer.portal.mapper.UserPortalMapper;
import cn.icheer.portal.service.UserPortalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fangweihao
 * @since 2025/7/11 11:19
 */
@Slf4j
@Service
public class UserPortalServiceImpl implements UserPortalService {
    @Resource
    private UserPortalMapper userPortalMapper;

    /**
     * 添加用户站点关系
     * @param userId Long
     * @param portalId Long
     * @return Integer
     */
    @Override
    public Integer addUserPortal(Long userId, Long portalId) {
        return userPortalMapper.addUserPortal(userId, portalId);
    }

    /**
     * 根据站点id删除user_portals表里的记录
     * @param id Long
     */
    @Override
    public void deleteUserPortalsByPortalId(Long id) {
        userPortalMapper.deleteUserPortalsByPortalId(id);
    }

    @Override
    public UserPortal getUserPortal(Long userId, Long portalId) {
        return userPortalMapper.getUserPortal(userId, portalId);
    }

    /**
     * 更新user_portals表的last_use_at字段
     * @param portalId Long
     */
    @Override
    public void updateUserPortalLastUseAt(Long userId, Long portalId) {
        userPortalMapper.updateUserPortalLastUseAt(userId, portalId);
    }
}
