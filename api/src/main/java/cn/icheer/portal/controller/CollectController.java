package cn.icheer.portal.controller;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.dto.CollectPortalDTO;
import cn.icheer.portal.dto.CollectRemarkDTO;
import cn.icheer.portal.dto.CollectTopDTO;
import cn.icheer.portal.dto.PortalIdDTO;

import cn.icheer.portal.enums.HttpStatusEnum;
import cn.icheer.portal.result.ResponseResult;
import cn.icheer.portal.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author fangweihao
 * @since 2025/7/8 8:53
 */
@Slf4j
@RestController
public class CollectController {
    @Resource
    private CollectService collectService;

    /**
     * 收藏站点（可收藏也可取消）
     *
     * @param collectPortalDTO CollectPortalDTO
     * @return Integer
     */
    @RequestMapping("/portal/collectPortal")
    public ResponseResult collectPortal(@RequestBody @Valid CollectPortalDTO collectPortalDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JWTUserDetails user = (JWTUserDetails) authentication.getPrincipal();
            log.debug("Collect portal: userId: {} .", user.getId());

            if (collectService.collectPortal(collectPortalDTO, user.getId()) == Constants.OPERATION_SUCCESS) {
                log.info("Collect portal success: userId: {} .", user.getId());

                return ResponseResult.success();
            } else {
                log.info("Collect portal failed: userId: {} .", user.getId());
                return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "操作失败");
            }


        } catch (Exception e) {

            log.error("Get user info failed: {}", e.getMessage());
            return ResponseResult.fail(HttpStatusEnum.UNAUTHORIZED.getCode(), "未登录或token失效");
        }
    }

    /**
     * 置顶站点（可置顶也可取消）(我的收藏里)
     * @param collectTopDTO CollectTopDTO
     * @return
     */
    @RequestMapping("/portal/collectTop")
    public ResponseResult collectTop(@RequestBody @Valid CollectTopDTO collectTopDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JWTUserDetails user = (JWTUserDetails) authentication.getPrincipal();
            log.debug("Top portal: userId: {} .", user.getId());

            if (collectService.collectTop(collectTopDTO, user.getId()) == Constants.OPERATION_SUCCESS) {
                log.debug("Top portal success .");
                return ResponseResult.success();
            } else {
                log.debug("Top portal failed .");
                return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "操作失败");
            }

        } catch (Exception e) {

            log.error("Get user info failed: {}", e.getMessage());
            return ResponseResult.fail(HttpStatusEnum.UNAUTHORIZED.getCode(), "未登录或token失效");
        }

    }

    /**
     * 获取收藏的备注
     * @param portalIdDTO PortalIdDTO
     * @return String
     */
    @RequestMapping("/portal/getCollectRemark")
    public ResponseResult getCollectRemark(@RequestBody @Valid PortalIdDTO portalIdDTO) {
        String remark = collectService.getCollectRemark(portalIdDTO.getPortalId());
        return ResponseResult.success("操作成功", remark);
    }

    /**
     * 修改收藏备注
     * @param collectRemarkDTO CollectRemarkDTO
     * @return ResponseResult
     */
    @RequestMapping("/portal/modifyCollectRemark")
    public ResponseResult modifyCollectRemark(@RequestBody @Valid CollectRemarkDTO collectRemarkDTO) {
        int flag = collectService.modifyCollectRemark(collectRemarkDTO);
        if (flag == Constants.OPERATION_SUCCESS) {
            return ResponseResult.success("修改成功");
        } else {
            return ResponseResult.fail("修改失败");
        }
    }
}
