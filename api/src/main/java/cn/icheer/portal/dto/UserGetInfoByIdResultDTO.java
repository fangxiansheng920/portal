package cn.icheer.portal.dto;

import cn.icheer.portal.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fangweihao
 * @since 2025/7/25 17:48
 */
@Data
public class UserGetInfoByIdResultDTO {

    /**
     * 用户信息
     */
    private User user;

    /**
     * 是否设置了密码
     */
    private Short hasPassword;
}
