package cn.icheer.portal.service;

import cn.icheer.portal.entity.UserPortal;

/**
 * @author fangweihao
 * @since 2025/7/11 11:18
 */
public interface UserPortalService {
    /**
     * 添加用户站点关系
     * @param userId Long
     * @param portalId Long
     * @return Integer
     */
    Integer addUserPortal(Long userId, Long portalId);

    /**
     * 根据站点id删除user_portals表里的记录
     * @param id Long
     */
    void deleteUserPortalsByPortalId(Long id);

    /**
     * 根据用户id和站点id获取user_portals表里的记录
     * @param userId Long
     * @param portalId Long
     * @return UserPortal
     */
    UserPortal getUserPortal(Long userId, Long portalId);

    /**
     * 更新user_portals表里的last_use_at字段
     * @param portalId Long
     */
    void updateUserPortalLastUseAt(Long userId, Long portalId);
}
