package cn.icheer.portal.controller;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.dto.*;
import cn.icheer.portal.entity.User;
import cn.icheer.portal.enums.HttpStatusEnum;
import cn.icheer.portal.result.ResponseResult;
import cn.icheer.portal.service.UserService;
import cn.icheer.portal.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author fangweihao
 * @since 2025/7/3 11:03
 */
@Slf4j
@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户通过密码登录
     *
     * @param userLoginByPasswordDTO 包括phoneNumber、password
     * @return UserLoginByPasswordResultDTO
     */
    @RequestMapping("/open/loginByPassword")
    public ResponseResult loginByPassword(@RequestBody @Valid UserLoginByPasswordDTO userLoginByPasswordDTO) {
        UserLoginByPasswordResultDTO userLoginByPasswordResultDTO = userService.loginByPassword(userLoginByPasswordDTO);
        int status = userLoginByPasswordResultDTO.getStatus();
        switch (status) {
            case Constants.ACCOUNT_NOT_FOUND:
                log.warn("User login failed: phoneNumber {} not found.", userLoginByPasswordDTO.getPhoneNumber());
                return ResponseResult.fail(HttpStatusEnum.NOT_FOUND.getCode(), "账号不存在");
            case Constants.ACCOUNT_NO_PASSWORD:
                log.warn("User login failed: phoneNumber {} not set password.", userLoginByPasswordDTO.getPhoneNumber());
                return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "账号未设置密码");
            case Constants.PASSWORD_ERROR:
                log.warn("User login failed: phoneNumber {} password error.", userLoginByPasswordDTO.getPhoneNumber());
                return ResponseResult.fail(HttpStatusEnum.UNAUTHORIZED.getCode(), "密码错误");
            case Constants.LOGIN_SUCCESS:
                log.info("User login success: phoneNumber {} .", userLoginByPasswordDTO.getPhoneNumber());
                return ResponseResult.success("登录成功", userLoginByPasswordResultDTO);
            default:
                log.error("User login failed: unknown error.");
                return ResponseResult.fail("未知错误");
        }
    }

    /**
     * 用户通过企业微信快速登录
     *
     * @param userQuickLoginByEpAuthDTO 包括code
     * @return UserQuickLoginResultDTO
     */
    @RequestMapping("/open/quickLoginByEpAuth")
    public ResponseResult quickLoginByEpAuth(@RequestBody @Valid UserQuickLoginByEpAuthDTO userQuickLoginByEpAuthDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 校验失败
            log.warn("User quick login failed: parameter validation error.");
            return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "参数校验失败");
        }

        log.info("User quick login success: code {} .", userQuickLoginByEpAuthDTO.getCode());
        return ResponseResult.success("登录成功", userService.quickLoginByEpAuth(userQuickLoginByEpAuthDTO));
    }

    /**
     * 用户登出
     *
     * @return
     */
    @PostMapping("/open/logout")
    public ResponseResult logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
        // 清除上下文信息，防止重复登出
        if (authentication != null) {
            SecurityContextHolder.clearContext();
        }
        // 清除token
        String token = request.getHeader("Authorization");
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        JWTUtils.invalidateToken(token);
        return ResponseResult.success("登出成功", "redirect:/login");
    }

    /**
     * 注册功能
     *
     * @param userRegisterDTO 包括phoneNumber、password、repeatPassword
     * @return Integer
     */
    @RequestMapping("/open/register")
    public ResponseResult register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        int result = userService.register(userRegisterDTO);
        switch (result) {
            case Constants.ACCOUNT_EXIST:
                log.warn("User register failed: phoneNumber {} already exists.", userRegisterDTO.getPhoneNumber());
                return ResponseResult.fail(HttpStatusEnum.CONFLICT.getCode(), "账号已被注册");
            case Constants.REPEAT_PASSWORD_ERROR:
                log.warn("User register failed: phoneNumber {} repeat password error.", userRegisterDTO.getPhoneNumber());
                return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "重复密码错误");
            case Constants.REGISTER_SUCCESS:
                log.info("User register success: phoneNumber {} .", userRegisterDTO.getPhoneNumber());
                return ResponseResult.success("注册成功");
            default:
                log.error("User register failed: unknown error.");
                return ResponseResult.fail("未知错误");
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return User
     */
    @RequestMapping("/portal/getUserInfo")
    public ResponseResult getUserInfoById() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JWTUserDetails jwtUserDetails = (JWTUserDetails) authentication.getPrincipal();
            log.info("Get user info success: userId {} .", jwtUserDetails.getId());
            return ResponseResult.success(userService.getUserInfoById(jwtUserDetails.getId()));
        } catch (Exception e) {
            log.error("Get user info failed: {}", e.getMessage());
            return ResponseResult.fail(HttpStatusEnum.UNAUTHORIZED.getCode(), "未登录或token失效");
        }
    }

    /**
     * 修改用户密码
     *
     * @param userChangePasswordDTO 包括oldPassword、newPassword、repeatPassword
     * @return Integer
     */
    @RequestMapping("/portal/changePassword")
    public ResponseResult changePassword(@RequestBody @Valid UserChangePasswordDTO userChangePasswordDTO) {
        Integer result = userService.changePassword(userChangePasswordDTO);
        switch (result) {
            case Constants.ACCOUNT_NO_PASSWORD:
                log.warn("User change password failed: no password set.");
                return ResponseResult.fail(HttpStatusEnum.NOT_FOUND.getCode(), "账号未设置密码");
            case Constants.PASSWORD_ERROR:
                log.warn("User change password failed: old password error.");
                return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "旧密码错误");
            case Constants.REPEAT_PASSWORD_ERROR:
                log.warn("User change password failed: repeat password error.");
                return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "重复密码错误");
            case Constants.PASSWORD_MODIFY_SUCCESS:
                log.info("User change password success.");
                return ResponseResult.success("修改成功");
            default:
                log.error("User change password failed: unknown error.");
                return ResponseResult.fail("未知错误");
        }
    }

    /**
     * 设置用户密码
     *
     * @param userSetPasswordDTO 包括password、repeatPassword
     * @return Integer
     */
    @RequestMapping("/portal/setPassword")
    public ResponseResult setPassword(@RequestBody @Valid UserSetPasswordDTO userSetPasswordDTO) {
        Integer result = userService.setPassword(userSetPasswordDTO);
        if (result == Constants.ACCOUNT_EXIST_PASSWORD) {
            log.warn("User set password failed: password already exists.");
            return ResponseResult.fail(HttpStatusEnum.CONFLICT.getCode(), "账号已有密码");
        } else if (result == Constants.REPEAT_PASSWORD_ERROR) {
            log.warn("User set password failed: repeat password error.");
            return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), "重复密码错误");
        } else if (result == Constants.PASSWORD_MODIFY_SUCCESS) {
            log.info("User set password success.");
            return ResponseResult.success("密码设置成功");
        } else {
            log.error("User set password failed: unknown error.");
            return ResponseResult.fail("未知错误");
        }
    }

    /**
     * 修改用户信息
     *
     * @param userModifyInfoDTO 包括avatar、userName
     * @return void
     */
    @RequestMapping("/portal/modifyUserInfo")
    public ResponseResult modifyUserInfo(@RequestBody UserModifyInfoDTO userModifyInfoDTO) {
        String token = userService.modifyUserInfo(userModifyInfoDTO);
        if (token == null || token.isEmpty()) {
            log.error("User modify info failed: generate new token error.");
            return ResponseResult.fail("修改失败");
        } else {
            log.info("User modify info success: token {} .", token);
            return ResponseResult.success("修改成功", token);
        }
    }
}
