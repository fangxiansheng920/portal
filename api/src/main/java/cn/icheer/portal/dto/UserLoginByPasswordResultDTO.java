package cn.icheer.portal.dto;

import lombok.Data;

/**
 * @author fangweihao
 * @since 2025/7/7 9:55
 */
@Data
public class UserLoginByPasswordResultDTO {
    /**
     * 状态：0：无此用户 1：未设置密码 2、密码错误 3、登录成功
     */
    private Integer status;

    /**
     * 用户token
     */
    private String token;

    /**
     * 用户姓名
     */
    private String userName;
}
