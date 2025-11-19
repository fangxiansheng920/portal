package cn.icheer.portal.service.impl;

import cn.icheer.portal.constant.Constants;
import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.dto.*;
import cn.icheer.portal.entity.User;
import cn.icheer.portal.mapper.PortalMapper;
import cn.icheer.portal.mapper.UserMapper;
import cn.icheer.portal.service.PortalService;
import cn.icheer.portal.service.UserService;
import cn.icheer.portal.utils.AesDecryptUtils;
import cn.icheer.portal.utils.JWTUtils;
import cn.icheer.portal.utils.MD5Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fangweihao
 * @since 2025/7/3 11:13
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PortalService portalService;

    /**
     * 用户通过密码登录
     *
     * @param userLoginByPasswordDTO 包括phoneNumber、password
     * @return UserLoginByPasswordResultDTO
     */
    @Override
    public UserLoginByPasswordResultDTO loginByPassword(UserLoginByPasswordDTO userLoginByPasswordDTO) {
        String phoneNumber = userLoginByPasswordDTO.getPhoneNumber();
        String password = userLoginByPasswordDTO.getPassword();

        User user = userMapper.getUserByPhoneNumber(phoneNumber);
        // 将用户输入的密码加密，方便对比
        password = MD5Utils.getPwd(password);

        UserLoginByPasswordResultDTO userLoginByPasswordResultDTO = new UserLoginByPasswordResultDTO();
        String token = "";
        /*
            ACCOUNT_NOT_FOUND: 无此账号
            ACCOUNT_NO_PASSWORD: 未设置密码
            PASSWORD_ERROR: 密码错误
            LOGIN_SUCCESS: 登录成功
         */
        if (user == null) {
            // user为空，数据库中无此用户
            userLoginByPasswordResultDTO.setStatus(Constants.ACCOUNT_NOT_FOUND);
        } else if (user.getPassword() == null) {
            // password为空，未设置密码
            userLoginByPasswordResultDTO.setStatus(Constants.ACCOUNT_NO_PASSWORD);
        } else if (!user.getPassword().equals(password)) {
            // 对比加密后的密码，如果错误
            userLoginByPasswordResultDTO.setStatus(Constants.PASSWORD_ERROR);
        } else {
            // 登录成功
            userLoginByPasswordResultDTO.setStatus(Constants.LOGIN_SUCCESS);
            // 更新用户的最后登入时间
            userMapper.updateLastLogin(phoneNumber);
            log.debug("Update last login time success: phoneNumber {} .", phoneNumber);

            // 生成token
            try {
                // 获取用户id和创建时间
                HashMap<String, String> map = new HashMap<>();
                map.put("id", user.getId().toString());
                map.put("phoneNumber", user.getPhoneNumber());
                if (user.getUserName() == null) {
                    map.put("userName", "");
                } else {
                    map.put("userName", user.getUserName());
                }
                map.put("createTime", user.getCreateTime().toString());
                token = JWTUtils.getToken(map);
                log.debug("Generate token success: phoneNumber {} .", phoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Generate token failed: phoneNumber {} .", phoneNumber);
            }
            userLoginByPasswordResultDTO.setToken(token);
            userLoginByPasswordResultDTO.setUserName(user.getUserName());
        }
        return userLoginByPasswordResultDTO;
    }

    /**
     * 用户通过企业微信快速登录
     *
     * @param userQuickLoginByEpAuthDTO 包括code
     * @return UserQuickLoginResultDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserQuickLoginResultDTO quickLoginByEpAuth(UserQuickLoginByEpAuthDTO userQuickLoginByEpAuthDTO) {
        // 获取code
        String code = userQuickLoginByEpAuthDTO.getCode();
        if (code == null || code.isEmpty()) {
            log.warn("User quick login failed: para code is null.");
            return new UserQuickLoginResultDTO(null, null, null, null);
        }
        // 获取用户登录信息
        String userInfo = AesDecryptUtils.aesDecrypt(code);
        // 提取用户信息（json -> map）
        ObjectMapper objectMapper = new ObjectMapper();
        String epWxId = "";
        String userName = "";
        String phoneNumber = "";
        try {
            Map jsonMap = objectMapper.readValue(userInfo, Map.class);
            epWxId = jsonMap.get("epUserId").toString();
            userName = (String) jsonMap.get("name");
            phoneNumber = (String) jsonMap.get("mobile");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("User quick login failed: {}", e.getMessage());
        }

        /* 判断是否首次登陆
            是：注册
            否：查询后登录
         */
        User user = userMapper.getUserByPhoneNumber(phoneNumber);

        if (user == null) {
            // 为空说明首次登录
            log.info("User quick login first: phoneNumber {} .", phoneNumber);
            // 注册
            UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
            userRegisterDTO.setPhoneNumber(phoneNumber);
            userMapper.registerUser(userRegisterDTO);
            // 存入用户信息，并存入登录时间
            UserInsertInfoDTO userInsertInfoDTO = new UserInsertInfoDTO(epWxId, userName, phoneNumber);
            userMapper.insertUserInfo(userInsertInfoDTO);
        } else {
            // 不为空直接登录成功，修改登录时间
            userMapper.updateLastLogin(phoneNumber);
        }

        // 更新一下user的数据
        user = userMapper.getUserByPhoneNumber(phoneNumber);

        // 生成token
        String token = "";
        try {
            // 获取用户id和创建时间
            HashMap<String, String> map = new HashMap<>();
            map.put("id", user.getId().toString());
            map.put("phoneNumber", user.getPhoneNumber());
            if (user.getUserName() == null) {
                map.put("userName", "");
            } else {
                map.put("userName", user.getUserName());
            }
            map.put("createTime", user.getCreateTime().toString());
            token = JWTUtils.getToken(map);
            log.debug("Generate token success: phoneNumber {} .", phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Generate token failed: phoneNumber {} .", phoneNumber);
            throw e;
        }

        // 返回所需要的用户信息
        UserQuickLoginResultDTO userQuickLoginResultDTO = new UserQuickLoginResultDTO(user.getAvatar(), userName, user.getRole(), token);
        return userQuickLoginResultDTO;
    }

    /**
     * 注册功能
     *
     * @param userRegisterDTO 包括phoneNumber、password、repeatPassword
     * @return Integer
     */
    @Override
    public Integer register(UserRegisterDTO userRegisterDTO) {
        // 需限制一下手机号和密码格式，前端or后端（未实现）

        String phoneNumber = userRegisterDTO.getPhoneNumber();
        String password = userRegisterDTO.getPassword();
        String repeatPassword = userRegisterDTO.getRepeatPassword();
        User user = userMapper.getUserByPhoneNumber(phoneNumber);
        if (user != null) {
            return Constants.ACCOUNT_EXIST;
        }
        if (!password.equals(repeatPassword)) {
            return Constants.REPEAT_PASSWORD_ERROR;
        }
        userRegisterDTO.setPassword(MD5Utils.getPwd(password));
        userMapper.registerUser(userRegisterDTO);
        return Constants.REGISTER_SUCCESS;
    }

    /**
     * 获取当前用户信息
     *
     * @param id Long
     * @return User
     */
    @Override
    public UserGetInfoByIdResultDTO getUserInfoById(Long id) {
        User user = userMapper.getUserInfoById(id);
        UserGetInfoByIdResultDTO userGetInfoByIdResultDTO = new UserGetInfoByIdResultDTO();
        if (user.getPassword() != null) {
            userGetInfoByIdResultDTO.setHasPassword((short) 1);
        } else {
            userGetInfoByIdResultDTO.setHasPassword((short) 0);
        }
        user.setPassword(null);
        userGetInfoByIdResultDTO.setUser(user);
        return userGetInfoByIdResultDTO;
    }

    /**
     * 修改密码
     *
     * @param userChangePasswordDTO 包括oldPassword、newPassword、repeatPassword
     * @return Integer
     */
    @Override
    public Integer changePassword(UserChangePasswordDTO userChangePasswordDTO) {
        // 需要判断当前用户是否设置了密码，如果有，则可以修改，没有，则限制修改密码

        // 获取旧密码、新密码、重复密码
        String oldPassword = userChangePasswordDTO.getOldPassword();
        String newPassword = userChangePasswordDTO.getNewPassword();
        String repeatPassword = userChangePasswordDTO.getRepeatPassword();
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTUserDetails jwtUserDetails = (JWTUserDetails) authentication.getPrincipal();
        Long userId = jwtUserDetails.getId();
        User user = userMapper.getUserInfoById(userId);
        // 判断用户是否设置了密码
        if (user.getPassword() == null) {
            // 没有设置密码，则不允许修改密码
            return Constants.ACCOUNT_NO_PASSWORD;
        }
        // 判断旧密码是否正确
        if (!MD5Utils.getPwd(oldPassword).equals(user.getPassword())) {
            return Constants.PASSWORD_ERROR;
        }
        // 判断新密码和重复密码是否一致
        if (!newPassword.equals(repeatPassword)) {
            return Constants.REPEAT_PASSWORD_ERROR;
        }
        userChangePasswordDTO.setNewPassword(MD5Utils.getPwd(newPassword));
        userMapper.changePassword(userChangePasswordDTO, userId);
        return Constants.PASSWORD_MODIFY_SUCCESS;
    }

    /**
     * 设置密码
     *
     * @param userSetPasswordDTO 包括password、repeatPassword
     * @return Integer
     */
    @Override
    public Integer setPassword(UserSetPasswordDTO userSetPasswordDTO) {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTUserDetails jwtUserDetails = (JWTUserDetails) authentication.getPrincipal();
        Long userId = jwtUserDetails.getId();
        if (userMapper.getUserInfoById(userId).getPassword() != null) {
            return Constants.ACCOUNT_EXIST_PASSWORD;
        }
        // 获取密码和重复密码
        String password = userSetPasswordDTO.getPassword();
        String repeatPassword = userSetPasswordDTO.getRepeatPassword();
        // 判断密码和重复密码是否一致
        if (!password.equals(repeatPassword)) {
            return Constants.REPEAT_PASSWORD_ERROR;
        }
        // 设置密码
        userMapper.setPassword(MD5Utils.getPwd(password), userId);
        return Constants.PASSWORD_MODIFY_SUCCESS;
    }

    /**
     * 修改用户信息
     *
     * @param userModifyInfoDTO 包括avatar、userName
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String modifyUserInfo(UserModifyInfoDTO userModifyInfoDTO) {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTUserDetails jwtUserDetails = (JWTUserDetails) authentication.getPrincipal();
        Long userId = jwtUserDetails.getId();
        // 修改用户信息
        userMapper.modifyUserInfo(userModifyInfoDTO, userId);
        log.debug("User modify info success.");
        // 同时要修改用户创建的站点的createBy（创建者）
        portalService.modifyPortalCreateBy(userModifyInfoDTO.getUserName(), userId);
        log.debug("User modify portal create by success.");
        // 生成新的token传回前端
        String token = "";
        try {
            // 获取用户id和创建时间
            HashMap<String, String> map = new HashMap<>();
            map.put("id", jwtUserDetails.getId().toString());
            map.put("phoneNumber", jwtUserDetails.getPhoneNumber());
            if (userModifyInfoDTO.getUserName() == null) {
                map.put("userName", "");
            } else {
                map.put("userName", userModifyInfoDTO.getUserName());
            }
            map.put("createTime", jwtUserDetails.getCreateTime().toString());
            token = JWTUtils.getToken(map);
            log.debug("Generate new token success: phoneNumber {} .", jwtUserDetails.getPhoneNumber());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Generate new token failed: phoneNumber {} .", jwtUserDetails.getPhoneNumber());
            throw e;
        }
        return token;
    }

}
