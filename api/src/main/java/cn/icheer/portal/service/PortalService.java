package cn.icheer.portal.service;

import cn.icheer.portal.dto.*;
import cn.icheer.portal.entity.Portal;
import cn.icheer.portal.enums.HttpStatusEnum;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author fangweihao
 * @since 2025/7/8 10:53
 */
public interface PortalService {

    /**
     * 获取我创建的站点列表
     * @param portalPageCurrentDTO PortalPageCurrentDTO
     * @return PageInfo
     */
    PageInfo<PortalWithCollectFlagDTO> getMyCreatePageList(PortalPageCurrentDTO portalPageCurrentDTO);

    /**
     * 获取我收藏的站点列表
     * @param portalPageCurrentDTO PortalPageCurrentDTO
     * @return PageInfo
     */
    PageInfo<PortalWithCollectFlagDTO> getMyCollectPageList(PortalPageCurrentDTO portalPageCurrentDTO);

    /**
     * 通过id获取站点信息
     * @param portalId Long
     * @return Portal
     */
    Portal getPortalInfoById(Long portalId);

    /**
     * 创建站点
     * @param portalCreateDTO PortalCreateDTO
     */
    Integer createPortal(PortalCreateDTO portalCreateDTO);

    /**
     * 删除站点(放进回收站)
     * @param portalId Long
     * @return Integer
     */
    Integer deletePortalById(Long portalId);

    /**
     * 彻底删除站点（从数据库中移除）
     * @param portalId Long
     * @return Integer
     */
    Integer deletePortalForever(Long portalId);

    /**
     * 更新（修改）站点信息
     * @param portalUpdateDTO PortalUpdateDTO
     * @return Integer
     */
    Integer updatePortalInfo(PortalUpdateDTO portalUpdateDTO);

    /**
     * 搜索栏用关键词搜索站点
     * @param portalSearchByKeywordDTO PortalSearchByKeywordDTO
     * @return PageInfo<PortalClassifyByTagsDTO>
     */
    PageInfo<PortalWithCollectFlagDTO> searchPortalByKeyword(PortalSearchByKeywordDTO portalSearchByKeywordDTO);

    /**
     * 根据标签获取站点列表(每一个标签都返回)
     * @return List<PortalClassifyByTagsDTO>
     */
    List<PortalClassifyByTagsDTO> getAllPortalByTags(PortalClassifyNumDTO portalClassifyNumDTO);

    /**
     * 根据标签获取站点列表（单个标签且分页）
     * @param portalGetByTagDTO PortalGetByTagDTO
     * @return PageInfo<PortalClassifyByTagsDTO>
     */
    PageInfo<PortalWithCollectFlagDTO> getPortalByOneTag(PortalGetByTagDTO portalGetByTagDTO);
    /**
     * 点击站点链接后更新最后访问时间（用于更新最近访问）
     * @param portalId Long
     */
    Integer clickPortalUrl(Long portalId);

    /**
     * 修改站点创建者
     * @param userName String
     * @param userId Long
     */
    void modifyPortalCreateBy(String userName, Long userId);

    /**
     * 获取回收站的站点列表(deleted = 0 or status = -1)
     * @return PageInfo<Portal>
     */
    PageInfo<Portal> getDeletedPortal(PortalPageCurrentDTO portalPageCurrentDTO);

    /**
     * 恢复已删除的站点
     * @param portalId Long
     */
    Integer restoreDeletedPortal(Long portalId);

}
