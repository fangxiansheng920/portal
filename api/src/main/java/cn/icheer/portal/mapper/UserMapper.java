package cn.icheer.portal.mapper;

import cn.icheer.portal.dto.UserChangePasswordDTO;
import cn.icheer.portal.dto.UserInsertInfoDTO;
import cn.icheer.portal.dto.UserModifyInfoDTO;
import cn.icheer.portal.dto.UserRegisterDTO;
import cn.icheer.portal.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.aop.IntroductionInterceptor;

/**
 * @author fangweihao
 * @since 2025/7/3 11:18
 */
@Mapper
public interface UserMapper {
    /**
     * 通过手机号获取用户所有信息
     *
     * @param phoneNumber String
     * @return User
     */
    User getUserByPhoneNumber(String phoneNumber);

    /**
     * 更新用户最后登录时间
     *
     * @param phoneNumber String
     */
    void updateLastLogin(String phoneNumber);

    /**
     * 手机号密码注册
     *
     * @param userRegisterDTO UserRegisterDTO
     */
    void registerUser(UserRegisterDTO userRegisterDTO);

    /**
     * 插入企业微信首次登录的信息，不作为个人信息修改的方法
     *
     * @param userInsertInfoDTO UserInsertInfoDto
     */
    void insertUserInfo(UserInsertInfoDTO userInsertInfoDTO);

    /**
     * 获取当前用户信息
     *
     * @param id Long
     * @return User
     */
    User getUserInfoById(Long id);

    /**
     * 修改密码
     *
     * @param userChangePasswordDTO UserChangePasswordDTO
     */
    void changePassword(@Param("userChangePassword") UserChangePasswordDTO userChangePasswordDTO, @Param("userId") Long userId);

    /**
     * 设置密码
     *
     * @param password String
     * @param userId Long
     */
    Integer setPassword(String password, Long userId);

    /**
     * 修改用户信息
     *
     * @param userModifyInfoDTO UserModifyInfoDTO
     */
    void modifyUserInfo(@Param("userModifyInfo") UserModifyInfoDTO userModifyInfoDTO, @Param("userId") Long userId);

}
