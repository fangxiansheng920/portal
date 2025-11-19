package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author fangweihao
 * @since 2025/7/18 9:07
 */
@Data
public class UserSetPasswordDTO {
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 重复密码
     */
    @NotBlank(message = "重复密码不能为空")
    private String repeatPassword;
}
