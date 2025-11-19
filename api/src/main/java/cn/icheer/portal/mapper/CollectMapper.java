package cn.icheer.portal.mapper;

import cn.icheer.portal.dto.CollectPortalDTO;
import cn.icheer.portal.dto.CollectTopDTO;
import cn.icheer.portal.entity.Collect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author fangweihao
 * @since 2025/7/8 8:54
 */
@Mapper
public interface CollectMapper {
    /**
     * 获取站点是否被收藏 1：收藏 0：未收藏
     *
     * @param portalId Long
     * @return Integer
     */
    Short getPortalDoCollect(Long portalId, Long userId);

    /**
     * 收藏站点
     *
     * @param collectPortalDTO CollectPortalDTO
     * @param userId           Long
     * @return Integer
     */
    Integer collectPortal(@Param("collectPortalDTO") CollectPortalDTO collectPortalDTO, @Param("userId") Long userId);

    /**
     * 判断用户是否有收藏过站点
     *
     * @param userId   Long
     * @param portalId Long
     * @return Collect
     */
    Collect getCollect(Long userId, Long portalId);

    /**
     * 更新收藏状态
     *
     * @param collectPortalDTO CollectPortalDTO
     * @param userId           Long
     * @return Integer
     */
    Integer updateDoCollect(@Param("collectPortalDTO") CollectPortalDTO collectPortalDTO, @Param("userId") Long userId);

    /**
     * 根据站点id删除收藏记录
     *
     * @param id Long
     */
    void deleteCollectByPortalId(Long id);

    /**
     * 我的收藏里置顶站点（可置顶也可取消）
     *
     * @param collectTopDTO CollectTopDTO
     * @return Integer
     */
    Integer collectTop(@Param("collectTopDTO") CollectTopDTO collectTopDTO, @Param("userId") Long userId);

    /**
     * 获取站点是否置顶 1：是 0：否
     * @param portalId Long
     * @param userId Long
     * @return Short
     */
    Short getCollectPortalIsTop(Long portalId, Long userId);

    /**
     * 判断是否有用户收藏过站点
     * @param portalId Long
     * @return Integer
     */
    Integer isCollectByAnyone(Long portalId);

    /**
     * 获取收藏的备注
     * @param userId Long
     * @param portalId Long
     * @return String
     */
    String getCollectRemark(Long userId, Long portalId);

    /**
     * 修改收藏备注
     * @param userId Long
     * @param portalId Long
     * @param remark String
     * @return Integer
     */
    Integer modifyCollectRemark(Long userId, Long portalId, String remark);

    /**
     * 修改更新时间
     * @param userId Long
     * @param portalId Long
     */
    void modifyUpdateTime(Long userId, Long portalId);
}
