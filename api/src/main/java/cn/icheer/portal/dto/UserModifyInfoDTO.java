package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author fangweihao
 * @since 2025/7/18 13:16
 */
@Data
public class UserModifyInfoDTO {
    /**
     * 用户姓名
     */
    @Size(max = 20, message = "用户姓名长度不能超过20个字")
    private String userName;

    /**
     * 头像
     */
    private String avatar;
}
