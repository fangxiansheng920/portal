package cn.icheer.portal.mapper;

import cn.icheer.portal.entity.UserPortal;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author fangweihao
 * @since 2025/7/11 11:20
 */
@Mapper
public interface UserPortalMapper {
    /**
     * 添加用户和站点的关系
     * @param userId Integer
     * @param portalId Integer
     */
    Integer addUserPortal(Long userId, Long portalId);

    /**
     * 根据站点id删除user_portals表里的记录
     * @param id Long
     */
    void deleteUserPortalsByPortalId(Long id);

    /**
     * 根据用户id和站点id查询user_portals表里的记录
     * @param userId Long
     * @param portalId Long
     * @return UserPortal
     */
    UserPortal getUserPortal(Long userId, Long portalId);

    /**
     * 更新user_portals表的last_use_at字段
     * @param portalId Long
     */
    void updateUserPortalLastUseAt(Long userId, Long portalId);
}
