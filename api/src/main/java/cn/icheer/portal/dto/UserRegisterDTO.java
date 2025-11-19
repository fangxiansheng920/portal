package cn.icheer.portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author fangweihao
 * @since 2025/7/4 14:13
 */
@Data
public class UserRegisterDTO {
    /**
     * 用户手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 重复密码
     */
    @NotBlank(message = "重复密码不能为空")
    private String repeatPassword;

    public UserRegisterDTO() {
    }

    public UserRegisterDTO(String phoneNumber, String password, String repeatPassword) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }
}
