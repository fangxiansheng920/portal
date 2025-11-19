package cn.icheer.portal.mapper;

import cn.icheer.portal.dto.*;
import cn.icheer.portal.entity.Portal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/8 10:55
 */
@Mapper
public interface PortalMapper {

    /**
     * 获取所有站点的id列表
     *
     * @param id Long
     * @return List
     */
    List<Long> getAllPortalIdList(Long id);

    /**
     * 获取用户创建的站点id列表
     *
     * @param id Long
     * @return List
     */
    List<Long> getMyCreatePortalIdList(Long id);

    /**
     * 获取用户创建的站点列表
     *
     * @param id Long
     * @return List
     */
    List<PortalWithCollectFlagDTO> getMyCreatePortalList(Long id);

    /**
     * 获取用户收藏的站点id列表
     *
     * @param id Long
     * @return List
     */
    List<Long> getMyCollectPortalIdList(Long id);

    /**
     * 获取用户收藏的站点列表
     *
     * @param portalIdList List
     * @return List
     */
    List<PortalWithCollectFlagDTO> getMyCollectPortalList(@Param("portalIdList") List<Long> portalIdList, @Param("userId") Long userId);

    /**
     * 获取共享的站点id列表
     *
     * @param id Long
     * @return List
     */
    List<Long> getSharedPageIdList(Long id);

    /**
     * 通过站点id获取站点信息
     *
     * @param id Long
     * @return Portal
     */
    Portal getPortalInfoById(Long id);

    /**
     * 创建站点
     *
     * @param portalCreateDTO PortalCreateDTO
     */
    Integer createPortal(PortalCreateDTO portalCreateDTO);

    /**
     * 修改站点删除位
     *
     * @param deleted int
     * @param id      Long
     */
    Integer updatePortalDeletedById(int deleted, Long id);

    /**
     * 彻底删除站点（从数据库中移除）
     *
     * @param id Long
     */
    void deletePortalForever(Long id);

    /**
     * 更新站点更新时间
     *
     * @param portalId Long
     */
    void updatePortalUpdateTime(Long portalId);

    /**
     * 更新（修改）站点信息
     *
     * @param portalUpdateDTO PortalUpdateDTO
     */
    Integer updatePortalInfo(PortalUpdateDTO portalUpdateDTO);

    /**
     * 搜索栏用关键词搜索站点
     *
     * @param keyword String
     * @return PageInfo<Portal>
     */
    List<PortalWithCollectFlagDTO> searchPortalByKeyword(List<Long> portalIdList, String keyword, Long userId);


    /**
     * 搜索栏用关键词搜索站点（在收藏中）
     * @param portalIdList List<Long> 站点id列表
     * @param keyword String 搜索关键词
     * @param userId  Long 用户id
     * @return
     */
    List<PortalWithCollectFlagDTO> searchPortalByKeywordInCollect(List<Long> portalIdList, String keyword, Long userId);


    /**
     * 修改站点创建者
     *
     * @param userName String 用户姓名
     * @param userId   Long 用户id
     */
    void modifyPortalCreateBy(String userName, Long userId);

    /**
     * 获取回收站的站点列表(deleted = 1 or status = -1)
     */
    List<Portal> getDeletedPortal(Long id);

    /**
     * 根据标签获取站点列表(默认限制20个)
     *
     * @param userId Long
     * @param tagId  Long
     * @return List<PortalWithCollectFlagDTO>
     */
    List<PortalWithCollectFlagDTO> getPortalByTag(@Param("userId") Long userId, @Param("tagId") Long tagId, @Param("portalClassifyNumDTO") PortalClassifyNumDTO portalClassifyNumDTO);

    /**
     * 根据标签获取站点列表总数
     *
     * @param userId Long
     * @param tagId  Long
     * @return Integer
     */
    Integer getPortalByTagCount(Long userId, Long tagId);

    /**
     * 根据标签获取站点列表（单个标签且分页）
     *
     * @param userId Long
     * @param portalGetByTagDTO PortalGetByTagDTO
     * @return List<PortalWithCollectFlagDTO>
     */
    List<PortalWithCollectFlagDTO> getPortalByOneTag(@Param("userId") Long userId, @Param("portalGetByTagDTO") PortalGetByTagDTO portalGetByTagDTO);

    /**
     * 获取没有标签的站点
     * @param userId Long
     * @param portalClassifyNumDTO PortalClassifyNumDTO
     * @return List<PortalWithCollectFlagDTO>
     */
    List<PortalWithCollectFlagDTO> getPortalNoTag(@Param("userId") Long userId, @Param("portalClassifyNumDTO") PortalClassifyNumDTO portalClassifyNumDTO);

    /**
     * 获取站点是否公开
     *
     * @param portalId Long
     * @return Integer
     */
    Short getPortalPublicFlag(Long portalId);

    /**
     * 获取站点是否属于当前用户
     *
     * @param portalId Long
     * @param userId   Long
     * @return Integer
     */
    Integer getPortalIsOwn(Long portalId, Long userId);

    /**
     * 获取没有标签的站点总数
     * @param userId Long
     * @return
     */
    Integer getPortalNoTagCount(Long userId);
}
