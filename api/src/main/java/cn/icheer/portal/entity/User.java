package cn.icheer.portal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fangweihao
 * @since 2025/7/3 9:11
 */
@Data
public class User {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户企业微信中的id
     */
    private String epWxId;

    /**
     * 绑定微信用户标识
     */
    private String unionId;

    /**
     * 用户姓名(不作为登录使用)
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户默认角色
     */
    private Short role;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 删除标志位
     */
    private Short deleted;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 用户手机号码
     */
    private String phoneNumber;
}
