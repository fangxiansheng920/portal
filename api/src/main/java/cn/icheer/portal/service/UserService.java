package cn.icheer.portal.service;

import cn.icheer.portal.dto.*;
import cn.icheer.portal.entity.User;

/**
 * @author fangweihao
 * @since 2025/7/3 11:13
 */
public interface UserService {
    /**
     * 用户通过密码登录
     * @param userLoginByPasswordDTO 包括phoneNumber、password
     * @return UserLoginByPasswordResultDTO
     */
    UserLoginByPasswordResultDTO loginByPassword(UserLoginByPasswordDTO userLoginByPasswordDTO);

    /**
     * 用户通过企业微信快速登录
     * @param userQuickLoginByEpAuthDTO 包括code
     * @return UserQuickLoginResultDTO
     */
    UserQuickLoginResultDTO quickLoginByEpAuth(UserQuickLoginByEpAuthDTO userQuickLoginByEpAuthDTO);

    /**
     * 注册功能
     * @param userRegisterDTO 包括phoneNumber、password、repeatPassword
     * @return Integer
     */
    Integer register(UserRegisterDTO userRegisterDTO);

    /**
     * 获取当前用户信息
     * @param id Long
     * @return User
     */
    UserGetInfoByIdResultDTO getUserInfoById(Long id);

    /**
     * 修改密码
     * @param userChangePasswordDTO 包括oldPassword、newPassword、repeatPassword
     * @return Integer
     */
    Integer changePassword(UserChangePasswordDTO userChangePasswordDTO);

    /**
     * 设置密码
     * @param userSetPasswordDTO 包括password、repeatPassword
     * @return Integer
     */
    Integer setPassword(UserSetPasswordDTO userSetPasswordDTO);

    /**
     * 修改用户信息
     *
     * @param userModifyInfoDTO 包括avatar、userName
     */
    String modifyUserInfo(UserModifyInfoDTO userModifyInfoDTO);

}
