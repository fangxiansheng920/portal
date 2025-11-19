package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author fangweihao
 * @since 2025/7/17 13:04
 */
@Data
public class UserChangePasswordDTO {
    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    /**
     * 重复密码
     */
    @NotBlank(message = "重复密码不能为空")
    private String repeatPassword;

    public UserChangePasswordDTO() {
    }

    public UserChangePasswordDTO(String oldPassword, String newPassword, String repeatPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.repeatPassword = repeatPassword;
    }
}
