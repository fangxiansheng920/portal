package cn.icheer.portal.dto;

import lombok.Data;

/**
 * @author fangweihao
 * @since 2025/7/4 12:17
 */
@Data
public class UserQuickLoginResultDTO {
    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户角色（权限）
     */
    private Short role;

    /**
     * 用户token
     */
    private String token;

    public UserQuickLoginResultDTO() {
    }

    public UserQuickLoginResultDTO(String avatar, String userName, Short role, String token) {
        this.avatar = avatar;
        this.userName = userName;
        this.role = role;
        this.token = token;
    }
}
