package cn.icheer.portal.controller;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.dto.*;
import cn.icheer.portal.entity.Portal;
import cn.icheer.portal.enums.HttpStatusEnum;
import cn.icheer.portal.result.ResponseResult;
import cn.icheer.portal.service.CollectService;
import cn.icheer.portal.service.PortalService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


/**
 * @author fangweihao
 * @since 2025/7/8 10:52
 */
@Slf4j
@RestController
public class PortalController {
    @Resource
    private PortalService portalService;

    /**
     * 获取我创建的站点列表
     *
     * @param portalPageCurrentDTO PortalPageCurrentDTO
     * @return PageInfo<Portal>
     */
    @RequestMapping("/portal/myCreatePage")
    public ResponseResult getMyCreatePageList(@RequestBody PortalPageCurrentDTO portalPageCurrentDTO) {
        return ResponseResult.success(portalService.getMyCreatePageList(portalPageCurrentDTO));
    }

    /**
     * 获取我收藏的站点列表
     *
     * @param portalPageCurrentDTO PortalPageCurrentDTO
     * @return PageInfo<Portal>
     */
    @RequestMapping("/portal/myCollectPage")
    public ResponseResult getMyCollectPageList(@RequestBody PortalPageCurrentDTO portalPageCurrentDTO) {
        return ResponseResult.success(portalService.getMyCollectPageList(portalPageCurrentDTO));
    }


    /**
     * 通过id获取站点信息
     *
     * @param portalIdDTO PortalIdDTO
     * @return Portal
     */
    @RequestMapping("/portal/getPortalInfoById")
    public ResponseResult getPortalInfoById(@RequestBody @Valid PortalIdDTO portalIdDTO) {
        Long portalId = portalIdDTO.getPortalId();
        Portal portal = portalService.getPortalInfoById(portalId);
        if (portal == null) {
            return ResponseResult.fail(HttpStatusEnum.NOT_FOUND.getCode(), "站点不存在");
        } else {
            return ResponseResult.success(portal);
        }
    }

    /**
     * 创建站点
     *
     * @param portalCreateDTO PortalCreateDTO
     * @return Integer
     */
    @RequestMapping("/portal/createPortal")
    public ResponseResult createPortal(@RequestBody @Valid PortalCreateDTO portalCreateDTO) {
        int flag = portalService.createPortal(portalCreateDTO);
        if (flag == Constants.OPERATION_FAILED) {
            return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "创建失败");
        } else if (flag == Constants.TAG_NOT_EXIST) {
            return ResponseResult.fail(HttpStatusEnum.NOT_FOUND.getCode(), "标签不存在");
        }
        log.info("Create portal success, portalId = {} .", portalCreateDTO.getId());
        return ResponseResult.success(HttpStatusEnum.CREATED.getCode(), HttpStatusEnum.CREATED.getMessage(), portalCreateDTO.getId());
    }

    /**
     * 删除站点（软删除，放进回收站）
     *
     * @param portalIdDTO PortalIdDTO
     * @return Integer
     */
    @RequestMapping("/portal/deletePortal")
    public ResponseResult deletePortalById(@RequestBody @Valid PortalIdDTO portalIdDTO) {
        Long portalId = portalIdDTO.getPortalId();
        int flag = portalService.deletePortalById(portalId);
        if (flag == Constants.OPERATION_SUCCESS) {
            // 没人收藏，直接放进回收站
            log.info("Delete portal success, no one collect, portalId = {} .", portalId);
            return ResponseResult.success(HttpStatusEnum.NO_CONTENT.getCode(), "删除成功");
        } else if (flag == Constants.OPERATION_FAILED) {
            log.info("Delete portal failed, portalId = {} .", portalId);
            return ResponseResult.fail(HttpStatusEnum.FORBIDDEN.getCode(), "删除失败，站点不是自己创建的");
        } else {
            // 没有找到站点，删除失败
            log.info("Delete portal failed, not found portalId = {} .", portalId);
            return ResponseResult.fail(HttpStatusEnum.NOT_FOUND.getCode(), "删除失败，站点不存在");
        }
    }

    /**
     * 彻底删除站点（从数据库中移除）
     *
     * @param portalIdDTO PortalIdDTO
     * @return Integer
     */
    @RequestMapping("/portal/deletePortalForever")
    public ResponseResult deletePortalForever(@RequestBody @Valid PortalIdDTO portalIdDTO) {
        Long portalId = portalIdDTO.getPortalId();
        int flag = portalService.deletePortalForever(portalId);
        if (flag == Constants.OPERATION_SUCCESS) {
            // 删除成功
            log.info("Delete portal forever success, portalId = {} .", portalId);
            return ResponseResult.success(HttpStatusEnum.NO_CONTENT.getCode(), "删除成功");
        } else if (flag == Constants.IS_COLLECT_BY_ANYONE) {
            // 有人收藏，无法软删除
            log.info("Delete portal failed, portal be collected, portalId = {} .", portalId);
            return ResponseResult.fail(HttpStatusEnum.CONFLICT.getCode(), "删除失败，站点有人收藏");
        } else if (flag == Constants.OPERATION_FAILED) {
            // 删除失败，站点不是自己创建的
            log.info("Delete portal failed, portalId = {} .", portalId);
            return ResponseResult.fail(HttpStatusEnum.FORBIDDEN.getCode(), "删除失败，站点不是自己创建的");
        } else {
            // 没有找到站点，删除失败
            log.info("Delete portal failed, not found portalId = {} .", portalId);
            return ResponseResult.fail(HttpStatusEnum.NOT_FOUND.getCode(), "删除失败，站点不存在");
        }
    }

    /**
     * 更新（修改）站点信息
     *
     * @param portalUpdateDTO PortalUpdateDTO
     * @return Integer
     */
    @RequestMapping("/portal/updatePortalInfo")
    public ResponseResult updatePortalInfo(@RequestBody @Valid PortalUpdateDTO portalUpdateDTO) {
        Integer flag = portalService.updatePortalInfo(portalUpdateDTO);
        if (flag == Constants.OPERATION_FAILED) {
            return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "更新失败");
        }
        if (flag == Constants.NO_PERMISSION_MODIFY) {
            return ResponseResult.fail(HttpStatusEnum.FORBIDDEN.getCode(), "更新失败，没有权限更改publicFlag");
        }

        log.info("Update portal success, portalId = {} .", portalUpdateDTO.getPortalId());
        return ResponseResult.success("更新成功");
    }

    /**
     * 搜索栏用关键词搜索站点
     *
     * @param portalSearchByKeywordDTO PortalSearchByKeywordDTO
     * @return PageInfo<PortalClassifyByTagsDTO>
     */
    @RequestMapping("/portal/searchPortalByKeyword")
    public ResponseResult searchPortalByKeyword(@RequestBody @Valid PortalSearchByKeywordDTO portalSearchByKeywordDTO) {
        PageInfo<PortalWithCollectFlagDTO> pageInfo = portalService.searchPortalByKeyword(portalSearchByKeywordDTO);
        log.debug("Search portal success, keyword = {} .", portalSearchByKeywordDTO.getKeyword());
        return ResponseResult.success(pageInfo);
    }

    /**
     * 根据标签获取站点列表(每一个标签都返回)
     *
     * @return List<PortalClassifyByTagsDTO>
     */
    @RequestMapping("/portal/getAllPortalByTags")
    public ResponseResult getAllPortalByTags(@RequestBody PortalClassifyNumDTO portalClassifyNumDTO) {
        // 代码中默认设置每个tag返回20个站点
        List<PortalClassifyByTagsDTO> list = portalService.getAllPortalByTags(portalClassifyNumDTO);
        log.debug("Get all portal by every tags success.");
        return ResponseResult.success(list);
    }

    /**
     * 根据标签获取站点列表（单个标签且分页）
     *
     * @param
     * @return PageInfo<PortalClassifyByTagsDTO>
     */
    @RequestMapping("/portal/getPortalByOneTag")
    public ResponseResult getPortalByOneTag(@RequestBody @Valid PortalGetByTagDTO portalGetByTagDTO) {
        PageInfo<PortalWithCollectFlagDTO> pageInfo = portalService.getPortalByOneTag(portalGetByTagDTO);
        log.debug("Get portal by tag success.");
        return ResponseResult.success(pageInfo);
    }


    /**
     * 点击站点链接后更新最后访问时间（用于更新最近访问）
     *
     * @param portalIdDTO PortalIdDTO
     * @return
     */
    @RequestMapping("/portal/clickPortalUrl")
    public ResponseResult clickPortalUrl(@RequestBody @Valid PortalIdDTO portalIdDTO) {
        portalService.clickPortalUrl(portalIdDTO.getPortalId());
        return ResponseResult.success("更新成功");

    }

    /**
     * 获取回收站的站点列表(deleted = 1 or status = -1)
     *
     * @return
     */
    @RequestMapping("/portal/getDeletedPortal")
    public ResponseResult getDeletedPortal(@RequestBody PortalPageCurrentDTO portalPageCurrentDTO) {
        return ResponseResult.success(portalService.getDeletedPortal(portalPageCurrentDTO));
    }

    /**
     * 恢复已删除的站点
     *
     * @param portalIdDTO PortalIdDTO
     * @return Integer
     */
    @RequestMapping("/portal/restoreDeletedPortal")
    public ResponseResult restoreDeletedPortal(@RequestBody @Valid PortalIdDTO portalIdDTO) {
        if (portalService.restoreDeletedPortal(portalIdDTO.getPortalId()) == Constants.OPERATION_SUCCESS) {
            return ResponseResult.success("恢复成功");
        } else {
            return ResponseResult.fail("恢复失败");
        }
    }
}
