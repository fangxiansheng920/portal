package cn.icheer.portal.service.impl;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.dto.*;
import cn.icheer.portal.entity.Portal;
import cn.icheer.portal.entity.Tag;
import cn.icheer.portal.entity.UserPortal;
import cn.icheer.portal.mapper.PortalMapper;
import cn.icheer.portal.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author fangweihao
 * @since 2025/7/8 10:53
 */
@Slf4j
@Service
public class PortalServiceImpl implements PortalService {
    @Resource
    private PortalMapper portalMapper;

    @Resource
    private CollectService collectService;

    @Resource
    private UserPortalService userPortalService;

    @Resource
    private TagService tagService;

    @Resource
    private PortalTagService portalTagService;

    /**
     * 查询并设置站点是否被收藏
     *
     * @param portalList List
     */
    private void setCollectFlag(List<PortalWithCollectFlagDTO> portalList) {
        // 遍历给collectFlag字段赋值
        for (PortalWithCollectFlagDTO portalWithCollectFlagDTO : portalList) {
            portalWithCollectFlagDTO.setCollectFlag(collectService.getPortalDoCollect(portalWithCollectFlagDTO.getId()));
            portalWithCollectFlagDTO.setCollectRemark(collectService.getCollectRemark(portalWithCollectFlagDTO.getId()));
        }
    }

    /**
     * 查询并设置站点是否被置顶
     *
     * @param portalList List
     */
    private void setIsTop(List<PortalWithCollectFlagDTO> portalList) {
        // 遍历给isTop字段赋值
        for (PortalWithCollectFlagDTO portalWithCollectFlagDTO : portalList) {
            portalWithCollectFlagDTO.setIsTop(collectService.getCollectPortalIsTop(portalWithCollectFlagDTO.getId()));
        }
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return JWTUserDetails
     */
    private JWTUserDetails getJwtUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取当前登录的用户信息
        return (JWTUserDetails) authentication.getPrincipal();
    }

    /**
     * 获取我创建的站点列表
     *
     * @param portalPageCurrentDTO portalPageCurrentDTO
     * @return PageInfo
     */
    @Override
    public PageInfo<PortalWithCollectFlagDTO> getMyCreatePageList(PortalPageCurrentDTO portalPageCurrentDTO) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        if (user == null) {
            log.debug("User is not login.");
            return new PageInfo<>(new ArrayList<>());
        }
        PageHelper.startPage(portalPageCurrentDTO.getPageCurrent(), portalPageCurrentDTO.getPageSize());
        List<PortalWithCollectFlagDTO> portalList = portalMapper.getMyCreatePortalList(user.getId());
        setCollectFlag(portalList);
        log.debug("Get my create page list success.");
        return new PageInfo<>(portalList);
    }

    /**
     * 获取我收藏的站点列表
     *
     * @param portalPageCurrentDTO portalPageCurrentDTO
     * @return PageInfo
     */
    @Override
    public PageInfo<PortalWithCollectFlagDTO> getMyCollectPageList(PortalPageCurrentDTO portalPageCurrentDTO) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        if (user == null) {
            log.debug("User is not login.");
            return new PageInfo<>(new ArrayList<>());
        }
        // 获取用户收藏的站点id列表
        List<Long> portalIdList = portalMapper.getMyCollectPortalIdList(user.getId());
        // 需在此处判断一下是否为空，否则空列表进入sql查询会出现逻辑上的错误
        if (portalIdList == null || portalIdList.isEmpty()) {
            PageInfo<PortalWithCollectFlagDTO> pageInfo = new PageInfo<>(new ArrayList<>());
            pageInfo.setPageSize(portalPageCurrentDTO.getPageSize());
            return pageInfo;
        }
        PageHelper.startPage(portalPageCurrentDTO.getPageCurrent(), portalPageCurrentDTO.getPageSize());
        List<PortalWithCollectFlagDTO> portalList = portalMapper.getMyCollectPortalList(portalIdList, user.getId());
        setCollectFlag(portalList);
        // 设置置顶信息
        setIsTop(portalList);
        log.debug("Get my collect page list success.");
        return new PageInfo<>(portalList);
    }

    /**
     * 通过id获取站点信息
     *
     * @param portalId int
     * @return PortalGetInfoDTO
     */
    @Override
    public Portal getPortalInfoById(Long portalId) {
        Portal portal = portalMapper.getPortalInfoById(portalId);

        if (portal == null) {
            log.debug("Portal is not exist.");
            return portal;
        }
        /*
            1、先查看用户和站点之间有没有user_portals的记录
            2、没有：加入user_portals 记录
            3、有：更新last_use_at字段
         */

        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        UserPortal userPortal = userPortalService.getUserPortal(user.getId(), portalId);
        if (userPortal == null) {
            // 没有记录，加入一条记录
            userPortalService.addUserPortal(user.getId(), portal.getId());
        } else {
            // 有记录，更新last_use_at字段
            userPortalService.updateUserPortalLastUseAt(user.getId(), portal.getId());
        }
        log.debug("Get portal info by id success.");
        return portal;
    }

    /**
     * 创建站点
     *
     * @param portalCreateDTO PortalCreateDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createPortal(PortalCreateDTO portalCreateDTO) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        portalCreateDTO.setUserId(user.getId());
        portalCreateDTO.setCreateBy(user.getUsername());
        // 判断标签是否存在
        Set<Long> tagIdSet = new HashSet<>(portalCreateDTO.getTagIdSet());
        for (Long tagId : tagIdSet) {
            if (tagService.getIdIsExist(tagId) == Constants.TAG_NOT_EXIST) {
                log.debug("Tag is not exist.");
                return Constants.TAG_NOT_EXIST;
            }
        }


        // 获取站点id
        Long portalId = null;
        // 判断创建站点是否成功
        try {
            portalMapper.createPortal(portalCreateDTO);
            portalId = portalCreateDTO.getId();
            log.info("Create portal success, portalId = {} .", portalId);
        } catch (Exception e) {
            log.debug("Create portal failed.");
            throw e;
        }

        // 创建站点成功后，加入站点标签portal_tag记录
        if (!tagIdSet.isEmpty()) {
            try {
                portalTagService.addPortalTag(portalId, tagIdSet);
                log.debug("Add portal tag success, portalId = {}.", portalId);
            } catch (Exception e) {
                log.debug("Add portal tag failed, portalId = {}.", portalId);
                throw e;
            }
        }

        // 创建站点成功后，加入user_portals记录
        userPortalService.addUserPortal(user.getId(), portalId);

        // 判断一下collectFlag的值，如果为1，自动加入收藏
        if (portalCreateDTO.getCollectFlag() == 1) {
            collectService.collectPortal(new CollectPortalDTO(portalId, (short) 1), user.getId());
        }
        return Constants.OPERATION_SUCCESS;
    }

    /**
     * 删除站点(软删除，放进回收站)
     * 0：删除失败，站点不是自己创建的
     * 1：删除成功
     * 12: 已被收藏，删除失败
     *
     * @param portalId Long
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deletePortalById(Long portalId) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();

        Portal portal = portalMapper.getPortalInfoById(portalId);
        // 如果portal为空，删除失败
        if (portal == null) {
            return Constants.PORTAL_NOT_EXIST;
        }
        // 如果不是自己创建的站点，无法删除
        if (!Objects.equals(portal.getUserId(), user.getId())) {
            return Constants.OPERATION_FAILED;
        }
        // 如果是自己创建的，修改portal的删除位（deleted）
        portalMapper.updatePortalDeletedById(1, portal.getId());
        log.debug("Delete portal success, portalId = {} .", portal.getId());
        // 更新站点修改时间
        portalMapper.updatePortalUpdateTime(portalId);
        log.debug("Update portal update time success, portalId = {} .", portal.getId());
        return Constants.OPERATION_SUCCESS;
    }

    /**
     * 彻底删除站点（从数据库中移除）
     *
     * @param portalId Long
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deletePortalForever(Long portalId) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        Portal portal = portalMapper.getPortalInfoById(portalId);

        // 如果portal为空，删除失败
        if (portal == null) {
            return Constants.PORTAL_NOT_EXIST;
        }

        // 如果该站点已被其他人收藏，则不允许删除
        if (collectService.isCollectByAnyone(portalId) == Constants.IS_COLLECT_BY_ANYONE) {
            return Constants.IS_COLLECT_BY_ANYONE;
        }

        // 如果不是自己创建的站点，无法删除
        if (!Objects.equals(portal.getUserId(), user.getId())) {
            return Constants.OPERATION_FAILED;
        }

        // 如果是自己创建的，直接从数据库中移除
        portalMapper.deletePortalForever(portal.getId());
        log.debug("Delete portal forever success, portalId = {} .", portal.getId());

        // 删除portal_tag表里关于要删除站点的数据
        portalTagService.deletePortalTagByPortalId(portal.getId());
        log.debug("Delete portal tag success, portalId = {} .", portal.getId());

        // 删除collect表里关于要删除站点的数据
        collectService.deleteCollectByPortalId(portal.getId());
        log.debug("Delete collect success, portalId = {} .", portal.getId());

        // 删除user_portals里的相关数据
        userPortalService.deleteUserPortalsByPortalId(portal.getId());
        log.debug("Delete user_portal success, portalId = {} , userId = {} .", portal.getId(), user.getId());
        return Constants.OPERATION_SUCCESS;
    }

    /**
     * 更新（修改）站点信息
     *
     * @param portalUpdateDTO PortalUpdateDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updatePortalInfo(PortalUpdateDTO portalUpdateDTO) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();

        // publicFlag用来判断站点是否公开
        Short publicFlag = portalMapper.getPortalPublicFlag(portalUpdateDTO.getPortalId());
        Integer isOwn = portalMapper.getPortalIsOwn(portalUpdateDTO.getPortalId(), user.getId());

        // 没有权限修改站点信息
        if (publicFlag == 0 && isOwn == 0) {
            log.debug("No permission to modify portal.");
            return Constants.NO_PERMISSION_MODIFY;
        }

        // 没有权限更改public_flag，getPublicFlag用来判断是否要修改publicFlag字段
        if (portalUpdateDTO.getPublicFlag() == 0 && isOwn == 0) {
            log.debug("No permission to modify public_flag.");
            return Constants.NO_PERMISSION_MODIFY;
        }

        // 获取更新的tagId集合
        Set<Long> newTagIdSet = new HashSet<>(portalUpdateDTO.getTagIdSet());
        // 获取现有的tagId集合
        Set<Long> nowTagIdSet = portalTagService.getPortalTagIdSet(portalUpdateDTO.getPortalId());
        // 判断更新的tag是否存在
        for (Long tagId : newTagIdSet) {
            if (tagService.getIdIsExist(tagId) == Constants.TAG_NOT_EXIST) {
                log.debug("Tag is not exist.");
                return Constants.TAG_NOT_EXIST;
            }
        }

        Set<Long> deleteSet = new HashSet<>(nowTagIdSet);
        Set<Long> addSet = new HashSet<>(newTagIdSet);
        // 1、需要删除的tagId集合 nowTagIdSet - newTagIdSet
        deleteSet.removeAll(newTagIdSet);
        // 2、需要增加的tagId集合 newTagIdSet - nowTagIdSet
        addSet.removeAll(nowTagIdSet);

        // 删除不需要的tagId集合
        if (!deleteSet.isEmpty()) {
            portalTagService.deletePortalTag(portalUpdateDTO.getPortalId(), deleteSet);
        }

        // 增加新的tagId集合
        if (!addSet.isEmpty()) {
            portalTagService.addPortalTag(portalUpdateDTO.getPortalId(), addSet);
        }

        // 更新站点信息
        if (portalMapper.updatePortalInfo(portalUpdateDTO) == 0) {
            log.debug("Update portal failed.");
            return Constants.OPERATION_FAILED;
        }
        log.info("Update portal success, portalId = {} .", portalUpdateDTO.getPortalId());

        UserPortal userPortal = userPortalService.getUserPortal(user.getId(), portalUpdateDTO.getPortalId());
        if (userPortal == null) {
            // 没有记录，加入一条记录
            userPortalService.addUserPortal(user.getId(), portalUpdateDTO.getPortalId());
        } else {
            // 有记录，更新last_use_at字段
            userPortalService.updateUserPortalLastUseAt(user.getId(), portalUpdateDTO.getPortalId());
        }
        return Constants.OPERATION_SUCCESS;
    }

    /**
     * 搜索栏用关键词搜索站点
     *
     * @param portalSearchByKeywordDTO PortalSearchByKeywordDTO
     * @return PageInfo<Portal>
     */
    @Override
    public PageInfo<PortalWithCollectFlagDTO> searchPortalByKeyword(PortalSearchByKeywordDTO portalSearchByKeywordDTO) {
        Integer pageIndex = portalSearchByKeywordDTO.getPageIndex();
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();

        // 决定搜出来几个一页
        Integer pageSize = portalSearchByKeywordDTO.getPageSize();
        List<Long> portalIdList = new ArrayList<>();
        if (pageIndex == 1) {
            // 所有站点
            portalIdList = portalMapper.getAllPortalIdList(user.getId());
        } else if (pageIndex == 2) {
            // 我的收藏
            portalIdList = portalMapper.getMyCollectPortalIdList(user.getId());
        } else if (pageIndex == 3) {
            // 我的创建
            portalIdList = portalMapper.getMyCreatePortalIdList(user.getId());
        } else if (pageIndex == 4) {
            // 共享站点
            portalIdList = portalMapper.getSharedPageIdList(user.getId());
        } else if (pageIndex == 5) {
            // 大搜索
            portalIdList = portalMapper.getAllPortalIdList(user.getId());
        }
        if (portalIdList == null || portalIdList.isEmpty()) {
            PageInfo<PortalWithCollectFlagDTO> pageInfo = new PageInfo<>(new ArrayList<>());
            pageInfo.setPageSize(portalSearchByKeywordDTO.getPageSize());
            return pageInfo;
        }
        PageHelper.startPage(portalSearchByKeywordDTO.getPageCurrent(), pageSize);
        // 根据提取出来的id进行查找站点列表
        List<PortalWithCollectFlagDTO> portalList = new ArrayList<>();
        if (pageIndex == 2) {
            portalList = portalMapper.searchPortalByKeywordInCollect(portalIdList, portalSearchByKeywordDTO.getKeyword(), user.getId());
            setCollectFlag(portalList);
            log.debug("Search portal by keyword success.");
            setIsTop(portalList);
        } else {
            portalList = portalMapper.searchPortalByKeyword(portalIdList, portalSearchByKeywordDTO.getKeyword(), user.getId());
            setCollectFlag(portalList);

            log.debug("Search portal by keyword success.");
        }
        return new PageInfo<>(portalList);
    }

    /**
     * 修改站点创建者
     *
     * @param userName String
     * @param userId   Long
     */
    @Override
    public void modifyPortalCreateBy(String userName, Long userId) {
        portalMapper.modifyPortalCreateBy(userName, userId);
        log.debug("Modify portal create by success.");
    }

    /**
     * 根据标签获取站点列表(每一个标签都返回)
     *
     * @return List<PortalClassifyByTagsDTO>
     */
    @Override
    public List<PortalClassifyByTagsDTO> getAllPortalByTags(PortalClassifyNumDTO portalClassifyNumDTO) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        // 需要返回的list
        List<PortalClassifyByTagsDTO> list = new ArrayList<>();
        // 获取所有标签
        List<Tag> tagList = tagService.getAllTags();
        // 根据标签获取站点列表
        tagList.forEach(tag -> {
            PortalClassifyByTagsDTO portalClassifyByTagsDTO = new PortalClassifyByTagsDTO();

            // 默认返回20个，具体看portalClassifyNumDTO
            List<PortalWithCollectFlagDTO> portalList = portalMapper.getPortalByTag(user.getId(), tag.getId(), portalClassifyNumDTO);

            if (portalList != null && !portalList.isEmpty()) {
                // 设置收藏情况，用于返回是否收藏（portalWithCollectFlagDTO中的CollectFlag字段）
                setCollectFlag(portalList);
                // 设置标签
                portalClassifyByTagsDTO.setTag(tag);
                // 把站点列表放进portalClassifyByTagsDTO中
                portalClassifyByTagsDTO.setPortals(portalList);
                // 设置查询到的站点总个数（不是20个，20个只是限制返回）
                portalClassifyByTagsDTO.setPortalTotal(portalMapper.getPortalByTagCount(user.getId(), tag.getId()));
                list.add(portalClassifyByTagsDTO);
            }
        });

        PortalClassifyByTagsDTO portalClassifyByTagsDTO = new PortalClassifyByTagsDTO();
        // 查询没有标签的站点
        List<PortalWithCollectFlagDTO> portalList = portalMapper.getPortalNoTag(user.getId(), portalClassifyNumDTO);
        if (portalList != null && !portalList.isEmpty()) {
            // 设置收藏情况，用于返回是否收藏（portalWithCollectFlagDTO中的CollectFlag字段）
            setCollectFlag(portalList);
            // 设置标签
            portalClassifyByTagsDTO.setTag(new Tag(null, "空标签", null, null));
            // 把站点列表放进portalClassifyByTagsDTO中
            portalClassifyByTagsDTO.setPortals(portalList);
            // 设置查询到的站点总个数（不是20个，20个只是限制返回）
            portalClassifyByTagsDTO.setPortalTotal(portalMapper.getPortalNoTagCount(user.getId()));
            list.add(portalClassifyByTagsDTO);
        }

        log.debug("Get all portal by every tags success.");
        return list;
    }

    /**
     * 根据标签获取站点列表（单个标签且分页）
     *
     * @param portalGetByTagDTO PortalGetByTagDTO
     * @return PageInfo<PortalClassifyByTagsDTO>
     */
    @Override
    public PageInfo<PortalWithCollectFlagDTO> getPortalByOneTag(PortalGetByTagDTO portalGetByTagDTO) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        if (user == null) {
            log.debug("User is not login.");
            return new PageInfo<>(new ArrayList<>());
        }
        PageHelper.startPage(portalGetByTagDTO.getPageNum(), portalGetByTagDTO.getPageSize());
        List<PortalWithCollectFlagDTO> portalList = portalMapper.getPortalByOneTag(user.getId(), portalGetByTagDTO);
        setCollectFlag(portalList);
        log.debug("Get portal by tag success.");
        return new PageInfo<>(portalList);
    }

    /**
     * 点击站点链接后更新最后访问时间（用于更新最近访问）
     *
     * @param portalId Long
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer clickPortalUrl(Long portalId) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        UserPortal userPortal = userPortalService.getUserPortal(user.getId(), portalId);
        if (userPortal == null) {
            // 没有记录，加入一条记录
            userPortalService.addUserPortal(user.getId(), portalId);
        } else {
            // 有记录，更新last_use_at字段
            userPortalService.updateUserPortalLastUseAt(user.getId(), portalId);
        }
        log.debug("Update portal last use time success.");

        if (collectService.getPortalDoCollect(portalId) == 1) {
            collectService.modifyUpdateTime(portalId);
        }
        return 1;
    }

    /**
     * 获取回收站的站点列表(deleted = 1 or status = -1)
     *
     * @return PageInfo<Portal>
     */
    @Override
    public PageInfo<Portal> getDeletedPortal(PortalPageCurrentDTO portalPageCurrentDTO) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        if (user == null) {
            log.debug("User is not login.");
            return new PageInfo<>(new ArrayList<>());
        }
        PageHelper.startPage(portalPageCurrentDTO.getPageCurrent(), portalPageCurrentDTO.getPageSize());
        List<Portal> portalList = portalMapper.getDeletedPortal(user.getId());
        log.debug("Get deleted portal success.");
        return new PageInfo<>(portalList);
    }

    /**
     * 恢复已删除的站点
     *
     * @param portalId Long
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer restoreDeletedPortal(Long portalId) {
        // 获取当前登录的用户信息
        JWTUserDetails user = getJwtUserDetails();
        // 如果这个站点不是自己的，恢复不了
        if (portalMapper.getPortalIsOwn(portalId, user.getId()) == 0) {
            log.debug("Restore deleted portal failed, not own.");
            return Constants.OPERATION_FAILED;
        }
        // 恢复站点
        if (portalMapper.updatePortalDeletedById(0, portalId) == 0) {
            log.debug("Restore deleted portal failed.");
            return Constants.OPERATION_FAILED;
        }
        log.debug("Restore deleted portal success.");
        // 更新站点更新时间
        portalMapper.updatePortalUpdateTime(portalId);
        log.debug("Update portal update time success.");
        return Constants.OPERATION_SUCCESS;
    }


}
