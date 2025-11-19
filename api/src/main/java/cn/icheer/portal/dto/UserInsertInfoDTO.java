package cn.icheer.portal.dto;

import lombok.Data;

/**
 * @author fangweihao
 * @since 2025/7/4 14:58
 */
@Data
public class UserInsertInfoDTO {
    /**
     * 企业微信id
     */
    private String epWxId;

    /**
     * 用户名字
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String phoneNumber;

    public UserInsertInfoDTO() {
    }

    public UserInsertInfoDTO(String epWxId, String userName, String phoneNumber) {
        this.epWxId = epWxId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }
}
